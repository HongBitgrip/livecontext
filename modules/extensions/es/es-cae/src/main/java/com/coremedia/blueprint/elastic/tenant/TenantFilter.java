package com.coremedia.blueprint.elastic.tenant;

import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.api.tenant.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PreDestroy;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TenantFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(TenantFilter.class);

  private ElasticSocialPlugin elasticSocialPlugin;
  private TenantService tenantService;
  private Set<String> registeredTenants = Collections.synchronizedSet(new HashSet<String>());
  private Pattern excludes;

  @Required
  public void setElasticSocialPlugin(ElasticSocialPlugin elasticSocialPlugin) {
    this.elasticSocialPlugin = elasticSocialPlugin;
  }

  @Required
  public void setTenantService(TenantService tenantService) {
    this.tenantService = tenantService;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do on init
  }

  public String determineTenant(ServletRequest request) {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      String pathInfo = httpServletRequest.getPathInfo();
      if(excludes != null) {
        Matcher m = excludes.matcher(pathInfo);
        if (m.matches()) {
          LOG.debug("excluding request {} from tenant lookup", toString(request));
          return null;
        }
      }

      Content siteRootDocument = SiteHelper.findSite(request)
              .map(Site::getSiteRootDocument)
              .orElse(null);

      if (siteRootDocument != null) {
        ElasticSocialConfiguration elasticSocialConfiguration = elasticSocialPlugin
                .getElasticSocialConfiguration(siteRootDocument);

        String tenantName = elasticSocialConfiguration.getTenant();
        if (tenantName != null) {
          LOG.debug("tenant for request {} is {}", toString(request), tenantName);
          return tenantName;
        }
      }
      LOG.warn("Cannot find elastic social tenant for request '{}'", toString(request));
    }
    return null;
  }

  private String toString(ServletRequest request) {
    if (request instanceof HttpServletRequest) {
      return ((HttpServletRequest) request).getRequestURI();
    }
    return request.toString();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String tenantName = determineTenant(request);
    if (tenantName != null) {
      //todo the following calls should be combined to a new method, e.g. TenantService#useTenant()
      tenantService.register(tenantName);
      registeredTenants.add(tenantName);
      tenantService.setCurrent(tenantName);
    }
    try {
      chain.doFilter(request, response);
    } finally {
      tenantService.clearCurrent();
    }
  }

  @Override
  @PreDestroy
  public void destroy() {
    tenantService.deregisterAll(getTenants());
  }

  private Collection<String> getTenants() {
    return registeredTenants;
  }

  public void setExcludes(String exclude) {
    this.excludes = Pattern.compile(exclude);
  }

}

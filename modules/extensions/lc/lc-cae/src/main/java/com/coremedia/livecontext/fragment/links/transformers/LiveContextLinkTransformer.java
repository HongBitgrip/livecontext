package com.coremedia.livecontext.fragment.links.transformers;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.web.taglib.FindNavigationContext;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMExternalLink;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.CurrentContextService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.LiveContextLinkResolver;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.remove;

/**
 * LiveContextLinkTransformer that creates Commerce-Links with cm-Param containing the backend-url.
 * in addition:
 * - removing jsessionid
 * - passing ShopController-Redirect Requests
 */
public class LiveContextLinkTransformer implements LinkTransformer {

  protected static final Logger LOG = LoggerFactory.getLogger(LiveContextLinkTransformer.class);

  private static final String VARIANT_PARAM = "variant";
  private static final String LIVECONTEXT_CONTENT_LED = "livecontext.contentLed";

  private List<LiveContextLinkResolver> liveContextLinkResolverList;
  private boolean isRemoveJSession = true;
  private CurrentContextService currentContextService;
  private SettingsService settingsService;
  private SitesService sitesService;

  @Override
  public String transform(String cmsLink, Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) {
    // Only transform links for Fragment Requests
    if (!isFragmentRequest(request)) {
      return cmsLink;
    }

    StoreContext storeContext = CurrentCommerceConnection.get().getStoreContext();

    String siteId = requireNonNull(storeContext.getSiteId(), "Site ID must be set on store context.");
    Site site = sitesService.findSite(siteId)
            .orElseThrow(() -> new IllegalStateException(
                    String.format("Could not find a site for store id '%s' and locale '%s'.",
                            storeContext.getStoreId(), storeContext.getLocale())));

    boolean isContentLed = settingsService.settingWithDefault(LIVECONTEXT_CONTENT_LED, Boolean.class, false, site);
    if (isContentLed) {
      return cmsLink;
    }

    // do not post-process external links
    if (bean instanceof CMExternalLink) {
      return cmsLink;
    }

    boolean isLinkable = bean instanceof CMLinkable ||
            bean instanceof LiveContextNavigation ||
            bean instanceof Product ||
            bean instanceof ProductInSite ||
            bean instanceof Category ||
            bean instanceof CategoryInSite;
    boolean isPage = bean instanceof Page;
    if (!isLinkable && !isPage) {
      return cmsLink;
    }

    if (cmsLink != null && cmsLink.contains("/" + UriConstants.Segments.PREFIX_DYNAMIC + "/")) {
      return cmsLink;
    }

    CMNavigation navigation = getNavigation(bean);
    Object content = getContent(bean);

    // do not post-process links to content of a different site
    if (isContentOfDifferentSite(navigation, request)) {
      return cmsLink;
    }

    String modifiableSource = removeBaseUri(cmsLink, request);
    modifiableSource = removeJSession(modifiableSource);
    Object variant = ViewUtils.getParameters(request).get(VARIANT_PARAM);

    return transform(modifiableSource, content, variant, navigation, request);
  }

  private static boolean isFragmentRequest(HttpServletRequest request) {
    return FragmentContextProvider.findFragmentContext(request)
            .map(FragmentContext::isFragmentRequest)
            .orElse(false);
  }

  private String transform(String modifiableSource, Object content, Object variant, CMNavigation navigation, HttpServletRequest request) {
    String lcUrl = null;

    for (LiveContextLinkResolver resolver : liveContextLinkResolverList) {
      if (resolver.isApplicable(content)) {
        lcUrl = resolver.resolveUrl(content, variant != null ? variant + "" : null, navigation, request);
        if ((lcUrl != null)) {
          break;
        }
      }
    }

    if (isNotBlank(lcUrl)) {
      return lcUrl;
    } else if (isNotBlank(modifiableSource)) {
      return modifiableSource;
    }
    return "#";
  }

  private String removeBaseUri(String source, HttpServletRequest request) {
    String baseUri = ViewUtils.getBaseUri(request);
    if (source != null && source.startsWith(baseUri)) {
      return remove(source, baseUri);
    }

    return source;
  }

  private String removeJSession(String source) {
    if (isRemoveJSession && source != null && source.contains(";jsessionid")) {
      int jSessionIndex = StringUtils.indexOf(source, ";jsessionid");
      return left(source, jSessionIndex);
    }

    return source;
  }

  private Object getContent(Object bean) {
    Object content = bean;

    if (content instanceof Page) {
      content = ((Page) content).getContent();
    }

    return content;
  }

  private CMNavigation getNavigation(Object bean) {
    if (bean instanceof Page) {
      bean = ((Page) bean).getNavigation();
    }

    if (bean instanceof CMNavigation) {
      return (CMNavigation) bean;
    }


    if (bean instanceof CMLinkable) {
      List<? extends CMContext> contexts = ((CMLinkable) bean).getContexts();
      if (!contexts.isEmpty()) {
        return contexts.get(0);
      }
    }

    CMNavigation navigation = currentContextService.getContext();
    if (navigation != null) {
      return navigation;
    }

    return null;
  }

  /**
   * Return true if this is content of a different site
   */
  protected boolean isContentOfDifferentSite(CMNavigation navigation, HttpServletRequest request) {
    try {
      CMNavigation targetRootNavigation = navigation.getRootNavigation();
      Navigation currentNavigation = FindNavigationContext.getNavigation(request);
      return !currentNavigation.getRootNavigation().equals(targetRootNavigation);
    }
    catch (Exception e) {
      LOG.error("Cannot determine whether the given content belongs to a different site: " + e.getMessage(), e);
      return false;
    }
  }

  @Required
  public void setLiveContextLinkResolverList(List<LiveContextLinkResolver> liveContextLinkResolverList) {
    this.liveContextLinkResolverList = ImmutableList.copyOf(liveContextLinkResolverList);
  }

  public void setRemoveJSession(boolean removeJSession) {
    isRemoveJSession = removeJSession;
  }

  @Required
  public void setCurrentContextService(CurrentContextService currentContextService) {
    this.currentContextService = currentContextService;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }
}

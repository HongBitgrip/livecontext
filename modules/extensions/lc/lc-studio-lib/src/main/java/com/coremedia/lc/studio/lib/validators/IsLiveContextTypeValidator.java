package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

/**
 * Make ssure the content type can only be used in livecontext sites
 */
public class IsLiveContextTypeValidator extends ContentTypeValidatorBase {

  private static final String CODE_ISSUE_DOC_TYPE_NOT_SUPPORTED = "DocTypeNotSupported";

  private CommerceConnectionInitializer commerceConnectionInitializer;
  private SitesService sitesService;

  @Override
  public void validate(Content content, Issues issues) {
    if (content == null || !content.isInProduction()) {
      return;
    }

    //check if the content belongs to a livecontext site
    Optional<Site> site = sitesService.getContentSiteAspect(content).findSite();
    Optional<CommerceConnection> commerceConnection = site.flatMap(s -> commerceConnectionInitializer.findConnectionForSite(s));
    if (!commerceConnection.isPresent() || "coremedia".equals(commerceConnection.get().getVendorName())) {
      issues.addIssue(Severity.ERROR, null, getContentType() + "_" + CODE_ISSUE_DOC_TYPE_NOT_SUPPORTED, getContentType());
    }
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

}

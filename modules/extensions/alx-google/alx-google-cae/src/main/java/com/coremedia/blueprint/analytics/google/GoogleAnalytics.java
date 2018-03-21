package com.coremedia.blueprint.analytics.google;


import com.coremedia.blueprint.analytics.AnalyticsProvider;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.Page;

public class GoogleAnalytics extends AnalyticsProvider {
  /**
   * The Google Analytics service key.
   */
  public static final String GOOGLE_ANALYTICS_SERVICE_KEY = "googleAnalytics";

  private static final String WEB_PROPERTY_ID_KEY = "webPropertyId";
  private static final String DOMAIN_NAME_KEY = "domainName";
  private static final String DEFAULT_DOMAIN_NAME = "auto";

  public GoogleAnalytics(Page page, SettingsService settingsService) {
    super(GOOGLE_ANALYTICS_SERVICE_KEY, page, settingsService);
  }

  @Override
  protected boolean isConfigValid() {
    return isNonEmptyString(getWebPropertyId(), WEB_PROPERTY_ID_KEY);
  }

  public Object getWebPropertyId() {
    return getSettings().get(WEB_PROPERTY_ID_KEY);
  }

  public String getDomainName() {
    final Object domainName = getSettings().get(DOMAIN_NAME_KEY);
    return domainName instanceof String ? (String) domainName : DEFAULT_DOMAIN_NAME;
  }
}

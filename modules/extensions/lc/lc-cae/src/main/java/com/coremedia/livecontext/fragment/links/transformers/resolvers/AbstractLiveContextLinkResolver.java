package com.coremedia.livecontext.fragment.links.transformers.resolvers;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.services.context.CurrentContextService;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * Base class for all LiveContext link resolvers.
 */
public abstract class AbstractLiveContextLinkResolver implements LiveContextLinkResolver {
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractLiveContextLinkResolver.class);

  protected static final String TYPE_DEFAULT = "DEFAULT";
  protected static final String TYPE_WILDCARD = "*";
  protected static final String TYPE_DELIMITER = "#";

  public static final String KEY_PLAIN_LINK = "--PLAIN_LINK--";

  public static final String LIVECONTEXT_COMMENT_PREFIX = "<!--CM ";
  public static final String LIVECONTEXT_COMMENT_SUFFIX = "CM-->";

  private DataViewFactory dataViewFactory;

  private CurrentContextService currentContextService;
  private SettingsService settingsService;

  @Override
  public String resolveUrl(Object bean, String variant, CMNavigation navigation, HttpServletRequest request) {
    long start = System.currentTimeMillis();
    String result = null;

    try {

      JSONObject json = resolveUrlInternal(bean, variant, navigation, request);
      // Special handling for Links that are resolved directly,
      // i.e. for com.coremedia.blueprint.common.contentbeans.CMExternalLink
      if (json.has(KEY_PLAIN_LINK)) {
        return json.getString(KEY_PLAIN_LINK);
      }

      result = LIVECONTEXT_COMMENT_PREFIX.concat(json.toString()).concat(LIVECONTEXT_COMMENT_SUFFIX);
    } catch (JSONException e) {
      LOG.error("Could not build URL JSON for {}", bean.toString().concat("#").concat("variant"), e);
    }

    if (LOG.isTraceEnabled()) {
      long duration = System.currentTimeMillis() - start;
      LOG.trace("building url {} takes {} milliseconds.", result, duration);
    }

    return result;
  }

  /**
   * @param bean       Bean for which URL is to be rendered
   * @param variant    Link variant
   * @param navigation Current navigation of bean for which URL is to be rendered
   * @param request
   * @return JSON object containing all relevant details for URL rendering, except for "type":"URL", which
   * will be added by {@link AbstractLiveContextLinkResolver} automatically.
   */
  protected abstract JSONObject resolveUrlInternal(Object bean, String variant, CMNavigation navigation, HttpServletRequest request) throws JSONException;

  protected Map getSettingsMap(String type, String variant, CMNavigation navigation) {
    CMNavigation context = navigation;
    if (context == null) {
      context = getCurrentContextService().getContext();
    }
    // Ensure dataview is loaded for context
    context = getDataViewFactory().loadCached(context, null);

    Map result = settingsService.setting(type + TYPE_DELIMITER + variant, Map.class, context);
    if (result == null) {
      result = settingsService.setting(TYPE_WILDCARD + TYPE_DELIMITER + variant, Map.class, context);
    }
    if (result == null) {
      result = settingsService.setting(type, Map.class, context);
    }
    if (result == null) {
      result = settingsService.setting(TYPE_DEFAULT, Map.class, context);
    }
    if (result == null) {
      result = Collections.emptyMap();
    }
    return result;
  }

  protected String getContentType(Object bean) {
    String result = null;
    if (bean instanceof CMObject) {
      result = ((CMObject) bean).getContent().getType().getName();
    }
    return result;
  }

  public DataViewFactory getDataViewFactory() {
    return dataViewFactory;
  }

  @Required
  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  public SettingsService getSettingsService() {
    return settingsService;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  public CurrentContextService getCurrentContextService() {
    return currentContextService;
  }

  @Required
  public void setCurrentContextService(CurrentContextService currentContextService) {
    this.currentContextService = currentContextService;
  }
}

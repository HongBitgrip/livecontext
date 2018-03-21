package com.coremedia.livecontext.fragment;


import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.user.User;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.handler.util.CaeStoreContextUtil;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.UserVariantHelper;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_HTML;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_SERVICE;

/**
 * This handler serves all fragment requests, called by the lc:include tag of commerce.
 */
@RequestMapping
public class FragmentPageHandler extends PageHandlerBase {
  private static final Logger LOG = LoggerFactory.getLogger(FragmentPageHandler.class);

  private static final String SEGMENT_STOREID = "storeId";
  private static final String SEGMENT_LOCALE = "locale";
  private static final String SEGMENT_MATRIX_PARAMS = "params";
  private CatalogAliasTranslationService catalogAliasTranslationService;

  //parameter name used for putting the value of the matrix-parameter "parameter" into the request
  private static final String ATTR_NAME_FRAGMENT_PARAMETER = "fragmentParameter";

  public static final String FRAGMENT_URI_PREFIX = '/' + PREFIX_SERVICE + '/' + SEGMENTS_FRAGMENT;
  public static final String FRAGMENT_INTERCEPTOR_PATTERN = FRAGMENT_URI_PREFIX + "/{all:.*}";
  public static final String URI_PATTERN = FRAGMENT_URI_PREFIX +
          "/{" + SEGMENT_STOREID + '}' +
          "/{" + SEGMENT_LOCALE + '}' +
          "/{" + SEGMENT_MATRIX_PARAMS + ":.*}";

  private boolean isPreview = false;
  private List<FragmentHandler> fragmentHandlers;

  // --- interface --------------------------------------------------

  /**
   * This is the central request mapping for all fragment request.
   * The @see com.coremedia.livecontext.handler.FragmentCommerceContextInterceptor should have been executed
   * before to ensure that the store context has been set properly.
   *
   * @param storeId The storeId to identify the store.
   * @param locale  The locale to identify the store.
   * @param request The actual request, needed to put the optional "parameter" into the request.
   */
  @RequestMapping(value = URI_PATTERN, produces = CONTENT_TYPE_HTML)
  public ModelAndView handleFragment(@PathVariable(SEGMENT_STOREID) String storeId,
                                     @PathVariable(SEGMENT_LOCALE) Locale locale,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
    StoreContext storeContext = CurrentCommerceConnection.find().map(CommerceConnection::getStoreContext).orElse(null);
    if (storeContext == null) {
      return HandlerHelper.badRequest("Store context not initialized for fragment call " + request.getRequestURI());
    }

    response.setContentType(CONTENT_TYPE_HTML);
    FragmentParameters fragmentParameters = FragmentContextProvider.getFragmentContext(request).getParameters();

    //resolve the site first
    Site site = getSitesService().getSite(storeContext.getSiteId());
    if (site == null) {
      return createNoSiteModelAndView(fragmentParameters, storeId, locale);
    }
    SiteHelper.setSiteToRequest(site, request);

    //update store context with fragment parameters
    CaeStoreContextUtil.updateStoreContextWithFragmentParameters(catalogAliasTranslationService, storeContext, fragmentParameters, site);

    ModelAndView modelAndView;
    FragmentHandler handler = selectHandler(fragmentParameters);
    if (handler != null) {
      modelAndView = handler.createModelAndView(fragmentParameters, request);
      if (modelAndView == null) {
        return createErrorModelAndView(fragmentParameters, handler);
      }
    } else {
      User developer = UserVariantHelper.getUser(request);
      modelAndView = createDefaultModelAndView(fragmentParameters, site, developer);
    }

    //apply the parameter value to the request if a value was set
    modelAndView.addObject(ATTR_NAME_FRAGMENT_PARAMETER, fragmentParameters);
    return modelAndView;
  }

  //-------------- Config --------------------

  @Required
  public void setFragmentHandlers(List<FragmentHandler> fragmentHandlers) {
    this.fragmentHandlers = ImmutableList.copyOf(fragmentHandlers);
  }

  public void setPreview(boolean isPreview) {
    this.isPreview = isPreview;
  }

  @Required
  public void setCatalogAliasTranslationService(CatalogAliasTranslationService catalogAliasTranslationService) {
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }

  // --- internal ---------------------------------------------------

  /**
   * search for a FragmentHandler that feels responsible for the request,
   * depending on the parameters.
   */
  private FragmentHandler selectHandler(FragmentParameters fragmentParameters) {
    return fragmentHandlers.stream()
            .filter(handler -> handler.include(fragmentParameters))
            .findFirst()
            .orElse(null);
  }

  /**
   * If no handler has been applied we assume the default behaviour.
   * This usually happens if only the view param is passed.
   */
  private ModelAndView createDefaultModelAndView(FragmentParameters fragmentParameters, Site site, @Nullable User developer) {
    Content rootChannel = site.getSiteRootDocument();
    CMChannel channel = getContentBeanFactory().createBeanFor(rootChannel, CMChannel.class);
    Page page = asPage(channel, channel, developer);
    return createModelAndView(page, fragmentParameters.getView());
  }

  private ModelAndView createNoSiteModelAndView(FragmentParameters fragmentParameters, String storeId, Locale locale) {
    if (isPreview) {
      return HandlerHelper.badRequest("Could not find a site for store " + fragmentParameters);
    }
    LOG.warn("Received an invalid fragment request. No site found for store '{}' with locale '{}'. " +
            "Will return an empty response with return code 200 to not break the whole WCS page.", storeId, locale);
    return null;
  }

  private ModelAndView createErrorModelAndView(FragmentParameters fragmentParameters, FragmentHandler handler) {
    LOG.warn("Fragment handler '" + handler + "' did not return any ModelAndView for " + fragmentParameters);
    return HandlerHelper.notFound("Fragment handler '" + handler + "' did not return any ModelAndView for " + fragmentParameters);
  }
}

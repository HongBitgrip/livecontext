package com.coremedia.livecontext.marketingspot;

import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMMarketingSpot;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.UserVariantHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_HTML;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.TARGETVIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.base.links.UriConstants.Views.VIEW_FRAGMENT;

/**
 * Handle dynamic/personalized marketing spots via esi/client include.
 * see also {@link CMMarketingSpotDynamicIncludePredicate}
 */
@Link
@RequestMapping
public class CMMarketingSpotHandler extends PageHandlerBase {

  private static final String URI_PREFIX = "marketingspot";
  private static final String MARKETING_SPOT_ID_VARIABLE = "id";
  private static final String SHOP_NAME_VARIABLE = "shopName";

  /**
   * URI pattern, for URIs like "/dynamic/fragment/marketingspot/shopName/id"
   */
  public static final String DYNAMIC_URI_PATTERN = '/' + PREFIX_DYNAMIC +
          '/' + SEGMENTS_FRAGMENT +
          '/' + URI_PREFIX +
          "/{" + SHOP_NAME_VARIABLE + "}" +
          "/{" + MARKETING_SPOT_ID_VARIABLE + ":" + PATTERN_NUMBER + "}";

  private ContentRepository contentRepository;

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @RequestMapping(value = DYNAMIC_URI_PATTERN, produces = CONTENT_TYPE_HTML, method = RequestMethod.GET)
  public ModelAndView handleFragmentRequest(@PathVariable(MARKETING_SPOT_ID_VARIABLE) int marketingSpotId,
                                            @RequestParam(value = TARGETVIEW_PARAMETER, required = false) String view,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
    CMMarketingSpot cmMarketingSpot = resolveMarketingSpot(marketingSpotId);

    if (cmMarketingSpot != null) {
      //strange, the "produces" annotation value does not work, so we set the response mime type manually
      response.setContentType(CONTENT_TYPE_HTML);
      CMNavigation cmNavigation = getContextHelper().contextFor(cmMarketingSpot);
      ModelAndView modelWithView = HandlerHelper.createModelWithView(cmMarketingSpot, view);

      Page page = asPage(cmNavigation, cmNavigation, UserVariantHelper.getUser(request));
      addPageModel(modelWithView, page);
      return modelWithView;
    }
    return HandlerHelper.notFound();
  }

  @Link(type = CMMarketingSpot.class, uri = DYNAMIC_URI_PATTERN, view = VIEW_FRAGMENT)
  public UriComponents buildFragmentLink(CMMarketingSpot marketingSpot, UriTemplate uriPattern, Map<String, Object> linkParameters) {
    return buildLinkInternal(marketingSpot, uriPattern, linkParameters);
  }

  private CMMarketingSpot resolveMarketingSpot(int marketingSpotId) {

    return asContentBean(marketingSpotId, CMMarketingSpot.class);
  }

  private UriComponents buildLinkInternal(CMMarketingSpot marketingSpot, UriTemplate uriPattern, Map<String, Object> linkParameters) {
    UriComponentsBuilder result = UriComponentsBuilder.fromPath(uriPattern.toString());
    result = addLinkParametersAsQueryParameters(result, linkParameters);
    Site site = getSitesService().getContentSiteAspect(marketingSpot.getContent()).getSite();
    if(site != null) {
      String siteName = getContentLinkBuilder().getVanityName(site.getSiteRootDocument());

      return result.buildAndExpand(ImmutableMap.of(MARKETING_SPOT_ID_VARIABLE, marketingSpot.getContentId(),
              SHOP_NAME_VARIABLE, siteName));
    }
    return null;
  }

  private <T> T asContentBean(final int contentId, final Class<T> type) {
    return asContentBean(contentRepository.getContent(IdHelper.formatContentId(contentId)), type);
  }

  private <T> T asContentBean(final Content content, final Class<T> type) {
    if (content != null) {
      ContentBean contentBean = getContentBeanFactory().createBeanFor(content);
      if (type.isAssignableFrom(contentBean.getClass())) {
        return type.cast(contentBean);
      }
    }
    return null;
  }
}

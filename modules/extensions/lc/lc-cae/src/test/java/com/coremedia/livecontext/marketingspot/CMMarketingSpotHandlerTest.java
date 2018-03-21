package com.coremedia.livecontext.marketingspot;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.contentbeans.CMMarketingSpot;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CMMarketingSpotHandlerTest {

  private static final String MARKETINGSPOT_PREFIX = '/' + PREFIX_DYNAMIC + '/' + SEGMENTS_FRAGMENT + "/marketingspot/";
  private static final String SITE_VANITYNAME = "anySiteVanityName";
  private CMMarketingSpotHandler testling;

  @Mock
  private BeanFactory beanFactory;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private SitesService sitesService;

  @Mock
  private ContentLinkBuilder contentLinkBuilder;


  @Before
  public void setUp() throws Exception {
    testling = new CMMarketingSpotHandler();
    testling.setBeanFactory(beanFactory);
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setContentRepository(contentRepository);
    testling.setSitesService(sitesService);
    testling.setContextHelper(contextHelper);
    testling.setContentLinkBuilder(contentLinkBuilder);

    Cache noCache = mock(Cache.class);
    when(beanFactory.getBean("cmPage", PageImpl.class)).thenReturn(new PageImpl(false, sitesService, noCache, null, null, null));
  }

  @Test
  public void testBuildLinkForCMMarketingSpot() throws Exception {
    UriTemplate uriTemplate = mock(UriTemplate.class);
    when(uriTemplate.toString()).thenReturn(CMMarketingSpotHandler.DYNAMIC_URI_PATTERN);

    Content marketingSpotContent = mock(Content.class);
    Content siteContent = mock(Content.class);
    CMMarketingSpot cmMarketingSpot = mock(CMMarketingSpot.class);
    Site site = mock(Site.class);
    ContentSiteAspect contentSiteAspect = mock(ContentSiteAspect.class);
    when(cmMarketingSpot.getContentId()).thenReturn(4711);
    when(cmMarketingSpot.getContent()).thenReturn(marketingSpotContent);
    when(sitesService.getContentSiteAspect(marketingSpotContent)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);
    when(site.getSiteRootDocument()).thenReturn(siteContent);
    when(contentLinkBuilder.getVanityName(siteContent)).thenReturn(SITE_VANITYNAME);

    Map<String, Object> linkParameters = new HashMap<>();
    linkParameters.put("targetView", "fragment");

    UriComponents uriComponents = testling.buildFragmentLink(cmMarketingSpot, uriTemplate, linkParameters);

    assertEquals("Expected link does not match built link.",
                 MARKETINGSPOT_PREFIX + SITE_VANITYNAME+"/4711", uriComponents.getPath());
  }

  @Test
  public void testHandleDynamicRequestCMMarketingSpot() throws Exception {
    Content marketingSpotContent = mock(Content.class);
    Content cmNavigationContent = mock(Content.class);
    ContentType cmNavigationContentType = mock(ContentType.class);
    CMMarketingSpot cmMarketingSpot = mock(CMMarketingSpot.class);
    CMNavigation cmNavigation = mock(CMNavigation.class);
    when(contentRepository.getContent(anyString())).thenReturn(marketingSpotContent);
    when(contentBeanFactory.createBeanFor(marketingSpotContent)).thenReturn(cmMarketingSpot);
    when(contextHelper.contextFor(cmMarketingSpot)).thenReturn(cmNavigation);
    when(cmNavigation.getContent()).thenReturn(cmNavigationContent);
    when(cmNavigationContent.getType()).thenReturn(cmNavigationContentType);
    when(cmNavigationContentType.getName()).thenReturn(CMMarketingSpot.NAME);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    ModelAndView modelAndView = testling.handleFragmentRequest(4711, "fragment", request, response);
    assertEquals("fragment", modelAndView.getViewName());
    assertTrue(modelAndView.getModel().get("self") instanceof CMMarketingSpot);
    assertTrue(modelAndView.getModel().get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION) instanceof CMNavigation);
  }
}

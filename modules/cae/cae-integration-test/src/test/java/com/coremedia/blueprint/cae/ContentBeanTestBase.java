package com.coremedia.blueprint.cae;

import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.coremedia.cap.common.IdHelper.formatContentId;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CACHE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CONTENT_BEAN_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.DATA_VIEW_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.ID_PROVIDER;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.LINK_FORMATTER;


/**
 * Base test infrastructure all content bean tests
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ContentBeanTestBase.LocalConfig.class)
public abstract class ContentBeanTestBase {
  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ContentBeanFactory contentBeanFactory;
  @Inject
  private DataViewFactory dataViewFactory;
  @Inject
  private Cache cache;
  @Inject
  private SitesService sitesService;

  public Content getContent(int id) {
    return contentRepository.getContent(formatContentId(id));
  }

  /**
   * Returns the ContentBean with the given id.
   *
   * @param id Id of ContentBean to get
   * @return ContentBean
   */
  protected <T> T getContentBean(int id) {
    return (T) contentBeanFactory.createBeanFor(getContent(id));
  }

  public ContentBeanFactory getContentBeanFactory() {
    return contentBeanFactory;
  }

  public DataViewFactory getDataViewFactory() {
    return dataViewFactory;
  }

  public ContentRepository getContentRepository() {
    return contentRepository;
  }

  public Cache getCache() {
    return cache;
  }

  @Configuration
  @ImportResource(
          value = {
                  CACHE,
                  CONTENT_BEAN_FACTORY,
                  DATA_VIEW_FACTORY,
                  ID_PROVIDER,
                  LINK_FORMATTER,
                  "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
                  "classpath:/framework/spring/blueprint-contentbeans.xml"
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  public static class LocalConfig {
    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
    }
  }

  public SitesService getSitesService() {
    return sitesService;
  }
}



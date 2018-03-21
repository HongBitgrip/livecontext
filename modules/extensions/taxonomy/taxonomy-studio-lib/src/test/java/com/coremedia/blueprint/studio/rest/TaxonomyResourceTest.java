package com.coremedia.blueprint.studio.rest;

import com.coremedia.blueprint.studio.rest.taxonomies.TaxonomyResource;
import com.coremedia.blueprint.taxonomies.Taxonomy;
import com.coremedia.blueprint.taxonomies.TaxonomyNode;
import com.coremedia.blueprint.taxonomies.TaxonomyNodeList;
import com.coremedia.blueprint.taxonomies.TaxonomyResolver;
import com.coremedia.blueprint.taxonomies.semantic.SemanticStrategy;
import com.coremedia.blueprint.taxonomies.semantic.service.SemanticServiceStrategy;
import com.coremedia.blueprint.taxonomies.strategy.TaxonomyResolverImpl;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, TaxonomyResourceTest.LocalConfig.class})
public class TaxonomyResourceTest {
  @Inject
  private TaxonomyResource taxonomyResource;
  @Inject
  private ContentRepository contentRepository;
  @Inject
  private TaxonomyResolver taxonomyResolver;

  @Test
  public void testTaxonomyResource() throws Exception {
    TaxonomyNodeList roots = taxonomyResource.getRoots(null,false);
    Assert.assertFalse(roots.getNodes().isEmpty());
    roots.sortByName();
    for (TaxonomyNode node : roots.getNodes()) {
      Assert.assertTrue(node.getName() != null);
      Assert.assertTrue(node.getRef() != null);
      Assert.assertTrue(node.getType() != null);
      Assert.assertTrue(node.isRoot());
      Assert.assertTrue(node.getPath() == null);
      Assert.assertTrue(node.getTaxonomyId() != null);

      Assert.assertTrue(taxonomyResource.getRoot(null, node.getTaxonomyId()) != null);
      Assert.assertTrue(taxonomyResource.getNode(null, node.getTaxonomyId(), node.getRef()) != null);

      TaxonomyNodeList children = taxonomyResource.getChildren(null, node.getTaxonomyId(), node.getRef(), 0, 50);
      Assert.assertTrue(children != null);
      for (TaxonomyNode child : children.getNodes()) {
        Assert.assertTrue(taxonomyResource.getPath(null, child.getTaxonomyId(), child.getRef()) != null);
      }
    }
  }

  @Test
  public void testSemanticMatching() throws Exception {
    SemanticServiceStrategy strategy = new SemanticServiceStrategy();
    strategy.setContentRepository(contentRepository);
    strategy.setServiceId("nameMatching");

    Collection<Taxonomy> taxonomies = taxonomyResolver.getTaxonomies();
    Taxonomy taxonomy = taxonomies.iterator().next();
    //mpf, not a real test since the SOLR instance is null
  }

  @Configuration
  @ImportResource(value = {"classpath:com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
  },
          reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class)
  static class LocalConfig {
    @Bean
    XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
    }

    @Bean
    TaxonomyResource taxonomyResource(TaxonomyResolver taxonomyResolver) {
      final TaxonomyResource taxonomyResource = new TaxonomyResource();
      taxonomyResource.setSemanticStrategies(new ArrayList<SemanticStrategy>());
      taxonomyResource.setStrategyResolver(taxonomyResolver);
      taxonomyResource.afterPropertiesSet();
      return taxonomyResource;
    }

    @Bean
    @Inject
    TaxonomyResolver getTaxonomyResolver(ContentRepository contentRepository, SitesService sitesService) throws Exception {
      TaxonomyResolverImpl resolver = new TaxonomyResolverImpl();
      resolver.setContentRepository(contentRepository);
      resolver.setGlobalConfigPath("/");
      resolver.setSiteConfigPath("Settings/Options/");
      resolver.setSitesService(sitesService);
      Map<String, String> aliasMapping = new HashMap<>();
      aliasMapping.put("Query", "Subject");
      aliasMapping.put("QueryLocation", "Location");
      resolver.setAliasMapping(aliasMapping);
      resolver.setContentType("CMTaxonomy");
      resolver.afterPropertiesSet();
      return resolver;
    }
  }}

package com.coremedia.blueprint.cae.layout;

import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.PageGridConstants;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.layout.PageGridRow;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.viewtype.ViewtypeService;
import com.coremedia.blueprint.viewtype.configuration.ViewtypeServiceConfiguration;
import com.coremedia.cache.EvaluationException;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PageGridImplTest.LocalConfig.class, XmlRepoConfiguration.class})
public class PageGridImplTest {

  @Inject
  private ContentBackedPageGridService contentBackedPageGridService;
  @Inject
  private ValidationService<Linkable> validationService;
  @Inject
  private ContentBeanFactory contentBeanFactory;
  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ViewtypeService viewtypeService;

  private PageGridImpl pageGrid;

  // --- setup ------------------------------------------------------

  @Before
  public void setUp() throws Exception {
    Content content = contentRepository.getContent(IdHelper.formatContentId(222));
    CMChannel channel = (CMChannel) contentBeanFactory.createBeanFor(content);
    pageGrid = new PageGridImpl(channel, contentBackedPageGridService, validationService, viewtypeService);
  }


  // --- Tests ------------------------------------------------------

  @Test
  public void testGetRows() {
    List<PageGridRow> rows = pageGrid.getRows();
    assertEquals("wrong number of rows", 3, rows.size());
    assertEquals("wrong length row 1", 2, rows.get(0).getPlacements().size());
    assertEquals("wrong west placements", "west", rows.get(0).getPlacements().get(0).getName());
    assertEquals("wrong north placements", "north", rows.get(0).getPlacements().get(1).getName());
    assertEquals("wrong length row 2", 2, rows.get(1).getPlacements().size());
    assertEquals("wrong main placements", PageGridConstants.MAIN_PLACEMENT_NAME, rows.get(1).getPlacements().get(0).getName());
    assertEquals("wrong east placements", "east", rows.get(1).getPlacements().get(1).getName());
    assertEquals("wrong length row 3", 1, rows.get(2).getPlacements().size());
    assertEquals("wrong south placements", "south", rows.get(2).getPlacements().get(0).getName());
  }

  @Test
  public void testGetNumcols() {
    assertEquals("wrong number of columns", 4, pageGrid.getNumcols());
  }

  @Test
  public void testGetMainItems() {
    List<? extends Content> mainItems = pageGrid.getMainItems();
    assertEquals("wrong number of main items", 1, mainItems.size());
    assertEquals("wrong main content", "article1", mainItems.get(0).getName());
  }

  @Test
  public void testPlacement() {
    PageGridPlacement pageGridPlacement = pageGrid.getRows().get(2).getPlacements().get(0);
    assertEquals("wrong col", 1, pageGridPlacement.getCol());
    assertEquals("wrong colspan", 2, pageGridPlacement.getColspan());
    assertEquals("wrong width", 50, pageGridPlacement.getWidth());
    assertEquals("wrong name", "south", pageGridPlacement.getName());
    assertEquals("wrong placements", 1, pageGridPlacement.getItems().size());
    assertNull("unexpected viewtype", pageGridPlacement.getViewTypeName());
  }

  @Test
  public void testPlacementByName() {
    PageGridPlacement pageGridPlacement = pageGrid.getPlacementForName("main");
    assertEquals("wrong col", 2, pageGridPlacement.getCol());
    assertEquals("wrong colspan", 1, pageGridPlacement.getColspan());
    assertEquals("wrong width", 25, pageGridPlacement.getWidth());
    assertEquals("wrong name", "main", pageGridPlacement.getName());
    assertEquals("wrong placements", 1, pageGridPlacement.getItems().size());
    assertNull("unexpected viewtype", pageGridPlacement.getViewTypeName());
  }

  @Test
  public void testPlacementByWrongName() {
    PageGridPlacement pageGridPlacement = pageGrid.getPlacementForName("wrongName");
    assertNull("placement is not null", pageGridPlacement);
  }

  @Test(expected = EvaluationException.class)
  public void testBrokenPageGrid() {
    Content content = contentRepository.getContent(IdHelper.formatContentId(668));
    CMChannel brokenChannel = (CMChannel) contentBeanFactory.createBeanFor(content);
    PageGrid brokenGrid = new PageGridImpl(brokenChannel, contentBackedPageGridService, validationService, viewtypeService);

    //Exception will be thrown when trying to access a placement.
    PageGridPlacement pageGridPlacement = brokenGrid.getPlacementForName("wrongName");
  }

  @Test
  public void testGetCssClass() {
    assertEquals("wrong cssClassName", "cm-grid--test-setting", pageGrid.getCssClassName());
  }

  //====================================================================================================================

  @Configuration
  @Import(ViewtypeServiceConfiguration.class)
  @ImportResource(value = {"classpath:/framework/spring/blueprint-contentbeans.xml", "classpath:/framework/spring/blueprint-services.xml"},
          reader = ResourceAwareXmlBeanDefinitionReader.class)
  public static class LocalConfig {
    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/blueprint/cae/layout/pagegridcontent.xml");
    }
  }

}

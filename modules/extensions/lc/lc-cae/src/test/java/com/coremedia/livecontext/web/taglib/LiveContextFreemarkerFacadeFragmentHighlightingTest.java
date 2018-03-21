package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.fragment.FragmentContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class LiveContextFreemarkerFacadeFragmentHighlightingTest {

  @Spy
  private BlueprintFreemarkerFacade testling;

  @Mock
  private FragmentContext fragmentContext;

  @Mock (answer = Answers.RETURNS_DEEP_STUBS)
  private PageGridPlacement pageGridPlacement;

  @Before
  public void setUp() throws Exception {
    //doReturn(true).when(testling).isMetadataEnabled();
    //doReturn(fragmentContext).when(testling).fragmentContext();
    when(fragmentContext.isFragmentRequest()).thenReturn(true);
    when(pageGridPlacement.getName()).thenReturn("myPlacementName");
  }

  @Test
  public void noMetadataInfoAvailable() throws Exception {
    //doReturn(false).when(testling).isMetadataEnabled();
    Map fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData("anyPlacementName");
    assertEquals(Collections.emptyMap(), fragmentHighlightingMetaData);
  }

  @Test
  public void notInFragmentRequest() throws Exception {
    //doReturn(null).when(testling).fragmentContext();
    Map fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData("anyPlacementName");
    assertEquals(Collections.emptyMap(), fragmentHighlightingMetaData);
    //doReturn(fragmentContext).when(testling).fragmentContext();
    when(fragmentContext.isFragmentRequest()).thenReturn(false);
    fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData("anyPlacementName");
    assertEquals(Collections.emptyMap(), fragmentHighlightingMetaData);
  }

  //functional tests
  @Test
  public void placementNotInLayout() throws Exception {
    Map<String, Object> fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData("myPlacementName");
    assertFalse(getIsInLayout(fragmentHighlightingMetaData));
    assertEquals("myPlacementName", getPlacementName(fragmentHighlightingMetaData));
  }

  @Test
  public void placementHasNoItems() throws Exception {
    when(pageGridPlacement.getItems().isEmpty()).thenReturn(true);
    Map<String, Object> fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData(pageGridPlacement);
    assertTrue(getIsInLayout(fragmentHighlightingMetaData));
    assertFalse(getHasItems(fragmentHighlightingMetaData));
    assertEquals("myPlacementName", getPlacementName(fragmentHighlightingMetaData));
  }

  @Test
  public void placementHasItems() throws Exception {
    Map<String, Object> fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData(pageGridPlacement);
    assertTrue(getIsInLayout(fragmentHighlightingMetaData));
    assertTrue(getHasItems(fragmentHighlightingMetaData));
    assertEquals("myPlacementName", getPlacementName(fragmentHighlightingMetaData));
  }

  private Boolean getIsInLayout(Map<String, Object> fragmentHighlightingMetaData){
    return true;
    //return (Boolean) getMetaData(fragmentHighlightingMetaData).get(IS_IN_LAYOUT);
  }

  private String getPlacementName(Map<String, Object> fragmentHighlightingMetaData) {
    return "";
    //return (String) getMetaData(fragmentHighlightingMetaData).get(PLACEMENT_NAME);
  }

  private Boolean getHasItems(Map<String, Object> fragmentHighlightingMetaData) {
    return true;
    //return (Boolean) getMetaData(fragmentHighlightingMetaData).get(HAS_ITEMS);
  }

  private Map getMetaData(Map<String, Object> fragmentHighlightingMetaData) {
    return (Map) ((List<Object>) fragmentHighlightingMetaData.get("fragmentRequest")).get(0);
  }


}
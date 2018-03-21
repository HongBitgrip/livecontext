package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.common.contentbeans.CMQueryList;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import com.coremedia.objectserver.beans.ContentBean;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CMQueryListImplTest extends ContentBeanTestBase {

  private SearchResultFactory resultFactoryEmptyResult;
  private SearchResultFactory resultFactoryNonEmptyResult;

  private CMQueryList contentBean;
  private CMQueryList extendedItemsContentBean;

  private CMTeasable dynamicContent;

  private ContentBean content2;
  private ContentBean content6;


  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    setUpPreviewDate();
    contentBean = getContentBean(108);
    resultFactoryEmptyResult = mock(SearchResultFactory.class);
    resultFactoryNonEmptyResult = mock(SearchResultFactory.class);

    SearchResultBean emptyResult = mock(SearchResultBean.class);
    when(resultFactoryEmptyResult.createSearchResultUncached(any(SearchQueryBean.class))).thenReturn(emptyResult);
    when(emptyResult.getHits()).thenReturn(emptyList());

    SearchResultBean nonEmptyResult1 = mock(SearchResultBean.class);
    when(resultFactoryNonEmptyResult.createSearchResultUncached(any(SearchQueryBean.class))).thenReturn(nonEmptyResult1);
    dynamicContent = getContentBean(10);
    List contentBeans = Collections.singletonList(dynamicContent);
    when(nonEmptyResult1.getHits()).thenReturn(contentBeans);

    extendedItemsContentBean = getContentBean(150);

    content2 = getContentBean(2);
    content6 = getContentBean(6);
  }

  @Test
  public void testGetItemsLegacy() throws Exception {
    CMQueryListImpl queryListImpl = (CMQueryListImpl) contentBean;
    queryListImpl.setResultFactory(resultFactoryEmptyResult);
    assertEquals(1, contentBean.getItems().size());
  }

  @Test
  public void testGetItemsLegacy_withSearchResult() throws Exception {
    CMQueryListImpl queryListImpl = (CMQueryListImpl) contentBean;
    queryListImpl.setResultFactory(resultFactoryNonEmptyResult);
    assertEquals(2, contentBean.getItems().size());
  }

  @Test
  public void testGetExtendedItems() throws Exception {
    CMQueryListImpl queryListImpl = (CMQueryListImpl) extendedItemsContentBean;
    queryListImpl.setResultFactory(resultFactoryEmptyResult);
    List<? extends Linkable> items = extendedItemsContentBean.getItems();
    assertEquals(2, items.size());
    assertEquals(content2, items.get(0));
    assertEquals(content6, items.get(1));
  }

  @Test
  public void testGetExtendedItems_withSearchResult() throws Exception {
    CMQueryListImpl queryListImpl = (CMQueryListImpl) extendedItemsContentBean;
    queryListImpl.setResultFactory(resultFactoryNonEmptyResult);
    List<? extends Linkable> items = extendedItemsContentBean.getItems();
    assertEquals(3, items.size());
    assertEquals(content2, items.get(0));
    assertEquals(dynamicContent, items.get(1));
    assertEquals(content6, items.get(2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedItems_withDuplicate() throws Exception {
    SearchResultFactory resultFactory = mock(SearchResultFactory.class);
    SearchResultBean nonEmptyResult2 = mock(SearchResultBean.class);
    when(resultFactory.createSearchResultUncached(any(SearchQueryBean.class))).thenReturn(nonEmptyResult2);
    CMTeasable duplicateContent = getContentBean(2);
    List duplicates = Collections.singletonList(duplicateContent);
    when(nonEmptyResult2.getHits()).thenReturn(duplicates);

    CMQueryListImpl queryListImpl = (CMQueryListImpl) extendedItemsContentBean;
    queryListImpl.setResultFactory(resultFactory);
    List<? extends Linkable> items = extendedItemsContentBean.getItems();
    assertEquals(2, items.size());
    assertEquals(content2, items.get(0));
    assertEquals(content6, items.get(1));
  }

  @Test
  public void testGetExtendedItems_withDuplicateInStruct() throws Exception {
    CMQueryList duplicateExtendedItemsContentBean = getContentBean(174);
    CMQueryListImpl queryListImpl = (CMQueryListImpl) duplicateExtendedItemsContentBean;
    queryListImpl.setResultFactory(resultFactoryEmptyResult);
    List<? extends Linkable> items = duplicateExtendedItemsContentBean.getItems();
    assertEquals(3, items.size());
    assertEquals(content2, items.get(0));
    assertEquals(content2, items.get(1));
    assertEquals(content6, items.get(2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedItems_limitMixedItems() throws Exception {
    SearchResultFactory resultFactory = mock(SearchResultFactory.class);
    SearchResultBean nonEmptyResult = mock(SearchResultBean.class);
    when(resultFactory.createSearchResultUncached(any(SearchQueryBean.class))).thenReturn(nonEmptyResult);
    CMTeasable searchResultContent1 = getContentBean(12);
    CMTeasable searchResultContent2 = getContentBean(14);
    CMTeasable searchResultContent3 = getContentBean(16);
    List contents = Arrays.asList(searchResultContent1, searchResultContent2, searchResultContent3);
    when(nonEmptyResult.getHits()).thenReturn(contents);

    CMQueryListImpl queryListImpl = (CMQueryListImpl) extendedItemsContentBean;
    queryListImpl.setResultFactory(resultFactory);

    List<? extends Linkable> items = extendedItemsContentBean.getItems();
    assertEquals(4, items.size());
    assertEquals(content2, items.get(0));
    assertEquals(searchResultContent1, items.get(1));
    assertEquals(content6, items.get(2));
    assertEquals(searchResultContent2, items.get(3));
  }
}

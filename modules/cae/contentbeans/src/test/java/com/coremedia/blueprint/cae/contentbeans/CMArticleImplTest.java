package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.contentbeans.CMVideo;
import com.coremedia.xml.MarkupUtil;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import com.coremedia.blueprint.common.services.context.CurrentContextService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CMArticleImplTest extends ContentBeanTestBase {
  private CMArticle article_en;
  private CMArticle article_de;

  @Inject
  private SettingsService settingsService;

  @Before
  public void setUp() throws Exception {
    article_en = getContentBean(2);
    article_de = getContentBean(4);
  }

  @Test
  public void testGetVariantsByLocale() {
    Map<Locale, ? extends CMArticle> result = article_de.getVariantsByLocale();
    // the article_de is filtered by validation date
    assertEquals(1, result.size());
    Assert.assertTrue(result.containsKey(new Locale("en")));
    Assert.assertFalse(result.containsKey(new Locale("de")));
    assertEquals(article_en, result.get(new Locale("en")));
    assertEquals(null, result.get(new Locale("de")));
  }

  @Test
  public void getTeaserTitle() {
    assertEquals("teaserTitle", article_en.getTeaserTitle());
  }

  @Test
  public void getEmptyTeaserTitle() {
    assertEquals("title", this.<CMTeasable>getContentBean(4).getTeaserTitle());
  }

  @Test
  public void getTeaserText() {
    assertEquals("teaserText", MarkupUtil.asPlainText(article_en.getTeaserText()).trim());
  }

  @Test
  public void getEmptyTeaserText() {
    assertEquals("detailText", MarkupUtil.asPlainText(this.<CMTeasable>getContentBean(4).getTeaserText()).trim());
  }

  @Test
  public void testIsSearchable() throws Exception {
    Assert.assertTrue(article_en.isNotSearchable());
    CMArticle isSearchable = getContentBean(4);
    Assert.assertFalse(isSearchable.isNotSearchable());
    CMArticle searchAbleNotSet = getContentBean(6);
    Assert.assertFalse(searchAbleNotSet.isNotSearchable());
  }

  @Test
  public void testSettingsMechanism() throws Exception {

    CMArticleImpl settingsTest1 = getContentBean(4);
    CMChannel context = getContentBean(14);
    Assert.assertTrue(settingsService.setting("booleanProperty", Boolean.class, settingsTest1));
    assertEquals("testString", settingsService.setting("stringProperty", String.class, settingsTest1));
    assertEquals(42, settingsService.setting("integerProperty", Integer.class, settingsTest1).intValue());
    assertEquals("2010-01-01T10:00:23-10:00", settingsService.setting("dateProperty", String.class, settingsTest1));
    List<CMTeasable> links = settingsService.settingAsList("linkProperty", CMTeasable.class, settingsTest1);
    assertEquals(6, links.get(0).getContentId());
    Assert.assertTrue(settingsService.setting("kid", Boolean.class, settingsTest1, context));
    Assert.assertTrue(settingsService.setting("father", Boolean.class, settingsTest1, context));
    Assert.assertTrue(settingsService.setting("grandfather", Boolean.class, settingsTest1, context));

    CMArticle merged = getContentBean(6);
    Assert.assertTrue(settingsService.setting("setIndirectly", Boolean.class, merged));
    Assert.assertTrue(settingsService.setting("setDirectly", Boolean.class, merged));
    Assert.assertFalse(settingsService.setting("willBeOverridden", Boolean.class, merged));
  }


  @Test
  public void testGetAspectByName() throws Exception {
    assertEquals(0, article_en.getAspectByName().size());
  }

  @Test
  public void testGetAspects() throws Exception {
    assertEquals(0, article_en.getAspects().size());
  }

  @Test
  public void testGetMaster() throws Exception {
    assertEquals(article_en, article_de.getMaster());
  }

  @Test
  public void testGetLocalizations() throws Exception {
    setUpPreviewDate();
    Collection<? extends CMArticle> localizations = article_en.getLocalizations();
    // the article_de is filtered by validation date
    assertEquals(1, localizations.size());
    Assert.assertTrue(localizations.contains(article_en));
    Assert.assertFalse(localizations.contains(article_de));
  }

  @Test
  public void testGetPictures() throws Exception {
    setUpPreviewDate();
    assertEquals(2, article_de.getPictures().size());
    assertEquals(article_de.getPicture(), article_de.getPictures().get(0));
  }

  @Test
  public void testGetRelatedByReferrers() throws Exception {
    setUpPreviewDate(2010, Calendar.FEBRUARY, 1);
    assertEquals(1, article_en.getRelatedByReferrers().size());
  }

  @Test
  public void testGetRelatedBySimilarTaxonomies() throws Exception {
    setUpPreviewDate(2010, Calendar.FEBRUARY, 1);
    CurrentContextService ccs = mockCurrentContextTo14();
    CMArticleImpl impl = (CMArticleImpl) article_de;
    impl.setCurrentContextService(ccs);
    SearchResultFactory resultFactory = Mockito.mock(SearchResultFactory.class);
    SearchResultBean searchResultBean = new SearchResultBean();
    List<CMLinkable> hits = new ArrayList<>();
    hits.add(this.<CMVideo>getContentBean(106));
    searchResultBean.setHits(hits);
    when(resultFactory.createSearchResult(Mockito.any(SearchQueryBean.class), Mockito.any(Long.class))).thenReturn(searchResultBean);
    impl.setResultFactory(resultFactory);
    assertEquals(1, article_de.getRelated().size());
    assertEquals(1, article_de.getRelatedBySimilarTaxonomies().size());
    assertEquals(2, article_de.getRelatedAll().size());
    assertEquals(2, article_de.getRelatedAllByType().size());
    assertEquals(1, article_de.getRelatedImplicitly().size());
    Map<String, List<CMTeasable>> relatedImplicitlyByType = article_de.getRelatedImplicitlyByType();
    assertEquals(1, relatedImplicitlyByType.size());
    Assert.assertNotNull(relatedImplicitlyByType.get("CMVideo"));
  }

  private CurrentContextService mockCurrentContextTo14() {
    CurrentContextService ccs = Mockito.mock(CurrentContextService.class);
    CMContext context = getContentBean(14);
    when(ccs.getContext()).thenReturn(context);
    return ccs;
  }

  @Test
  public void testGetHtmlDescription() {
    assertEquals("HtmlDescription should be empty", "", article_de.getHtmlDescription());
    assertEquals("HtmlDescription should be set", "My HTML Description", article_en.getHtmlDescription());
  }

  @Test
  public void testGetHtmlTitle() {
    assertEquals("HtmlTitle should be fallback to title", "title", article_de.getHtmlTitle());
    assertEquals("HtmlTitle should be set", "My HTML Title", article_en.getHtmlTitle());
  }

  @Test
  public void testExternallyDisplayedDate() {
    assertEquals("Date should be: 2009-06-01T20:59:42.000+01:00", 1243886382000L, article_en.getExternallyDisplayedDate().getTimeInMillis());
    assertEquals("Date should be: 2010-01-01T06:00:00+01:00", 1262322000000L, article_de.getExternallyDisplayedDate().getTimeInMillis());
  }

}

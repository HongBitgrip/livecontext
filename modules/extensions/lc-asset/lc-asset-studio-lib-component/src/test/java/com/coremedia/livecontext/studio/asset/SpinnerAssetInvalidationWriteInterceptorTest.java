package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.ecommerce.studio.rest.cache.CommerceCacheInvalidationSource;
import com.coremedia.livecontext.asset.util.AssetReadSettingsHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.livecontext.studio.asset.SpinnerAssetInvalidationWriteInterceptor.SEQUENCE_SPINNER_PROPERTY;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@RunWith(MockitoJUnitRunner.class)
public class SpinnerAssetInvalidationWriteInterceptorTest {

  @InjectMocks
  private SpinnerAssetInvalidationWriteInterceptor testling;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private AssetReadSettingsHelper assetReadSettingsHelper;

  @Mock
  private CommerceCacheInvalidationSource invalidationSource;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ContentRepository contentRepository;

  @Mock
  private Map<String, Object> oldLocalSettings;

  @Mock
  private Map<String, Object> newLocalSettings;

  @Mock
  private ContentWriteRequest contentWriteRequest;

  @Mock
  private Content content;

  @Mock
  private Struct emptyStruct;

  @Before
  public void setup() {
    when(commerceConnectionSupplier.findConnectionForContent(any(Content.class)))
            .thenReturn(Optional.of(commerceConnection));

    when(contentRepository.getConnection().getStructService().createStructBuilder().build()).thenReturn(emptyStruct);
  }

  @Test
  public void testAssignedProductsChangedNewSettingsNoCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(false);

    assertThat(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings)).isFalse();
  }

  @Test
  public void testAssignedProductsChangedOldSettingsNoCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(false);

    assertThat(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings)).isTrue();
  }

  @Test
  public void testAssignedProductsChangedNewSettingsHaveNoProducts() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(false);

    assertThat(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings)).isFalse();
  }

  @Test
  public void testAssignedProductsChangedEqualProductList() throws Exception {
    List<String> newProducts = ImmutableList.of("1", "2");
    List<String> oldProducts = ImmutableList.of("1", "2");
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newProducts);
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldProducts);

    assertThat(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings)).isFalse();
  }

  @Test
  public void testAssignedProductsChangedNotEqualProductList() throws Exception {
    List<String> newProducts = ImmutableList.of("1", "2");
    List<String> oldProducts = ImmutableList.of("1", "3");
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newProducts);
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldProducts);

    assertThat(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings)).isTrue();
  }

  @Test
  public void testSequencePropertyChanged() throws Exception {
    Map<String, Object> newProperties = new HashMap<>();
    newProperties.put(SEQUENCE_SPINNER_PROPERTY, mock(Object.class));
    when(contentWriteRequest.getProperties()).thenReturn(newProperties);

    assertThat(testling.sequencePropertyChanged(contentWriteRequest)).isTrue();
  }

  @Test
  public void testSequencePropertyNotChanged() throws Exception {
    Map<String, Object> newProperties = new HashMap<>();
    when(contentWriteRequest.getProperties()).thenReturn(newProperties);

    assertThat(testling.sequencePropertyChanged(contentWriteRequest)).isFalse();
  }

  @Test
  public void resolveAllSpinnerPicturesNoPictures() throws Exception {
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties();

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldLocalSettings, properties);

    assertThat(contents).isEmpty();
  }

  @Test
  public void resolveAllSpinnerPicturesFromNewProperties() {
    Content newPicture1 = mock(Content.class);
    Content newPicture2 = mock(Content.class);
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties(newPicture1, newPicture2);

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldLocalSettings, properties);

    assertThat(contents).hasSize(2);
    assertThat(contents).as("New Picture 1 must be part of the result but is not").contains(newPicture1);
    assertThat(contents).as("New Picture 2 must be part of the result but is not").contains(newPicture2);
  }

  @Test
  public void resolveAllSpinnerPicturesFromContent() throws Exception {
    Content oldPicture1 = mock(Content.class);
    Content oldPicture2 = mock(Content.class);
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties();
    Map<String, Object> oldProperties = allSpinnerPicturesTestNewProperties(oldPicture1, oldPicture2);

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldProperties, properties);

    assertThat(contents).hasSize(2);
    assertThat(contents).as("Old Picture 1 must be part of the result but is not").contains(oldPicture1);
    assertThat(contents).as("Old Picture 2 must be part of the result but is not").contains(oldPicture2);
  }

  @Test
  public void resolveAllSpinnerPicturesFromNewPropertiesEvenIfOldExist() throws Exception {
    Content oldPicture1 = mock(Content.class);
    Content newPicture1 = mock(Content.class);
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties(newPicture1);
    Map<String, Object> oldProperties = allSpinnerPicturesTestNewProperties(oldPicture1);

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldProperties, properties);

    assertThat(contents).hasSize(1);
    assertThat(contents).as("New Picture 1 must be part of the result but is not").contains(newPicture1);
  }

  @Test
  public void testCheckPreconditions() throws Exception {
    when(contentWriteRequest.getEntity()).thenReturn(mock(Content.class));
    when(contentWriteRequest.getProperties()).thenReturn(new HashMap<String, Object>());

    boolean actual = testling.checkPreconditions(contentWriteRequest);

    assertThat(actual).isTrue();
  }

  @Test
  public void testCheckPreconditionsNoContent() throws Exception {
    when(contentWriteRequest.getEntity()).thenReturn(null);
    when(contentWriteRequest.getProperties()).thenReturn(new HashMap<String, Object>());

    boolean actual = testling.checkPreconditions(contentWriteRequest);

    assertThat(actual).isFalse();
  }

  @Test
  public void testCheckPreconditionsNoNewProperties() throws Exception {
    when(contentWriteRequest.getEntity()).thenReturn(mock(Content.class));
    when(contentWriteRequest.getProperties()).thenReturn(null);

    boolean actual = testling.checkPreconditions(contentWriteRequest);

    assertThat(actual).isFalse();
  }

  @Test
  public void testInheritedChangedNoCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.FALSE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertThat(inheritedChanged).isFalse();
  }

  @Test
  public void testInheritedChangedNullInputs() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(null)).thenReturn(Boolean.FALSE);

    boolean inheritedChanged = testling.inheritedChanged(null, null);

    assertThat(inheritedChanged).isFalse();
  }

  @Test
  public void testInheritedChangedNoOldCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(Boolean.FALSE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertThat(inheritedChanged).isTrue();
  }

  @Test
  public void testInheritedChangedInheritStatesAreEqual() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(Boolean.TRUE);

    when(assetReadSettingsHelper.readInheritedField(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.readInheritedField(oldLocalSettings)).thenReturn(Boolean.TRUE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertThat(inheritedChanged).isFalse();
  }

  @Test
  public void testInheritedChangedInheritStatesAreNotEqual() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(Boolean.TRUE);

    when(assetReadSettingsHelper.readInheritedField(newLocalSettings)).thenReturn(Boolean.FALSE);
    when(assetReadSettingsHelper.readInheritedField(oldLocalSettings)).thenReturn(Boolean.TRUE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertThat(inheritedChanged).isTrue();
  }

  @Test
  public void testInvalidateExternalReferences() throws Exception {
    testling.invalidateExternalReferences(content, emptyMap(), emptyMap());

    verify(invalidationSource, times(1)).invalidateReferences(Collections.<String>emptySet());
  }

  @Test
  public void testInvalidateExternalReferencesNoNewSettings() throws Exception {
    List<String> oldSettingsInvalidation = ImmutableList.of("1ProductReference", "2ProductReference", "3ProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(oldLocalSettings)).thenReturn(Boolean.TRUE);

    testling.invalidateExternalReferences(content, oldLocalSettings, emptyMap());

    verify(invalidationSource, times(1)).invalidateReferences(argThat(new SetContainsMatcher(oldSettingsInvalidation)));
  }

  @Test
  public void testInvalidateExternalReferencesNoOldSettings() throws Exception {
    List<String> newSettingsInvalidation = ImmutableList.of("1ProductReference", "2ProductReference", "3ProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(Boolean.TRUE);

    testling.invalidateExternalReferences(content, emptyMap(), newLocalSettings);

    verify(invalidationSource, times(1)).invalidateReferences(argThat(new SetContainsMatcher(newSettingsInvalidation)));
  }

  @Test
  public void testInvalidateExternalReferencesBothSettings() throws Exception {
    List<String> oldSettingsInvalidation = ImmutableList.of("1OldProductReference", "2OldProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(oldLocalSettings)).thenReturn(Boolean.TRUE);

    List<String> newSettingsInvalidation = ImmutableList.of("1ProductReference", "2ProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(Boolean.TRUE);

    List<String> expected = ImmutableList.of("1OldProductReference", "2OldProductReference", "1ProductReference", "2ProductReference");

    testling.invalidateExternalReferences(content, oldLocalSettings, newLocalSettings);

    verify(invalidationSource, times(1)).invalidateReferences(argThat(new SetContainsMatcher(expected)));
  }

  @Test
  public void evaluateNewSettingsFoundationNoSettings() {
    Struct newStruct = testling.evaluateNewSettingsFoundation(oldLocalSettings, newLocalSettings);

    assertThat(newStruct).isSameAs(emptyStruct);
  }

  @Test
  public void evaluateNewSettingsSettingsFoundationFromContent() {
    Struct expectedSettings = mock(Struct.class);
    Map<String, Object> ownOldProperties = new HashMap<>();
    ownOldProperties.put("localSettings", expectedSettings);

    Struct newStruct = testling.evaluateNewSettingsFoundation(ownOldProperties, newLocalSettings);

    assertThat(newStruct).isSameAs(expectedSettings);
  }

  @Test
  public void evaluateNewSettingsSettingsFoundationFromProperties() {
    Struct expectedSettings = mock(Struct.class);
    Map<String, Object> ownNewProperties = new HashMap<>();
    ownNewProperties.put("localSettings", expectedSettings);

    Struct newStruct = testling.evaluateNewSettingsFoundation(oldLocalSettings, ownNewProperties);

    assertThat(newStruct).isSameAs(expectedSettings);
  }

  @Test
  public void evaluateNewSettingsSettingsFoundationFromPropertiesIfBothGiven() {
    Struct expectedSettings = mock(Struct.class);
    Struct notExpectedSettings = mock(Struct.class);

    Map<String, Object> ownNewProperties = new HashMap<>();
    ownNewProperties.put("localSettings", expectedSettings);

    Map<String, Object> ownOldProperties = new HashMap<>();
    ownOldProperties.put("localSettings", notExpectedSettings);

    Struct newStruct = testling.evaluateNewSettingsFoundation(ownOldProperties, ownNewProperties);

    assertThat(newStruct).isSameAs(expectedSettings);
  }

  private Map<String, Object> allSpinnerPicturesTestNewProperties(Content... pictures) {
    Map<String, Object> stringObjectHashMap = new HashMap<>();
    if (pictures != null && pictures.length > 0) {
      List<Content> newPictures = ImmutableList.copyOf(pictures);
      stringObjectHashMap.put(SEQUENCE_SPINNER_PROPERTY, newPictures);
    }
    return stringObjectHashMap;
  }
}
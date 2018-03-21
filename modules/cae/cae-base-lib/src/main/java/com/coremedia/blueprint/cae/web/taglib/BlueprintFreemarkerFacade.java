package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.base.cae.web.taglib.CssClassFor;
import com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions;
import com.coremedia.blueprint.base.cae.web.taglib.SettingsFunction;
import com.coremedia.blueprint.base.cae.web.taglib.UniqueIdGenerator;
import com.coremedia.blueprint.base.cae.web.taglib.ViewHookEventNamesFreemarker;
import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.action.webflow.BlueprintFlowUrlHandler;
import com.coremedia.blueprint.cae.web.links.ThemeResourceLinkBuilder;
import com.coremedia.blueprint.common.contentbeans.AbstractPage;
import com.coremedia.blueprint.common.contentbeans.CMCollection;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMImageMap;
import com.coremedia.blueprint.common.contentbeans.CMLocalized;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.Container;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.navigation.HasViewTypeName;
import com.coremedia.blueprint.common.util.ContainerFlattener;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.cap.transform.Transformation;
import com.coremedia.image.ImageDimensionsExtractor;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import com.coremedia.objectserver.web.taglib.MetadataTagSupport;
import com.coremedia.util.WordAbbreviator;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.MimeType;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * A Facade for utility functions used by FreeMarker templates.
 */
public class BlueprintFreemarkerFacade extends MetadataTagSupport {

  private static final Logger LOG = LoggerFactory.getLogger(BlueprintFreemarkerFacade.class);
  private static final String RESPONSIVE_SETTINGS_KEY = "responsiveImageSettings";
  private static final String FRAGMENT_PREVIEW_KEY = "fragmentPreview";

  public static final String DEFAULT_DIRECTION = "ltr";
  public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
  /**
   * @deprecated Use {@link #DEFAULT_LOCALE} and {@code getLanguage()} instead.
   */
  @SuppressWarnings("unused")
  @Deprecated
  public static final String DEFAULT_LANGUAGE = DEFAULT_LOCALE.getLanguage();

  private static final int DISPLAYABLE_IMAGE_MAX_SIZE = 10485760; // 10 MB
  private static final int DISPLAYABLE_VIDEO_MAX_SIZE = 1073741824; // 1 GB
  private static final String DISPLAYABLE_IMAGE_PRIMARY_MIMETYPE = "image";
  private static final String DISPLAYABLE_VIDEO_PRIMARY_MIMETYPE = "video";
  private static final List<String> DISPLAYABLE_IMAGE_SUB_MIMETYPES = Lists.newArrayList(
          "jpg",
          "jpeg",
          "png"
  );

  static final String HAS_ITEMS = "hasItems";
  static final String PLACEMENT_NAME = "placementName";
  private static final String STORE_REF = "storeRef";
  static final String IS_IN_LAYOUT = "isInLayout";

  private ContentBeanFactory contentBeanFactory;
  private DataViewFactory dataViewFactory;
  private SettingsService settingsService;
  private ImageDimensionsExtractor imageDimensionsExtractor;
  private ThemeResourceLinkBuilder themeResourceLinkBuilder;
  private TransformImageService transformImageService;
  private WordAbbreviator abbreviator;
  private MimeTypeService mimeTypeService;

  private final ViewHookEventNamesFreemarker viewHookEventNames = new ViewHookEventNamesFreemarker();

  // --- spring config -------------------------------------------------------------------------------------------------

  @Autowired
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Autowired
  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  @Autowired
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Autowired
  public void setImageDimensionsExtractor(ImageDimensionsExtractor imageDimensionsExtractor) {
    this.imageDimensionsExtractor = imageDimensionsExtractor;
  }

  @Autowired
  public void setStringAbbreviator(WordAbbreviator abbreviator) {
    this.abbreviator = abbreviator;
  }

  @Autowired
  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  @Autowired
  public void setTransformImageService(TransformImageService transformImageService) {
    this.transformImageService = transformImageService;
  }

  @Autowired
  public void setThemeResourceLinkBuilder(ThemeResourceLinkBuilder themeResourceLinkBuilder) {
    this.themeResourceLinkBuilder = themeResourceLinkBuilder;
  }

  // --- functionality -------------------------------------------------------------------------------------------------

  public ContentBean createBeanFor(Content content) {
    return dataViewFactory.loadCached(contentBeanFactory.createBeanFor(content), null);
  }

  public List<Transformation> getTransformations(Content content) {
    return transformImageService.getTransformations(content);
  }

  public List createBeansFor(List<Content> contents) {
    return dataViewFactory.loadAllCached(contentBeanFactory.createBeansFor(contents), null);
  }

  public Object setting(Object self, String key) {
    return setting(self, key, null);
  }

  public Object setting(Object self, String key, Object defaultValue) {
    return SettingsFunction.setting(settingsService, self, key, defaultValue);
  }

  public Boolean isActiveNavigation(Object navigation, List<Object> navigationPathList) {
    return navigationPathList.contains(navigation);
  }

  public String generateId(String prefix) {
    return UniqueIdGenerator.generateId(prefix, FreemarkerUtils.getCurrentRequest());
  }

  public String cssClassFor(Boolean itemHasNext, Integer index, Boolean createCssClassAttribute) {
    return com.coremedia.blueprint.base.cae.web.taglib.CssClassFor.cssClassFor(itemHasNext, index,
            createCssClassAttribute);
  }

  public String cssClassForFirstLast(Boolean itemHasNext, Integer index, Boolean createCssClassAttribute) {
    return com.coremedia.blueprint.base.cae.web.taglib.CssClassFor.cssClassForFirstLast(itemHasNext, index,
            createCssClassAttribute);
  }

  public String cssClassForOddEven(Boolean itemHasNext, Integer index, Boolean createCssClassAttribute) {
    return com.coremedia.blueprint.base.cae.web.taglib.CssClassFor.cssClassForOddEven(itemHasNext, index,
            createCssClassAttribute);
  }

  public String cssClassAppendNavigationActive(String currentCssClass, String appendix, Object navigation, List<Object> navigationPathList) {
    return CssClassFor.cssClassAppendNavigationActive(currentCssClass, appendix, navigation, navigationPathList);
  }

  public String getStackTraceAsString(Exception e) {
    //print stackTrace the Java way here so that we automatically get all causes, messages etc.
    StringWriter stringWriter = new StringWriter(); //NOSONAR
    e.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

  /**
   * Returns a String representation of an array of JSON objects with a list of aspect ratios with image links for different sizes.
   *
   * @param picture      the image
   * @param page         the root page
   * @param aspectRatios list of aspect ratios to use for this image
   * @return Json Object with a list of aspect ratios with image links for different sizes
   * @throws IOException
   */
  public List<TransformationLinks> responsiveImageLinksData(CMPicture picture, Page page, List<String> aspectRatios) throws IOException {
    if (picture == null) {
      throw new IllegalArgumentException("Error creating responsive image links: picture must not be null");
    }

    // get responsive image settings
    Map<String, Map> responsiveImageSettings = getResponsiveImageSettings(page);
    List<String> aspectRatiosToUse = aspectRatios;
    // use list of given aspect ratios if set, otherwise use all
    if (isEmpty(aspectRatiosToUse)) {
      aspectRatiosToUse = new ArrayList<>(responsiveImageSettings.keySet());
    }

    HttpServletRequest currentRequest = FreemarkerUtils.getCurrentRequest();
    HttpServletResponse currentResponse = FreemarkerUtils.getCurrentResponse();
    List<TransformationLinks> result = new ArrayList<>();
    for (String aspectRatioName : aspectRatiosToUse) {
      @SuppressWarnings("unchecked")
      Map<String, Map> aspectRatioSizes = responsiveImageSettings.get(aspectRatioName);
      if (aspectRatioSizes != null) {
        Blob blob = picture.getTransformedData(aspectRatioName);
        Map<Integer, String> links = ImageFunctions.getImageLinksForAspectRatios(blob, aspectRatioName, aspectRatioSizes, currentRequest, currentResponse);

        if (!isEmpty(links)) {
          // only the "TransformImageService" holds the actual crop ratio in proper values
          Transformation transformation = transformImageService.getTransformation(picture.getContent(), aspectRatioName);
          if (transformation == null) {
            throw new IllegalArgumentException("Could not find image variant for name " + aspectRatioName);
          }
          TransformationLinks transformationLinks = new TransformationLinks(
                  aspectRatioName, transformation.getWidthRatio(), transformation.getHeightRatio(), links
          );
          result.add(transformationLinks);

        } else {
          LOG.info("No responsive image links found for CMPicture {} with transformationName {}", picture, aspectRatioName);
        }
      }

    }

    if (isEmpty(result)) {
      LOG.warn("No responsive image links found for CMPicture {}", picture);
    }

    return result;
  }


  public String getLinkForBiggestImageWithRatio(CMPicture picture, Page page, String aspectRatio) {
    if (picture == null) {
      throw new IllegalArgumentException("Error creating responsive image links: picture must not be null");
    }
    // get responsive image settings
    Map<String, Map> responsiveImageSettings = getResponsiveImageSettings(page);
    // use list of given aspect ratios if set, otherwise use all
    if (aspectRatio == null) {
      throw new IllegalArgumentException("Error creating responsive image links: aspect ratio must not be null");
    }
    @SuppressWarnings("unchecked")
    Map<String, ?> aspectRatioSizes = responsiveImageSettings.get(aspectRatio);
    if (aspectRatioSizes == null || aspectRatioSizes.isEmpty()) {
      throw new IllegalArgumentException(String.format("Error creating responsive image links: aspect ratio '%s' not defined", aspectRatio));
    }
    String link = null;
    @SuppressWarnings("unchecked")
    Map<String, ?> biggestSize = ImageFunctions.getBiggestSize((Map<String, Map<String, Object>>) aspectRatioSizes);
    if (biggestSize != null) {
      link = ImageFunctions.getImageLinkForAspectRatio(picture.getTransformedData(aspectRatio),
              aspectRatio, biggestSize, FreemarkerUtils.getCurrentRequest(), FreemarkerUtils.getCurrentResponse());
    }
    return link;
  }

  private Map<String, Map> getResponsiveImageSettings(Page page) {
    if (page == null) {
      throw new IllegalArgumentException("Error creating responsive image links: page must not be null");
    }
    // get responsive image settings
    Map<String, Map> responsiveImageSettings = settingsService.settingAsMap(RESPONSIVE_SETTINGS_KEY, String.class, Map.class, page);
    if (isEmpty(responsiveImageSettings)) {
      throw new IllegalArgumentException("Error creating responsive image links: No responsive image settings found");
    }
    return responsiveImageSettings;
  }

  /**
   * Retrieves the preview views of an object based on its hierachry
   *
   * @param self                 the object to preview
   * @param page                 the page used to find the setting named "fragmentPreview
   * @param defaultFragmentViews a Map defining defaults
   * @return a List of maps of Strings defining which views should be rendered
   */
  public List<Map<String, Object>> getPreviewViews(ContentBean self, Page page, List<Map<String, Object>> defaultFragmentViews) {
    ContentType contentType = self.getContent().getType();
    List<Map<String, Object>> result = defaultFragmentViews;
    while (contentType != null) {
      List<Map<String, Object>> retrievedSettings = settingsService.nestedSetting(Arrays.asList(FRAGMENT_PREVIEW_KEY, contentType.toString()), List.class, page);
      if (retrievedSettings != null) {
        result = retrievedSettings;
        break;
      }
      contentType = contentType.getParent();
    }
    return result;
  }

  /**
   * Returns a {@link java.lang.String} URL of the uncropped image
   *
   * @param picture the image for which an URL should be determined
   * @return a {@link java.lang.String} URL of the uncropped image
   */
  public String uncroppedImageLink(CMPicture picture) {
    if (picture == null) {
      throw new IllegalArgumentException("Error creating image link: picture must not be null");
    }

    Blob blob = picture.getData();
    return ImageFunctions.uncroppedImageLink(blob, FreemarkerUtils.getCurrentRequest(),
            FreemarkerUtils.getCurrentResponse());
  }

  public CMContext getPageContext(Page page) throws IOException {
    return page.getContext();
  }

  public String getPlacementPropertyName(PageGridPlacement placement) {
    return placement != null ? placement.getPropertyName() : "";
  }

  /**
   * @param container The container the metadata should be determined for
   * @return The metadata that was determined either as list or as plain object
   */
  public Object getContainerMetadata(Container container) {
    if (container instanceof ContainerWithViewTypeName) {
      return getContainerMetadata(((ContainerWithViewTypeName) container).getBaseContainer());
    }
    if (container instanceof CMCollection) {
      return of(((CMCollection) container).getContent(), "properties.items");
    }
    if (container instanceof PageGridPlacement) {
      return getPlacementPropertyName((PageGridPlacement) container);
    }
    return Collections.emptyList();
  }

  /**
   * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
   * the items the original container had.
   *
   * @param items The items to be put inside the new container
   * @return a new container
   */
  public Container getContainer(final List<Object> items) {
    return new Container() {
      @Override
      public List getItems() {
        return items;
      }

      @Override
      public List getFlattenedItems() {
        return ContainerFlattener.flatten(this, Object.class);
      }
    };
  }

  /**
   * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
   * the items the original container had.
   *
   * @param baseContainer The base container the new container shall be created from
   * @param items         The items to be put inside the new container
   * @return a new container based on the given base container
   */
  public Container getContainer(Container baseContainer, List<Object> items) {
    return new ContainerWithViewTypeName(baseContainer, items);
  }

  public boolean isWebflowRequest() {
    HttpServletRequest currentRequest = FreemarkerUtils.getCurrentRequest();
    return currentRequest.getRequestURL().toString().contains(UriConstants.Segments.PREFIX_DYNAMIC)
            && currentRequest.getParameterMap().containsKey(BlueprintFlowUrlHandler.FLOW_EXECUTION_KEY_PARAMETER);
  }

  /**
   * @param size in bytes
   * @return a human readable size
   */
  public String getDisplaySize(int size) {
    int unit = 1024;
    if (size < unit) {
      return size + " Bytes";
    }
    int exp = (int) (Math.log(size) / Math.log(unit));
    char pre = "KMGTPE".charAt(exp - 1);
    return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
  }

  public String getDisplayFileFormat(String mimeType) {
    return mimeTypeService.getExtensionForMimeType(mimeType);
  }

  public boolean isDisplayableImage(Blob blob) {
    if (blob.getSize() > DISPLAYABLE_IMAGE_MAX_SIZE) {
      return false;
    }
    MimeType mimeType = blob.getContentType();
    if (!DISPLAYABLE_IMAGE_PRIMARY_MIMETYPE.equals(mimeType.getPrimaryType())) {
      return false;
    }
    return DISPLAYABLE_IMAGE_SUB_MIMETYPES.contains(mimeType.getSubType());
  }

  public boolean isDisplayableVideo(Blob blob) {
    if (blob.getSize() > DISPLAYABLE_VIDEO_MAX_SIZE) {
      return false;
    }
    MimeType mimeType = blob.getContentType();
    return DISPLAYABLE_VIDEO_PRIMARY_MIMETYPE.equals(mimeType.getPrimaryType());
  }

  public List<Map<String, Object>> responsiveImageMapAreas(CMImageMap imageMap, List<String> transformationNames) {

    List<Map<String, Object>> result = Collections.emptyList();
    final CMPicture picture = imageMap.getPicture();

    if (picture != null) {
      // determine which transformations to apply
      final Map<String, String> transformMap = picture.getTransformMap();
      final List<Map<String, Object>> imageMapAreas = imageMap.getImageMapAreas();

      result = ImageFunctions.responsiveImageMapAreas(picture.getData(), picture.getDisableCropping(), imageMapAreas, transformMap, imageDimensionsExtractor, transformationNames);
    }

    return result;
  }

  public Map<String, Object> responsiveImageMapAreaData(Map<String, Object> coords) {
    return ImageFunctions.responsiveImageMapAreaData(coords);
  }

  public int getImageTransformationBaseWidth() {
    return ImageFunctions.getImageTransformationBaseWidth();
  }

  public ViewHookEventNamesFreemarker getViewHookEventNames() {
    return viewHookEventNames;
  }

  /**
   * @return given String truncated to given length, based on words.
   */
  public String truncateText(Object text, int maxLength) {

    String toTruncate = "";

    if (text != null) {
      if (text instanceof Markup) {
        toTruncate = MarkupUtil.asPlainText((Markup) text, true);
      } else if (text instanceof String) {
        toTruncate = (String) text;
      } else {
        // should not happen
        LOG.error("Could not abbreviate text since it's type was not supported: {} instead of Markup or String. Input was: {}" + text.getClass().getName(), text);
        throw new UnsupportedOperationException("Cannot abbreviate value " + text + " of Type" + text.getClass().getName());
      }
    }

    return abbreviator.abbreviateString(toTruncate, maxLength);
  }

  /**
   * Calls truncate text with given parameters and closes the last bold tag at the end.
   * This method is kind of stupid: It just adds the bold tag at the end and does not know
   * about where to add it. It's hard to determine where the bold end tag should be added (after which word?
   * truncate text adds three dots...) but it's easy to say that after the truncated text no bold tag should be open.
   *
   * @param text      the highlighted text
   * @param maxLength the length the text will be truncated to
   * @return truncated text with closed bold tag
   */
  public String truncateHighlightedText(Object text, int maxLength) {
    String startTag = "<b>";
    String endTag = "</b>";
    return truncateHighlightedText(text, maxLength, startTag, endTag);
  }

  /**
   * More generic version of {@link #truncateHighlightedText(Object, int)}.
   * Instead of closing a bold tag this method gets start and end tag as input and closes the given start tag with the
   * given end tag. The tags must be
   *
   * @param text      the highlighted text
   * @param maxLength the length the text will be truncated to
   * @param startTag  start tag which should be closed if it's not closed at the end
   * @param endTag    end tag - used to close the start tag if it's not closed at the end
   * @return truncated text with closed bold tag
   */
  public String truncateHighlightedText(Object text, int maxLength, String startTag, String endTag) {
    String truncatedText = truncateText(text, maxLength);

    //if the tag is smaller, everything is fine if none of those tags exist -1 < -1 is false, but we shouldn't add an end tag
    boolean lastStartTagHasBeenClosed = truncatedText.lastIndexOf(startTag) <= truncatedText.lastIndexOf(endTag);

    if (lastStartTagHasBeenClosed) {
      return truncatedText;
    }

    return truncatedText + endTag;
  }

  public PageGridPlacement getPlacementByName(String name, PageGrid pageGrid) {
    return pageGrid.getPlacementForName(name);
  }

  /**
   * Retrieves the URL path that belongs to a theme resource (image, webfont, etc.) defined by its path within the
   * theme folder. The path must not contain any <strong>..</strong>
   * descending path segments.
   *
   * @param pathToResource path to the resource within the theme folder
   * @return the URL path that belongs to a theme resource or an empty link
   */
  public String getLinkToThemeResource(String pathToResource) {
    return themeResourceLinkBuilder.getLinkToThemeResource(pathToResource, FreemarkerUtils.getCurrentRequest(), FreemarkerUtils.getCurrentResponse());
  }

  /**
   * <p>
   * Returns the ISO 639 language code for the given object.
   * </p>
   * <dl>
   * <dt><strong>Note:</strong></dt>
   * <dd>
   * ISO 639 is not a stable standard and most likely not the code you want to use in your HTML code as
   * W3C specifies to use IETF BCP 47 language tags. To retrieve a IETF BCP 47 language tag use
   * {@link #getLanguageTag(Object)}.
   * </dd>
   * </dl>
   *
   * @param object object to determine the locale from
   * @return ISO 639 language code
   * @see #getLanguageTag(Object)
   */
  public static String getLanguage(Object object) {
    return getLocale(object).getLanguage();
  }

  /**
   * <p>
   * Returns the IETF BCP 47 language code for the given object. IETF BCP 47 is the specified standard for
   * the {@code lang} attribute of HTML elements.
   * </p>
   *
   * @param object object to determine the locale from
   * @return IETF BCP 47 language code
   * @see <a href="https://www.w3.org/International/questions/qa-html-language-declarations" target="_blank">w3.org: Declaring language in HTML</a>
   * @see #getLanguage(Object)
   */
  public static String getLanguageTag(Object object) {
    return getLocale(object).toLanguageTag();
  }

  @Nonnull
  private static Locale getLocale(Object object) {
    Locale locale = DEFAULT_LOCALE;
    if (object instanceof AbstractPage) {
      AbstractPage abstractPage = (AbstractPage) object;
      locale = abstractPage.getLocale();
    }
    if (object instanceof CMLocalized) {
      CMLocalized localized = (CMLocalized) object;
      locale = localized.getLocale();
    }
    return locale;
  }

  /**
   * @param object object to determine the locale direction from
   * @return 'ltr' or 'rtl'
   * @see <a href="https://www.w3.org/International/questions/qa-html-dir#documentlevel" target="_blank">w3.org: Structural markup and right-to-left text in HTML</a>
   */
  public static String getDirection(Object object) {
    if (object instanceof AbstractPage) {
      AbstractPage abstractPage = (AbstractPage) object;
      return abstractPage.getDirection();
    }
    return DEFAULT_DIRECTION;
  }

  /**
   * This method returns a {@link Map} which contains information about the state of the placement.<br>
   * The map contains the following keys: {@link #PLACEMENT_NAME}, {@link #IS_IN_LAYOUT} and {@link #HAS_ITEMS}.<br>
   * The values of those keys are of type boolean.
   *
   * @param placementObject a PageGridPlacement
   * @return a map containing informations for placement highlighting
   * @throws IOException
   */
  @Nonnull
  public Map<String, Object> getPlacementHighlightingMetaData(@Nonnull Object placementObject) throws IOException {
    if (placementObject instanceof ContainerWithViewTypeName) {
      return getPlacementHighlightingMetaData(((ContainerWithViewTypeName) placementObject).getBaseContainer());
    }
    if (placementObject instanceof CMCollection) {
      return Collections.emptyMap();
    }

    PageGridPlacement placement = asPageGridPlacement(placementObject);
    String placementName = placement != null ? placement.getName() : asPageGridPlacementName(placementObject);

    if (placementName == null || !isMetadataEnabled()) {
      return Collections.emptyMap();
    }

    return getPlacementHighlightingMetaDataInternal(placement, placementName);
  }

  private Map<String, Object> getPlacementHighlightingMetaDataInternal(PageGridPlacement placement, String placementName) throws IOException {
    boolean isInLayout = placement != null;
    boolean hasItems = hasItems(placement, isInLayout);

    List<Object> metaDataList = new LinkedList<>();
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    builder.put(IS_IN_LAYOUT, isInLayout);
    builder.put(HAS_ITEMS, hasItems);
    builder.put(PLACEMENT_NAME, placementName);
    metaDataList.add(builder.build());

    return Collections.<String, Object>singletonMap("placementRequest", metaDataList);
  }

  private static PageGridPlacement asPageGridPlacement(Object object) {
    if (object instanceof PageGridPlacement) {
      return (PageGridPlacement) object;
    }
    return null;
  }

  private static String asPageGridPlacementName(Object object) {
    if (object instanceof String) {
      return (String) object;
    }
    return null;
  }

  private static boolean hasItems(PageGridPlacement placement, boolean isInLayout) {
    return isInLayout && !placement.getItems().isEmpty();
  }

  //====================================================================================================================

  private static class RelatedByTypePredicate implements Predicate<String> {

    private List<String> types;

    public RelatedByTypePredicate(List<String> types) {
      this.types = types;
    }

    @Override
    public boolean apply(String type) {
      return types.contains(type);
    }
  }

  /**
   * Represents custom container having elements and a viewtype name based on the given base container.
   */
  private class ContainerWithViewTypeName implements Container<Object>, HasViewTypeName {

    private Container baseContainer;
    private List<Object> items;

    public ContainerWithViewTypeName(Container baseContainer, List<Object> items) {
      this.baseContainer = baseContainer;
      this.items = items;
    }

    public Container getBaseContainer() {
      return baseContainer;
    }

    @Override
    public String getViewTypeName() {
      if (baseContainer instanceof HasViewTypeName) {
        return ((HasViewTypeName) baseContainer).getViewTypeName();
      }
      return null;
    }

    @Override
    public List<Object> getItems() {
      return items;
    }

    @Override
    public List<Object> getFlattenedItems() {
      return ContainerFlattener.flatten(this, Object.class);
    }
  }

  /**
   * Combines the information about the transformation with the given "name" in a simple bean that can be easily
   * serialized into a JSON object. This resulting JSON object is processed by the JavaScript that handles the
   * responsive image handling (see "jquery.coremedia.responsiveimages.js").
   * Consequently every change to this data structure is likely to require changes to the JavaScript.
   */
  private static final class TransformationLinks {
    private String name = StringUtils.EMPTY;
    private Integer ratioWidth = 1;
    private Integer ratioHeight = 1;
    private Map<Integer, String> linksForWidth = Collections.emptyMap();

    public TransformationLinks(String name, Integer ratioWidth, Integer ratioHeight, Map<Integer, String> linksForWidth) {
      this.name = name;
      this.ratioWidth = ratioWidth;
      this.ratioHeight = ratioHeight;
      this.linksForWidth = linksForWidth;
    }

    public String getName() {
      return name;
    }

    public Integer getRatioWidth() {
      return ratioWidth;
    }

    public Integer getRatioHeight() {
      return ratioHeight;
    }

    public Map<Integer, String> getLinksForWidth() {
      return linksForWidth;
    }

  }
}

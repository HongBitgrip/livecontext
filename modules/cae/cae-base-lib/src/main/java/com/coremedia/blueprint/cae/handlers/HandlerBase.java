package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanIdConverter;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import javax.activation.MimeType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Base implementation for resources that are represented by request handler and link schemes
 */
public abstract class HandlerBase {

  private static final Splitter PATH_SPLITTER = Splitter.on('/').omitEmptyStrings();
  private static final Joiner PATH_JOINER = Joiner.on('/');

  protected static final Logger LOG = LoggerFactory.getLogger(HandlerBase.class);
  protected ContentLinkBuilder contentLinkBuilder;
  protected UrlPathFormattingHelper urlPathFormattingHelper;

  private MimeTypeService mimeTypeService;
  private DataViewFactory dataViewFactory;
  private ContentBeanIdConverter contentBeanIdConverter = new ContentBeanIdConverter();
  private List<String> permittedLinkParameterNames = Collections.emptyList();

  // --- Spring Config -------------------------------------------------------------------------------------------------

  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  /**
   * @param permittedLinkParameterNames Names of all
   *                                    {@link com.coremedia.objectserver.view.ViewUtils#getParameters(javax.servlet.ServletRequest) link parameters} to shall be copied
   *                                    to the link
   */
  public void setPermittedLinkParameterNames(List<String> permittedLinkParameterNames) {
    this.permittedLinkParameterNames = permittedLinkParameterNames;
  }

  public void setContentBeanIdConverter(ContentBeanIdConverter contentBeanIdConverter) {
    this.contentBeanIdConverter = contentBeanIdConverter;
  }

//  @Required
  public void setContentLinkBuilder(ContentLinkBuilder contentLinkBuilder) {
    this.contentLinkBuilder = contentLinkBuilder;
  }

//  @Required
  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }

  // ===================================================================================================================


  /**
   * Provides a file extension (e.g. "jpg" or "html") for a given content type
   *
   * @param contentType The content type, e.g. "text/html"
   * @param fallback    The fallback (if no extension could be determined)
   * @return The extension
   */
  protected String getExtension(String contentType, String fallback) {
    String extension = mimeTypeService.getExtensionForMimeType(contentType);
    return StringUtils.isNotBlank(extension) ? extension : fallback;
  }

  /**
   * Provides a ContentBean's numeric ID
   *
   * @return The id as a string
   */
  protected String getId(ContentBean bean) {
    return contentBeanIdConverter.convert(bean);
  }

  protected String getId(Content content) {
    return String.valueOf(IdHelper.parseContentId(content.getId()));
  }

  protected String getExtension(MimeType contentType, String fallback) {
    return getExtension(contentType.toString(), fallback);
  }


  protected MimeTypeService getMimeTypeService() {
    return mimeTypeService;
  }

  protected DataViewFactory getDataViewFactory() {
    return dataViewFactory;
  }

  protected Logger getLogger() {
    return LOG;
  }

  /**
   * Adds all permitted
   * {@link com.coremedia.objectserver.view.ViewUtils#getParameters(javax.servlet.ServletRequest) cm parameters} to
   * the URI under construction.
   *
   * @param parameters The link parameters that have been obtained via {@link com.coremedia.objectserver.view.ViewUtils#getParameters}
   */
  protected UriComponentsBuilder addLinkParametersAsQueryParameters(UriComponentsBuilder source, Map<String, Object> parameters) {

    if (permittedLinkParameterNames.isEmpty()) {
      return source;
    }

    for (Map.Entry<String, Object> parameter : parameters.entrySet()) {

      String name = parameter.getKey();
      if (permittedLinkParameterNames.contains(name)) {

        String value = parameter.getValue() == null ? "" : parameter.getValue().toString();
        source.queryParam(name, value);
      }
    }

    return source;

  }

  protected String removeSpecialCharacters(String segment) {
    return urlPathFormattingHelper==null ? segment : urlPathFormattingHelper.tidyUrlPath(segment);
  }

  public List<String> splitPathInfo(String path) {
    return newArrayList(PATH_SPLITTER.split(path));
  }

  public String joinPath(List<String> nodes) {
    return PATH_JOINER.join(nodes);
  }

  @Override
  public String toString() {
    return "HandlerBase{" +
            "contentLinkBuilder=" + contentLinkBuilder +
            ", urlPathFormattingHelper=" + urlPathFormattingHelper +
            ", mimeTypeService=" + mimeTypeService +
            ", dataViewFactory=" + dataViewFactory +
            ", contentBeanIdConverter=" + contentBeanIdConverter +
            ", permittedLinkParameterNames=" + permittedLinkParameterNames +
            '}';
  }
}

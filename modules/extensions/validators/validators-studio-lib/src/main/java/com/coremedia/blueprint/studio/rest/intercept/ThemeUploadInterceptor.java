package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.blueprint.themeimporter.ThemeFileUtil;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.themeimporter.ThemeImporter;
import com.coremedia.cap.themeimporter.ThemeImporterResult;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.InterceptorControlAttributes;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * If the file to be uploaded is a theme, process it with the theme importer
 * instead of simply creating a CMDownload document.
 */
public class ThemeUploadInterceptor extends ContentWriteInterceptorBase {
  private String dataProperty;
  private ThemeImporter themeImporter;


  // --- construct and configure ------------------------------------

  @Required
  public void setDataProperty(String dataProperty) {
    this.dataProperty = dataProperty;
  }

  @Required
  public void setThemeImporter(ThemeImporter themeImporter) {
    this.themeImporter = themeImporter;
  }


  // --- ContentWriteInterceptor ------------------------------------

  /**
   * Checks if the uploaded file is a theme and if so, invokes the
   * theme importer
   */
  @Override
  public void intercept(ContentWriteRequest request) {
    Blob themeBlob = fetchThemeBlob(request);
    if (themeBlob!=null) {
      try (InputStream is = themeBlob.getInputStream()) {
        Content targetFolder = request.getParent();
        Content homeFolder = targetFolder.getRepository().getConnection().getSession().getUser().getHomeFolder();
        boolean isProductionTheme = homeFolder == null || !targetFolder.isChildOf(homeFolder);

        // Usecase/Assumption/Motivation:
        // A production theme is checked in, versioned and ready for publication.
        // A developer theme remains checked out for subsequent changes.
        boolean checkInAfterImport = isProductionTheme;
        // cleanBeforeImport is true for development themes, since we do not
        // expect frontend developers to use Studio for uploading partial themes.
        // We may be wrong here though, and change this again in a later version.
        // cleanBeforeImport is false for production themes, because an existing
        // production theme is linked and published, so that deletion would not
        // work without further ado.
        boolean cleanBeforeImport = !isProductionTheme;

        ThemeImporterResult themeImporterResult =
                themeImporter.importThemes(request.getParent().getPath(), Collections.singletonList(is), checkInAfterImport, cleanBeforeImport);
        request.setAttribute(InterceptorControlAttributes.DO_NOTHING, true);
        Set<Content> themeDescriptors = themeImporterResult.getThemeDescriptors();
        request.setAttribute(InterceptorControlAttributes.UPLOADED_DOCUMENTS, themeDescriptors);
      } catch (IOException e) {
        throw new RuntimeException("Error closing blob input stream", e);
      }
    }
  }


  // --- internal ---------------------------------------------------

  /**
   * Extracts the theme blob from the request
   *
   * @return the theme blob, or null if there is no theme blob.
   */
  private Blob fetchThemeBlob(ContentWriteRequest request) {
    Object value = request.getProperties().get(dataProperty);
    if (value instanceof Blob) {
      Blob blob = (Blob) value;
      try (InputStream inputStream = blob.getInputStream()) {
        return ThemeFileUtil.isZip(blob.getContentType()) && ThemeFileUtil.isTheme(inputStream) ? blob : null;
      } catch (IOException e) {
        throw new IllegalArgumentException("Error reading input stream", e);
      }
    }
    return null;
  }

}

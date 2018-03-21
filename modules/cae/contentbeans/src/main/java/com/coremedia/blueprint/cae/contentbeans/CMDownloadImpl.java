package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.base.settings.SettingsService;

/**
 * Generated extension class for immutable beans of document type "CMDownload".
 */
public class CMDownloadImpl extends CMDownloadBase {

  public static final String SETTING_USE_CM_DOWNLOAD_FILENAME = "useCMDownloadFilename";
  public static final boolean DEFAULT_USE_CM_DOWNLOAD_FILENAME = false;

  @Override
  public String getFilename() {
    String filename = super.getFilename();

    SettingsService settingsService = getSettingsService();
    Boolean useCMDownloadFilename = settingsService.settingWithDefault(
            SETTING_USE_CM_DOWNLOAD_FILENAME,
            Boolean.class, DEFAULT_USE_CM_DOWNLOAD_FILENAME,
            this, getCurrentContextService().getContext());

    return useCMDownloadFilename != null && useCMDownloadFilename
            ? filename
            : null;
  }
}

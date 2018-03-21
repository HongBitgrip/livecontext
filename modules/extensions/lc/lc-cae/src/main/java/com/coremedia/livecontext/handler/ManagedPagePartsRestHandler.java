package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Nonnull;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * <p>
 * Rest Handler to resolve Settings from the Content Managed System.<br>
 * This Rest Handler provides the information if the CMS can provide some parts of a page for an augmentation scenario
 * in LiveContext.
 * </p>
 */
@Controller
public class ManagedPagePartsRestHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ManagedPagePartsRestHandler.class);

  /**
   * settings name in the CMS which indicates if the navigation can be provided.
   */
  static final String MANAGED_NAVIGATION_KEY = "livecontext.manageNavigation";

  /**
   * settings name in the CMS which indicates if the header can be provided.
   */
  static final String MANAGED_HEADER_KEY = "livecontext.manageHeader";

  /**
   * settings name in the CMS which indicates if the footer can be provided.
   */
  static final String MANAGED_FOOTER_KEY = "livecontext.manageFooter";

  /**
   * settings name in the CMS which indicates if the footer navigation can be provided.
   */
  static final String MANAGED_FOOTER_NAVIGATION_KEY = "livecontext.manageFooterNavigation";

  private static final String PATH = "service/lcsettings/{storeId}/{locale}/managedPageParts";
  private static final boolean DEFAULT_VALUE = Boolean.FALSE;

  private LiveContextSiteResolver siteResolver;
  private SettingsService settingsService;

  public ManagedPagePartsRestHandler(@Nonnull LiveContextSiteResolver siteResolver,
                                     @Nonnull SettingsService settingsService) {
    requireNonNull(siteResolver);
    requireNonNull(settingsService);

    this.siteResolver = siteResolver;
    this.settingsService = settingsService;
  }

  @GetMapping(value = PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ManagedPagePartsSettings> managedPagePartsHandler(@PathVariable("storeId") String storeId,
                                                                          @PathVariable("locale") Locale locale) {
    requireNonNull(storeId);
    requireNonNull(locale);

    Site site = siteResolver.findSiteFor(storeId, locale);

    if (site == null) {
      LOG.info("No Site found for storeId \"{0}\" and locale \"{1}\"", storeId, locale);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Boolean managedNavigation = settingsService.setting(MANAGED_NAVIGATION_KEY,
                                                        Boolean.class,
                                                        site.getSiteRootDocument());
    Boolean managedHeader = settingsService.setting(MANAGED_HEADER_KEY,
                                                    Boolean.class,
                                                    site.getSiteRootDocument());
    Boolean managedFooter = settingsService.setting(MANAGED_FOOTER_KEY,
                                                    Boolean.class,
                                                    site.getSiteRootDocument());
    Boolean managedFooterNavigation = settingsService.setting(MANAGED_FOOTER_NAVIGATION_KEY,
                                                              Boolean.class,
                                                              site.getSiteRootDocument());

    ManagedPagePartsSettings settings = new ManagedPagePartsSettings();
    settings.setManagedFooter(managedFooter != null ? managedFooter : DEFAULT_VALUE);
    settings.setManagedHeader(managedHeader != null ? managedHeader : DEFAULT_VALUE);
    settings.setManagedNavigation(managedNavigation != null ? managedNavigation : DEFAULT_VALUE);
    settings.setManagedFooterNavigation(managedFooterNavigation != null ? managedFooterNavigation : DEFAULT_VALUE);
    return new ResponseEntity<>(settings, HttpStatus.OK);
  }
}

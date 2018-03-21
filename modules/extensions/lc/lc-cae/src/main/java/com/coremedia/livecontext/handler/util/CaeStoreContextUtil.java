package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentParameters;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CaeStoreContextUtil {
  /**
   * Update the StoreContext with fragment parameter information
   * @param catalogAliasTranslationService
   * @param storeContext current store context
   * @param fragmentParameters parameters coming from fragment urls
   * @param site the current site
   */
  public static void updateStoreContextWithFragmentParameters(@Nonnull CatalogAliasTranslationService catalogAliasTranslationService,
                                                              @Nonnull StoreContext storeContext,
                                                              @Nonnull FragmentParameters fragmentParameters,
                                                              @Nonnull Site site) {
    Optional<String> catalogIdOpt = fragmentParameters.getCatalogId();
    if (catalogIdOpt.isPresent()) {
      CatalogId catalogId = CatalogId.of(catalogIdOpt.get());
      Optional<CatalogAlias> catalogAlias = catalogAliasTranslationService.getCatalogAliasForId(catalogId, site.getId());
      storeContext.setCatalog(catalogAlias.orElse(null), catalogId);
    }
  }
}

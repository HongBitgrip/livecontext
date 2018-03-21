package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class CaeStoreContextUtilTest {

  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock
  private Site site;

  CatalogAlias myCatalogAlias;

  @Test
  public void updateStoreContextWithFragmentParameters() throws Exception {
    StoreContext storeContext = StoreContextImpl.newStoreContext();

    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;catalogId=myCatalogId";
    FragmentParameters fragmentParameters = FragmentParametersFactory.create(url);

    CaeStoreContextUtil.updateStoreContextWithFragmentParameters(catalogAliasTranslationService, storeContext, fragmentParameters, site);

    assertThat(storeContext.getCatalogAlias()).as("catalogAlias should be updated").isEqualTo(myCatalogAlias);
  }


  @Before
  public void setup() {
    initMocks(this);
    myCatalogAlias = CatalogAlias.of("myCatalogAlias");
    when(catalogAliasTranslationService.getCatalogAliasForId(CatalogId.of("myCatalogId"), "mySite")).thenReturn(Optional.of(myCatalogAlias));
    when(site.getId()).thenReturn("mySite");
  }

}

package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;

public class AssetServiceMock implements AssetService {

  @Nonnull
  @Override
  public CatalogPicture getCatalogPicture(@Nonnull String url, @Nonnull CommerceId commerceId) {
    return mock(CatalogPicture.class);
  }

  @Nonnull
  @Override
  public List<Content> findPictures(@Nonnull CommerceId id) {
    Content content = mock(Content.class);
    return singletonList(content);
  }

  @Nonnull
  @Override
  public List<Content> findPictures(@Nonnull CommerceId id, boolean withDefault) {
    Content content = mock(Content.class);
    return singletonList(content);
  }

  @Nonnull
  @Override
  public List<Content> findVisuals(@Nonnull CommerceId id, boolean withDefault) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Content> findVisuals(@Nonnull CommerceId id) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Content> findDownloads(@Nullable CommerceId id) {
    return emptyList();
  }

  @Nullable
  @Override
  public Content getDefaultPicture(@Nonnull Site site) {
    return null;
  }

  @Nonnull
  @Override
  public AssetService withStoreContext(StoreContext storeContext) {
    return this;
  }
}

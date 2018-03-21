package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.hybris.cache.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.MediaDocument;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.hybris.common.HybrisCommerceIdProvider.commerceId;

public class CategoryImpl extends AbstractHybrisCommerceBean implements Category {

  public static final String ROOT_CATEGORY_ROLE_ID = "ROOT";
  private List<Category> children;
  private List<Product> products;

  @Override
  public void load() {
    if (isRoot()) {
      CategoryDocument delegate = new CategoryDocument();
      delegate.setCode(ROOT_CATEGORY_ROLE_ID);
      setDelegate(delegate);
    } else {
      CategoryCacheKey cacheKey = new CategoryCacheKey(getId(), getContext(), getCatalogResource(), getCommerceCache());
      loadCached(cacheKey);
    }
  }

  @Override
  public CategoryDocument getDelegate() {
    return (CategoryDocument) super.getDelegate();
  }

  @Override
  public String getName() {
    String name = getDelegate().getName();
    return (name != null) ? name : getExternalId();
  }

  @Override
  public Markup getShortDescription() {
    return getLongDescription(); // Hybris Categories only have one description field, use the same value for both
  }

  @Override
  public Markup getLongDescription() {
    String description = getDelegate().getDescription();
    return buildRichtextMarkup(description);
  }

  @Override
  public String getThumbnailUrl() {
    List<Content> pictures = getPictures();

    if (!pictures.isEmpty()) {
      return getAssetUrlProvider().getImageUrl("/catalogimage/category/" +
              StoreContextHelper.getStoreId() + "/" +
              StoreContextHelper.getLocale() + "/thumbnail/" + getExternalId() + ".jpg");
    }

    MediaDocument doc = getDelegate().getThumbnail();
    return doc != null ? getAssetUrlProvider().getImageUrl(doc.getDownloadUrl()) : getDefaultImageUrl();
  }

  @Override
  public String getDefaultImageUrl() {
    List<Content> pictures = getPictures();

    if (!pictures.isEmpty()) {
      return getAssetUrlProvider().getImageUrl("/catalogimage/category/" +
              StoreContextHelper.getStoreId() + "/" +
              StoreContextHelper.getLocale() + "/full/" + getExternalId() + ".jpg");
    }

    MediaDocument doc = getDelegate().getPicture();
    return doc != null ? getAssetUrlProvider().getImageUrl(doc.getDownloadUrl()) : null;
  }

  @Nonnull
  @Override
  public List<Category> getChildren() {
    if (children == null) {
      if (isRoot()) {
        CatalogAlias catalogAlias = getId().getCatalogAlias();
        return getCatalogService().findTopCategories(catalogAlias, getContext());
      }

      List<Category> childrenNew = new ArrayList<>();

      List<CategoryRefDocument> refDocuments = getDelegate().getSubCategories();
      if (refDocuments != null) {
        for (CategoryRefDocument refDocument : refDocuments) {
          // lazy loading of subcategories
          String externalId = refDocument.getCode();
          CommerceId commerceId = commerceId(CATEGORY).withExternalId(externalId).build();
          childrenNew.add((Category) getCommerceBeanFactory().createBeanFor(commerceId, getContext()));
        }
      }

      children = childrenNew;
    }
    return children;
  }

  @Nonnull
  @Override
  public List<Product> getProducts() {
    if (products == null) {
      products = getCatalogService().findProductsByCategory(this);
    }

    return products;
  }

  @Nullable
  @Override
  public Category getParent() {
    if (isRoot()) {
      return null;
    }

    String parentId = getDelegate().getParentId();
    CatalogService catalogService = getCatalogService();

    if (StringUtils.isBlank(parentId)) {
      return catalogService.findRootCategory(getCatalogAlias(), getContext());
    }

    CommerceId commerceId = commerceId(CATEGORY).withExternalId(parentId).build();
    return catalogService.findCategoryById(commerceId, getContext());
  }

  @Nonnull
  @Override
  public List<Category> getBreadcrumb() {
    List<Category> result = new ArrayList<>();

    Category parent = getParent();
    if (parent != null) {
      result.addAll(parent.getBreadcrumb());
    }

    if (!isRoot()) {
      result.add(this);
    }

    return result;
  }

  @Nullable
  @Override
  public String getSeoSegment() {
    return null;
  }

  @Override
  public String getMetaDescription() {
    return null;
  }

  @Override
  public String getMetaKeywords() {
    return null;
  }

  @Override
  public String getTitle() {
    return getName();
  }

  @Nonnull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @Nullable
  @Override
  public CatalogPicture getCatalogPicture() {
    return null;
  }

  @Nullable
  @Override
  public Content getPicture() {
    return null;
  }

  @Nonnull
  @Override
  public List<Content> getPictures() {
    return findAssetService()
            .map(assetService -> assetService.findPictures(getReference()))
            .orElseGet(Collections::emptyList);
  }

  @Nonnull
  @Override
  public List<Content> getVisuals() {
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<Content> getDownloads() {
    return Collections.emptyList();
  }

  @Override
  public boolean isRoot() {
    return isRootCategoryId(getId());
  }

  public static boolean isRootCategoryId(@Nonnull CommerceId id) {
    return id.getExternalId().map(ROOT_CATEGORY_ROLE_ID::equals).orElse(false);
  }
}

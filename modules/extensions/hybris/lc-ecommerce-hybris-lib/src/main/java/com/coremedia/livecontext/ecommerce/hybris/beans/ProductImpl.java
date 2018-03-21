package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.cache.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.pricing.PriceServiceImpl;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductVariantRefDocument;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;
import static com.coremedia.livecontext.ecommerce.hybris.common.HybrisCommerceIdProvider.commerceId;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class ProductImpl extends AbstractHybrisCommerceBean implements Product {

  private static final Logger LOG = LoggerFactory.getLogger(ProductImpl.class);

  private PriceServiceImpl priceService;

  private List<String> variantAxis;

  private List<ProductAttribute> definingAttributes;
  private List<ProductAttribute> describingAttributes;

  public PriceServiceImpl getPriceService() {
    return priceService;
  }

  public void setPriceService(PriceServiceImpl priceService) {
    this.priceService = priceService;
  }

  @Override
  public ProductDocument getDelegate() {
    return (ProductDocument) super.getDelegate();
  }

  @Override
  public void load() {
    ProductCacheKey cacheKey = new ProductCacheKey(getId(), getContext(), getCatalogResource(), getCommerceCache());
    loadCached(cacheKey);
  }

  @Override
  public Currency getCurrency() {
    StoreContext storeContext = CurrentCommerceConnection.get().getStoreContext();
    return storeContext.getCurrency();
  }

  @Override
  public String getName() {
    String name = getDelegate().getName();
    return (name != null) ? name : getExternalId();
  }

  @Override
  public Markup getShortDescription() {
    return buildRichtextMarkup(getDelegate().getSummary());
  }

  @Override
  public Markup getLongDescription() {
    String description = getDelegate().getDescription();
    return buildRichtextMarkup(description);
  }

  @Override
  public String getTitle() {
    return getName();
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
  public BigDecimal getListPrice() {
    return priceService.findListPriceForProduct(this);
  }

  @Override
  public BigDecimal getOfferPrice() {
    return priceService.findOfferPriceForProduct(this);
  }

  @Nullable
  @Override
  public String getSeoSegment() {
    // so seo segment in hybris
    return null;
  }

  @Nullable
  @Override
  public Category getCategory() {
    String categoryId = getDelegate().getCategoryId();

    if (StringUtils.isBlank(categoryId)) {
      return null;
    }

    CommerceId commerceId = commerceId(CATEGORY).withExternalId(categoryId).build();
    return getCatalogService().findCategoryById(commerceId, getContext());
  }

  @Nonnull
  @Override
  public List<Category> getCategories() {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return emptyList();
  }

  @Nullable
  @Override
  public String getDefaultImageAlt() {
    return null;
  }

  @Nullable
  @Override
  public String getDefaultImageUrl() {
    List<Content> pictures = getPictures();

    if (!pictures.isEmpty()) {
      return getAssetUrlProvider().getImageUrl("/catalogimage/product/" +
              StoreContextHelper.getStoreId() + "/" +
              StoreContextHelper.getLocale() + "/full/" + getExternalId() + ".jpg");
    }

    return getAssetUrlProvider().getImageUrl(getDelegate().getPictureDownloadUrl());
  }

  @Nullable
  @Override
  public String getThumbnailUrl() {
    List<Content> pictures = getPictures();

    if (!pictures.isEmpty()) {
      return getAssetUrlProvider().getImageUrl("/catalogimage/product/" +
              StoreContextHelper.getStoreId() + "/" +
              StoreContextHelper.getLocale() + "/thumbnail/" + getExternalId() + ".jpg");
    }

    return getAssetUrlProvider().getImageUrl(getDelegate().getThumbnailDownloadUrl());
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    if (definingAttributes == null) {
      loadDefiningAttributes();
    }

    return definingAttributes;
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    if (describingAttributes == null) {
      loadDescribingAttributes();
    }

    return describingAttributes;
  }

  private void loadDescribingAttributes() {
    describingAttributes = new ArrayList<>();
  }

  protected void loadDefiningAttributes() {
    definingAttributes = new ArrayList<>();

    ProductVariant firstSku = findFirstSku().orElse(null);
    if (firstSku instanceof ProductVariantImpl) {
      ProductVariantImpl productVariant = (ProductVariantImpl) firstSku;

      List<ProductAttribute> productAttributes = productVariant.getDefiningAttributes();
      for (ProductAttribute productAttribute : productAttributes) {
        definingAttributes.add(productAttribute);
      }
    }
  }

  @Nonnull
  @Override
  public List<String> getVariantAxisNames() {
    if (variantAxis == null) {
      List<String> newVariantAxis = new ArrayList<>();

      for (ProductAttribute definingAttribute : getDefiningAttributes()) {
        String id = definingAttribute.getId();
        if (!newVariantAxis.contains(id)) {
          newVariantAxis.add(id);
        }
      }

      variantAxis = newVariantAxis;
    }

    return variantAxis;
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nonnull List<VariantFilter> filters) {
    List<Object> result = new ArrayList<>();

    List<ProductVariant> availableProducts = getVariants(filters);
    for (ProductVariant productVariant : availableProducts) {
      Object attributeValue = productVariant.getAttributeValue(axisName);
      if (attributeValue != null && !result.contains(attributeValue)) {
        result.add(attributeValue);
      }
    }

    return result;
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable VariantFilter filter) {
    if (filter == null) {
      return getVariantAxisValues(axisName, emptyList());
    }

    List<VariantFilter> filters = newArrayList(filter);

    return getVariantAxisValues(axisName, filters);
  }

  @Nonnull
  private Optional<ProductVariant> findFirstSku() {
    return getVariants().stream().filter(variant -> variant.getVariants().isEmpty()).findFirst();
  }

  @Nonnull
  @Override
  public List<Object> getAttributeValues(@Nonnull String attributeId) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants() {
    List<ProductVariantRefDocument> variantRefDocuments = getDelegate().getVariantRefDocuments();

    if (variantRefDocuments == null) {
      return emptyList();
    }

    List<ProductVariant> variants = new ArrayList<>();

    for (ProductVariantRefDocument variantRefDocument : variantRefDocuments) {
      String externalId = variantRefDocument.getCode();
      CommerceId commerceId = commerceId(SKU).withExternalId(externalId).build();
      ProductVariant variant = getCatalogService().findProductVariantById(commerceId, getContext());
      if (variant == null) {
        LOG.warn("Cannot find sku '{}'.", externalId);
      } else {
        variants.add(variant);
        variants.addAll(variant.getVariants());
      }
    }

    return variants;
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nonnull List<VariantFilter> filters) {
    List<ProductVariant> allVariants = getVariants();

    if (filters.isEmpty()) {
      return allVariants;
    }

    return allVariants.stream()
            .filter(variant -> isProductVariantIncludedinAllFilters(variant, filters))
            .collect(toList());
  }

  private static boolean isProductVariantIncludedinAllFilters(ProductVariant productVariant,
                                                              @Nonnull Collection<VariantFilter> filters) {
    return filters.stream().allMatch(filter -> filter.matches(productVariant));
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable VariantFilter filter) {
    if (filter == null) {
      return getVariants(emptyList());
    }

    return getVariants(singletonList(filter));
  }

  @Nonnull
  @Override
  public Map<ProductVariant, AvailabilityInfo> getAvailabilityMap() {
    return emptyMap();
  }

  @Override
  public float getTotalStockCount() {
    return 0;
  }

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public boolean isVariant() {
    return getDelegate().getBaseProduct() != null;
  }

  @Nonnull
  @Override
  public CatalogPicture getCatalogPicture() {
    return findAssetService()
            .map(assetService -> assetService.getCatalogPicture(getCatalogPictureDefaultImageUrl(), getReference()))
            .orElseGet(() -> new CatalogPicture("#", null));
  }

  private String getCatalogPictureDefaultImageUrl() {
    String defaultImageUrl = getDefaultImageUrl();

    ProductDocument delegate = getDelegate();
    if (isNullOrEmpty(defaultImageUrl) || isNullOrEmpty(delegate.getPictureDownloadUrl())) {
      ProductRefDocument baseProduct = delegate.getBaseProduct();
      if (baseProduct != null) {
        String baseProductUri = baseProduct.getCode();
        if (!isNullOrEmpty(baseProductUri)) {
          CommerceId commerceId = commerceId(PRODUCT).withExternalId(baseProductUri).build();
          defaultImageUrl = getCatalogService().findProductById(commerceId, getContext()).getDefaultImageUrl();
        }
      }
    }

    return defaultImageUrl;
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
    return findAssetService()
            .map(assetService -> assetService.findVisuals(getReference(), false))
            .orElseGet(Collections::emptyList);
  }

  @Nonnull
  @Override
  public List<Content> getDownloads() {
    return findAssetService()
            .map(assetService -> assetService.findDownloads(getReference()))
            .orElseGet(Collections::emptyList);
  }
}

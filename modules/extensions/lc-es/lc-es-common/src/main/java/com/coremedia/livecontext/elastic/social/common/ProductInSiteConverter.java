package com.coremedia.livecontext.elastic.social.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoCommerceConnectionAvailable;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.api.models.UnresolvableReferenceException;
import com.coremedia.elastic.core.base.serializer.AbstractTypeConverter;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.navigation.ProductInSiteImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static java.util.Objects.requireNonNull;

@Named
public class ProductInSiteConverter extends AbstractTypeConverter<ProductInSite> {

  protected static final String ID = "id";
  protected static final String SITE_ID = "site";

  private final SitesService sitesService;
  private final CommerceConnectionInitializer connectionInitializer;

  @Inject
  public ProductInSiteConverter(SitesService sitesService, CommerceConnectionInitializer connectionInitializer) {
    this.sitesService = sitesService;
    this.connectionInitializer = connectionInitializer;
  }

  @Override
  public Class<ProductInSite> getType() {
    return ProductInSite.class;
  }

  @Override
  public void serialize(ProductInSite productInSite, Map<String, Object> serializedObject) {
    Product product = productInSite.getProduct();
    Site site = productInSite.getSite();
    String productId = format(getNormalizeProductId(product, site));

    serializedObject.put(ID, productId);
    serializedObject.put(SITE_ID, site.getId());
  }

  private CommerceId getNormalizeProductId(@Nonnull Product product, @Nonnull Site site){
    CatalogAlias catalogAlias = product.getId().getCatalogAlias();
    String externalId = product.getExternalId();
    return getCommerceConnectionForSerialization(site, product.getExternalId()).getIdProvider()
            .formatProductId(catalogAlias, externalId);
  }

  @Nonnull
  private CommerceConnection getCommerceConnectionForSerialization(@Nonnull Site site, String externalProductId) {
    return connectionInitializer.findConnectionForSite(site)
            .orElseThrow(() -> new NoCommerceConnectionAvailable(String.format(
                    "No commerce connection available for site '%s'; not serializing product with external id '%s'.",
                    site, externalProductId))
            );
  }

  @Override
  @Nonnull
  public ProductInSite deserialize(Map<String, Object> serializedObject) {
    String productId = (String) serializedObject.get(ID);
    String siteId = (String) serializedObject.get(SITE_ID);

    if (productId == null) {
      throwUnresolvable(null, siteId);
    }

    requireNonNull(siteId, "Site ID must be set.");
    Site site = sitesService.findSite(siteId)
            .orElseThrow(() -> new UnresolvableReferenceException(
                    String.format("Site ID %s could not be resolved.", siteId)));

    Product product = findProduct(site, productId);

    if (product == null) {
      throwUnresolvable(productId, siteId);
    }

    return new ProductInSiteImpl(product, site);
  }

  @Nullable
  private Product findProduct(@Nonnull Site site, @Nonnull String productId) {
    Product product = null;

    // `CommerceConnectionFilter` does not recognize Elastic Social
    // Studio calls, so we have to setup the commerce connection.
    CommerceConnection oldConnection = CurrentCommerceConnection.find().orElse(null);
    try {
      CommerceConnection myConnection = getCommerceConnectionForDeserialization(site, productId);

      CurrentCommerceConnection.set(myConnection);

      product = findProduct(myConnection, productId);
    } catch (RuntimeException exception) {
      throwUnresolvable(productId, site.getId(), exception);
    } finally {
      if (oldConnection != null) {
        CurrentCommerceConnection.set(oldConnection);
      } else {
        CurrentCommerceConnection.remove();
      }
    }

    return product;
  }

  @Nonnull
  private CommerceConnection getCommerceConnectionForDeserialization(@Nonnull Site site, String productId) {
    return connectionInitializer.findConnectionForSite(site)
            .orElseThrow(() -> new UnresolvableReferenceException(String.format(
                    "Cannot resolve product with ID '%s' and site '%s' (commerce connection unavailable for that site).",
                    productId, site)));
  }

  @Nullable
  private static Product findProduct(@Nonnull CommerceConnection connection, @Nonnull String productId) {
    CatalogService catalogService = connection.getCatalogService();

    if (catalogService == null) {
      return null;
    }

    return catalogService.findProductById(CommerceIdParserHelper.parseCommerceIdOrThrow(productId), connection.getStoreContext());
  }

  private static void throwUnresolvable(String productId, String siteId) {
    throwUnresolvable(productId, siteId, null);
  }

  private static void throwUnresolvable(String productId, String siteId, @Nullable Throwable exception) {
    String message = String.format("Product with ID '%s' and site ID '%s' could not be resolved.", productId, siteId);
    throw new UnresolvableReferenceException(message, exception);
  }
}

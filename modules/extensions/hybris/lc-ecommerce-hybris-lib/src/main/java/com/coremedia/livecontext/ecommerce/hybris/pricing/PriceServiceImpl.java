package com.coremedia.livecontext.ecommerce.hybris.pricing;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.ProductImpl;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractHybrisService;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.livecontext.ecommerce.pricing.PriceService;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class PriceServiceImpl extends AbstractHybrisService implements PriceService {

  private static final Logger LOG = LoggerFactory.getLogger(PriceServiceImpl.class);

  private CatalogResource catalogResource;

  @Inject
  @Named("hybrisCatalogService")
  private CatalogService catalogService;

  /**
   * Returns the list price value for the given product.
   * @param product
   * @return The list price value or NULL if no such price exists.
   */
  public BigDecimal findOfferPriceForProduct(Product product) {
    // TODO Use cache keys
    // read prices via REST service
    List<PriceDocument> prices = getPriceDocuments(product);
    return findOfferPriceForPrices(prices);
  }

  protected BigDecimal findOfferPriceForPrices(List<PriceDocument> prices) {

    // filter currency
    StoreContext currentContext = CurrentCommerceConnection.get().getStoreContext();
    List<PriceDocument> filteredPriceDocuments = filterCurrency(prices, currentContext.getCurrency());

    // The offer price in Hybris is modeled as price.isGiveAwayPrice
    PriceDocument priceDocument = filterGiveAwayPrice(filteredPriceDocuments, Boolean.TRUE);
    BigDecimal price = null;
    if (priceDocument != null) {
      price = convertStringToBigDecimal(priceDocument.getPrice());
    }
    return price;
  }

  /**
   * Returns the list price value for the given product.
   * @param product
   * @return The list price value or NULL if no such price exists.
   */
  public BigDecimal findListPriceForProduct(Product product) {
    // TODO Use cache keys
    // read prices via REST service
    List<PriceDocument> prices = getPriceDocuments(product);
    return findListPriceForPrices(prices);
  }

  protected BigDecimal findListPriceForPrices(List<PriceDocument> prices) {

    // filter currency
    StoreContext currentContext = CurrentCommerceConnection.get().getStoreContext();
    List<PriceDocument> filteredPriceDocuments = filterCurrency(prices, currentContext.getCurrency());

    // The list price in Hybris is modeled as !price.isGiveAwayPrice
    PriceDocument priceDocument = filterGiveAwayPrice(filteredPriceDocuments, Boolean.FALSE);
    BigDecimal price = null;
    if (priceDocument != null) {
      price = convertStringToBigDecimal(priceDocument.getPrice());
    }
    return price;
  }


  protected List<PriceDocument> getPriceDocuments(Product product) {
    ProductDocument delegate = ((ProductImpl) product).getDelegate();
    List<PriceDocument> prices = new ArrayList<>();
    List<PriceRefDocument> priceRefDocuments = delegate.getPriceRefDocuments();
    if (priceRefDocuments == null) {
      return prices;
    }
    for (PriceRefDocument priceRefDocument: priceRefDocuments) {
      PriceDocument priceDocument = catalogResource.getPriceDocumentById(priceRefDocument.getKey(), StoreContextHelper.getCurrentContext());
      if (priceDocument == null) {
        LOG.warn("Cannot find price " + priceRefDocument.getKey() + " for product " + product.getExternalId());
      }
      prices.add(priceDocument);
    }
    return prices;
  }

  /**
   * Returns the price which matches the given boolean give away price value.
   * @param priceDocuments The prices
   * @param giveAwayPrice
   * @return The first price which matches the boolean value giveAwayPrice. Returns NULL if no price matches.
   */
  protected PriceDocument filterGiveAwayPrice(List<PriceDocument> priceDocuments, Boolean giveAwayPrice) {

    for (PriceDocument priceDocument: priceDocuments) {
      if (giveAwayPrice.equals(priceDocument.isGiveAwayPrice())) {
        return priceDocument;
      }
    }

    return null;
  }

  /**
   * Filters prices of given currency.
   * @param priceDocuments The prices.
   * @param currency The required currency.
   * @return Only the prices which are of the given currency. Empty list if no price matches the given currency.
   */
  protected List<PriceDocument> filterCurrency(List<PriceDocument> priceDocuments, Currency currency) {
    List<PriceDocument> filteredPriceDocuments = new ArrayList<>();
    if (currency == null) {
      return filteredPriceDocuments;
    }
    String currencyCode = currency.getCurrencyCode();
    for (PriceDocument priceDocument: priceDocuments) {
        if (priceDocument != null && currencyCode.equals(priceDocument.getCurrencyISOCode())) {
        filteredPriceDocuments.add(priceDocument);
      }
    }

    return filteredPriceDocuments;
  }

  @Override
  public BigDecimal findOfferPriceForProduct(String productId) {
    Product product = catalogService.findProductById(CommerceIdParserHelper.parseCommerceIdOrThrow(productId), StoreContextHelper.getCurrentContextOrThrow());
    return product.getOfferPrice();
  }

  @Override
  public BigDecimal findListPriceForProduct(String productId) {
    Product product = catalogService.findProductById(CommerceIdParserHelper.parseCommerceIdOrThrow(productId), StoreContextHelper.getCurrentContextOrThrow());
    return product.getListPrice();
  }


  @Nonnull
  @Override
  public PriceService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, PriceService.class);
  }

  protected BigDecimal convertStringToBigDecimal(String value) {
    if (NumberUtils.isNumber(value)) {
      return NumberUtils.createBigDecimal(value);
    }
    return null;
  }

  public CatalogResource getCatalogResource() {
    return catalogResource;
  }

  @Required
  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }

}

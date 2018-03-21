package com.coremedia.livecontext.ecommerce.hybris.pricing;

import com.coremedia.blueprint.lc.test.AbstractServiceTest;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.hybris.HybrisTestConfig;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, HybrisTestConfig.class})
public class PriceServiceImplTest extends AbstractServiceTest {

  @Inject
  private PriceServiceImpl testling;

  @Test
  public void filterGiveAwayPrice_NoPriceMatches() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.isGiveAwayPrice()).thenReturn(Boolean.FALSE);

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.isGiveAwayPrice()).thenReturn(Boolean.FALSE);

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2);
    PriceDocument priceDocument = testling.filterGiveAwayPrice(priceDocuments, Boolean.TRUE);

    assertThat(priceDocument).isNull();
  }

  @Test
  public void filterGiveAwayPrice_EmptyList() {
    List<PriceDocument> priceDocuments = newArrayList();
    PriceDocument priceDocument = testling.filterGiveAwayPrice(priceDocuments, Boolean.TRUE);

    assertThat(priceDocument).isNull();
  }

  @Test
  public void filterGiveAwayPrice_OnePriceMatches() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.isGiveAwayPrice()).thenReturn(Boolean.FALSE);

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.isGiveAwayPrice()).thenReturn(Boolean.TRUE);

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2);
    PriceDocument priceDocument;

    priceDocument = testling.filterGiveAwayPrice(priceDocuments, Boolean.TRUE);
    assertThat(priceDocument).as("Second price is net price").isEqualTo(priceDocument2);

    priceDocument = testling.filterGiveAwayPrice(priceDocuments, Boolean.FALSE);
    assertThat(priceDocument).as("First price is not a net price").isEqualTo(priceDocument1);
  }

  @Test
  public void filterCurrency_EmptyList() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.getCurrencyISOCode()).thenReturn("USD");

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.getCurrencyISOCode()).thenReturn("EUR");

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2);
    List<PriceDocument> filteredPriceDocuments = testling.filterCurrency(priceDocuments, Currency.getInstance("AUD"));

    assertThat(filteredPriceDocuments).isEmpty();
  }

  @Test
  public void filterCurrency_OnePriceFound() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.getCurrencyISOCode()).thenReturn("USD");

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.getCurrencyISOCode()).thenReturn("EUR");

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2);
    List<PriceDocument> filteredPriceDocuments = testling.filterCurrency(priceDocuments, Currency.getInstance("USD"));

    assertThat(filteredPriceDocuments).hasSize(1);
    assertThat(filteredPriceDocuments.get(0)).isEqualTo(priceDocument1);
  }

  @Test
  public void filterCurrency_TwoPricesFound() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.getCurrencyISOCode()).thenReturn("USD");

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.getCurrencyISOCode()).thenReturn("EUR");

    PriceDocument priceDocument3 = mock(PriceDocument.class);
    when(priceDocument3.getCurrencyISOCode()).thenReturn("EUR");

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2, priceDocument3);
    List<PriceDocument> filteredPriceDocuments = testling.filterCurrency(priceDocuments, Currency.getInstance("EUR"));

    assertThat(filteredPriceDocuments).hasSize(2);
    assertThat(filteredPriceDocuments.get(0)).isEqualTo(priceDocument2);
    assertThat(filteredPriceDocuments.get(1)).isEqualTo(priceDocument3);
  }

  @Test
  public void findListPriceForPrices_OneListPriceAvailable() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.getCurrencyISOCode()).thenReturn("GBP");
    when(priceDocument1.isGiveAwayPrice()).thenReturn(Boolean.FALSE);
    when(priceDocument1.getPrice()).thenReturn("99.99");

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.getCurrencyISOCode()).thenReturn("EUR");
    when(priceDocument2.isGiveAwayPrice()).thenReturn(Boolean.FALSE);
    when(priceDocument2.getPrice()).thenReturn("199.99");

    PriceDocument priceDocument3 = mock(PriceDocument.class);
    when(priceDocument3.getCurrencyISOCode()).thenReturn("AUD");
    when(priceDocument3.isGiveAwayPrice()).thenReturn(Boolean.FALSE);
    when(priceDocument3.getPrice()).thenReturn("188.88");

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2, priceDocument3);
    BigDecimal price = testling.findListPriceForPrices(priceDocuments);

    assertThat(price.toString()).isEqualTo(priceDocument1.getPrice());
  }

  @Test
  public void findListPriceForPrices_NoListPriceAvailable() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.getCurrencyISOCode()).thenReturn("USD");
    when(priceDocument1.isGiveAwayPrice()).thenReturn(Boolean.TRUE);
    when(priceDocument1.getPrice()).thenReturn("99.99");

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.getCurrencyISOCode()).thenReturn("EUR");
    when(priceDocument2.isGiveAwayPrice()).thenReturn(Boolean.TRUE);
    when(priceDocument2.getPrice()).thenReturn("199.99");

    PriceDocument priceDocument3 = mock(PriceDocument.class);
    when(priceDocument3.getCurrencyISOCode()).thenReturn("AUD");
    when(priceDocument3.isGiveAwayPrice()).thenReturn(Boolean.TRUE);
    when(priceDocument3.getPrice()).thenReturn("188.88");

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2, priceDocument3);
    BigDecimal price = testling.findListPriceForPrices(priceDocuments);

    assertThat(price).isNull();
  }

  @Test
  public void findOfferPriceForPrices_OneListPriceAvailable() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.getCurrencyISOCode()).thenReturn("GBP");
    when(priceDocument1.isGiveAwayPrice()).thenReturn(Boolean.TRUE);
    when(priceDocument1.getPrice()).thenReturn("99.99");

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.getCurrencyISOCode()).thenReturn("EUR");
    when(priceDocument2.isGiveAwayPrice()).thenReturn(Boolean.TRUE);
    when(priceDocument2.getPrice()).thenReturn("199.99");

    PriceDocument priceDocument3 = mock(PriceDocument.class);
    when(priceDocument3.getCurrencyISOCode()).thenReturn("AUD");
    when(priceDocument3.isGiveAwayPrice()).thenReturn(Boolean.TRUE);
    when(priceDocument3.getPrice()).thenReturn("188.88");

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2, priceDocument3);
    BigDecimal price = testling.findOfferPriceForPrices(priceDocuments);

    assertThat(price.toString()).isEqualTo(priceDocument1.getPrice());
  }

  @Test
  public void findListPriceForPrices_NoOfferPriceAvailable() {
    PriceDocument priceDocument1 = mock(PriceDocument.class);
    when(priceDocument1.getCurrencyISOCode()).thenReturn("USD");
    when(priceDocument1.isGiveAwayPrice()).thenReturn(Boolean.FALSE);
    when(priceDocument1.getPrice()).thenReturn("99.99");

    PriceDocument priceDocument2 = mock(PriceDocument.class);
    when(priceDocument2.getCurrencyISOCode()).thenReturn("EUR");
    when(priceDocument2.isGiveAwayPrice()).thenReturn(Boolean.FALSE);
    when(priceDocument2.getPrice()).thenReturn("199.99");

    PriceDocument priceDocument3 = mock(PriceDocument.class);
    when(priceDocument3.getCurrencyISOCode()).thenReturn("AUD");
    when(priceDocument3.isGiveAwayPrice()).thenReturn(Boolean.FALSE);
    when(priceDocument3.getPrice()).thenReturn("188.88");

    List<PriceDocument> priceDocuments = newArrayList(priceDocument1, priceDocument2, priceDocument3);
    BigDecimal price = testling.findOfferPriceForPrices(priceDocuments);

    assertThat(price).isNull();
  }
}

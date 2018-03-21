package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.cae.ContentBeanTestBase;
import com.coremedia.xml.Markup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class PlainViewTest extends ContentBeanTestBase {
  private Markup markup;

  @Before
  public void setUp() throws Exception {
    markup = getContent(2).getMarkup("detailText");
  }


  @Test
  public void testRender() throws Exception {
    Assert.assertNotNull(markup);
    OutputStream stream = new ByteArrayOutputStream();
    Writer writer = new OutputStreamWriter(stream);
    new PlainView().render(markup, null, writer, null, null);
    Assert.assertEquals("\n" +
            "  \n" +
            "\n" +
            "In retail business, a warranty (or \"extended warranty\") commonly refers to a guarantee of the reliability of a\n" +
            "    product under conditions of ordinary use. It is called \"extended\" warranty because it covers defects that could\n" +
            "    arise some time after the date of sale. Should the product malfunction within a stipulated amount of time after the\n" +
            "    purchase, the manufacturer or distributor is typically required to provide the customer with a replacement, repair,\n" +
            "    or refund. Such warranties usually do not cover \"acts of God\", owner abuse, malicious destruction, commercial use,\n" +
            "    or anything, for that matter, outside of a mechanical failure incurred with normal personal usage. Most warranties\n" +
            "    exclude parts that normally wear out, and supplies that must be periodically replaced as they are normally used up\n" +
            "    (e.g., tires and lubrication on a vehicle). An extended warranty may be included in the purchase price, or\n" +
            "    optionally extended for an additional fee, and may have yearly extensions as well as ambiguous terms like\n" +
            "    \"lifetimes\" of the product.\n" +
            "  \n" +
            "  \n" +
            "\n" +
            "A manufacturer or distributor may be required to carry reserve funds on its financial balance sheet to cover\n" +
            "    potential services or refunds that may arise for any products still covered \"under warranty\".\n" +
            "  \n" +
            "  \n" +
            "\n" +
            "Third-party warranty providers offer optional \"extended warranty\" agreements for a multitude of products,\n" +
            "    considered a contract of insurance for that product. Third parties are sold through a range of smaller, self-insured\n" +
            "    companies as well as larger, well known store chains, such as Best Buy and Circuit City. As with other types of\n" +
            "    insurance, the companies are gambling that the products will be reliable, that the warranty will be forgotten, or\n" +
            "    that any claims made can be handled inexpensively. Some third party companies provide their own support such as JTF\n" +
            "    Business Systems; these companies will remove the defective part and send it back to the manufacturer for\n" +
            "    replacement.\n" +
            "  \n" +
            "  \n" +
            "\n" +
            "Extended warranties are not usually provided through the manufacturer but are extended through independent\n" +
            "    administrators. In some circumstances it may work to the consumer's benefit having an assurance to the product from\n" +
            "    a company outside of place of purchase and/or service. For instance, when an auto warranty is provided through a car\n" +
            "    dealership, it's usually a sub-contracted warranty (often from the retailer with the lowest offer), where vehicle\n" +
            "    repairs are negotiated to a lower rate, often compromising the service, labor and parts to a lower standard. Many\n" +
            "    times these types of warranties require an unexpected out-of-pocket expense at the time of repair, such as:\n" +
            "    -unexpected services provided outside of the warranty terms -uncovered parts and labor rates -paying the full\n" +
            "    balance while a reimbursement is arranged through dealership/warranty claims offices. Some mechanics and dealer\n" +
            "    service centers might put off, or defer the needed repair until the dealership's warranty has expired so that their\n" +
            "    (in-house) warranty will no longer be bound to cover the cost of repair, or so that the ordinary (higher) shop rate\n" +
            "    will apply.\n" +
            "  \n" +
            "  \n" +
            "\n" +
            "Copyright notice: This article uses material from the Wikipedia.\n" +
            "  \n", stream.toString());
  }
}

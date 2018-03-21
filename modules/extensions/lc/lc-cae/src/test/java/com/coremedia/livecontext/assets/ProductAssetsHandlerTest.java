package com.coremedia.livecontext.assets;

import com.coremedia.livecontext.asset.ProductAssetsHandler;
import com.coremedia.livecontext.ecommerce.catalog.AxisFilter;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductAssetsHandlerTest {

  @Test
  public void testParseAttributesFromCSL1() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=1,b=2,c=3");
    assertThat(filters).hasSize(3);

    AxisFilter secondFilter = (AxisFilter) filters.get(1);
    assertThat(secondFilter.getName()).isEqualTo("b");
    assertThat(secondFilter.getValue()).isEqualTo("2");
  }

  @Test
  public void testParseAttributesFromCSL2() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=1");
    assertThat(filters).hasSize(1);

    AxisFilter firstFilter = (AxisFilter) filters.get(0);
    assertThat(firstFilter.getName()).isEqualTo("a");
    assertThat(firstFilter.getValue()).isEqualTo("1");
  }

  @Test
  public void testParseAttributesFromCSL3() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromCSL4() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=,b=,c=");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromCSL5() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromCSL6() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromSSL1() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;1;b;2;c;3");
    assertThat(filters).hasSize(3);

    AxisFilter secondFilter = (AxisFilter) filters.get(1);
    assertThat(secondFilter.getName()).isEqualTo("b");
    assertThat(secondFilter.getValue()).isEqualTo("2");
  }

  @Test
  public void testParseAttributesFromSSL2() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;1");
    assertThat(filters).hasSize(1);

    AxisFilter firstFilter = (AxisFilter) filters.get(0);
    assertThat(firstFilter.getName()).isEqualTo("a");
    assertThat(firstFilter.getValue()).isEqualTo("1");
  }

  @Test
  public void testParseAttributesFromSSL3() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromSSL4() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;;b;;c;");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromSSL5() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromSSL6() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a");
    assertThat(filters).isEmpty();
  }

  @Test
  public void testParseAttributesFromSSL7() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;1;b;2;c;3;");
    assertThat(filters).hasSize(3);

    AxisFilter thirdFilter = (AxisFilter) filters.get(2);
    assertThat(thirdFilter.getName()).isEqualTo("c");
    assertThat(thirdFilter.getValue()).isEqualTo("3");
  }

  @Test
  public void testParseAttributesFromSSL8() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;;b;;c;;");
    assertThat(filters).isEmpty();
  }
}

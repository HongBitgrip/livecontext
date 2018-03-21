package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.VariantAttributeDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ProductAttributeImpl implements ProductAttribute {

  private Boolean isDefining;
  private String id;
  private Object value;
  private VariantAttributeDocument delegate;

  public ProductAttributeImpl(Boolean isDefining, VariantAttributeDocument delegate) {
    super();
    this.delegate = delegate;
    this.isDefining = isDefining;
  }

  public ProductAttributeImpl(Boolean isDefining, String id, Object value) {
    super();
    this.id = id;
    this.value = value;
    this.isDefining = isDefining;
  }

  public VariantAttributeDocument getDelegate() {
    return delegate;
  }

  @Override
  public String getId() {
    if (id == null && delegate != null) {
      id = getDelegate().getName();
    }

    return id;
  }

  @Override
  public String getDisplayName() {
    return getId();
  }

  @Override
  public String getType() {
    return null;
  }

  @Override
  public String getUnit() {
    return null;
  }

  @Override
  public String getDescription() {
    return getId();
  }

  @Override
  public String getExternalId() {
    return getId();
  }

  @Nullable
  @Override
  public Object getValue() {
    if (value == null && delegate != null) {
      value = getDelegate().getValue();
    }

    return value;
  }

  @Nonnull
  @Override
  public List<Object> getValues() {
    Object value = getValue();
    return newArrayList(value);
  }

  @Override
  public boolean isDefining() {
    return this.isDefining;
  }
}

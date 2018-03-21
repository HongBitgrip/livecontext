package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupDocument;
import com.coremedia.livecontext.ecommerce.p13n.Segment;

public class SegmentImpl extends AbstractHybrisCommerceBean implements Segment {

  @Override
  public UserGroupDocument getDelegate() {
    return (UserGroupDocument) super.getDelegate();
  }

  @Override
  public String getName() {
    return getDelegate().getUid();
  }

  @Override
  public String getDescription() {
    return getDelegate().getDescription();
  }

}

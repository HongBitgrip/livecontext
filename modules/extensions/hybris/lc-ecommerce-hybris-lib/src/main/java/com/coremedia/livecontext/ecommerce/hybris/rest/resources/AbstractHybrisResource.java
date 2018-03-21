package com.coremedia.livecontext.ecommerce.hybris.rest.resources;


import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.hybris.rest.HybrisRestConnector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

abstract class AbstractHybrisResource {

  private HybrisRestConnector connector;
  private HybrisRestConnector occConnector;

  public HybrisRestConnector getConnector() {
    return connector;
  }

  @Inject
  @Named("hybrisRestConnector")
  public void setConnector(HybrisRestConnector connector) {
    this.connector = connector;
  }

  public HybrisRestConnector getOccConnector() {
    return occConnector;
  }

  @Inject
  @Named("hybrisOccRestConnector")
  public void setOccConnector(HybrisRestConnector occConnector) {
    this.occConnector = occConnector;
  }

  @SafeVarargs
  @Nonnull
  static <E> List<E> newUriTemplateParameters(@Nullable Object source, E... elements) {
    for (Object o : elements) {
      if (o == null) {
        throw new InvalidContextException(String.format("required REST URL parameter is null (%s)", source));
      }
    }
    return newArrayList(elements);
  }

}

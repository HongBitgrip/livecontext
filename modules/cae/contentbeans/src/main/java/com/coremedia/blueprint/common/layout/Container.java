package com.coremedia.blueprint.common.layout;

import java.util.List;

/**
 * @cm.template.api
 */
public interface Container<T> {
  /**
   * Retrieves the items of the implementing class.
   *
   * @return a list of items computed for the backing content 'proxy' object
   * @cm.template.api
   */
  List<? extends T> getItems();

  /**
   * Returns the items, transitively flattening inner containers.
   *
   * @cm.template.api
   */
  List<?> getFlattenedItems();
}

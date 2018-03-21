package com.coremedia.lc.studio.lib.augmentation;

import com.coremedia.blueprint.base.util.ContentStringPropertyIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Named;

/**
 * Invalidates category remote beans
 */
@SuppressWarnings("unused") // It's autowired by spring's built-in application listener
@Named
public class CategoryInvalidator extends CommerceBeanInvalidator {

  @Autowired
  @Qualifier("catalogExternalChannelIndex")
  void setSource(ContentStringPropertyIndex source) {
    super.setSource(source);
  }

}

package com.coremedia.blueprint.assets.validation;

import com.coremedia.blueprint.assets.contentbeans.AMAsset;
import com.coremedia.blueprint.common.services.validation.AbstractValidator;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nullable;

/**
 * Checks if an {@link AMAsset} is valid.
 * For a preview, it must have any renditions, for the live site, it must have only published renditions.
 */
public class HasPublishedRenditionsValidator extends AbstractValidator<AMAsset> {

  private boolean preview;

  @Value("${cae.is.preview:false}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  @Override
  protected Predicate createPredicate() {
    return new Predicate<AMAsset>() {
      @Override
      public boolean apply(@Nullable AMAsset input) {
        return null != input &&
                (preview ? !input.getRenditions().isEmpty() : !input.getPublishedRenditions().isEmpty());
      }
    };
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return AMAsset.class.isAssignableFrom(clazz);
  }
}

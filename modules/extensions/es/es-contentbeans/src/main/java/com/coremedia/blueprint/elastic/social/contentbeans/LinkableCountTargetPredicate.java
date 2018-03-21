package com.coremedia.blueprint.elastic.social.contentbeans;

import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.objectserver.beans.ContentBean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


final class LinkableCountTargetPredicate implements CountTargetPredicate<Linkable> {

  private final ValidationService<Linkable> validationService;

  public LinkableCountTargetPredicate(ValidationService<Linkable> validationService) {
    this.validationService = validationService;
  }

  @Override
  public boolean apply(@Nullable Linkable input) {
    boolean inProduction = true;
    if (input instanceof ContentBean) {
      final ContentBean contentBean = (ContentBean) input;
      inProduction = contentBean.getContent().isInProduction();
    }
    return null != input && inProduction && validationService.validate(input);
  }

  @Nonnull
  @Override
  public Class<Linkable> getType() {
    return Linkable.class;
  }
}

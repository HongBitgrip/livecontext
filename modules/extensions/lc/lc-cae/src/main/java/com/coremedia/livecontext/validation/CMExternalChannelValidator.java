package com.coremedia.livecontext.validation;

import com.coremedia.blueprint.common.services.validation.AbstractValidator;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link com.coremedia.livecontext.contentbeans.CMExternalChannel} may link to categories that
 * are not existent on the remote commerce system anymore. This
 * {@link com.coremedia.blueprint.common.services.validation.Validator validator} can be used to
 * {@link com.coremedia.blueprint.common.services.validation.ValidationService#filterList(java.util.List) filter out}
 * those objects silently, so that the layout of a web page containing such a collection will not be broken.
 */
public class CMExternalChannelValidator extends AbstractValidator<CMExternalChannel> {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalReferencePredicate.class);

  @Override
  protected Predicate createPredicate() {
    return new ExternalReferencePredicate();
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return CMExternalChannel.class.isAssignableFrom(clazz);
  }

  private class ExternalReferencePredicate implements Predicate<CMExternalChannel> {
    @Override
    public boolean apply(@Nullable CMExternalChannel externalChannel) {
      if (!(externalChannel == null || externalChannel.isCatalogRoot())) {
        String externalId = externalChannel.getExternalId();
        if (!StringUtils.hasText(externalId)) {
          LOG.info("external id property of {} is empty", externalChannel);
          return false;
        }
        return hasValidReference(externalChannel);
      }
      return true;
    }
  }

  private boolean hasValidReference(@Nonnull CMExternalChannel cmExternalChannel) {
    String externalId = cmExternalChannel.getExternalId();
    try {
      Category category = cmExternalChannel.getCategory();
      LOG.debug("E-Commerce category for {} with external id '{}' is '{}'", cmExternalChannel, externalId, category);
      return category != null;
    } catch (CommerceException e) {
      LOG.warn("Caught exception while validating external id '{}' of {}.", externalId, cmExternalChannel, e);
      return false;
    }
  }

}

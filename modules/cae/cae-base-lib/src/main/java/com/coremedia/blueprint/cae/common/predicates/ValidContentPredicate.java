package com.coremedia.blueprint.cae.common.predicates;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.datevalidation.DateValidationPredicate;

import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.common.util.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Calendar;
import java.util.Map;

/**
 * This predicate checks if the given object is a content bean and if this content bean is valid.
 * That means that it is a CMLinkable and the validFrom and validTo properties are not before and after now
 * respectively.
 */
public class ValidContentPredicate implements Predicate<Content> {

  private ContentBeanFactory contentBeanFactory;

  private static final Logger LOG = LoggerFactory.getLogger(ValidContentPredicate.class);

  @Override
  public boolean include(Content content) {
    if (content == null || content.isDestroyed() || !content.getType().isSubtypeOf("CMLinkable")) {
      return false;
    }

    LOG.debug("Found content of type CMLinkable. Verify that content is valid.");
    CMLinkable linkableBean = contentBeanFactory.createBeanFor(content, CMLinkable.class);
    Calendar validFrom = linkableBean.getValidFrom();
    Calendar validTo = linkableBean.getValidTo();
    Map<String,Calendar> validityDates = DateValidationPredicate.createValidityDates(validFrom, validTo);
    boolean result = new DateValidationPredicate(Calendar.getInstance()).apply(validityDates);
    if (!result) {
      LOG.debug("Content '{}' is currently not valid. Skipping.", content.getId());
    }
    return result;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }
}

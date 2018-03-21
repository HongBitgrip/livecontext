package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.AbstractHybrisCommerceBean;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.AbstractHybrisDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public abstract class AbstractHybrisService {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractHybrisService.class);

  private CommerceBeanFactory commerceBeanFactory;
  private CommerceCache commerceCache;

  protected CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  protected CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  /**
   * Creates a {@link CommerceBean} for the given delegate and target class.
   *
   * @param delegate delegate {@link AbstractHybrisDocument}
   * @param aClass   target class
   * @param <T>      type of the target {@link CommerceBean} class
   * @return {@link CommerceBean}
   */
  @Nullable
  protected <T extends CommerceBean> T createBeanFor(@Nonnull AbstractHybrisDocument delegate,
                                                     @Nonnull StoreContext storeContext,
                                                     @Nonnull CommerceBeanType beanType,
                                                     @Nonnull Class<T> aClass) {
    CommerceId commerceId = HybrisCommerceIdProvider
            .commerceId(beanType)
            .withExternalId(delegate.getCode())
            .build();

    AbstractHybrisCommerceBean bean = (AbstractHybrisCommerceBean) commerceBeanFactory
            .createBeanFor(commerceId, storeContext);

    if (bean == null) {
      throw new IllegalStateException(String.format("Could not create commerce bean for commerce ID '%s'.", commerceId));
    }

    bean.setDelegate(delegate);
    LOG.debug("Created commerce bean for '{}'", commerceId);

    return aClass.cast(bean);
  }

  /**
   * Creates a list of {@link CommerceBean}s for the given delegates with the given target class.
   *
   * @param delegates list of delegates {@link AbstractHybrisDocument}
   * @param aClass    target class
   * @param <T>       type of the target {@link CommerceBean} class
   * @return {@link CommerceBean}
   */
  @Nonnull
  protected <T extends CommerceBean> List<T> createBeansFor(@Nonnull List<? extends AbstractHybrisDocument> delegates,
                                                            @Nonnull StoreContext storeContext,
                                                            @Nonnull CommerceBeanType beanType,
                                                            @Nonnull Class<T> aClass) {
    if (delegates.isEmpty()) {
      return Collections.emptyList();
    }

    List<T> beans = delegates.stream()
            .map(delegate -> createBeanFor(delegate, storeContext, beanType, aClass))
            .filter(Objects::nonNull)
            .collect(toList());

    return Collections.unmodifiableList(beans);
  }
}

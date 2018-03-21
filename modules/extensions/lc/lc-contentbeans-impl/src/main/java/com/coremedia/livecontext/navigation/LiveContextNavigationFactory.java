package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.springframework.util.Assert.notNull;

public class LiveContextNavigationFactory {

  private LiveContextNavigationTreeRelation treeRelation;
  private SitesService sitesService;
  private ContentBeanFactory contentBeanFactory;
  private AugmentationService augmentationService;
  private ValidationService<LiveContextNavigation> validationService;

  /**
   * Creates a new live context navigation from the given category.
   * Since the category and therefore the corresponding store is already resolved,
   * we don't need to pass the channel document here.
   *
   * @return If the category is augmented a {@link LiveContextExternalChannelImpl} is returned, otherwise a {@link LiveContextCategoryNavigation}.
   *
   *
   * @param category The category the navigation should be build for.
   */
  @Nonnull
  public LiveContextNavigation createNavigation(@Nonnull Category category, @Nonnull Site site) {
    if (augmentationService != null) {
      Content externalChannelContent = augmentationService.getContent(category);
      LiveContextNavigation externalChannel = (LiveContextNavigation) contentBeanFactory.createBeanFor(externalChannelContent);
      if (null != externalChannel && validationService.validate(externalChannel)) {
        return externalChannel;
      }
    }
    return new LiveContextCategoryNavigation(category, site, treeRelation);
  }

  /**
   * Creates a new LiveContextNavigation by searching a category in the catalog by the given seo segment.
   * The context of the catalog (which shop should contain the seo segment) is resolved from a channel.
   *
   * @param parentChannel the channel to resolve the store context for
   * @param seoSegment the seo segment of the category which should be wrapped in a LiveContextNavigation
   * @return category the category found for given seo segment
   */
  @Nonnull
  public LiveContextNavigation createNavigationBySeoSegment(@Nonnull Content parentChannel, @Nonnull String seoSegment) {
    StoreContext storeContext = getStoreContextProvider().findContextByContent(parentChannel);
    notNull(storeContext, "No StoreContext found for " + parentChannel.getName());

    Category category = getCatalogService().findCategoryBySeoSegment(seoSegment, storeContext);
    notNull(category, "No category found for seo segment: " + seoSegment);

    Site site = sitesService.getContentSiteAspect(parentChannel).findSite()
            .orElseThrow(() -> new IllegalArgumentException("No site found for " + parentChannel));

    return createNavigation(category, site);
  }

  @Nullable
  public CategoryInSite createCategoryInSite(@Nonnull Category category, @Nonnull String siteId) {
    return sitesService.findSite(siteId)
            .map(site -> createCategoryInSite(category, site))
            .orElse(null);
  }

  @Nullable
  public ProductInSite createProductInSite(@Nonnull Product product, @Nonnull String siteId) {
    return sitesService.findSite(siteId)
            .map(site -> createProductInSite(product, site))
            .orElse(null);
  }

  @Nonnull
  public CategoryInSite createCategoryInSite(@Nonnull Category category, @Nonnull Site site) {
    return new CategoryInSiteImpl(category, site);
  }

  @Nonnull
  public ProductInSite createProductInSite(@Nonnull Product product, @Nonnull Site site) {
    return new ProductInSiteImpl(product, site);
  }


  // --- configuration ----------------------------------------------

  @Required
  public void setTreeRelation(LiveContextNavigationTreeRelation treeRelation) {
    this.treeRelation = treeRelation;
  }

  public StoreContextProvider getStoreContextProvider() {
    return CurrentCommerceConnection.get().getStoreContextProvider();
  }

  public CatalogService getCatalogService() {
    return CurrentCommerceConnection.get().getCatalogService();
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Autowired(required = false)
  @Qualifier("categoryAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setValidationService(ValidationService<LiveContextNavigation> validationService) {
    this.validationService = validationService;
  }
}

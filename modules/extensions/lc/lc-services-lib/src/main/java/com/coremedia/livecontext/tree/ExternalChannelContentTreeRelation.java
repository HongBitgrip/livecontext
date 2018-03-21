package com.coremedia.livecontext.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * The live context tree relation resolves content of type CMExternalChannel
 * and applies the matching category subtree and resolves matching contents of type CMExternalChannel
 * <p/>
 * The tree relation only support bottom up lookups.
 * Since CMExternalChannel does not reference any contents as children #getChildrenOf is not implemented.
 */
public class ExternalChannelContentTreeRelation implements TreeRelation<Content> {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalChannelContentTreeRelation.class);

  //local variables to avoid contentbean dependency
  private static final String EXTERNAL_ID = "externalId";
  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  private AugmentationService augmentationService;
  private CommerceTreeRelation commerceTreeRelation;
  private SitesService sitesService;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Override
  public Collection<Content> getChildrenOf(Content parent) {
    throw new UnsupportedOperationException(
            ExternalChannelContentTreeRelation.class.getName() + " only supports bottum up lookups.");
  }

  @Override
  public Content getParentOf(Content child) {
    if (!isApplicable(child)) {
      return null;
    }

    Site site = sitesService.getContentSiteAspect(child).getSite();
    if (site == null) {
      LOG.warn("Content '{}' has no site, cannot determine parent content.", child.getPath());
      return null;
    }

    Optional<CommerceId> childCommerceId = getCommerceIdFrom(child);
    if (!childCommerceId.isPresent()) {
      return null;
    }

    Optional<CommerceConnection> commerceConnectionOpt = commerceConnectionInitializer.findConnectionForSite(site);

    if (!commerceConnectionOpt.isPresent()) {
      LOG.debug("Commerce connection is not available for site '{}'; not looking up parent content.",
              site.getName());
      return null;
    }

    Category childCategory = getCategoryFor(childCommerceId.get());

    if (childCategory != null) {
      Category parentCategory = commerceTreeRelation.getParentOf(childCategory);
      if (parentCategory != null) {
        Content parentContent = getNearestContentForCategory(parentCategory, site);
        if (parentContent != null) {
          return parentContent;
        } else {
          return site.getSiteRootDocument();
        }
      } else if (childCategory.isRoot()) {
        // avoid infinite loop when called with site root document
        Content siteRoot = site.getSiteRootDocument();
        return child.equals(siteRoot) ? null : siteRoot;
      }
    }

    return null;
  }

  @Nullable
  public Content getNearestContentForCategory(@Nullable Category category, @Nullable Site site) {
    if (category == null || site == null) {
      return null;
    }

    Content augmentingContent = null;
    if (augmentationService != null) {
      augmentingContent = augmentationService.getContent(category);
    }

    if (null != augmentingContent) {
      return augmentingContent;
    }

    Category parentCategory = commerceTreeRelation.getParentOf(category);
    if (null != parentCategory) {
      return getNearestContentForCategory(parentCategory, site);
    }

    return null;
  }

  @Override
  public Content getParentUnchecked(Content child) {
    return getParentOf(child);
  }

  @Override
  public List<Content> pathToRoot(Content child) {
    List<Content> path = new ArrayList<>();
    Content parent = child;
    while (parent != null) {
      path.add(parent);
      parent = getParentOf(parent);
    }
    Collections.reverse(path);
    LOG.trace("path to root for {}: {}", child, path);
    return path;
  }

  @Override
  public boolean isRoot(Content item) {
    return getParentOf(item) == null;
  }

  @Override
  public boolean isApplicable(Content item) {
    return item != null && item.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL) && isLinkedCategoryValid(item);
  }

  private boolean isLinkedCategoryValid(Content item) {
    return getCommerceIdFrom(item)
            .map(this::getCategoryFor)
            .map(Objects::nonNull)
            .orElse(false);
  }

  private Optional<CommerceId> getCommerceIdFrom(Content content) {
    String reference = content.getString(EXTERNAL_ID);
    return CommerceIdParserHelper.parseCommerceId(reference);
  }

  @Nullable
  private Category getCategoryFor(@Nonnull CommerceId commerceId){
    CommerceConnection connection = CurrentCommerceConnection.get();
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");
    CommerceBeanFactory commerceBeanFactory = requireNonNull(connection.getCommerceBeanFactory(), "commerce bean factory not available");

    return (Category) commerceBeanFactory.loadBeanFor(commerceId, storeContext);
  }

  @Autowired(required = false)
  @Qualifier("categoryAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Autowired
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

  @Autowired
  public void setCommerceTreeRelation(CommerceTreeRelation commerceTreeRelation) {
    this.commerceTreeRelation = commerceTreeRelation;
  }
}

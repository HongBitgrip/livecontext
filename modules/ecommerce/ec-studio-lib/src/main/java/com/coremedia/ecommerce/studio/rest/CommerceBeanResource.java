package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.content.ContentRepositoryResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.net.URLEncoder;

import static java.text.MessageFormat.format;

/**
 * An abstract catalog object as a RESTful resource.
 */
public abstract class CommerceBeanResource<Entity extends CommerceBean> extends AbstractCatalogResource<Entity> {

  private static final String ID_AND_SITE_PARAM = "{0}&site={1}";

  private AugmentationService augmentationService;
  private SitesService sitesService;

  @Inject
  private ContentRepositoryResource contentRepositoryResource;

  protected void fillRepresentation(@Nonnull CommerceBeanRepresentation representation) {
    CommerceBean entity = getEntity();

    if (entity == null) {
      String errorMessage = String.format("Could not load commerce bean with id '%s'.", getId());
      throw new CatalogBeanNotFoundRestException(errorMessage);
    }

    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
    representation.setCustomAttributes(entity.getCustomAttributes());

    // set preview url
    representation.setPreviewUrl(computePreviewUrl());

    setVisuals(representation, entity);
  }

  @Nonnull
  String computePreviewUrl() {
    String previewControllerUriPattern = getContentRepositoryResource().getPreviewControllerUrlPattern();
    String encodedEntityId = URLEncoder.encode(CommerceIdFormatterHelper.format(getEntity().getId()));

    return formatPreviewUrl(previewControllerUriPattern, encodedEntityId, getSiteId());
  }

  @Nonnull
  public static String formatPreviewUrl(@Nonnull String previewControllerUriPattern, String id, String siteId) {
    // position 0 is reserved for formatted IDs, position 1 is reserved for numeric content IDs
    // the site param is appended to the formatted ID
    String idAndSiteParam = format(ID_AND_SITE_PARAM, id, siteId);
    return format(previewControllerUriPattern, idAndSiteParam);
  }

  public ContentRepositoryResource getContentRepositoryResource() {
    return contentRepositoryResource;
  }

  protected void setVisuals(CommerceBeanRepresentation representation, CommerceBean entity) {
    // get visuals directly via AssetService to avoid fallback to default picture
    AssetService assetService = getConnection().getAssetService();
    if (null != assetService) {
      representation.setVisuals(assetService.findVisuals(entity.getReference(), false));
    }
  }

  @Override
  public void setEntity(Entity entity) {
    setId(entity.getExternalId());
    setCatalogAlias(entity.getId().getCatalogAlias().value());

    StoreContext context = entity.getContext();

    setSiteId(context.getSiteId());
    setWorkspaceId(context.getWorkspaceId());
  }

  /**
   * @return the augmenting content which links to this commerce resource
   */
  @Nullable
  protected Content getContent() {
    if (augmentationService == null) {
      return null;
    }

    Entity entity = getEntity();

    String siteId = entity.getContext().getSiteId();

    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return null;
    }

    return augmentationService.getContent(entity);
  }

  /**
   * Set augmentation service in case the commerce bean can be augmented.
   *
   * @param augmentationService the augmentation service matching the concrete resource type
   */
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }
}

package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.MappedCatalogsProvider;
import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.rest.linking.LinkResolver;
import com.coremedia.rest.linking.LinkResolverUtil;
import com.coremedia.rest.linking.LocationHeaderResourceFilter;
import com.coremedia.rest.linking.RemoteBeanLink;
import com.google.common.collect.Ordering;
import com.sun.jersey.spi.container.ResourceFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * A store {@link com.coremedia.ecommerce.studio.rest.model.Store} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/store/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class StoreResource extends AbstractCatalogResource<Store> {

  private static final Logger LOG = LoggerFactory.getLogger(StoreResource.class);

  private static final String SHOP_URL_PBE_PARAM = "shopUrl";

  private List<PbeShopUrlTargetResolver> pbeShopUrlTargetResolvers;

  @Inject
  private LinkResolver linkResolver;

  @Inject
  private CategoryAugmentationHelper categoryAugmentationHelper;

  @Inject
  private ProductAugmentationHelper productAugmentationHelper;

  @Inject
  private MappedCatalogsProvider mappedCatalogsProvider;

  @PostConstruct
  void initialize() {
    if (pbeShopUrlTargetResolvers == null) {
      pbeShopUrlTargetResolvers = emptyList();
    } else {
      pbeShopUrlTargetResolvers = Ordering.from(AnnotationAwareOrderComparator.INSTANCE)
              .sortedCopy(pbeShopUrlTargetResolvers);
    }
  }

  @POST
  @Path("urlService")
  @Nullable
  public Object handlePost(@Nonnull Map<String, Object> rawJson) {
    String shopUrlStr = (String) rawJson.get(SHOP_URL_PBE_PARAM);
    Object resolved = null;
    if (shopUrlStr != null){
      resolved = findFirstPbeShopUrlTargetResolver(shopUrlStr).orElse(null);
    }

    if (resolved == null) {
      LOG.debug("Shop URL '{}' does not resolve to any known entity, returning null.", shopUrlStr);
    } else {
      LOG.debug("Shop URL '{}' resolves to '{}'.", shopUrlStr, resolved);
    }

    return resolved;
  }

  @Nonnull
  private Optional<Object> findFirstPbeShopUrlTargetResolver(@Nonnull String shopUrlStr) {
    String siteId = getSiteId();

    return pbeShopUrlTargetResolvers.stream()
            .map(resolver -> resolver.resolveUrl(shopUrlStr, siteId))
            .filter(Objects::nonNull)
            .findFirst();
  }

  @POST
  @Path("augment")
  @ResourceFilters(value = {LocationHeaderResourceFilter.class})
  @Nullable
  public Content augment(@Nonnull Map<String, Object> rawJson) {
    Object catalogObject = LinkResolverUtil.resolveJson(rawJson, linkResolver);

    if (catalogObject instanceof Category) {
      return categoryAugmentationHelper.augment((Category) catalogObject);
    } else if (catalogObject instanceof Product) {
      return productAugmentationHelper.augment((Product) catalogObject);
    } else {
      LOG.debug("Cannot augment object {}: only categories are supported. JSON parameters: {}", catalogObject, rawJson);
      return null;
    }
  }

  @Override
  public StoreRepresentation getRepresentation() {
    StoreRepresentation storeRepresentation = new StoreRepresentation();
    fillRepresentation(storeRepresentation);
    return storeRepresentation;
  }

  private void fillRepresentation(@Nonnull StoreRepresentation representation) {
    Store entity = getEntity();

    if (entity == null) {
      throw new StoreBeanNotFoundException(Response.Status.GONE, "Could not load store bean for site with ID '" + getSiteId() + "'.");
    }

    String siteId = getSiteId();

    try {
      CommerceConnection connection = getConnection();

      StoreContext context = entity.getContext();
      List<Catalog> configuredCatalogs = mappedCatalogsProvider.getConfiguredCatalogs(siteId, context);

      representation.setMarketingEnabled(hasMarketingSpots(connection, context));
      representation.setId(entity.getId());
      representation.setVendorUrl(entity.getVendorUrl().orElse(null));
      representation.setVendorName(entity.getVendorName().orElse(null));
      representation.setVendorVersion(entity.getVendorVersion().orElse(null));
      representation.setContext(getStoreContext());
      representation.setMultiCatalog(configuredCatalogs.size() > 1);
      representation.setDefaultCatalog(entity.getDefaultCatalog().orElse(null));
      representation.setCatalogs(configuredCatalogs);
      representation.setRootCategories(configuredCatalogs.stream()
              .map(Catalog::getRootCategory)
              .collect(toList()));
      representation.setRootCategory(RemoteBeanLink.create(rootCategoryUri(connection)));
    } catch (CommerceException e) {
      LOG.warn("Error loading store bean: {} (site: {})", e.getMessage(), siteId);
      throw e;
    }
  }

  private static boolean hasMarketingSpots(@Nonnull CommerceConnection connection, @Nonnull StoreContext context) {
    MarketingSpotService marketingSpotService = connection.getMarketingSpotService();
    return marketingSpotService != null && !marketingSpotService.findMarketingSpots(context).isEmpty();
  }

  @Nonnull
  private String rootCategoryUri(@Nonnull CommerceConnection connection) {
    StoreContext storeContext = connection.getStoreContext();

    String siteId = getSiteId();
    String workspaceId = getWorkspaceId();
    String catalogAlias = storeContext.getCatalogAlias().value();

    //TODO Refactor
    return "livecontext/category/" + siteId + "/" + catalogAlias + "/" + workspaceId + "/" + CategoryResource.ROOT_CATEGORY_ROLE_ID;
  }

  @Override
  protected Store doGetEntity() {
    return new Store(getStoreContext());
  }

  @Override
  public void setEntity(@Nonnull Store store) {
    StoreContext storeContext = store.getContext();

    setSiteId(storeContext.getSiteId());
    setWorkspaceId(storeContext.getWorkspaceId());
  }

  @Autowired(required = false)
  void setPbeShopUrlTargetResolvers(List<PbeShopUrlTargetResolver> pbeShopUrlTargetResolvers) {
    this.pbeShopUrlTargetResolvers = pbeShopUrlTargetResolvers;
  }
}

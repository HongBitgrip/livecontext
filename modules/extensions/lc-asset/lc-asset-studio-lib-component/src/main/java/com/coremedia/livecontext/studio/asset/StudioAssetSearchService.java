package com.coremedia.livecontext.studio.asset;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.asset.AssetSearchService;
import com.coremedia.rest.cap.content.search.SearchService;
import com.coremedia.rest.cap.content.search.SearchServiceResult;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StudioAssetSearchService implements AssetSearchService {

  private static final Logger LOG = LoggerFactory.getLogger(StudioAssetSearchService.class);
  private static final int DEFAULT_CAPACITY = 10000;

  private SearchService searchService;
  private ContentRepository contentRepository;
  private Cache cache;
  private long cacheForInSeconds = 300;

  @Nonnull
  @Override
  public List<Content> searchAssets(@Nonnull String contentType, @Nonnull String externalId, @Nonnull Site site) {
    return cache.get(new SolrQueryCacheKey(contentType, externalId, site, cacheForInSeconds));
  }

  @PostConstruct
  void initialize() {
    long capacity = cache.getCapacity(SolrQueryCacheKey.CACHE_CLASS);
    if(capacity < 10) {
      LOG.info("configuring default capacity {} for cache key {} (must be at least 10 to avoid warnings)", DEFAULT_CAPACITY, SolrQueryCacheKey.CACHE_CLASS);
      cache.setCapacity(SolrQueryCacheKey.CACHE_CLASS, DEFAULT_CAPACITY);
    }
  }

  @Nonnull
  private List<Content> doSearch(@Nonnull String contentType, @Nonnull String externalId, @Nonnull Site site) {

    ImmutableList<String> none = ImmutableList.of();

    SearchServiceResult result = searchService.search(
    /* query */              "",
    /* limit */              -1,
    /* sortCriteria */       none,
    /* folder */             site.getSiteRootFolder(), true,
    /* contentTypes */       contentTypes(contentType), true,
    /* filterQueries */      ImmutableList.of("commerceitems:\"" + externalId + '"', "isdeleted:false"),
    /* facetFieldCriteria */ none,
    /* facetQueries */       none
    );
    return result.getHits();
  }

  @Nonnull
  private ImmutableList<ContentType> contentTypes(@Nonnull String contentType) {
    ImmutableList<ContentType> contentTypes = ImmutableList.of();
    ContentType ct = contentRepository.getContentType(contentType);
    if (ct != null) {
      contentTypes = ImmutableList.of(ct);
    }
    return contentTypes;
  }

  @Required
  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  public void setCacheForInSeconds(long cacheForInSeconds) {
    this.cacheForInSeconds = cacheForInSeconds;
  }

  private class SolrQueryCacheKey extends CacheKey<List<Content>> {

    private static final String CACHE_CLASS = "com.coremedia.livecontext.studio.asset.StudioAssetSearchService.SolrQueryCacheKey";

    private final long cacheForInSeconds;
    private final String contentType;
    private final String externalId;
    private final Site site;

    // redundant, only for efficiency
    private final String myEqualsValue;

    private final Object uncacheableDependency = new Object();

    SolrQueryCacheKey(String contentType, String externalId, Site site, long cacheForInSeconds) {
      this.contentType = contentType;
      this.externalId = externalId;
      this.site = site;
      this.cacheForInSeconds = cacheForInSeconds;
      myEqualsValue = contentType+externalId+site.getId()+cacheForInSeconds;
    }

    @Override
    public String cacheClass(Cache cache, List<Content> value) {
      return CACHE_CLASS;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SolrQueryCacheKey that = (SolrQueryCacheKey) o;
      return myEqualsValue.equals(that.myEqualsValue);
    }

    @Override
    public int hashCode() {
      return 29 + myEqualsValue.hashCode();
    }

    @Override
    public List<Content> evaluate(Cache cache) {
      List<Content> result = null;
      try {
        Cache.disableDependencies();
        result = doSearch(contentType, externalId, site);
      } finally {
        Cache.enableDependencies();
      }

      if (cacheForInSeconds > 0) {
        LOG.debug("Caching for {} s", cacheForInSeconds);
        Cache.cacheFor(cacheForInSeconds, TimeUnit.SECONDS);
      } else {
        LOG.warn("Asset query has unreasonable cache time: {} and will not be cached", cacheForInSeconds);
        Cache.dependencyOn(uncacheableDependency);
        cache.invalidate(uncacheableDependency);
      }
      return result;
    }
  }

}

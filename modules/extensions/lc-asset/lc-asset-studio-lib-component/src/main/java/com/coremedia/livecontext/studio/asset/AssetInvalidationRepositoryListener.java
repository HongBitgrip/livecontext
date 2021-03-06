package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMVideo;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.cap.content.events.ContentRepositoryListenerBase;
import com.coremedia.ecommerce.studio.rest.cache.CommerceCacheInvalidationSource;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider.commerceId;
import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;

/**
 * A {@link com.coremedia.cap.content.events.ContentRepositoryListener}
 * which invalidates commerce remote beans in the studio associated with assets in the repository.
 * The invalidation is triggered by atomic content events like creation, deletion etc.
 * Invalidation triggered by property change events are handled by
 * {@link AssetInvalidationWriteInterceptor} and
 * {@link AssetInvalidationWritePostProcessor}
 */
class AssetInvalidationRepositoryListener extends ContentRepositoryListenerBase implements SmartLifecycle {

  private static final String ANY = "any";
  private static final Set<String> INVALIDATION_ALL_REFERENCES = ImmutableSet.of(
          format(commerceId(Vendor.of(ANY), CATEGORY).withTechId(ANY).build()),
          format(commerceId(Vendor.of(ANY), PRODUCT).withTechId(ANY).build()),
          format(commerceId(Vendor.of(ANY), SKU).withTechId(ANY).build())
  );

  private static final Set<String> EVENT_WHITELIST = ImmutableSet.of(
          ContentEvent.CONTENT_CREATED,
          ContentEvent.CONTENT_DELETED,
          ContentEvent.CONTENT_MOVED,
          ContentEvent.CONTENT_RENAMED,
          ContentEvent.CONTENT_REVERTED,
          ContentEvent.CONTENT_UNDELETED
  );

  private final AtomicBoolean running = new AtomicBoolean(false);

  private CommerceCacheInvalidationSource commerceCacheInvalidationSource;
  private ContentRepository repository;

  @Override
  protected void handleContentEvent(@Nonnull ContentEvent event) {
    if (!EVENT_WHITELIST.contains(event.getType())) {
      return;
    }

    Content content = event.getContent();
    if (content == null || content.isDestroyed() || !isRelevantType(content)) {
      return;
    }

    commerceCacheInvalidationSource.invalidateReferences(getReferences(event, content));
  }

  @Nonnull
  private Collection<String> getReferences(@Nonnull ContentEvent event, @Nonnull Content content) {
    if (event.getType().equals(ContentEvent.CONTENT_REVERTED)) {
      //when a content ist reverted we don't know the old external references.
      // So we have to invalidate all relevant catalog types
      return INVALIDATION_ALL_REFERENCES;
    }
    return CommerceReferenceHelper.getExternalReferences(content);
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    stop();
    callback.run();
  }

  @Override
  public void start() {
    if (!running.getAndSet(true)) {
      repository.addContentRepositoryListener(this);
    }
  }

  @Override
  public void stop() {
    if (running.getAndSet(false)) {
      repository.removeContentRepositoryListener(this);
    }
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public int getPhase() {
    return 0;
  }

  @Autowired
  public void setCommerceCacheInvalidationSource(CommerceCacheInvalidationSource commerceCacheInvalidationSource) {
    this.commerceCacheInvalidationSource = commerceCacheInvalidationSource;
  }

  @Autowired
  public void setRepository(ContentRepository repository) {
    this.repository = repository;
  }

  /**
   * @param content
   * @return true if the content is a picture, video or a download or one of their subtypes.
   */
  private static boolean isRelevantType(@Nonnull Content content) {
    ContentType contentType = content.getType();

    return contentType.isSubtypeOf(CMPicture.NAME) ||
            contentType.isSubtypeOf(CMVideo.NAME) ||
            contentType.isSubtypeOf(CMDownload.NAME);
  }
}

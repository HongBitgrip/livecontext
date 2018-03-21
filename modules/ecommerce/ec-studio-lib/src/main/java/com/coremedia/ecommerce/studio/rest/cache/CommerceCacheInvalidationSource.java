package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.ecommerce.studio.rest.model.Marketing;
import com.coremedia.ecommerce.studio.rest.model.Segments;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.event.InvalidationPropagator;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.rest.invalidations.SimpleInvalidationSource;
import com.coremedia.rest.linking.Linker;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static com.coremedia.ecommerce.studio.rest.cache.CommerceBeanDelegateProvider.createStoreContext;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

/**
 * Invalidates studio commerce remote beans based on incoming {@link InvalidationEvent} events.
 */
public class CommerceCacheInvalidationSource extends SimpleInvalidationSource implements InvalidationPropagator {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceCacheInvalidationSource.class);
  private static final String INVALIDATE_ALL_URI_PATTERN = "livecontext/{suffix:.*}";

  private static final long DELAY = 1000L;
  private static final String DELIMITER = "/";

  private TaskScheduler taskScheduler;
  private Linker linker;
  private SettingsService settingsService;

  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();
    if (null == taskScheduler) {
      LOG.info("creating single threaded task scheduler for delayed invalidations");
      taskScheduler = new ConcurrentTaskScheduler();
    }
  }

  @Override
  public void invalidate(@Nonnull List<InvalidationEvent> invalidations) {
    Optional<InvalidationEvent> clearAll = invalidations.stream()
            .filter(e -> InvalidationEvent.CLEAR_ALL_EVENT.equals(e.getContentType()))
            .findFirst();
    if (clearAll.isPresent()) {
      // Individual remote beans that need to be invalidated cannot be identified.
      // Invalidate all commerce remote beans according to the given pattern.
      addInvalidations(Collections.singleton(INVALIDATE_ALL_URI_PATTERN));
      return;
    }

    // no clear all found: let's create resource URIs for all invalidation events
    Set<String> toBeInvalidated = invalidations.stream()
            .flatMap(this::addUriToInvalidate)
            .collect(toSet());

    addInvalidations(toBeInvalidated);
  }

  @Nonnull
  private Stream<String> addUriToInvalidate(@Nonnull InvalidationEvent invalidation) {
    Optional<String> contentType = Optional.ofNullable(invalidation.getContentType());
    return contentType.flatMap(InvalidationEventTypeMapping::get)
            .map(this::link)
            .orElse(empty());
  }

  @Nonnull
  private Stream<String> link(@Nonnull Class<? extends CommerceBean> aClass) {
    String entityLink = createLink(aClass);

    if (MarketingSpot.class.equals(aClass)) {
      Marketing resourceObject = new Marketing(createStoreContext());
      return of(entityLink, createLink(resourceObject));
    } else if (Segment.class.equals(aClass)) {
      Segments resourceObject = new Segments(createStoreContext());
      return of(entityLink, createLink(resourceObject));
    }
    return of(entityLink);
  }

  @Nonnull
  private String createLink(@Nonnull Class<? extends CommerceBean> aClass) {
    Object commerceBeanProxy = settingsService.createProxy(aClass, CommerceBeanDelegateProvider.get());
    return CommerceBeanDelegateProvider.postProcess(createLink(commerceBeanProxy));
  }

  @Nonnull
  private String createLink(@Nonnull Object resourceObject) {
    return urlDecode(linker.link(resourceObject));
  }

  @Nonnull
  private static String urlDecode(@Nonnull URI commerceBeanUri) {
    try {
      return URLDecoder.decode(commerceBeanUri.toString(), StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Unable to decode '" + commerceBeanUri + "'", e);
    }
  }

  public void invalidateReferences(@Nonnull Collection<String> references) {
    Set<String> changes = references.stream()
            // convert to commerce id and retrieve external id
            .map(this::buildLinkFromReference)
            .flatMap(Streams::stream)
            .collect(Collectors.toSet());

    triggerDelayedInvalidation(changes);
  }

  @Nonnull
  private Optional<String> buildLinkFromReference(@Nonnull String commerceId) {
    return parseCommerceId(commerceId).flatMap(this::toCommerceBeanUri);
  }

  /**
   * Creates the entity resource URI with matching ID if commerce id contains a part number (external id). Otherwise,
   * all entities of the commerce id's bean type are invalidated.
   */
  @Nonnull
  public Optional<String> toCommerceBeanUri(@Nonnull CommerceId commerceId) {
    String commerceBeanTypeString = commerceId.getCommerceBeanType().type();
    Optional<Class<? extends CommerceBean>> beanTypeOptional = InvalidationEventTypeMapping.get(commerceBeanTypeString);
    return beanTypeOptional
            .map(this::createLink)
            .map(s -> CommerceBeanDelegateProvider.forEncodedExternalId(s, commerceId));
  }

  /**
   * Trigger studio resource invalidation with 1s delay due to possible race conditions.
   * Example: The computation of the catalog picture by the cae might not has been finished, when the invalidation is triggered.
   * Studio might show an outdated product picture.
   */
  private void triggerDelayedInvalidation(@Nonnull final Set<String> invalidations) {
    if (invalidations.isEmpty()) {
      // Do not schedule a task if there is nothing to invalidate.
      return;
    }

    Runnable addInvalidations = () -> addInvalidations(invalidations);

    Date startTime = new Date(System.currentTimeMillis() + DELAY);

    taskScheduler.schedule(addInvalidations, startTime);
  }

  @Autowired(required = false)
  void setTaskScheduler(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  @Autowired
  void setLinker(Linker linker) {
    this.linker = linker;
  }

  @Autowired
  void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }
}

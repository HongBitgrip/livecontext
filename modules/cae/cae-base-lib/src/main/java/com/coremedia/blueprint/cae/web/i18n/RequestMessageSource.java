package com.coremedia.blueprint.cae.web.i18n;

import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * A message source implementation that delegates to a message source that has probably been
 * {@link #setMessageSource(org.springframework.context.MessageSource, javax.servlet.http.HttpServletRequest) stored} as
 * a servlet request attributes. If no such source is available or if the message couldn't be resolved, then
 * a {@link #setParentMessageSource(org.springframework.context.MessageSource) parent source} is used.
 */
public class RequestMessageSource implements HierarchicalMessageSource {

  public static final String MESSAGESOURCE_ATTRIBUTE = MessageSource.class.getName();

  private MessageSource parent;

  /**
   * Stores a message source as a servlet request attribute
   *
   * @param source  The message source
   * @param request the request
   */
  public static void setMessageSource(MessageSource source, HttpServletRequest request) {
    request.setAttribute(MESSAGESOURCE_ATTRIBUTE, source);
  }

  @Override
  public void setParentMessageSource(MessageSource parent) {
    this.parent = parent;
  }

  @Override
  public MessageSource getParentMessageSource() {
    return parent;
  }

  @Override
  public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
    String result;
    MessageSource requestMessageSource = getRequestMessageSource();

    // Wait for Java 9's `Optional#or(Supplier<Optional>)` to become
    // available before refactoring this into an `Optional` chain.
    result = getMessageOrNull(requestMessageSource, code, args, locale);
    result = result != null ? result : getMessageOrNull(parent, code, args, locale);

    if (result == null) {
      if (requestMessageSource != null) {
        return requestMessageSource.getMessage(code, args, defaultMessage, locale);
      } else if (parent != null) {
        return parent.getMessage(code, args, defaultMessage, locale);
      } else {
        MessageFormat format = new MessageFormat(defaultMessage != null ? defaultMessage : "", locale);
        return format.format(args);
      }
    }

    return result;
  }

  @Override
  public String getMessage(String code, Object[] args, Locale locale) {
    String result;
    MessageSource requestMessageSource = getRequestMessageSource();

    // Wait for Java 9's `Optional#or(Supplier<Optional>)` to become
    // available before refactoring this into an `Optional` chain.
    result = getMessageOrNull(requestMessageSource, code, args, locale);
    result = result != null ? result : getMessageOrNull(parent, code, args, locale);

    if (result == null) {
      throw new NoSuchMessageException(code, locale);
    }

    return result;
  }

  @Override
  public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
    String result;
    MessageSource requestMessageSource = getRequestMessageSource();
    String defaultMessage = resolvable.getDefaultMessage();

    // Wait for Java 9's `Optional#or(Supplier<Optional>)` to become
    // available before refactoring this into an `Optional` chain.
    result = getMessageOrNull(requestMessageSource, resolvable, locale);
    result = result != null ? result : getMessageOrNull(parent, resolvable, locale);
    result = result != null ? result : defaultMessage;

    if (result == null) {
      String firstCode = getFirstCode(resolvable);
      throw new NoSuchMessageException(firstCode != null ? firstCode : "", locale);
    }

    return result;
  }

  /**
   * Provides the message source that has been stored in the request
   *
   * @return The message source or null if none available
   */
  @Nullable
  private static MessageSource getRequestMessageSource() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      return null;
    }

    return (MessageSource) attributes.getAttribute(MESSAGESOURCE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
  }

  @Nullable
  private static String getMessageOrNull(@Nullable MessageSource source, @Nonnull String code, @Nullable Object[] args,
                                         @Nonnull Locale locale) {
    if (source == null) {
      return null;
    }

    try {
      return source.getMessage(code, args, locale);
    } catch (NoSuchMessageException e) {
      return null;
    }
  }

  @Nullable
  private static String getMessageOrNull(@Nullable MessageSource source, @Nonnull MessageSourceResolvable resolvable,
                                         @Nonnull Locale locale) {
    if (source == null) {
      return null;
    }

    try {
      return source.getMessage(resolvable, locale);
    } catch (NoSuchMessageException e) {
      return null;
    }
  }

  @Nullable
  private static String getFirstCode(@Nonnull MessageSourceResolvable resolvable) {
    String[] codes = resolvable.getCodes();

    if (codes == null || codes.length == 0) {
      return null;
    }

    return codes[0];
  }
}

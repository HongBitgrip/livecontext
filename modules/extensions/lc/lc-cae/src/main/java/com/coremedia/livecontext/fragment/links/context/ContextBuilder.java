package com.coremedia.livecontext.fragment.links.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder for creating {@link Context} instances like <code>ContextBuilder.create().withValue("key", value).build()</code>.
 */
public final class ContextBuilder {

  public static final String CONTEXT_KEY_PREFIX = "wc.";

  private MapContext context = new MapContext();

  private ContextBuilder() {}

  /**
   * Creates a new builder instance
   */
  public static ContextBuilder create() {
    return new ContextBuilder();
  }

  /**
   * Adds a single  value to the context
   */
  public ContextBuilder withValue(String key, Object value) {
    this.context.put(key, value);
    return this;
  }

  /**
   * @return Provides the (final) context instance
   */
  public Context build() {
    return context;
  }

  // =========

  private static class MapContext implements Context {
    private Map<String, Object> context = new HashMap<>();

    @Override
    public Collection<String> getContextNames() {
      return context.keySet();
    }

    @Override
    public Object get(String name) {
      name = name != null ? name.toLowerCase() : null;

      if (name != null && !name.startsWith(CONTEXT_KEY_PREFIX)){
        return context.get(CONTEXT_KEY_PREFIX + name);
      } else {
        return context.get(name);
      }
    }

    @Override
    public void put(String name, Object value) {
      name = name != null ? name.toLowerCase() : null;

      if (name != null && !name.startsWith(CONTEXT_KEY_PREFIX)){
        context.put(CONTEXT_KEY_PREFIX + name, value);
      } else {
        context.put(name, value);
      }
    }

    @Override
    public void remove(String name) {
      name = name != null ? name.toLowerCase() : null;

      if (context.containsKey(name)) {
        context.remove(name);
      }
    }

    @Override
    public String toString() {
      return "Context["+context+"]";
    }
  }
}

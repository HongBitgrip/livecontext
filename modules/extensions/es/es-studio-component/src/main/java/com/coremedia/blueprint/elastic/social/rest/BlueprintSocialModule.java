package com.coremedia.blueprint.elastic.social.rest;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.module.SimpleModule;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

@Named
public class BlueprintSocialModule extends SimpleModule {
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")

  private Collection<JsonSerializer<?>> jsonSerializers;

  @Inject
  public BlueprintSocialModule(Collection<JsonSerializer<?>> jsonSerializers) {
    super("BlueprintSocial", new Version(1, 0, 0, null));
    this.jsonSerializers = jsonSerializers;
  }

  @Override
  public void setupModule(final SetupContext context) {
    for (final JsonSerializer<?> jsonSerializer : jsonSerializers) {
      if (jsonSerializer.getClass().getPackage().equals(BlueprintSocialModule.class.getPackage()) ) {
        addSerializer(jsonSerializer);
      }
    }
    super.setupModule(context);
  }
}

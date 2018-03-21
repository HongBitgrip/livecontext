package com.coremedia.lc.studio.lib.rest;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class LCSocialModule extends SimpleModule {

  @Inject
  private ProductInSiteJsonSerializer productInSiteJsonSerializer;

  public LCSocialModule() {
    super("LCSocial", new Version(1, 0, 0, null));
  }

  @Override
  public void setupModule(final SetupContext context) {
    addSerializer(productInSiteJsonSerializer);
    super.setupModule(context);
  }
}

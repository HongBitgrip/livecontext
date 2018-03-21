package com.coremedia.blueprint.elastic.social.demousers;

import com.coremedia.elastic.core.api.tenant.TenantServiceListenerBase;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

@Named
class DemoUserCreator extends TenantServiceListenerBase {

  @Inject
  Provider<DemoUserCreationService> provider;

  @Override
  public void onTenantRegistered(String tenant) {
    provider.get().createDemoUsersFor(tenant);
  }
}

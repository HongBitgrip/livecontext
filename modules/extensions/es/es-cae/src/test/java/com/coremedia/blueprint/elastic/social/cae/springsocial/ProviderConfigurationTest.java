package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.api.tenant.TenantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ProviderConfigurationTest {

  @InjectMocks
  private TestProviderConfiguration providerConfiguration;
  
  @Mock
  private TenantService tenantService;
  
  @Mock
  private Site nav1;

  @Mock
  private SettingsService settingsService;
  
  private final Map<String, Object> map = of(
          "tenant", (Object) "media",
          "facebook.clientId", "clientId",
          "facebook.clientSecret", "clientSecret",
          "twitter.consumerKey", "consumerKey",
          "twitter.consumerSecret", "consumerSecret"
  );
  
  @Before
  public void setup() {
    when(tenantService.getCurrent()).thenReturn("media");
    when(settingsService.settingAsMap("elasticSocial", String.class, Object.class, nav1)).thenReturn(map);
  }
  
  @Test
  public void getFacebookClientId() {
    String currentFacebookClientId = providerConfiguration.getCurrentFacebookClientId("");
    assertEquals("clientId", currentFacebookClientId);
  }

  @Test
  public void getFacebookClientSecret() {
    String currentFacebookClientId = providerConfiguration.getCurrentFacebookClientSecret("");
    assertEquals("clientSecret", currentFacebookClientId);
  }

  @Test
  public void getTwitterConsumerKey() {
    String currentTwitterConsumerKey = providerConfiguration.getCurrentTwitterConsumerKey("");
    assertEquals("consumerKey", currentTwitterConsumerKey);
  }

  @Test
  public void getTwitterConsumerSecret() {
    String currentTwitterConsumerSecret = providerConfiguration.getCurrentTwitterConsumerSecret("");
    assertEquals("consumerSecret", currentTwitterConsumerSecret);
  }

  public static class TestProviderConfiguration extends ProviderConfiguration {

    private Site nav1;

    public void setNav1(Site nav1) {
      this.nav1 = nav1;
    }

    @Override
    protected Site getCurrentNavigation() {
      return nav1;
    }
  }
}

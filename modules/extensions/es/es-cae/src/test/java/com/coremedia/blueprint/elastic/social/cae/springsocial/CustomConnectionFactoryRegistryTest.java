package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.twitter.api.Twitter;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomConnectionFactoryRegistryTest {

  private CustomConnectionFactoryRegistry registry;

  @Mock
  private ProviderConfiguration providerConfiguration;

  @Test
  public void supportedProviders() {
    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    assertTrue(registry.registeredProviderIds().contains("facebook"));
    assertTrue(registry.registeredProviderIds().contains("twitter"));
  }
  
  @Test
  public void facebookConnection() {
    when(providerConfiguration.getCurrentFacebookClientId("")).thenReturn("clientId");
    when(providerConfiguration.getCurrentFacebookClientSecret("")).thenReturn("clientSecret");
    
    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    ConnectionFactory<?> connectionFactory = registry.getConnectionFactory("facebook");
    ConnectionFactory<?> newConnectionFactory = registry.getConnectionFactory("facebook");

    assertNotNull(connectionFactory);
    assertEquals("facebook", connectionFactory.getProviderId());
    assertSame(connectionFactory, newConnectionFactory);
  }

  @Test (expected = IllegalArgumentException.class)
  public void facebookConnectionWithMissingConfigurationId() {
    when(providerConfiguration.getCurrentFacebookClientId("")).thenReturn(null);
    when(providerConfiguration.getCurrentFacebookClientSecret("")).thenReturn("clientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory("facebook");
  }

  @Test (expected = IllegalArgumentException.class)
  public void facebookConnectionWithMissingConfigurationSecret() {
    when(providerConfiguration.getCurrentFacebookClientId("")).thenReturn("clientId");
    when(providerConfiguration.getCurrentFacebookClientSecret("")).thenReturn(null);

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory("facebook");
  }

  @Test
  public void facebookConnectionByType() {
    when(providerConfiguration.getCurrentFacebookClientId("")).thenReturn("clientId");
    when(providerConfiguration.getCurrentFacebookClientSecret("")).thenReturn("clientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    ConnectionFactory<?> connectionFactory = registry.getConnectionFactory(Facebook.class);
    ConnectionFactory<?> newConnectionFactory = registry.getConnectionFactory(Facebook.class);
    ConnectionFactory<?> newConnectionFactoryById = registry.getConnectionFactory("facebook");

    assertNotNull(connectionFactory);
    assertEquals("facebook", connectionFactory.getProviderId());
    assertSame(connectionFactory, newConnectionFactory);
    assertSame(connectionFactory, newConnectionFactoryById);
  }  
  
  @Test
  public void changedFacebookConnection() {
    when(providerConfiguration.getCurrentFacebookClientId("")).thenReturn("clientId").thenReturn("newClientId");
    when(providerConfiguration.getCurrentFacebookClientSecret("")).thenReturn("clientSecret").thenReturn("newClientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);

    ConnectionFactory<?> connectionFactory = registry.getConnectionFactory("facebook");
    ConnectionFactory<?> newConnectionFactory = registry.getConnectionFactory("facebook");

    assertNotNull(connectionFactory);
    assertEquals("facebook", connectionFactory.getProviderId());
    assertNotNull(newConnectionFactory);
    assertEquals("facebook", newConnectionFactory.getProviderId());

    assertNotSame(connectionFactory, newConnectionFactory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void facebookConnectionWithoutId() {
    when(providerConfiguration.getCurrentFacebookClientId("")).thenReturn(null);
    when(providerConfiguration.getCurrentFacebookClientSecret("")).thenReturn("clientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory("facebook");
  }

  @Test(expected = IllegalArgumentException.class)
  public void facebookConnectionWithoutSecret() {
    when(providerConfiguration.getCurrentFacebookClientId("")).thenReturn("clientId");
    when(providerConfiguration.getCurrentFacebookClientSecret("")).thenReturn(null);

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory("facebook");
  }

  @Test
  public void twitterConnection() {
    when(providerConfiguration.getCurrentTwitterConsumerKey("")).thenReturn("clientId");
    when(providerConfiguration.getCurrentTwitterConsumerSecret("")).thenReturn("clientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);

    ConnectionFactory<?> connectionFactory = registry.getConnectionFactory("twitter");
    ConnectionFactory<?> newConnectionFactory = registry.getConnectionFactory("twitter");

    assertNotNull(connectionFactory);
    assertEquals("twitter", connectionFactory.getProviderId());
    assertSame(connectionFactory, newConnectionFactory);
  }

  @Test
  public void twitterConnectionByType() {
    when(providerConfiguration.getCurrentTwitterConsumerKey("")).thenReturn("clientId");
    when(providerConfiguration.getCurrentTwitterConsumerSecret("")).thenReturn("clientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);

    ConnectionFactory<?> connectionFactory = registry.getConnectionFactory(Twitter.class);
    ConnectionFactory<?> newConnectionFactory = registry.getConnectionFactory(Twitter.class);
    ConnectionFactory<?> newConnectionFactoryById = registry.getConnectionFactory("twitter");


    assertNotNull(connectionFactory);
    assertEquals("twitter", connectionFactory.getProviderId());
    assertSame(connectionFactory, newConnectionFactory);
    assertSame(connectionFactory, newConnectionFactoryById);
  }

  @Test
  public void changedTwitterConnection() {
    when(providerConfiguration.getCurrentTwitterConsumerKey("")).thenReturn("clientId").thenReturn("newClientId");
    when(providerConfiguration.getCurrentTwitterConsumerSecret("")).thenReturn("clientSecret").thenReturn("newClientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);

    ConnectionFactory<?> connectionFactory = registry.getConnectionFactory("twitter");
    ConnectionFactory<?> newConnectionFactory = registry.getConnectionFactory("twitter");

    assertNotNull(connectionFactory);
    assertEquals("twitter", connectionFactory.getProviderId());
    assertNotNull(newConnectionFactory);
    assertEquals("twitter", newConnectionFactory.getProviderId());

    assertNotSame(connectionFactory, newConnectionFactory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void twitterConnectionWithoutId() {
    when(providerConfiguration.getCurrentTwitterConsumerKey("")).thenReturn(null);
    when(providerConfiguration.getCurrentTwitterConsumerSecret("")).thenReturn("clientSecret");

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory("twitter");
  }

  @Test(expected = IllegalArgumentException.class)
  public void twitterConnectionWithoutSecret() {
    when(providerConfiguration.getCurrentTwitterConsumerKey("")).thenReturn("clientId");
    when(providerConfiguration.getCurrentTwitterConsumerSecret("")).thenReturn(null);

    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory("twitter");
  }

  @Test(expected = IllegalArgumentException.class)
  public void unsupportedFactory() {
    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory("unknown");
  }

  @Test(expected = IllegalArgumentException.class)
  public void unsupportedType() {
    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.getConnectionFactory(Object.class);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void addConnectionFactory() {
    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.addConnectionFactory(new FacebookConnectionFactory("id", "secret"));
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void setConnectionFactories() {
    registry = new CustomConnectionFactoryRegistry(providerConfiguration);
    registry.setConnectionFactories(Collections.singletonList(new FacebookConnectionFactory("id", "secret")));
  }

}

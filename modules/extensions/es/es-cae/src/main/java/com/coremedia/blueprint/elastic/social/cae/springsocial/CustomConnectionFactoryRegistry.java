package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookAdapter;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterAdapter;
import org.springframework.social.twitter.connect.TwitterServiceProvider;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;

public class CustomConnectionFactoryRegistry implements ConnectionFactoryLocator {

  private ConnectionFactory<Facebook> facebookConnectionFactory;
  private ConnectionFactory<Twitter> twitterConnectionFactory;

  private String currentFacebookClientId;
  private String currentFacebookClientSecret;
  private String currentTwitterClientId;
  private String currentTwitterClientSecret;

  private ProviderConfiguration providerConfiguration;

  private static final String FACEBOOK_PROVIDER_ID = "facebook";
  private static final String TWITTER_PROVIDER_ID = "twitter";
  
  private static final Class FACEBOOK_API_TYPE = Facebook.class;
  private static final Class TWITTER_API_TYPE = Twitter.class;

  private ApiAdapter<Facebook> facebookAdapter;
  private ApiAdapter<Twitter> twitterAdapter;
  
  private Set<String> supportedProviderIds = ImmutableSet.of(FACEBOOK_PROVIDER_ID, TWITTER_PROVIDER_ID);
  

  public CustomConnectionFactoryRegistry(ProviderConfiguration providerConfiguration) {
    this.providerConfiguration = providerConfiguration;
    facebookAdapter = new FacebookAdapter();
    twitterAdapter = new TwitterAdapter();
  }

  public void addConnectionFactory(ConnectionFactory<?> connectionFactory) {
    throw new UnsupportedOperationException("addConnectionFactory not supported");
  }

  public void setConnectionFactories(List<FacebookConnectionFactory> connectionFactories) {
    throw new UnsupportedOperationException("setConnectionFactories not supported");
  }

  @Override
  public ConnectionFactory<?> getConnectionFactory(String providerId) {
    String provider = providerId;
    String tenant = "";
    if (StringUtils.isNotBlank(providerId)) {
      String[] splitted = StringUtils.split(providerId, "_");
      provider = splitted[0];
      if (splitted.length > 1) {
        tenant = splitted[1];
      }
    }
    if (FACEBOOK_PROVIDER_ID.equals(provider)) {
      return getFacebookConnectionFactory(tenant);
    } else if (TWITTER_PROVIDER_ID.equals(providerId)) {
      return getTwitterConnectionFactory(tenant);
    }
    
    throw new IllegalArgumentException(format("Cannot find a ConnectionFactory for provider %s", providerId));
  }

  private ConnectionFactory<?> getFacebookConnectionFactory(String tenant) {

    String facebookClientId = providerConfiguration.getCurrentFacebookClientId(tenant);
    String facebookClientSecret = providerConfiguration.getCurrentFacebookClientSecret(tenant);
    String facebookClientNamespace = providerConfiguration.getCurrentFacebookClientNamespace(tenant);

    if (StringUtils.isBlank(facebookClientId) || StringUtils.isBlank(facebookClientSecret)) {
      throw new IllegalArgumentException("Facebook is not setup correctly");
    }

    if (StringUtils.isBlank(facebookClientNamespace)) {
      facebookClientNamespace = "";
    }

    if (!StringUtils.equals(facebookClientId, currentFacebookClientId)
            || !StringUtils.equals(facebookClientSecret, currentFacebookClientSecret)) {
      currentFacebookClientId = facebookClientId;
      currentFacebookClientSecret = facebookClientSecret;

      facebookConnectionFactory = new OAuth2ConnectionFactory<>(FACEBOOK_PROVIDER_ID,
              new FacebookServiceProvider(facebookClientId, facebookClientSecret, facebookClientNamespace), facebookAdapter);
    }

    return facebookConnectionFactory;
  }

  private ConnectionFactory<?> getTwitterConnectionFactory(String tenant) {

    String twitterClientId = providerConfiguration.getCurrentTwitterConsumerKey(tenant);
    String twitterClientSecret = providerConfiguration.getCurrentTwitterConsumerSecret(tenant);

    if (StringUtils.isEmpty(twitterClientId) || StringUtils.isEmpty(twitterClientSecret)) {
      throw new IllegalArgumentException("Twitter is not setup correctly");
    }

    if (!StringUtils.equals(twitterClientId, currentTwitterClientId)
            || !StringUtils.equals(twitterClientSecret, currentTwitterClientSecret)) {
      currentTwitterClientId = twitterClientId;
      currentTwitterClientSecret = twitterClientSecret;

      twitterConnectionFactory = new OAuth1ConnectionFactory<>(TWITTER_PROVIDER_ID,
              new TwitterServiceProvider(twitterClientId, twitterClientSecret), twitterAdapter);
    }

    return twitterConnectionFactory;
  }

  @Override
  public <A> ConnectionFactory<A> getConnectionFactory(Class<A> apiType) {
    if (FACEBOOK_API_TYPE.equals(apiType)) {
      //noinspection unchecked
      return (ConnectionFactory<A>) getFacebookConnectionFactory("");
    } else if (TWITTER_API_TYPE.equals(apiType)) {
      //noinspection unchecked
      return (ConnectionFactory<A>) getTwitterConnectionFactory("");
    }
    throw new IllegalArgumentException(format("Cannot find a ConnectionFactory for type %s", apiType));

  }

  @Override
  public Set<String> registeredProviderIds() {
    return supportedProviderIds;
  }
}

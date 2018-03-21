package com.coremedia.blueprint.lc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class BetamaxTestHelper {

  private static final Logger LOG = LoggerFactory.getLogger(BetamaxTestHelper.class);

  private static final String JAVA_SYSTEM_PROPERTY_HTTP_PROXY_PORT = "betamax.proxyPort";
  private static final String BETAMAX_PROPERTIES_PATH = "/betamax.properties";

  private BetamaxTestHelper() {
  }

  /**
   * Return `true` if Betamax tapes <em>should</em> be used.
   */
  public static boolean useBetamaxTapes() {
    return !ignoreBetamaxTapes();
  }

  /**
   * Return `true` if Betamax tapes should <em>not</em> be used.
   */
  private static boolean ignoreBetamaxTapes() {
    return "*".equals(getIgnoreHosts());
  }

  /**
   * Return the system property-configured hosts that should be ignored by Betamax.
   */
  @Nullable
  private static Object getIgnoreHosts() {
    return System.getProperties().get("betamax.ignoreHosts");
  }

  public static Properties updateSystemPropertiesWithBetamaxConfig() {
    Properties properties = loadPropertiesFromFile(BETAMAX_PROPERTIES_PATH);
    properties.put(JAVA_SYSTEM_PROPERTY_HTTP_PROXY_PORT, Integer.toString(getAvailableTcpPort()));

    mergeIntoSystemProperties(properties);

    return System.getProperties();
  }

  @Nonnull
  private static Properties loadPropertiesFromFile(@Nonnull String filename) {
    Properties properties = new Properties();

    InputStream inputStream = BetamaxTestHelper.class.getResourceAsStream(filename);
    if (inputStream != null) {
      try {
        properties.load(inputStream);
      } catch (IOException e) {
        LOG.error("Could not load properties below path {}", BETAMAX_PROPERTIES_PATH, e);
      }
    } else {
      LOG.error("Could not load properties below path: {}", BETAMAX_PROPERTIES_PATH);
    }

    return properties;
  }

  private static void mergeIntoSystemProperties(@Nonnull Properties properties) {
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      System.setProperty((String) entry.getKey(), (String) entry.getValue());
    }
  }

  private static int getAvailableTcpPort() {
    // Intentionally avoid Linux' ephemeral port range, 32768 to 60999
    // (and, of course, system ports).
    return SocketUtils.findAvailableTcpPort(1024, 32767);
  }
}

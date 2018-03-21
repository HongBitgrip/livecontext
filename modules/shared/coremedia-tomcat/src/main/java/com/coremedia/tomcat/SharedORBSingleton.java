package com.coremedia.tomcat;

import org.omg.CORBA.ORB;

import static org.omg.CORBA.ORB.init;

/**
 * maintain a single shared ORB instance.
 */
public final class SharedORBSingleton {
  private static ORB orb;

  private SharedORBSingleton() {
  }

  static synchronized void createORB() {
    if (orb != null) {
      throw new IllegalStateException("ORB already initialized");
    }
    orb = init(new String[0], System.getProperties());
  }

  static synchronized void destroyORB() {
    if (orb != null) {
      try {
        orb.shutdown(false);
      } finally {
        orb.destroy();
      }
    }
    orb = null;
  }

  public static synchronized ORB getOrb() {
    if (orb == null) {
      throw new IllegalStateException("ORB not initialized - did you add com.coremedia.tomcat.SharedORBListener to server.xml before the GlobalResourcesLifecycleListener?");
    }
    return orb;
  }
}

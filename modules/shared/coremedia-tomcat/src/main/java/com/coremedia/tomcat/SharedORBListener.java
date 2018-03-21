package com.coremedia.tomcat;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

/**
 * synchronize ORB lifecycle with tomcat lifecycle.
 * All ORB initialization parameters must have been set as system properties.
 */
public class SharedORBListener implements LifecycleListener {
  @Override
  public void lifecycleEvent(LifecycleEvent event) {
    if (Lifecycle.START_EVENT.equals(event.getType())) {
      SharedORBSingleton.createORB();
    } else if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
      SharedORBSingleton.destroyORB();
    }
  }
}

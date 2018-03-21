package com.coremedia.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;

import java.util.logging.Logger;

/**
 * This Listener shuts down tomcat if any deployed webapps context passes a lifecycle state FAILED.
 */
public class FailedContextShutdownServerListener implements LifecycleListener {

  private static final Logger log = Logger.getLogger(FailedContextShutdownServerListener.class.getName());

  private boolean contextFailed = false;

  @Override
  public void lifecycleEvent(LifecycleEvent event) {

    if (event.getLifecycle().getState().equals(LifecycleState.FAILED)) {
      contextFailed = true;
    }

    if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType()) && contextFailed) {
      String ctxName = "";
      try {
        if (event.getSource() instanceof Context) {
          Context ctx = (Context) event.getSource();
          ctxName = ctx.getName();
          if (ctx.getParent() != null && ctx.getParent() instanceof Host) {
            Host host = (Host) ctx.getParent();
            if (host.getParent() != null && host.getParent() instanceof Engine) {
              Engine engine = (Engine) host.getParent();
              if (engine.getService() != null && engine.getService().getServer() != null) {
                log.severe("Context " + ctxName + " failed, shutting down tomcat.");
                engine.getService().getServer().stop();
              }
            }
          }
        }
      } catch (LifecycleException e) {
        log.severe("Could not stop catalina after context " + ctxName + " failed." + e.getMessage());
      }
    }
  }
}

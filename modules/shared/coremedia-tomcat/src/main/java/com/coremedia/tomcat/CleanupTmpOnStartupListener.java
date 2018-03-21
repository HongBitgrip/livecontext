package com.coremedia.tomcat;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

import java.io.File;
import java.util.logging.Logger;

public class CleanupTmpOnStartupListener implements LifecycleListener {

  private static final Logger log = Logger.getLogger(CleanupTmpOnStartupListener.class.getName());

  /**
   * This listener cleans up the java.io.tmpdir folder during startup phase of tomcat.
   *
   * @param event
   */
  @Override
  public void lifecycleEvent(LifecycleEvent event) {
    if (Lifecycle.BEFORE_INIT_EVENT.equals(event.getType())) {
      File tmpDir = new File(System.getProperty("java.io.tmpdir"));
      if (tmpDir.exists()) {
        log.info("Trying to cleanup the temp dir " + tmpDir.getPath());
        try {
          cleanDirectory(tmpDir);
        } catch (SecurityException e) {
          log.warning("Could not clean up temp directory at " + System.getProperty("java.io.tmpdir") + "." + e.getMessage());
        }
      } else {
        try {
          if (!tmpDir.mkdirs()) {
            log.warning("Could not create temp directory at " + System.getProperty("java.io.tmpdir") + ".");
          }
        } catch (SecurityException e) {
          log.warning("Could not create temp directory at " + System.getProperty("java.io.tmpdir") + "." + e.getMessage());
        }
      }
    }
  }

  private static void cleanDirectory(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            cleanDirectory(file);
            if (!file.delete()) {
              log.warning("Could not clean up " + file.getPath());
            }
          } else {
            if (!file.delete()) {
              log.warning("Could not clean up " + file.getPath());
            }
          }
        }
      }
    }
  }

}

package com.coremedia.tomcat;

import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.core.StandardContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

public class LifecycleListenerTest {
  LifecycleEvent lifecycleEvent;
  StandardContext lifecycle;
  Host host;
  Engine engine;
  Service service;
  Server server;

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void setup() {
    lifecycle = mock(StandardContext.class);
    host = mock(Host.class);
    engine = mock(Engine.class);
    service = mock(Service.class);
    server = mock(Server.class);
    when(lifecycle.getName()).thenReturn("test");
    when(lifecycle.getParent()).thenReturn(host);
    when(host.getParent()).thenReturn(engine);
    when(engine.getService()).thenReturn(service);
    when(service.getServer()).thenReturn(server);
  }

  @Test
  public void testContextFailed() throws LifecycleException {
    lifecycleEvent = new LifecycleEvent(lifecycle, Lifecycle.AFTER_START_EVENT, null);
    when(lifecycle.getState()).thenReturn(LifecycleState.FAILED);
    LifecycleListener cut = new FailedContextShutdownServerListener();
    cut.lifecycleEvent(lifecycleEvent);
    verify(server, never()).stop();
    lifecycleEvent = new LifecycleEvent(lifecycle, Lifecycle.AFTER_STOP_EVENT, null);
    cut.lifecycleEvent(lifecycleEvent);
    verify(server).stop();
  }

  @Test
  public void testCleanupOnStartup() throws IOException {
    String oldTempDir = System.getProperty("java.io.tmpdir");
    folder.newFile("test.file");
    File subfolder = folder.newFolder();
    new File(subfolder, "another.file").createNewFile();
    System.setProperty("java.io.tmpdir", folder.getRoot().getPath());
    LifecycleListener cut = new CleanupTmpOnStartupListener();
    lifecycleEvent = new LifecycleEvent(lifecycle, Lifecycle.BEFORE_INIT_EVENT, null);
    cut.lifecycleEvent(lifecycleEvent);
    assertEquals(0, folder.getRoot().listFiles().length);
    System.setProperty("java.io.tmpdir", oldTempDir);
  }
}

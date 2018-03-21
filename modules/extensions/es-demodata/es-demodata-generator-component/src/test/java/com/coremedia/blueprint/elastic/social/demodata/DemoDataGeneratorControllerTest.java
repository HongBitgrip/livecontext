package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.tenant.TenantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator.STATE_RUNNING;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator.STATE_STOPPED;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGeneratorController.INFO_KEY;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGeneratorController.INTERVAL_PARAM;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGeneratorController.STATUS_KEY;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGeneratorController.STATUS_PARAM;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGeneratorController.STOP_PARAM;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGeneratorController.TENANT_PARAM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DemoDataGeneratorControllerTest {
  @InjectMocks
  private DemoDataGeneratorController demoDataGeneratorController = new DemoDataGeneratorController();

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpServletResponse httpServletResponse;

  @Mock
  private DemoDataGenerator demoDataGenerator;

  @Mock
  private TenantService tenantService;

  @Test
  public void handleStartWithNewTenant() {
    String tenant = "tenant";
    when(httpServletRequest.getParameter(INTERVAL_PARAM)).thenReturn("10");
    when(httpServletRequest.getParameter(TENANT_PARAM)).thenReturn(tenant);

    when(demoDataGenerator.getStatus()).thenReturn(STATE_STOPPED);
    ModelAndView mv = demoDataGeneratorController.handleRequestInternal(httpServletRequest, httpServletResponse);
    verify(tenantService).register(tenant);
    verify(tenantService).setCurrent(tenant);
    verify(tenantService).clearCurrent();
    verify(demoDataGenerator).setInterval(10);
    verify(demoDataGenerator).start();
    assertTrue(mv.getModel().entrySet().size() > 25);
  }

  @Test
  public void handleRequestInternalTenant() {
    String tenant = "xyz";
    when(httpServletRequest.getParameter("tenant")).thenReturn(tenant);
    when(demoDataGenerator.getStatus()).thenReturn(DemoDataGenerator.STATE_STOPPED);
    ModelAndView mv = demoDataGeneratorController.handleRequestInternal(httpServletRequest, httpServletResponse);
    verify(tenantService).register(tenant);
    verify(tenantService).setCurrent(tenant);
    verify(demoDataGenerator).start();
    assertTrue(mv.getModel().entrySet().size() > 25);
  }

  @Test
  public void handleRequestInternalStop() {
    // request stop, just needs to be != null
    when(httpServletRequest.getParameter(STOP_PARAM)).thenReturn("");
    when(demoDataGenerator.getStatus()).thenReturn(STATE_RUNNING);

    ModelAndView mv = demoDataGeneratorController.handleRequestInternal(httpServletRequest, httpServletResponse);

    verify(demoDataGenerator).stop();
    assertEquals(STATE_RUNNING, mv.getModel().get(STATUS_KEY));
    assertEquals("Stopped the DemoDataGenerator", mv.getModel().get(INFO_KEY));
  }

  @Test
  public void handleRequestInternalStopNotRunning() {
    // request stop, just needs to be != null
    when(httpServletRequest.getParameter(STOP_PARAM)).thenReturn("");
    when(demoDataGenerator.getStatus()).thenReturn(STATE_STOPPED).thenReturn(STATE_RUNNING);

    ModelAndView mv = demoDataGeneratorController.handleRequestInternal(httpServletRequest, httpServletResponse);

    verify(demoDataGenerator, never()).setInterval(anyInt());
    verify(demoDataGenerator, never()).stop();
    assertEquals(STATE_RUNNING, mv.getModel().get(STATUS_KEY));
    assertEquals("DemoDataGenerator not running", mv.getModel().get(INFO_KEY));
  }

  @Test
  public void handleRequestInternalStatus() {
    // request current status, just needs to be != null
    when(httpServletRequest.getParameter(STATUS_PARAM)).thenReturn("");
    when(demoDataGenerator.getStatus()).thenReturn(STATE_STOPPED);

    ModelAndView mv = demoDataGeneratorController.handleRequestInternal(httpServletRequest, httpServletResponse);

    verify(demoDataGenerator, never()).setInterval(anyInt());
    verify(demoDataGenerator, never()).start();
    assertEquals(demoDataGenerator.getStatus(), mv.getModel().get(STATUS_KEY));
  }

  @Test
  public void handleRequestInternalInvalidInterval() {
    when(httpServletRequest.getParameter(INTERVAL_PARAM)).thenReturn("abc");
    when(demoDataGenerator.getStatus()).thenReturn(STATE_STOPPED);
    when(demoDataGenerator.getDefaultInterval()).thenReturn(30);
    ModelAndView mv = demoDataGeneratorController.handleRequestInternal(httpServletRequest, httpServletResponse);
    assertTrue(mv.getModel().entrySet().size() > 25);
    verify(demoDataGenerator).setInterval(30);
    verify(demoDataGenerator).start();
  }

  @Test
  public void handleRequestInternalAlreadyRunning() {
    when(demoDataGenerator.getStatus()).thenReturn(STATE_RUNNING);
    when(demoDataGenerator.getDefaultInterval()).thenReturn(30);
    ModelAndView mv = demoDataGeneratorController.handleRequestInternal(httpServletRequest, httpServletResponse);
    assertTrue(mv.getModel().entrySet().size() > 25);
    verify(demoDataGenerator, never()).setInterval(anyInt());
    verify(demoDataGenerator, never()).start();
  }
}

package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.services.SessionSynchronizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionSynchronizationInterceptorTest {

  private static final String OPTIONS = "OPTIONS";
  private static final String GET = "GET";

  private SessionSynchronizationInterceptor testling;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private SessionSynchronizer sessionSynchronizer;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private UserSessionService userSessionService;

  @Test
  public void optionsRequest() throws GeneralSecurityException, IOException {
    when(request.getMethod()).thenReturn(OPTIONS);
    testling.preHandle(request, response, null);

    verify(request).getMethod();
    verify(sessionSynchronizer, never()).synchronizeUserSession(request, response);
  }

  @Test
  public void getRequest() throws GeneralSecurityException, IOException {
    when(commerceConnection.getUserSessionService()).thenReturn(userSessionService);
    CurrentCommerceConnection.set(commerceConnection);
    testling.preHandle(request, response, null);

    verify(request).getMethod();
    verify(sessionSynchronizer).synchronizeUserSession(request, response);
  }

  @Before
  public void defaultSetup() {
    testling = new SessionSynchronizationInterceptor();
    testling.setSessionSynchronizer(sessionSynchronizer);

    when(request.getMethod()).thenReturn(GET);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

}

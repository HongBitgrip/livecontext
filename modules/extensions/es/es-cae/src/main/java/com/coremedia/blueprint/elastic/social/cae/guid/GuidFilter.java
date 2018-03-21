package com.coremedia.blueprint.elastic.social.cae.guid;

import com.coremedia.elastic.core.api.settings.Settings;
import com.google.common.base.Strings;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.UUID;

import static java.lang.String.format;

@Named
public class GuidFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(GuidFilter.class);
  private static final ThreadLocal<String> GUID_THREAD_LOCAL = new ThreadLocal<>();

  private String cookieName = "guid";
  private final RSAKeyPair rsaKeyPair;

  @Inject
  public GuidFilter(Settings settings) throws NoSuchAlgorithmException {
    this.rsaKeyPair = RSAKeyPair.createFrom(settings);
  }

  String getCookieName() {
    return cookieName;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    String cookieNameParameter = filterConfig.getInitParameter("cookieName");
    if (cookieNameParameter != null) {
      cookieName = cookieNameParameter;
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String guid = null;
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      guid = extractGuid(httpServletRequest);
    }

    if (guid == null || !validateGuid(guid)) {
      guid = createGuid();
      if (response instanceof HttpServletResponse) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Cookie cookie = new Cookie(getCookieName(), guid); // NOSONAR rule 'Cookies should be "secure"', but we need it anyway
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
      }
    }

    setCurrentGuid(guid);
    try {
      chain.doFilter(request, response);
    } finally {
      clear();
    }
  }

  @Override
  public void destroy() {
  }

  private String extractGuid(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  String createGuid() {
    String uuid = UUID.randomUUID().toString();
    try {
      Signature signature = Signature.getInstance("SHA1withRSA");
      signature.initSign(rsaKeyPair.getPrivateKey());

      byte[] bytes = uuid.getBytes();
      signature.update(bytes, 0, bytes.length);
      byte[] signed = signature.sign();
      return uuid + '+' + Hex.encodeHexString(signed);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException("No such algorithm while generating signature for " + uuid + ": " + e.getMessage(), e);
    } catch (InvalidKeyException e) {
      throw new IllegalArgumentException("Invalid key while generating signature for " + uuid + ": " + e.getMessage(), e);
    } catch (SignatureException e) {
      throw new IllegalArgumentException("Cannot generate signature for " + uuid + ": " + e.getMessage(), e);
    }
  }

  boolean validateGuid(String guid) {
    if (Strings.isNullOrEmpty(guid)) {
      return false;
    }
    try {
      String uuid = extractUuidFromGuid(guid);
      String signature = extractSignatureFromGuid(guid);
      Signature sig = Signature.getInstance("SHA1withRSA");
      sig.initVerify(rsaKeyPair.getPublicKey());
      byte[] update = uuid.getBytes();
      sig.update(update, 0, update.length);
      byte[] verify = Hex.decodeHex(signature.toCharArray());
      return sig.verify(verify);
    } catch (DecoderException e) {
      LOG.warn(format("Hex decoder exception while validating signature for %s: %s", guid, e.getMessage()), e);
    } catch (NoSuchAlgorithmException e) {
      LOG.warn(format("No such algorithm while validating signature for %s: %s", guid, e.getMessage()), e);
    } catch (InvalidKeyException e) {
      LOG.warn(format("Invalid key while validating signature for %s: %s", guid, e.getMessage()), e);
    } catch (SignatureException e) {
      LOG.warn(format("Cannot validate signature for %s: %s", guid, e.getMessage()), e);
    } catch (IllegalArgumentException e) {
      LOG.warn(format("Invalid Guid: %s: %s", guid, e.getMessage()), e);
    }
    LOG.warn("Validation of given Guid failed, please check configuration of signCookie private and public key. A new Guid will be created.");
    return false;
  }

  public static String extractUuidFromGuid(String guid) {
    int index = guid.indexOf('+');
    if (index == -1) {
      throw new IllegalArgumentException("Not a valid guid: " + guid);
    }
    return guid.substring(0, index);
  }

  private static String extractSignatureFromGuid(String guid) {
    int index = guid.indexOf('+');
    if (index == -1) {
      throw new IllegalArgumentException("Not a valid guid: " + guid);
    }
    return guid.substring(index + 1);
  }

  @Nullable
  public static String getCurrentGuid() {
    return GUID_THREAD_LOCAL.get();
  }

  public static void setCurrentGuid(String guid) {
    GUID_THREAD_LOCAL.set(guid);
  }

  private static void clear() {
    GUID_THREAD_LOCAL.remove();
  }
}

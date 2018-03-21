package com.coremedia.blueprint.elastic.social.cae.flows;

import org.springframework.webflow.execution.RequestContext;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A simple form bean for use by the password reset web flow.
 */
public class PasswordReset implements Serializable {
  private static final long serialVersionUID = 42L;
  protected static final String PASSWORD = "password"; // NOSONAR false positive: Credentials should not be hard-coded

  private String emailAddress;
  private String password;
  private String confirmPassword;
  private PasswordPolicy passwordPolicy;

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public PasswordPolicy getPasswordPolicy() {
    return passwordPolicy;
  }

  public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
    this.passwordPolicy = passwordPolicy;
  }

  public void validate(RequestContext context) {
    if (isBlank(emailAddress)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.PASSWORD_RESET_EMAIL_ERROR, "emailAddress");
    }
  }

  public void validateResetForm(RequestContext context) {
    if (isBlank(password)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.CONFIRM_PASSWORD_RESET_PASSWORD_ERROR, PASSWORD);
      return;
    }

    if (!password.equals(confirmPassword)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.CONFIRM_PASSWORD_RESET_PASSWORDS_DO_NOT_MATCH, PASSWORD);
    }

    if (passwordPolicy != null && !passwordPolicy.verify(password)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.CONFIRM_PASSWORD_RESET_PASSWORD_TOO_WEAK, PASSWORD);
    }
  }
}

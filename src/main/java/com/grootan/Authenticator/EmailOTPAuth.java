package com.grootan.Authenticator;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Random;

public class EmailOTPAuth implements Authenticator {
  @Override
  public void authenticate(AuthenticationFlowContext context) {
    LoginFormsProvider formsProvider = context.form();
    Response challengeResponse = formsProvider.createForm("otp.ftl");
    try {
      context.getAuthenticationSession().setAuthNote("otp", generateAndSendOtp(context));
    } catch (EmailException e) {
      throw new RuntimeException(e);
    }
    context.challenge(challengeResponse);
  }

  private String generateAndSendOtp(AuthenticationFlowContext context) throws EmailException {
    int OTP = new Random().nextInt(900000) + 100000;
    KeycloakSession session = context.getSession();
    UserModel userModel = context.getUser();
    DefaultEmailSenderProvider senderProvider = new DefaultEmailSenderProvider(session);
    senderProvider.send(
        session.getContext().getRealm().getSmtpConfig(),
        userModel,
        "OTP",
        "OTP is" + OTP,
        "OTP is " + OTP);
    return String.valueOf(OTP);
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    if (validateForm(context, formData)) {
      context.success();
    } else {
      badOtpHandler(context, context.getUser());
    }
  }

  protected void badOtpHandler(AuthenticationFlowContext context, UserModel user) {
    context.getEvent().user(user);
    context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
    Response challengeResponse = challenge(context, "INVALID_OTP");
    context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
  }

  protected Response challenge(AuthenticationFlowContext context, String error) {
    LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
    form.addError(new FormMessage("OTP", error));
    return form.createForm("otp.ftl");
  }

  protected boolean validateForm(
      AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
    String userOtp = formData.getFirst("otp");
    String otp = context.getAuthenticationSession().getAuthNote("otp");
    return userOtp.equals(otp);
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
    return false;
  }

  @Override
  public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

  @Override
  public void close() {}
}

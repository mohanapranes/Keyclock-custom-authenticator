package com.grootan.Authenticator;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalUserAttributeValueFactory;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.managers.DefaultBruteForceProtector;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.lang.String;

import static com.grootan.Authenticator.Constants.*;

public class DobAuth implements Authenticator {


    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        if (authenticationFlowContext.getUser().getFirstAttribute(DATE_OF_BIRTH) == null) {
            authenticationFlowContext.getEvent().error(Errors.USER_NOT_FOUND);
            LoginFormsProvider form = authenticationFlowContext.form().setExecution(authenticationFlowContext.getExecution().getId());
            form.addError(new FormMessage(DATE_OF_BIRTH, DOB_NOT_FOUND));
            authenticationFlowContext.resetFlow();
            return;
        }
        LoginFormsProvider formsProvider = authenticationFlowContext.form();
        Response response = formsProvider.createForm("dob.ftl");
        authenticationFlowContext.challenge(response);
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {
        MultivaluedMap<String, String> formData = authenticationFlowContext.getHttpRequest().getDecodedFormParameters();
        UserModel userModel = authenticationFlowContext.getUser();
        RealmModel realmModel = authenticationFlowContext.getRealm();
        KeycloakSession session = authenticationFlowContext.getSession();
        KeycloakSessionFactory keycloakSessionFactory = session.getKeycloakSessionFactory();
        DefaultBruteForceProtector defaultBruteForceProtector = new DefaultBruteForceProtector(keycloakSessionFactory);


        if (validateForm(authenticationFlowContext, formData)) {
            if (validAuthorization(authenticationFlowContext, formData)) {
                authenticationFlowContext.success();
            } else {
                resetFlow(authenticationFlowContext, Errors.ACCESS_DENIED, NOT_AUTHORISED);
            }
        } else {
            defaultBruteForceProtector.failedLogin(realmModel, userModel, authenticationFlowContext.getConnection());
            if (defaultBruteForceProtector.isTemporarilyDisabled(session, realmModel, userModel)) {
                resetFlow(authenticationFlowContext, Errors.USER_DISABLED, BRUTE_FORCE);
                return;
            }
            badDoBHandler(authenticationFlowContext, userModel);

        }
    }

    private void resetFlow(AuthenticationFlowContext authenticationFlowContext, String error, String message) {
        authenticationFlowContext.getEvent().error(error);
        LoginFormsProvider form = authenticationFlowContext.form().setExecution(authenticationFlowContext.getExecution().getId());
        form.addError(new FormMessage(DATE_OF_BIRTH, message));
        authenticationFlowContext.resetFlow();
    }

    private void badDoBHandler(AuthenticationFlowContext context, UserModel user) {
        context.getEvent().user(user);
        context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
        Response challengeResponse = challenge(context, WRONG_CREDENTIALS, "dob.ftl");
        context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
    }

    private boolean validAuthorization(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String attributeName = config.get(ConditionalUserAttributeValueFactory.CONF_ATTRIBUTE_NAME);
        String userDoB = formData.getFirst(DATE_OF_BIRTH);
        return calculateAge(userDoB) > Integer.parseInt(attributeName);
    }

    private int calculateAge(String dobStr) {
        LocalDate curDate = LocalDate.now();
        LocalDate dob = LocalDate.parse(dobStr);
        Period period = Period.between(dob, curDate);
        return period.getYears();
    }

    private Response challenge(AuthenticationFlowContext context, String error, String fileName) {
        LoginFormsProvider form = context.form().setExecution(context.getExecution().getId());
        form.addError(new FormMessage(DATE_OF_BIRTH, error));
        return form.createForm(fileName);
    }

    private boolean validateForm(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        String userDoB = formData.getFirst(DATE_OF_BIRTH);
        String dob = context.getUser().getAttributes().get(DATE_OF_BIRTH).get(0);
        return userDoB.equals(dob);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }


}

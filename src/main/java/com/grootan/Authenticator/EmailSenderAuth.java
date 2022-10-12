package com.grootan.Authenticator;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.email.EmailSenderProviderFactory;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import static com.grootan.Constants.*;

public class EmailSenderAuth implements Authenticator {

    private static final Logger LOGGER = Logger.getLogger(EmailSenderAuth.class.getName());
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LoginFormsProvider loginFormsProvider = context.form();
        Response response = loginFormsProvider.createForm(EMAIL_FTL);
        context.challenge(response);
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        UserModel userModel = context.getUser();

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        Map<String, String> authConfig = context.getAuthenticatorConfig().getConfig();
        String AuthConfigEmailProvider = authConfig.get(PROVIDER_EMAIL);
        Map<String,String> emailProviderConfig = new HashMap<>();

        if(AuthConfigEmailProvider.equals(DEFAULT_EMAIL_PROVIDER)){
            emailProviderConfig = context.getRealm().getSmtpConfig();
        }
        else if(AuthConfigEmailProvider.equals(SENDGRID_EMAIL_PROVIDER)) {
           emailProviderConfig = context.getRealm().getSendgridConfig();
        }

        ServiceLoader<EmailSenderProviderFactory> emailSenderProviderFactories = ServiceLoader.load(EmailSenderProviderFactory.class);
        Map<String, String> finalEmailProviderConfig = emailProviderConfig;
        emailSenderProviderFactories.forEach(emailSenderProviderFactory->{
            if(emailSenderProviderFactory.getId().equals(AuthConfigEmailProvider)){
                LOGGER.info(AuthConfigEmailProvider+" factory is present");
                EmailSenderProvider emailSenderProvider = emailSenderProviderFactory.create(context.getSession());
                try {
                    emailSenderProvider.send(finalEmailProviderConfig, formData.getFirst(EMAIL),"hai","","hai");


                } catch (EmailException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        context.success();
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
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public void close() {

    }
}

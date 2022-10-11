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
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import static com.grootan.Authenticator.Constants.*;

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
        Map<String, String> config = context.getAuthenticatorConfig().getConfig();
        String configEmailProvider = config.get(PROVIDER_EMAIL);
        ServiceLoader<EmailSenderProviderFactory> emailSenderProviderFactories = ServiceLoader.load(EmailSenderProviderFactory.class);
        emailSenderProviderFactories.forEach(emailSenderProviderFactory->{
            if(emailSenderProviderFactory.getId().equals(configEmailProvider)){
                LOGGER.info("email sender provider factory is present");
                EmailSenderProvider emailSenderProvider = emailSenderProviderFactory.create(context.getSession());
                try {
                    emailSenderProvider.send(config, formData.get(EMAIL).toString(),"hai","","hai");
                    LOGGER.info("Email send completed");

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

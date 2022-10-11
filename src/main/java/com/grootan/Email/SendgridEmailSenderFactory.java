
package com.grootan.Email;

import org.keycloak.Config;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class SendgridEmailSenderFactory implements EmailSenderProviderFactory {

    private static final String PROVIDER_ID = "SendgridEmail";

    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new SendgridEmailSender(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

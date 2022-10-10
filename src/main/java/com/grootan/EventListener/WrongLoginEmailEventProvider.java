package com.grootan.EventListener;

import com.grootan.RequiredAction.BrowserRequiredAction;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.logging.Logger;

public class WrongLoginEmailEventProvider implements EventListenerProvider {

    private static final Logger LOGGER = Logger.getLogger(WrongLoginEmailEventProvider.class.getName());

    private final KeycloakSession keycloakSession;

    public WrongLoginEmailEventProvider(KeycloakSession session) {
        this.keycloakSession = session;
    }

    @Override
    public void onEvent(Event event) {
        if (EventType.LOGIN_ERROR.equals(event.getType())) {

            RealmModel realmModel = keycloakSession.realms().getRealm(event.getRealmId());
            if (realmModel == null) {
                LOGGER.warning("no such realm Model available");
                return;
            }
            LOGGER.info("realm model get successfully, realm id is " + realmModel.getId());
            UserModel userModel;
            try {
                userModel = keycloakSession.users().getUserById(realmModel, event.getUserId());
                LOGGER.info("userModel get successfully, userModel id is " + userModel.getId());
            } catch (NullPointerException exception) {
                LOGGER.warning("UserModel not available");
                return;
            }

            DefaultEmailSenderProvider emailSenderProvider = new DefaultEmailSenderProvider(keycloakSession);
            try {
                emailSenderProvider.send(realmModel.getSmtpConfig(), userModel, "wrong password or username", "login failed", "your login attempt was failed because of wrong password or username");
            } catch (EmailException e) {
                throw new RuntimeException(e);
            }

            LOGGER.info("wrong login email send working");

        }

    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {

    }

    @Override
    public void close() {

    }
}

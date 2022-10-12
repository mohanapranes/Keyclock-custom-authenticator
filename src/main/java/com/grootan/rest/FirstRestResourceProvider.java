package com.grootan.rest;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.logging.Logger;

public class FirstRestResourceProvider implements RealmResourceProvider {

    private final Logger LOGGER = Logger.getLogger(FirstRestResourceProvider.class.getName());
    private KeycloakSession keycloakSession;

    public FirstRestResourceProvider(KeycloakSession keycloakSession) {
        LOGGER.info("FirstRestResourceProvider initialized successfully");
        this.keycloakSession = keycloakSession;
    }

    @Override
    public Object getResource() {
        return new FirstRestResource(keycloakSession);
    }

    @Override
    public void close() {

    }
}

package com.grootan.RequiredAction;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.logging.Logger;

public class AgeRequiredAction implements RequiredActionProvider, RequiredActionFactory {

    private static final Logger LOGGER = Logger.getLogger(AgeRequiredAction.class.getName());
    public static final String PROVIDER_ID = "age-required-action";

    private final String NAME = "Age Required Action";
    @Override
    public String getDisplayText() {
        return "Age required action";
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        LOGGER.info(NAME+" evaluate trigger working");
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        LOGGER.info(NAME+"  required action challenge working");
        context.success();
    }

    @Override
    public void processAction(RequiredActionContext context) {
        LOGGER.info(NAME+" process action working");
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        session.
        LOGGER.info(NAME+" create working");
        return this;
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

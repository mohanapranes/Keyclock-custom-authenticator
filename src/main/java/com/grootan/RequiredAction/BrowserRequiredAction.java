package com.grootan.RequiredAction;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.grootan.Constants.*;

public class BrowserRequiredAction implements RequiredActionProvider, RequiredActionFactory {
    private static final Logger LOGGER = Logger.getLogger(BrowserRequiredAction.class.getName());
    private final String NAME = "Browser required action";

    @Override
    public String getDisplayText() {
        return NAME;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        LOGGER.info(NAME+" evaluate trigger working");

        UserModel userModel = context.getUser();

        String browserName = findBrowserName(context.getHttpRequest().getHttpHeaders().getRequestHeaders().getFirst("User-Agent"));
        List<String> oldBrowserNames = userModel.getAttributes().get(BROWSER_ATTRIBUTE);
        if (oldBrowserNames == null || !(oldBrowserNames.contains(browserName))) {
            if(oldBrowserNames==null) {
                oldBrowserNames = new ArrayList<>();
            }
            userModel.addRequiredAction(BROWSER_REQUIRED_ACTION);
            LOGGER.info(NAME + " required action set successfully");
            userModel.addRequiredAction(BROWSER_REQUIRED_ACTION);
            oldBrowserNames.add(browserName);
            userModel.setAttribute(BROWSER_ATTRIBUTE, oldBrowserNames);
        }
        LOGGER.info(NAME+" evaluate trigger is complete");
    }

    private String findBrowserName(String url){
        String browserName = "";
        if(url.contains("Version")&&url.contains("Safari")){
            browserName="safari";
        } else if (url.contains("OPR")) {
            browserName="opera";
        } else if (url.contains("Edg")) {
            browserName="edge";
        } else if (url.contains("Firefox")) {
            browserName="Firefox";
        } else if (url.contains("Chrome")) {
            browserName="chrome";
        }
        return browserName;
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        LOGGER.info(NAME+"  required action challenge working");
        context.challenge(context.form().createForm("terms.ftl"));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        LOGGER.info(NAME +" process action working");

        UserModel user = context.getUser();
        user.removeRequiredAction(BROWSER_REQUIRED_ACTION);

        if (context.getHttpRequest().getDecodedFormParameters().containsKey("cancel")) {
            user.removeAttribute(BROWSER_REQUIRED_ACTION);
            context.failure();
            return;
        }

        context.success();
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
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
        return BROWSER_REQUIRED_ACTION;
    }


}

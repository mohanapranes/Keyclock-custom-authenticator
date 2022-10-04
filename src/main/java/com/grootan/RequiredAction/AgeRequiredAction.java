package com.grootan.RequiredAction;

import org.checkerframework.checker.units.qual.N;
import org.keycloak.Config;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.Period;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static com.grootan.Authenticator.Constants.DATE_OF_BIRTH;

public class AgeRequiredAction implements RequiredActionProvider, RequiredActionFactory {

    private static final Logger LOGGER = Logger.getLogger(AgeRequiredAction.class.getName());
    public static final String PROVIDER_ID = "date-required-action";

    private final String NAME = "date Required Action";
    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }
    @Override
    public String getDisplayText() {
        return NAME;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        LOGGER.info(NAME+" evaluate trigger working");
        UserModel userModel = context.getUser();

        if(calculateAge(userModel.getFirstAttribute("dateOfBirth"))>25){
            userModel.addRequiredAction(PROVIDER_ID);
        }
    }
    private int calculateAge(String dobStr) {
        LocalDate curDate = LocalDate.now();
        LocalDate dob = LocalDate.parse(dobStr);
        Period period = Period.between(dob, curDate);
        return period.getYears();
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        LOGGER.info(NAME+"  required action challenge working");
        context.challenge(createForm(context,null));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        LOGGER.info(NAME +" process action working");
        UserModel user = context.getUser();
        user.removeRequiredAction(PROVIDER_ID);
        context.success();
    }
    private Response createForm(RequiredActionContext context, Consumer<LoginFormsProvider> formConsumer) {
        LoginFormsProvider form = context.form();
        return form.createForm("terms.ftl");
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
        return PROVIDER_ID;
    }
}

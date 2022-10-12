package com.grootan.Authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.*;

import static com.grootan.Constants.PROVIDER_EMAIL;

public class EmailSenderAuthFactory implements AuthenticatorFactory {

    public static final EmailSenderAuth SINGLETON = new EmailSenderAuth();
    public static final String PROVIDER_ID = "EmailSender";
    public static final String DISPLAY_TYPE = "email-sender";

    @Override
    public String getDisplayType() {
        return DISPLAY_TYPE;
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        List<String> emailSenderProviderList = new ArrayList<>();
        ServiceLoader<EmailSenderProviderFactory> emailSenderProviders = ServiceLoader.load(EmailSenderProviderFactory.class);
        emailSenderProviders.forEach(emailSenderProvider -> {
            emailSenderProviderList.add(emailSenderProvider.getId());
        });



        ProviderConfigProperty providerConfigProperty = new ProviderConfigProperty();
        providerConfigProperty.setType(ProviderConfigProperty.LIST_TYPE);
        providerConfigProperty.setName(PROVIDER_EMAIL);
        providerConfigProperty.setLabel(PROVIDER_EMAIL);
        providerConfigProperty.setOptions(emailSenderProviderList);

        return Collections.singletonList(providerConfigProperty);
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
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

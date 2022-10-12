package com.grootan.rest;

import org.keycloak.models.KeycloakSession;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class FirstRestResource {
    private KeycloakSession keycloakSession;

    public FirstRestResource(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @GET
    @Path("first")
    public String getHelloWorld(){
        return "Hello world";
    }
}

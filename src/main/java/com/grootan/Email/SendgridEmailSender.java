package com.grootan.Email;

import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

import static com.grootan.Authenticator.Constants.*;

public class SendgridEmailSender implements EmailSenderProvider {
    private final KeycloakSession session;
    private static final Logger LOGGER = Logger.getLogger(SendgridEmailSender.class.getName());

    public SendgridEmailSender(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void send(Map<String, String> config, String address, String subject, String textBody, String htmlBody) throws EmailException {
        Email from = new Email("mohanapraneswaran@gmail.com");
        Email to = new Email(address); // use your own email address here

        Content content = new Content("text/html", htmlBody);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        Response response = null;
        try {
            request.setBody(mail.build());
            response = sg.api(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info(String.valueOf(response.getStatusCode()));
        LOGGER.info(String.valueOf(response.getHeaders()));
        LOGGER.info(response.getBody());
    }

    @Override
    public void close() {

    }
}

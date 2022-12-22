package com.biit.gotify.client;

import com.biit.gotify.logger.GotifyLogger;
import com.biit.gotify.model.message.Message;
import com.biit.rest.client.RestGenericClient;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Component
public class GotifyClient {

    private final static String SEND_MESSAGE_ENDPOINT = "message";

    private final String serverUrl;

    private final String applicationToken;

    public GotifyClient(@Value("${gotify.server.url}") String serverUrl,
                        @Value("${gotify.application.token}") String applicationToken) {
        this.serverUrl = serverUrl;
        GotifyLogger.debug(this.getClass(), "Using url '{}'", serverUrl);
        this.applicationToken = applicationToken;
        GotifyLogger.debug(this.getClass(), "Using token '{}'", applicationToken);
    }

    public void sendPushNotification(Message message) throws UnprocessableEntityException, EmptyResultException {
        final boolean ssl = serverUrl.startsWith("https");
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("token", applicationToken);
        RestGenericClient.post(ssl, serverUrl, SEND_MESSAGE_ENDPOINT, message.toJson(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, false,
                parameters);
    }


}

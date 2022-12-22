package com.biit.gotify.client;

import com.biit.gotify.logger.GotifyLogger;
import com.biit.gotify.model.message.Message;
import com.biit.gotify.model.message.PagedMessages;
import com.biit.rest.client.RestGenericClient;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Component
public class GotifyClient {

    private static final String SEND_MESSAGE_ENDPOINT = "message";

    private final String serverUrl;

    private final String applicationToken;

    private final Integer applicationId;

    private final String applicationUser;

    private final String applicationPassword;

    private final ObjectMapper objectMapper;

    public GotifyClient(@Value("${gotify.server.url}") String serverUrl,
                        @Value("${gotify.application.token}") String applicationToken,
                        @Value("${gotify.application.id}") String applicationId,
                        @Value("${gotify.user}") String applicationUser,
                        @Value("${gotify.password}") String applicationPassword
    ) {
        this.objectMapper = new ObjectMapper();
        this.serverUrl = serverUrl;
        GotifyLogger.debug(this.getClass(), "Using url '{}'", serverUrl);
        this.applicationToken = applicationToken;
        GotifyLogger.debug(this.getClass(), "Using token '{}'", applicationToken);
        this.applicationUser = applicationUser;
        GotifyLogger.debug(this.getClass(), "Using user '{}'", applicationUser);
        this.applicationPassword = applicationPassword;
        GotifyLogger.debug(this.getClass(), "Using password '{}'", applicationPassword.replaceAll(".", "*"));

        Integer convertedApplicationId = null;
        if (applicationId != null) {
            try {
                convertedApplicationId = Integer.parseInt(applicationId);
                GotifyLogger.debug(this.getClass(), "Using application Id '{}'", applicationId);
            } catch (NumberFormatException e) {
                GotifyLogger.severe(this.getClass(), "Invalid application Id '{}'", applicationId);
            }
        }
        this.applicationId = convertedApplicationId;
    }


    public Message sendPushNotification(Message message) throws UnprocessableEntityException, EmptyResultException {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("token", applicationToken);
        try {
            final String response = RestGenericClient.post(serverUrl, SEND_MESSAGE_ENDPOINT, objectMapper.writeValueAsString(message),
                    MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, false,
                    parameters);
            try {
                return objectMapper.readValue(response, new TypeReference<Message>() {
                });
            } catch (JsonProcessingException e) {
                GotifyLogger.severe(getClass().getName(), "Error parsing response from Gotify server:\n '{}'", response);
                throw new RuntimeException("Error parsing response from Gotify server", e);
            }
        } catch (JsonProcessingException e) {
            GotifyLogger.warning(getClass().getName(), "Error serializing message: '{}'", message.getMessage());
            throw new RuntimeException("Error serializing message:" + message.getMessage(), e);
        }
    }


    public PagedMessages readAppMessages(Integer limit, Integer since) throws UnprocessableEntityException, EmptyResultException {
        return readAppMessages(applicationId, limit, since);
    }


    public PagedMessages readAppMessages(Integer appId, Integer limit, Integer since) throws UnprocessableEntityException, EmptyResultException {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("token", applicationToken);
        if (limit != null) {
            parameters.put("limit", limit);
        }
        if (since != null) {
            parameters.put("since", since);
        }

        final String response = RestGenericClient.get(serverUrl, "application/" + appId + "/message", MediaType.APPLICATION_JSON,
                applicationUser, applicationPassword, parameters);
        try {
            return objectMapper.readValue(response, new TypeReference<PagedMessages>() {
            });
        } catch (JsonProcessingException e) {
            GotifyLogger.severe(getClass().getName(), "Error parsing response from Gotify server:\n '{}'", response);
            throw new RuntimeException("Error parsing response from Gotify server", e);
        }
    }


    public void deleteAppMessages(Integer messageId) throws UnprocessableEntityException, EmptyResultException {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("token", applicationToken);

        RestGenericClient.delete(serverUrl, "message/" + messageId, MediaType.APPLICATION_JSON,
                applicationUser, applicationPassword, parameters);
    }


}

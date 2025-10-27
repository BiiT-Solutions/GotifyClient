package com.biit.gotify.client;

/*-
 * #%L
 * Gotify Client
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.gotify.logger.GotifyLogger;
import com.biit.gotify.model.message.Message;
import com.biit.gotify.model.message.PagedMessages;
import com.biit.rest.client.RestGenericClient;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public Message sendPushNotification(String content, String title) throws UnprocessableEntityException, EmptyResultException {
        final Message message = new Message();
        message.setMessage(content);
        if (applicationId != null) {
            message.setAppid(applicationId);
        }
        message.setTitle(title);
        return sendPushNotification(message);
    }

    public Message sendPushNotification(String content, String title, Integer priority) throws UnprocessableEntityException, EmptyResultException {
        final Message message = new Message();
        message.setMessage(content);
        if (applicationId != null) {
            message.setAppid(applicationId);
        }
        message.setTitle(title);
        message.setPriority(priority);
        return sendPushNotification(message);
    }


    public Message sendPushNotification(Message message) throws UnprocessableEntityException, EmptyResultException {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("token", applicationToken);
        try {
            final Response response = RestGenericClient.post(serverUrl, SEND_MESSAGE_ENDPOINT, objectMapper.writeValueAsString(message),
                    MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, false,
                    parameters);
            try {
                return objectMapper.readValue(response.readEntity(String.class), new TypeReference<Message>() {
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

        final Response response = RestGenericClient.get(serverUrl, "application/" + appId + "/message", MediaType.APPLICATION_JSON,
                applicationUser, applicationPassword, parameters);
        try {
            return objectMapper.readValue(response.readEntity(String.class), new TypeReference<PagedMessages>() {
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

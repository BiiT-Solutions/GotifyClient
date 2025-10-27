package com.biit.gotify.test;

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

import com.biit.gotify.client.GotifyClient;
import com.biit.gotify.model.message.Message;
import com.biit.gotify.model.message.PagedMessages;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@SpringBootTest
@Test(groups = {"sendPushNotification"})
public class PushMessageTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private GotifyClient gotifyClient;

    private Message messageSend;

    @Test
    public void sendPushNotification() throws UnprocessableEntityException, EmptyResultException {
        messageSend = gotifyClient.sendPushNotification("This is a test message", "\uD83D\uDEA8 Test Title \uD83D\uDEA8");
        Assert.assertNotNull(messageSend);
    }

    @Test(dependsOnMethods = "sendPushNotification")
    public void getPushNotification() throws UnprocessableEntityException, EmptyResultException {
        PagedMessages pagedMessages = gotifyClient.readAppMessages(100, 0);
        Assert.assertTrue(pagedMessages.getMessages().stream().anyMatch(message -> Objects.equals(message.getId(), messageSend.getId())));
    }

    @Test(dependsOnMethods = "getPushNotification")
    public void deleteMessage() throws UnprocessableEntityException, EmptyResultException {
        gotifyClient.deleteAppMessages(messageSend.getId());
    }
}

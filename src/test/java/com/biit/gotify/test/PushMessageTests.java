package com.biit.gotify.test;

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

import java.util.Objects;

@SpringBootTest
@Test(groups = {"sendPushNotification"})
public class PushMessageTests extends AbstractTestNGSpringContextTests {
    @Value("${gotify.application.id}")
    private String applicationId;

    @Autowired
    private GotifyClient gotifyClient;

    private Message messageSend;

    @Test
    public void sendPushNotification() throws UnprocessableEntityException, EmptyResultException {
        Message message = new Message();
        message.setMessage("This is a test message");
        message.setAppid(Integer.parseInt(applicationId));
        messageSend = gotifyClient.sendPushNotification(message);
        Assert.assertNotNull(message);
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

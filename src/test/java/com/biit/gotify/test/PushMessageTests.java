package com.biit.gotify.test;

import com.biit.gotify.client.GotifyClient;
import com.biit.gotify.model.message.Message;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"sendPushNotification"})
public class PushMessageTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private GotifyClient gotifyClient;

    @Test
    public void sendPushNotification() throws UnprocessableEntityException, EmptyResultException {
        Message message = new Message();
        message.setMessage("This is a test message");
        gotifyClient.sendPushNotification(message);
    }
}

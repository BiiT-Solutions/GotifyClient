package com.biit.gotify.model.message;


import java.util.List;

public class PagedMessages {
    private List<Message> messages;
    private Paging paging;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}

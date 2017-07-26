package com.baodanyun.websocket.event;

import com.baodanyun.websocket.model.MessageArchiveAdapter;

/**
 * Created by liaowuhen on 2017/7/21.
 */
public class MessageArchiveAdapterEvent {
    private MessageArchiveAdapter messageArchiveAdapter;

    public MessageArchiveAdapter getMessageArchiveAdapter() {
        return messageArchiveAdapter;
    }

    public void setMessageArchiveAdapter(MessageArchiveAdapter messageArchiveAdapter) {
        this.messageArchiveAdapter = messageArchiveAdapter;
    }
}

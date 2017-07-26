package com.baodanyun.websocket.service;

import com.baodanyun.websocket.model.MessageArchiveAdapter;

/**
 * Created by liaowuhen on 2017/7/21.
 */
public interface MessageArchiveAdapterService {
    int insert(MessageArchiveAdapter record);

    MessageArchiveAdapter selectByMessageId(String messageid);

    int deleteByMessageId(String messageid);
}

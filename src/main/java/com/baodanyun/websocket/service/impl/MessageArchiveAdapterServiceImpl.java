package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.dao.MessageArchiveAdapterMapper;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.MessageArchiveAdapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/7/21.
 */

@Service
public class MessageArchiveAdapterServiceImpl implements MessageArchiveAdapterService {
    @Autowired
    private MessageArchiveAdapterMapper messageArchiveAdapterMapper;


    @Override
    public int insert(MessageArchiveAdapter record) {
        return messageArchiveAdapterMapper.insert(record);
    }

    @Override
    public MessageArchiveAdapter selectByMessageId(String messageid) {
        return messageArchiveAdapterMapper.selectByMessageId(messageid);
    }

    @Override
    public int deleteByMessageId(String messageid) {
        return messageArchiveAdapterMapper.deleteByMessageId(messageid);
    }
}

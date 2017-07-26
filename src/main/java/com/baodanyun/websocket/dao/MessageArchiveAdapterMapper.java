package com.baodanyun.websocket.dao;

import com.baodanyun.websocket.model.MessageArchiveAdapter;

public interface MessageArchiveAdapterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MessageArchiveAdapter record);

    int insertSelective(MessageArchiveAdapter record);

    MessageArchiveAdapter selectByPrimaryKey(Integer id);

    MessageArchiveAdapter selectByMessageId(String messageid);

    int deleteByMessageId(String messageid);

    int updateByPrimaryKeySelective(MessageArchiveAdapter record);

    int updateByPrimaryKey(MessageArchiveAdapter record);
}
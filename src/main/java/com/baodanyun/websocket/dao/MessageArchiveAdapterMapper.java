package com.baodanyun.websocket.dao;

import com.baodanyun.websocket.model.MessageArchiveAdapter;

import java.util.List;

public interface MessageArchiveAdapterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MessageArchiveAdapter record);

    int insertSelective(MessageArchiveAdapter record);

    MessageArchiveAdapter selectByPrimaryKey(Integer id);

    MessageArchiveAdapter selectByMessageId(String messageid);

    List<MessageArchiveAdapter> selectByFromAndTo(MessageArchiveAdapter record);

    int deleteByMessageId(String messageid);

    int updateByPrimaryKeySelective(MessageArchiveAdapter record);

    int updateByPrimaryKey(MessageArchiveAdapter record);
}
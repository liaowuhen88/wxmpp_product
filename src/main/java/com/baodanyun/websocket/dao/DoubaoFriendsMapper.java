package com.baodanyun.websocket.dao;

import com.baodanyun.websocket.model.DoubaoFriends;

import java.util.List;

public interface DoubaoFriendsMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByFriendJname(String friendJname);

    int insert(DoubaoFriends record);

    int insertSelective(DoubaoFriends record);

    DoubaoFriends selectByPrimaryKey(Integer id);

    List<DoubaoFriends> selectByJid(String jId);

    DoubaoFriends selectBySelective(DoubaoFriends df);

    int updateByPrimaryKeySelective(DoubaoFriends record);

    int updateByPrimaryKey(DoubaoFriends record);
}
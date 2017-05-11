package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.DoubaoFriends;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liaowuhen on 2017/4/20.
 */
@Service
public interface DoubaoFriendsService {
    void dealFrinds(Msg msg) throws BusinessException;

    //void dealFrinds(Msg msg,XmppContentMsg xcg);

    int insert(DoubaoFriends df);

    int deleteByFriendJname(String Jid,String friendJname);

    List<DoubaoFriends> selectByJid(String jId);

    DoubaoFriends selectByRealFromFromMap(String Jid,String WXid);

    DoubaoFriends selectByRealFrom(String Jid,String WXid);

    DoubaoFriends selectBySelective(DoubaoFriends df);



}

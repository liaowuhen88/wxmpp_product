package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.dao.DoubaoFriendsMapper;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.DoubaoFriends;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.MsgSendControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2017/4/20.
 */

@Service("doubaoFriendsNoJsonService")
public class DoubaoFriendsServiceNoJsonImpl implements DoubaoFriendsService {
    private static Logger logger = LoggerFactory.getLogger(DoubaoFriendsServiceNoJsonImpl.class);

    // jid  不同的客服获取不同的用户列表
    private static Map<String, Map<String, DoubaoFriends>> map = new ConcurrentHashMap();
    @Autowired
    private DoubaoFriendsMapper doubaoFriendsMapper;
    @Autowired
    private MsgSendControl msgSendControl;

    @Override
    public void dealFrinds(Msg msg) throws BusinessException {
        if (null != msg) {


        } else {
            logger.info("msg is null");
        }

    }

    @Override
    public int insert(DoubaoFriends df) {
        return doubaoFriendsMapper.insert(df);
    }

    @Override
    public int deleteByFriendJname(String jid, String friendJname) {
        Map<String, DoubaoFriends> friendsMap = map.get(jid);
        if (null != friendsMap) {
            friendsMap.remove(friendJname);
        }
        return doubaoFriendsMapper.deleteByFriendJname(friendJname);
    }

    @Override
    public List<DoubaoFriends> selectByJid(String jId) {
        return doubaoFriendsMapper.selectByJid(jId);
    }

    @Override
    public DoubaoFriends selectByRealFromFromMap(String Jid, String WXid) {
        // to 为客服Jid   from 为微信用户
        DoubaoFriends df = null;
        Map<String, DoubaoFriends> friendsMap = map.get(Jid);
        if (null == friendsMap) {
            friendsMap = new HashMap<>();
            map.put(Jid, friendsMap);
        } else {
            df = friendsMap.get(WXid);
        }


        return df;
    }

    @Override
    public DoubaoFriends selectByRealFrom(String Jid, String WXid) {
        DoubaoFriends df = selectByRealFromFromMap(Jid, WXid);
        if (null == df) {
            DoubaoFriends dfs = new DoubaoFriends();
            dfs.setFriendJname(WXid);
            df = doubaoFriendsMapper.selectBySelective(dfs);
        }

        if (null != df) {
            map.get(Jid).put(WXid, df);
        }

        return df;
    }

    @Override
    public DoubaoFriends selectBySelective(DoubaoFriends df) {
        return doubaoFriendsMapper.selectBySelective(df);
    }


}

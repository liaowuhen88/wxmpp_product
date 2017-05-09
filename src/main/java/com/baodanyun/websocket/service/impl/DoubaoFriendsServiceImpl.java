package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.XmppContentMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.dao.DoubaoFriendsMapper;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.DoubaoFriends;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2017/4/20.
 */

@Service("doubaoFriendsService")
public class DoubaoFriendsServiceImpl implements DoubaoFriendsService {
    private static Logger logger = Logger.getLogger(DoubaoFriendsServiceImpl.class);

    // jid  不同的客服获取不同的用户列表
    private static Map<String, Map<String, DoubaoFriends>> map = new ConcurrentHashMap();
    @Autowired
    private DoubaoFriendsMapper doubaoFriendsMapper;
    @Autowired
    private MsgSendControl msgSendControl;

    @Override
    public void dealFrinds(Msg msg) throws BusinessException {
        try {
            if(null != msg){
                XmppContentMsg msf = JSONUtil.toObject(XmppContentMsg.class, msg.getContent());
                dealFrinds(msg, msf);
            }else {
                logger.info("msg is null");
            }
        } catch (Exception e) {
            logger.error(e);
            throw new BusinessException("解析content异常");
        }
    }

    @Override
    public void dealFrinds(Msg msg, XmppContentMsg xcg) {
        String to = msg.getTo();
        String from = msg.getFrom();
        msg.setFrom(xcg.getRealFrom());
        msg.setContent(xcg.getContent());
        msg.setContentType(xcg.getContentType());
        msg.setFromName(xcg.getFromName());
        msg.setIcon(xcg.getFromIcon());
        DoubaoFriends df  = selectByRealFrom(msg.getTo(),xcg.getRealFrom());

        if (null == df) {

            df = new DoubaoFriends();
            df.setFriendType(xcg.getFromType());
            df.setFriendIcon(xcg.getFromIcon());
            df.setFriendJname(xcg.getRealFrom());
            df.setFriendGroup(xcg.getGroupName());
            df.setJname(xcg.getFromAgent());
            df.setFriendJid(from);
            df.setJid(to);
            insert(df);

            map.get(msg.getTo()).put(msg.getFrom(), df);

            try {
                StatusMsg sm = JSONUtil.toObject(StatusMsg.class, JSONUtil.toJson(msg));
                sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
                sm.setType(Msg.Type.status.toString());
                sm.setContentType(null);
                sm.setContent(null);
                sm.setLoginTime(new Date().getTime());
                sm.setFromName(df.getRealGroupName());
                sm.setLoginUsername(xcg.getGroupName());
                sm.setCt(new Date().getTime());

                msgSendControl.sendMsg(sm);
            } catch (InterruptedException e) {
                logger.equals(e);
            } catch (Exception e) {
                logger.equals(e);
            }
        }

    }

    @Override
    public int insert(DoubaoFriends df) {
        return doubaoFriendsMapper.insert(df);
    }

    @Override
    public int deleteByFriendJname(String jid,String friendJname) {
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
    public DoubaoFriends selectByRealFromFromMap(String Jid,String WXid) {
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
        DoubaoFriends df = selectByRealFromFromMap(Jid,WXid);
        if (null == df) {
            DoubaoFriends dfs = new DoubaoFriends();
            dfs.setFriendJname(WXid);
            df = doubaoFriendsMapper.selectBySelective(dfs);
        }

        if(null != df){
            map.get(Jid).put(WXid, df);
        }

        return df;
    }

    @Override
    public DoubaoFriends selectBySelective(DoubaoFriends df) {
        return doubaoFriendsMapper.selectBySelective(df);
    }


}

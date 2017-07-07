package com.baodanyun.websocket.bean.user;

import com.baodanyun.websocket.bean.msg.ConversationMsg;

import java.util.Comparator;

/**
 * Created by liaowuhen on 2017/3/8.
 */
public class ComparatorConversationMsg implements Comparator<ConversationMsg> {

    @Override
    public int compare(ConversationMsg o1, ConversationMsg o2) {

        /***
         * 倒序排列算法
         */
        int flag = o2.getCt().compareTo(o1.getCt());
        return flag;

    }
}

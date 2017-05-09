package com.baodanyun.websocket.bean.user;

import java.util.Comparator;

/**
 * Created by liaowuhen on 2017/3/8.
 */
public class ComparatorFriend implements Comparator<Friend> {

    @Override
    public int compare(Friend o1, Friend o2) {

        /***
         * 倒序排列算法
         */
        int flag=o2.getOnlineStatus().compareTo(o1.getOnlineStatus());
        return flag;

    }
}

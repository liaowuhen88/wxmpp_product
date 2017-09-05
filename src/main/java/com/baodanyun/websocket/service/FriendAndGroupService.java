package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.bootstrap.Node;
import com.baodanyun.websocket.bean.response.FriendAndGroupResponse;
import com.baodanyun.websocket.bean.user.GroupUser;

import java.util.List;

/**
 * Created by liaowuhen on 2017/8/31.
 */
public interface FriendAndGroupService {
    List<FriendAndGroupResponse> get(String appkey, String nickName) throws Exception;

    /**
     * 获取群内组员
     *
     * @param username
     * @return
     */
    List<GroupUser> getGroupUsers(String appkey, String username) throws Exception;

    List<Node> adapter(List<FriendAndGroupResponse> list);
}

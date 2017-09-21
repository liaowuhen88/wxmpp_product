package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.bootstrap.Node;
import com.baodanyun.websocket.bean.response.FriendAndGroupResponse;
import com.baodanyun.websocket.bean.user.GroupUser;
import com.baodanyun.websocket.bean.user.PublicUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.FriendAndGroupService;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.HttpUtils;
import com.baodanyun.websocket.util.JSONUtil;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liaowuhen on 2017/8/31.
 */
@Service
public class FriendAndGroupServiceImpl implements FriendAndGroupService {
    protected static Logger logger = LoggerFactory.getLogger(FriendAndGroupServiceImpl.class);

    @Override
    public List<FriendAndGroupResponse> get(String appkey, String nickName) throws Exception {

        Response response = get(appkey, "searchfriendqungroup", "nickName", nickName);

        java.lang.reflect.Type fs = new TypeToken<List<FriendAndGroupResponse>>() {
        }.getType();

        return JSONUtil.fromJson(JSONUtil.toJson(response.getData()), fs);

    }

    @Override
    public List<GroupUser> getGroupUsers(String appkey, String username) throws Exception {

        Response response = get(appkey, "getqunmemberlist", "username", username);

        java.lang.reflect.Type fs = new TypeToken<List<GroupUser>>() {
        }.getType();

        return JSONUtil.fromJson(JSONUtil.toJson(response.getData()), fs);

    }

    @Override
    public PublicUser getPublicUser(String appkey, String username) throws Exception {

        Response response = get(appkey, "getfollowinfo", "username", username);


        return JSONUtil.toObject(PublicUser.class, JSONUtil.toJson(response.getData()));
    }


    public Response get(String appkey, String action, String key, String value) throws Exception {
        Map<String, String> content = new HashMap<>();
        if (StringUtils.isEmpty(value)) {
            content.put(key, value);
        } else {
            content.put(key, value.trim());
        }

        Map<String, String> query = new HashMap<>();
        query.put("action", action);
        query.put("appkey", appkey);
        query.put("content", JSONUtil.toJson(content));

        String result = HttpUtils.get(Config.appKeyUrl, query);
        //logger.info(result);
        if (StringUtils.isEmpty(result)) {
            throw new BusinessException("查询结构为空");
        }
        Response response = JSONUtil.toObject(Response.class, result);
        if (!"1".equals(response.getStatus()) && !response.isSuccess()) {
            throw new BusinessException(response.getMessage());
        }

        return response;
    }

    @Override
    public List<Node> adapter(List<FriendAndGroupResponse> list) {
        List<Node> node_wx_list = null;
        if (null != list) {
            node_wx_list = new ArrayList<>();

            for (FriendAndGroupResponse fr : list) {
                Node node_wx = new Node();
                node_wx.setText(fr.getNickName());
                node_wx_list.add(node_wx);

                List<Node> node_friendAndType = new ArrayList<>();
                node_wx.setNodes(node_friendAndType);


                List<FriendAndGroupResponse.BasicNode> friend = fr.getFriend();
                init(node_friendAndType, friend, "好友");

                List<FriendAndGroupResponse.BasicNode> qungroup = fr.getQungroup();
                init(node_friendAndType, qungroup, "群组");
            }

        }
        return node_wx_list;
    }

    @Override
    public String getRobotJid(String appkey, String username) throws Exception {
        Response response = get(appkey, "getrobotjid", "username", username);

        return response.getData().toString();
    }

    private void init(List<Node> node_friendAndType, List<FriendAndGroupResponse.BasicNode> basicNodes, String text) {
        if (null != basicNodes) {
            Node nodeGroupRoot = new Node();
            nodeGroupRoot.setText(text);
            node_friendAndType.add(nodeGroupRoot);

            List<Node> node_Group = new ArrayList<>();
            nodeGroupRoot.setNodes(node_Group);
            for (FriendAndGroupResponse.BasicNode bn : basicNodes) {
                Node f = new Node();
                f.setText(bn.getNickName());
                f.setJid(bn.getJid());
                node_Group.add(f);
            }
        }
    }
}

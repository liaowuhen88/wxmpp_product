package com.baodanyun.websocket.bean.response;

import java.util.List;

/**
 * Created by liaowuhen on 2017/7/17.
 */
public class FriendAndGroupResponse {
    private List<BasicNode> friend;
    private List<BasicNode> qungroup;
    private String nickName;

    public List<BasicNode> getFriend() {
        return friend;
    }

    public void setFriend(List<BasicNode> friend) {
        this.friend = friend;
    }

    public List<BasicNode> getQungroup() {
        return qungroup;
    }

    public void setQungroup(List<BasicNode> qungroup) {
        this.qungroup = qungroup;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public class BasicNode {
        String nickName;
        String icon;
        String jid;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getJid() {
            return jid;
        }

        public void setJid(String jid) {
            this.jid = jid;
        }
    }


}

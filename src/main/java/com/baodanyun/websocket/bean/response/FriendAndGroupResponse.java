package com.baodanyun.websocket.bean.response;

import java.util.List;

/**
 * Created by liaowuhen on 2017/7/17.
 */
public class FriendAndGroupResponse {
    private List<BasicNode> friend;
    private List<BasicNode> qungroup;
    private String nickName;
    private String username; //插件的JID

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

        @Override
        public int hashCode() {
            return this.getJid().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            } else {
                if (obj instanceof BasicNode) {
                    return ((BasicNode) obj).getJid().equals(this.getJid());
                } else {
                    return false;
                }
            }
        }
    }
}

package com.baodanyun.websocket.bean;

/**
 * Created by liaowuhen on 2017/4/20.
 */
public class XmppContentMsg {
    private String fromName;     //  ;    //发送者姓名,
    private String msgType;    //消息类别，比如说有文本信息，图片信息，语音信息,    //    active, msg, status
    private String contentType;    //消息内容类别，比如说有文本信息，图片信息，语音信息,  // text, video, file, audio, image, receiptMsg
    private String fromIcon;    //发送者头像,
    private String from;    //发送者JID,              // xmpp 的 id
  //11微信好友 12微信群 21公众号 22公众号h5 31QQ好友 32qq群 33qq讨论组 41网页
    private String fromType;    //发送者类别,         // 个人 或者群    1 为 个人  2 为群
    private String groupName;    //群名称,
    private String realFrom;
    private String to;    //发送到客服的JID,          // xmpp  的 id  客服
    private String content;    //发送消息内容,
    private String fromAgent;    // "机器人名称" //新增



    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFromIcon() {
        return fromIcon;
    }

    public void setFromIcon(String fromIcon) {
        this.fromIcon = fromIcon;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRealFrom() {
        return realFrom;
    }

    public void setRealFrom(String realFrom) {
        this.realFrom = realFrom;
    }

    public String getFromAgent() {
        return fromAgent;
    }

    public void setFromAgent(String fromAgent) {
        this.fromAgent = fromAgent;
    }
}

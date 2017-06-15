package com.baodanyun.websocket.bean.msg.status;

import com.baodanyun.websocket.bean.msg.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于描述消息的状态
 */
public class StatusMsg extends Msg {
    private static Logger logger = LoggerFactory.getLogger(StatusMsg.class);
    public Status status;
    private int waitIndex = 0;//排队索引 只有在等待队列中时 这个值才>0
    private String ackId;
    private String loginUsername;
    private Long loginTime;//登录时间
    private Byte[] iconByte;

    public StatusMsg() {
        setType(Type.status.toString());
    }

    public static StatusMsg buildStatus( Type type) {
        StatusMsg statusMsg = new StatusMsg();
        statusMsg.setType(type.toString());
        return statusMsg;
    }

    public Byte[] getIconByte() {
        return iconByte;
    }

    public void setIconByte(Byte[] iconByte) {
        this.iconByte = iconByte;
    }

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getWaitIndex() {
        return waitIndex;
    }

    public void setWaitIndex(int waitIndex) {
        this.waitIndex = waitIndex;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public enum Status {
        //心跳状态 所有客户端 均需要满足
        heartbeat,

        //只适合客服上线，客服没有队列概念
        customerOnline,
        customerOffline,

        //全适用
        initSuccess,//初始化成功
        initError,//初始化失败
        offline,//下线
        loginSuccess,//登录成功
        loginError,//登录失败
        kickOff,//被服务端踢出，账号重复登录
        serverACK,//服务器消息确认ack

        //进入队列成功状态
        onlineQueueSuccess,//上线不等待
        waitQueueSuccess,//上线且等待
        backUpQueueSuccess,//上线到backup队列

        offlineWaitQueue,//离开等待队列 准备进入到线上队列
        offlineBackUpQueue,//离开backup队列

        //进入队列失败状态
        onlineQueueError,//进入线上队列失败
        waitQueueError,//进入等待队列失败
        backUpQueueError//进入backup队列失败
    }
}

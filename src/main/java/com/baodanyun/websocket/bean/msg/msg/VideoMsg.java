package com.baodanyun.websocket.bean.msg.msg;

import com.baodanyun.websocket.bean.msg.Msg;

/**
 * Created by yutao on 2016/7/12.
 */
public class VideoMsg extends Msg {

    public VideoMsg(String content) {
        setContentType(MsgContentType.video.toString());
        setContent(content);
    }

}

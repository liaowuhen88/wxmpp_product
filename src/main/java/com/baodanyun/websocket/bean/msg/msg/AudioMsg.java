package com.baodanyun.websocket.bean.msg.msg;

import com.baodanyun.websocket.bean.msg.Msg;

/**
 * Created by yutao on 2016/7/12.
 */
public class AudioMsg extends Msg {

    public AudioMsg(String content) {
        setContentType(MsgContentType.audio.toString());
        setContent(content);
    }

}

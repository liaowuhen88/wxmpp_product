package com.baodanyun.websocket.bean.msg.msg;

import com.baodanyun.websocket.bean.msg.Msg;

/**
 * Created by yutao on 2016/7/12.
 */
public class VoiceMsg extends Msg {

    public VoiceMsg(String content) {
        setContentType(MsgContentType.voice.toString());
        setContent(content);
    }

}

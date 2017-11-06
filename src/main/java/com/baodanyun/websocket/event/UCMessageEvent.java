package com.baodanyun.websocket.event;

import com.baodanyun.websocket.bean.apiBean.ExpandMsg;

/**
 * Created by liaowuhen on 2017/10/23.
 */
public class UCMessageEvent {

    private ExpandMsg expandMsg;


    public ExpandMsg getExpandMsg() {
        return expandMsg;
    }

    public void setExpandMsg(ExpandMsg expandMsg) {
        this.expandMsg = expandMsg;
    }
}

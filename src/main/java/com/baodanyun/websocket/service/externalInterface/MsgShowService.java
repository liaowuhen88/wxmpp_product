package com.baodanyun.websocket.service.externalInterface;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.request.MaterialPageBean;
import com.baodanyun.websocket.bean.request.MsgShowBean;

/**
 * Created by liaowuhen on 2017/8/2.
 * <p/>
 * 群或者个人消息是否显示设置
 */
public interface MsgShowService {

    Response update(String appKey, String action, MsgShowBean re) throws Exception;

    Response getMaterial(String appKey, MaterialPageBean re) throws Exception;
}
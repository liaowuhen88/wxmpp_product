package com.baodanyun.websocket.service;

import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.model.OfmucroomKey;

/**
 * Created by liaowuhen on 2017/6/15.
 */
public interface OfmucroomService {
    Ofmucroom selectByPrimaryKey(OfmucroomKey key);

    Ofmucroom selectByPrimaryKey(Long serviceid, String name) throws BusinessException;
}

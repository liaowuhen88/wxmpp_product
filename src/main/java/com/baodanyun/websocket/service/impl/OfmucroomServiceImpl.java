package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.dao.OfmucroomMapper;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.model.OfmucroomKey;
import com.baodanyun.websocket.service.OfmucroomService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/6/15.
 */
@Service
public class OfmucroomServiceImpl implements OfmucroomService {

    @Autowired
    private OfmucroomMapper ofmucroomMapper;


    @Override
    public Ofmucroom selectByPrimaryKey(OfmucroomKey key) {
        return ofmucroomMapper.selectByPrimaryKey(key);
    }

    @Override
    public Ofmucroom selectByPrimaryKey(Long serviceid, String name) throws BusinessException {
        if (null == serviceid || StringUtils.isEmpty(name)) {
            throw new BusinessException("参数不能为空");
        }
        OfmucroomKey ok = new OfmucroomKey();
        ok.setServiceid(serviceid);
        ok.setName(name);
        return selectByPrimaryKey(ok);
    }
}

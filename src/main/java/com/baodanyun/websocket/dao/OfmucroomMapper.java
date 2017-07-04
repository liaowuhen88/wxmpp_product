package com.baodanyun.websocket.dao;

import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.model.OfmucroomKey;

public interface OfmucroomMapper {
    int deleteByPrimaryKey(OfmucroomKey key);

    int insert(Ofmucroom record);

    int insertSelective(Ofmucroom record);

    Ofmucroom selectByPrimaryKey(OfmucroomKey key);

    int updateByPrimaryKeySelective(Ofmucroom record);

    int updateByPrimaryKey(Ofmucroom record);
}
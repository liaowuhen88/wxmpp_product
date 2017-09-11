package com.baodanyun.websocket.dao;

import com.baodanyun.websocket.model.Ofvcard;

public interface OfvcardMapper {
    int deleteByPrimaryKey(String username);

    int insert(Ofvcard record);

    int insertSelective(Ofvcard record);

    Ofvcard selectByPrimaryKey(String username);

    int updateByPrimaryKeySelective(Ofvcard record);

    int updateByPrimaryKeyWithBLOBs(Ofvcard record);
}
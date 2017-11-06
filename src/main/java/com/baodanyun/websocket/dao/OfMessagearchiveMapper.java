package com.baodanyun.websocket.dao;

import com.baodanyun.websocket.model.OfMessagearchiveWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OfMessagearchiveMapper {
    int insert(OfMessagearchiveWithBLOBs record);

    List<OfMessagearchiveWithBLOBs> select(@Param("jid") String jid);

    int insertSelective(OfMessagearchiveWithBLOBs record);

    Long getGroupMessageCount(@Param("start") Long start, @Param("end") Long end);
}
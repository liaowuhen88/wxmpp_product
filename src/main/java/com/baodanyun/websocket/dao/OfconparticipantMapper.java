package com.baodanyun.websocket.dao;

import com.baodanyun.websocket.model.Ofconparticipant;

public interface OfconparticipantMapper {
    int insert(Ofconparticipant record);

    int insertSelective(Ofconparticipant record);

    Long getLeftdate(String bareJID);
}
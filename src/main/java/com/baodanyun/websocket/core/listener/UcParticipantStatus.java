package com.baodanyun.websocket.core.listener;

import org.apache.log4j.Logger;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

/**
 * Created by liaowuhen on 2017/6/8.
 */
public class UcParticipantStatus implements ParticipantStatusListener {
    private static Logger logger = Logger.getLogger(UcParticipantStatus.class);

    @Override
    public void joined(String participant) {
        logger.info("joined" + participant);
    }

    @Override
    public void left(String participant) {
        logger.info("left" + participant);
    }

    @Override
    public void kicked(String participant, String actor, String reason) {

    }

    @Override
    public void voiceGranted(String participant) {

    }

    @Override
    public void voiceRevoked(String participant) {

    }

    @Override
    public void banned(String participant, String actor, String reason) {

    }

    @Override
    public void membershipGranted(String participant) {

    }

    @Override
    public void membershipRevoked(String participant) {

    }

    @Override
    public void moderatorGranted(String participant) {

    }

    @Override
    public void moderatorRevoked(String participant) {

    }

    @Override
    public void ownershipGranted(String participant) {

    }

    @Override
    public void ownershipRevoked(String participant) {

    }

    @Override
    public void adminGranted(String participant) {

    }

    @Override
    public void adminRevoked(String participant) {

    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {

    }
}

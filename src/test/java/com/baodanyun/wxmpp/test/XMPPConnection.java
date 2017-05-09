package com.baodanyun.wxmpp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMPPConnection {
    protected static final Logger logger = LoggerFactory.getLogger(XMPPConnection.class);


    /*
    @Test
    public void group1() throws XMPPException, IOException, SmackException {
        AbstractXMPPConnection connection = connect();

        Roster roster = Roster.getInstanceFor(connection);
        roster.setRosterLoadedAtLogin(true);
        connection.login("zwv11132","111111");
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        roster.addRosterListener(new Rost());
        roster.createEntry("zwv11132@126xmpp", "zwv11132", new String[]{"friends"});

        RosterGroup group = roster.getGroup("friends");
        group(group);
       *//* Collection<RosterGroup> entriesGroup = roster.getGroups();
        for(RosterGroup group: entriesGroup){

        }*//*
    }

    class Rost implements RosterListener{

        @Override
        public void entriesAdded(Collection<String> addresses) {
            logger.info("---entriesAdded: ");
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {

        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {

        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    }

    */

}
package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.factory.XMPPConnectionFactory;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence;
import org.jivesoftware.smackx.xdata.Form;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class XMPPConnectionClientTest{
    protected static final Logger logger = LoggerFactory.getLogger(XMPPConnectionClientTest.class);


    private AbstractXMPPConnection conn;
    private Roster roster;
    private MultiUserChat muc;
    private static ChatManager chatManager;

    boolean  login = false;
    private String username = "yt";
    private String password = "111111";

    /**
     * 穿件连接
     *
     * @throws IOException-
     * @throws XMPPException
     * @throws SmackException
     */


    @Test
    public void connect() throws IOException, XMPPException, SmackException {
        conn = XMPPConnectionFactory.getXMPPConnectionNew();

        ConnectionListener connectionListener = new ConnectionListenerTest();
        //初始化的连接监听器
        conn.addConnectionListener(connectionListener);
        conn.connect();


    }

    /**
     * 登录 并且生成 chatManager,并且添加消息接收监听器
     * 重复登陆会报错
     * org.jivesoftware.smack.SmackException$AlreadyLoggedInException: Client is already logged in
     *
     * Presence presence = new Presence(Presence.Type.available);   //这里如果改成unavailable则会显示用户不在线
     presence.setStatus("Go fishing");
     conn.sendPacket(presence);
     */
    @Test
    public void login() throws XMPPException, IOException, SmackException {
        if(login){
            return ;
        }
        connect();


        conn.login(username,password);


    }


    @Test
    public void getGroup() throws XMPPException, IOException, SmackException {
        String frindName = "friends";
        Roster roster = Roster.getInstanceFor(conn);
        //roster.addRosterListener(new Rost());
        List<Presence> li = roster.getAllPresences("zwv11132@126xmpp");
        RosterGroup group = roster.getGroup(frindName);
        /*Collection<RosterGroup> entriesGroup = roster.getGroups();
        for(RosterGroup group: entriesGroup){
            group(group);
        }*/
        group(group);
    }


    public void group( RosterGroup group){
        Collection<RosterEntry> entries = group.getEntries();
        logger.info("---"+ group.getName());
        for (RosterEntry entry : entries) {
            logger.info("---user: "+entry.getUser());
            logger.info("---name: "+entry.getName());
            logger.info("---tyep: "+entry.getType());
            logger.info("---status: "+entry.getStatus());
            logger.info("---groups: "+entry.getGroups());
        }


    }


    /**
     * 注册账号
     */
    @Test
    public void createAccount() throws Exception {
        login();
        String username = "mmmm";
        AccountManager amgr = AccountManager.getInstance(conn);
        amgr.createAccount(username, "111111");
    }


    public void createAccount(String uname) throws Exception {
        login();
        AccountManager amgr = AccountManager.getInstance(conn);
        amgr.createAccount(uname, "111111");
    }


    /**
     * 当前登录用户添加好友
     */
    @Test
    public void createEntry() throws Exception {
        String username = "zwc_xxx";
        createAccount(username);
        Roster roster = Roster.getInstanceFor(conn);
        roster.createEntry(username+"@126xmpp", username, new String[]{"friends"});
        System.out.print(123);
    }


    /**
     * 发送加好友信息
     * 模拟账号添加好友
     * @param uname
     * @throws Exception
     */
    public void addFiends(String uname,String to) throws Exception {
        String[] groups = new String[]{"friends"};
        String user = uname + "@126xmpp";

        createAccount(uname);

        if (!conn.isAuthenticated()) {
            throw new SmackException.NotLoggedInException();
        } else if (conn.isAnonymous()) {
            throw new IllegalStateException("Anonymous users can\'t have a roster.");
        } else {
            RosterPacket rosterPacket = new RosterPacket();
            rosterPacket.setType(IQ.Type.set);
            RosterPacket.Item item = new RosterPacket.Item(user, uname);
            if (groups != null) {
                String[] presencePacket = groups;
                int var8 = groups.length;

                for (int var9 = 0; var9 < var8; ++var9) {
                    String group = presencePacket[var9];
                    if (group != null && group.trim().length() > 0) {
                        item.addGroupName(group);
                    }
                }
            }

            rosterPacket.addRosterItem(item);

            ExtensionElement ex = new ExtensionElement() {
                //用户信息元素名称
                private String elementName = "need";

                @Override
                public CharSequence toXML() {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<");
                    sb.append(elementName);
                    sb.append(">");
                    sb.append(true);
                    sb.append("</");
                    sb.append(elementName);
                    sb.append(">");
                    return sb.toString();
                }
                @Override
                public String getElementName() {
                    return elementName;
                }
                @Override
                public String getNamespace() {
                    return  null;
                }
            };
            rosterPacket.addExtension(ex);

            // 模拟客服添加欺骗账号
            rosterPacket.setFrom(to);
            conn.sendStanza(rosterPacket);

            // 添加订阅
            Presence var11 = new Presence(Presence.Type.subscribe);
            var11.setTo(user);
            var11.setFrom(to);

            conn.sendStanza(var11);

            // 发送在线信息

            Presence online = new Presence(Presence.Type.available);
            online.setFrom(user);
            online.setTo(to);
            conn.sendStanza(online);
        }


    }

    /**
     * 发送消息好友
     */
    @Test
    public void sendMessage() throws Exception {

        // 默认插件
        String agent = "yt";
        // 客服地址
        String to ="zwc@126xmpp";
        // 待伪装名称
        String name ="zwc33344555667777888999";
        // 真实名称
        String realName = agent+"_"+name;
        try{
            addFiends(realName,to);
        }catch (Exception e){
            e.printStackTrace();
        }

        while(true){

            Thread.sleep(10000);

            Message msg = new Message();
            msg.setFrom(realName + "@126xmpp");
            msg.setType(Message.Type.chat);
            msg.setTo(to);
            msg.setBody("4576");
            conn.sendStanza(msg);
        }


    }

    @Test
    public void sendMessageGroup() throws Exception {

        login();
        // 默认插件
        // 客服地址
        String to ="zwc@126xmpp";
        String agent = "zwc1@126xmpp";
        // 待伪装名称
        String groupNum = UUID.randomUUID().toString();
        // 真实名称
        String realName1 ="zwc1_1@126xmpp/Smack";
        String realName2 ="zwc1_2@126xmpp/Smack";
        String realName3 ="zwc1_3@126xmpp/Smack";
        String realName4 ="zwc1_4@126xmpp/Smack";

        String groupName = "22" ;
        String group = groupName+"@conference.126xmpp";

        joinMultiUserChat("agent",null,groupName);
        inviteJoinMultiUserChat(to,"kf");

        //JoinMultiUserChat("agent",agent,group);
        //JoinMultiUserChat("kf",to,group);
      /*  createAccount("zwc1_1");
        createAccount("zwc1_2");
        createAccount("zwc1_3");
        createAccount("zwc1_4");*/

        JoinMultiUserChat("真实用户1",realName1,group);
        JoinMultiUserChat("真实用户2",realName2,group);
        JoinMultiUserChat("真实用户3",realName3,group);
        JoinMultiUserChat("真实用户4",realName4,group);
        /*createRoom(realName,group,"nickname");*/


        muc.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                System.out.println(message);
           }
        });


        while (true){
            try {
                Thread.sleep(10000);
                Message msg1 = new Message();
                msg1.setType(Message.Type.groupchat);
                msg1.setTo(group);
                msg1.setBody("111111");
                msg1.setFrom(realName1);

                conn.sendStanza(msg1);

                Message msg2 = new Message();
                msg2.setType(Message.Type.groupchat);
                msg2.setBody("222222");
                msg2.setTo(group);
                msg2.setFrom(realName2);

                conn.sendStanza(msg2);


                Message msg3= new Message();
                msg3.setType(Message.Type.groupchat);
                msg3.setBody("3333333");
                msg3.setTo(group);
                msg3.setFrom(realName3);

                conn.sendStanza(msg3);

                Message msg4= new Message();
                msg4.setType(Message.Type.groupchat);
                msg4.setBody("444444");
                msg4.setFrom(realName4);
                msg4.setTo(group);
                conn.sendStanza(msg4);

                Message msg5= new Message();
                msg5.setType(Message.Type.groupchat);
                msg5.setBody("11");
                msg5.setTo(group);
                msg5.setFrom(realName1);

                conn.sendStanza(msg5);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



       /* ChatManagerListenerTest mt = new ChatManagerListenerTest();


        chatManager.addChatListener(mt);

        ChatMessageListenerTest li = new ChatMessageListenerTest();

        Chat newChat = chatManager.createChat(toUsername, li);

        newChat.sendMessage(msg);

        System.out.print(123);*/
    }

    /**
     * 当前登录用户加入会议室
     *
     * @param user 昵称
     * @param password 会议室密码
     * @param roomsName 会议室名
     * @param
     */
    public MultiUserChat joinMultiUserChat(String user, String password, String roomsName) throws  Exception {
        try {

            // 使用XMPPConnection创建一个MultiUserChat窗口
            final MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);
            String room = roomsName + "@conference." + conn.getServiceName();
            muc = manager.getMultiUserChat(room);

            // 聊天室服务将会决定要接受的历史记录数量
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            // 用户加入聊天室
            muc.join(user, password, history, conn.getPacketReplyTimeout());
            System.out.println("会议室加入成功........");
            return muc;
        } catch (XMPPException e) {
            e.printStackTrace();
            System.out.println("会议室加入失败........");
            return null;
        }
    }


    /**
     * 邀请用户加入群
     *
     * @param
     */
    public void inviteJoinMultiUserChat(String to,String nickName) throws  Exception {
             muc.invite(to,nickName);

    }

    /**
     * 伪装用户加入群
     *
     * @param
     */
    public void JoinMultiUserChat(String nickname,String from,String room) throws  Exception {
        // We enter a room by sending a presence packet where the "to"
        // field is in the form "roomName@service/nickname"
        Presence joinPresence = new Presence(Presence.Type.available);
        joinPresence.setTo(room + "/" + nickname);
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);

        MUCInitialPresence.History mucHistory = new MUCInitialPresence.History();

        // Indicate the the client supports MUC
        MUCInitialPresence mucInitialPresence = new MUCInitialPresence();
        if (password != null) {
            mucInitialPresence.setPassword(password);
        }
        if (history != null) {
            mucInitialPresence.setHistory(mucHistory);
        }
        joinPresence.addExtension(mucInitialPresence);

        // Wait for a presence packet back from the server.
      /*  StanzaFilter responseFilter = new AndFilter(FromMatchesFilter.createFull(room + "/"
                + nickname), new StanzaTypeFilter(Presence.class));

        Presence presence;
        try {
            presence = conn.createPacketCollectorAndSend(responseFilter, joinPresence).nextResultOrThrow(conn.getPacketReplyTimeout());
        }
        catch (SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
            // Ensure that all callbacks are removed if there is an exception
            throw e;
        }*/
        joinPresence.setFrom(from);
       conn.sendStanza(joinPresence);
        // Update the list of joined rooms

    }


    public void createRoom(String realname,String room,String nickname) throws  Exception{
        try {
            MultiUserChatManager mcm = MultiUserChatManager.getInstanceFor(conn);
            MultiUserChat muc = mcm.getMultiUserChat(room);

            //创建聊天室
            muc.create("testbot");

            // 发送一个空表单配置这显示我们想要一个instant room

            // 获得聊天室的配置表单
            Form form = muc.getConfigurationForm();
            // 根据原始表单创建一个要提交的新表单。
            Form submitForm = form.createAnswerForm();
           /* // 向要提交的表单添加默认答复
            for (Iterator fields = form.getFields(); fields.hasNext();) {
                FormField field = (FormField) fields.next();
                if (!FormField.TYPE_HIDDEN.equals(field.getType())
                        && field.getVariable() != null) {
                    // 设置默认值作为答复
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }*/
            // 设置聊天室的新拥有者
            // List owners = new ArrayList();
            // owners.add("liaonaibo2\\40slook.cc");
            // owners.add("liaonaibo1\\40slook.cc");
            // submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            // 能够发现占有者真实 JID 的角色
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            // 允许使用者修改昵称
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            // 允许用户注册房间
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            // 发送已完成的表单（有默认值）到服务器来配置聊天室
            muc.sendConfigurationForm(submitForm);

           // 假如聊天室
            muc.join("test");
            //enter(realname,room,nickname);


            System.out.println("会议室加入成功........");

        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }


    private Presence enter(String from,String room,String nickname
                          ) throws SmackException.NotConnectedException, SmackException.NoResponseException,
            XMPPException.XMPPErrorException {
        StringUtils.requireNotNullOrEmpty(nickname, "Nickname must not be null or blank.");
        // We enter a room by sending a presence packet where the "to"
        // field is in the form "roomName@service/nickname"
        Presence joinPresence = new Presence(Presence.Type.available);
        joinPresence.setTo(room + "/" + nickname);

        // Indicate the the client supports MUC
        MUCInitialPresence mucInitialPresence = new MUCInitialPresence();
        if (password != null) {
            mucInitialPresence.setPassword(password);
        }

        joinPresence.setFrom(from);
        joinPresence.addExtension(mucInitialPresence);

        // Wait for a presence packet back from the server.
        StanzaFilter responseFilter = new AndFilter(FromMatchesFilter.createFull(room + "/"
                + nickname), new StanzaTypeFilter(Presence.class));

        Presence presence;
        try {
            presence = conn.createPacketCollectorAndSend(responseFilter, joinPresence).nextResultOrThrow(conn.getPacketReplyTimeout());
        }
        catch (SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
            // Ensure that all callbacks are removed if there is an exception
            throw e;
        }

        return presence;
    }

    /**

     *//**
     * 使用了createOrJoin。
     * @param roomName
     *//*
    public void joinRoom(String roomName){

        try {
            // Get the MultiUserChatManager
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);

            // Create a MultiUserChat using an XMPPConnection for a room

            String servicename="";
            if("".equals(serviceName.getText())){
                servicename="@127.0.0.1";
            }else{
                servicename=roomName+"@"+serviceName.getText()+".127.0.0.1";
            }
            MultiUserChat muc = manager.getMultiUserChat(servicename);

            // User2 joins the new room using a password and specifying
            // the amount of history to receive. In this example we are requesting the last 5 messages.
            DiscussionHistory history = new DiscussionHistory();
            //不设置即接受所有聊天记录
//          history.setMaxStanzas(5);
            muc.join(username, "", history, conn.getPacketReplyTimeout());
            muc.addMessageListener(new MessageListener() {

                @Override
                public void processMessage(Message msg) {
                    System.out.println("join");
                    textArea.append(msg.getFrom()+":"+msg.getBody());
                    textArea.append("\n");
                    textArea.append("\n");
                    DelayInformation d=(DelayInformation) msg.getExtension("urn:xmpp:delay");
                    //若是自己发送的消息，那stamp==null

                }
            });


            textArea.append("已经加入聊天室"+roomName);
            textArea.append("\n");
        } catch (XMPPErrorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoResponseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void quitRoom(){
        try {
            // Get the MultiUserChatManager
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);

            String servicename="";
            if("".equals(serviceName.getText())){
                servicename="@127.0.0.1";
            }else{
                servicename=roomnameField.getText()+"@"+serviceName.getText()+".127.0.0.1";
            }

            MultiUserChat muc = manager.getMultiUserChat(servicename);
            if(muc.isJoined()){
                muc.leave();
                textArea.append(muc.getNickname()+"离开"+muc.getRoom());
                textArea.append("\n");
            }

        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

    *//**
     * * 注册用户信息 * @param user 账号，是用来登陆用的，不是用户昵称 * @param password 账号密码 * @param
     * attributes 账号其他属性，参考AccountManager.getAccountAttributes()的属性介绍 * @return
     *//*
    public boolean registerUser(String user, String password, Map<String, String> attributes) {
        if (!conn.isConnected()) {
            return false;
        }
        try {

            AccountManager.sensitiveOperationOverInsecureConnectionDefault(false);
            AccountManager.getInstance(conn).createAccount(user, password,attributes);
            textArea.append("注册成功!\n");
            return true;
        } catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
            e.printStackTrace();
            textArea.append("注册失败!\n");
            return false;
        }
    }

    public void disConnented(){
        if(conn!=null){
            conn.disconnect();
            textArea.append("注销成功!");
            textArea.append("\n");
            this.setTitle("未登录");
        }

    }

    *//**
     * 创建并加入聊天室
     *//*
    public void createRoom(){
        try{
            // Get the MultiUserChatManager
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);

            String servicename="";
            if("".equals(serviceName.getText())){
                servicename="roomnameField.getText()+@127.0.0.1";
            }else{
                servicename=roomnameField.getText()+"@"+serviceName.getText()+".127.0.0.1";
            }

            // Create a MultiUserChat using an XMPPConnection for a room
            MultiUserChat muc =  manager.getMultiUserChat(servicename);

            // Create the room
            muc.create(username);


            // Get the the room's configuration form
            Form form = muc.getConfigurationForm();
            // Create a new form to submit based on the original form
            Form submitForm = form.createAnswerForm();
            // Add default answers to the form to submit
            for (Iterator fields = form.getFields().iterator(); fields.hasNext();) {
                FormField field = (FormField) fields.next();

                if (!FormField.Type.hidden.equals(field.getType()) && field.getVariable() != null) {
                    // Sets the default value as the answer
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", false);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            // 能够发现占有者真实 JID 的角色
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", false);
            // 允许使用者修改昵称
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            // 允许用户注册房间
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            //房间最大人数，0表示无限制
            List<String> maxPeopleList=new ArrayList<>();
            maxPeopleList.add("0");
            submitForm.setAnswer("muc#roomconfig_maxusers",maxPeopleList);
            // Send the completed form (with default values) to the server to configure the room
            muc.sendConfigurationForm(submitForm);

            muc.addMessageListener(new MessageListener() {

                @Override
                public void processMessage(Message msg) {
                    textArea.append(msg.getFrom()+":"+msg.getBody());
                    textArea.append("\n");
                    textArea.append("\n");
                    DelayInformation d=(DelayInformation) msg.getExtension("urn:xmpp:delay");
                    //若是自己发送的消息，那stamp==null
                    if(d != null){
                        System.out.println(d.getStamp());
                    }

                }
            });

            textArea.append(roomnameField.getText()+"创建成功!");
            textArea.append("\n");
        }catch(Exception e){
            textArea.append("createRoom.error\n");
            e.printStackTrace();
        }
    }

    public void findExistRooms(){
        try {
            // Get the MultiUserChatManager
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);

            // Discover information about the room roomName@conference.myserver
            String servicename="";
            if("".equals(serviceName.getText())){
                servicename="127.0.0.1";
            }else{
                servicename=serviceName.getText()+".127.0.0.1";
            }
            List<HostedRoom> hostedRooms = manager.getHostedRooms(servicename);
            for(HostedRoom r:hostedRooms){

                textArea.append(r.getJid());
                textArea.append(":");
                textArea.append(r.getName());
                textArea.append("\n");
                textArea.append("\n");
            }
        } catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendgroupMessage(){
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);
            MultiUserChat muc = manager.getMultiUserChat(roomnameField.getText()+"@"+serviceName.getText()+".127.0.0.1");

            muc.sendMessage(message.getText());
        } catch (NotConnectedException e) {

            e.printStackTrace();
        }
    }*/

    /**
     * 查询会议室成员名字
     * @param muc
     */
    public static List<String> findMulitUser(MultiUserChat muc){
        List<String> listUser = new ArrayList<String>();
        Iterator<String> it = muc.getOccupants().iterator();
        //遍历出聊天室人员名称
        while (it.hasNext()) {
            // 聊天室成员名字
            String name = it.next();
            listUser.add(name);
        }
        return listUser;
    }


}
package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.core.listener.UcMessageListener;
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
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class XMPPConnectionClientTest {
    protected static final Logger logger = LoggerFactory.getLogger(XMPPConnectionClientTest.class);
    private static ChatManager chatManager;
    boolean login = false;
    int i = 0;
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
            return null;
        }
    };
    private AbstractXMPPConnection conn;
    private Roster roster;
    private MultiUserChat muc;
    private String username = "yt";
    private String password = "111111";

    /**
     * 查询会议室成员名字
     *
     * @param muc
     */
    public static List<String> findMulitUser(MultiUserChat muc) {
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
     * <p/>
     * Presence presence = new Presence(Presence.Type.available);   //这里如果改成unavailable则会显示用户不在线
     * presence.setStatus("Go fishing");
     * conn.sendPacket(presence);
     */
    @Test
    public void login() throws XMPPException, IOException, SmackException {
        if (login) {
            return;
        }
        connect();

        conn.login(username, password);

        login = true;
    }

    public void login(String userName) throws XMPPException, IOException, SmackException {
        if (login) {
            return;
        }
        connect();


        conn.login(userName, password);


    }

    @Test
    public void loginKf() throws XMPPException, IOException, SmackException, InterruptedException {
        if (login) {
            return;
        }
        connect();

        conn.login("zwc", password);

        Thread.sleep(1000000);
    }

    /**
     * 直接获取群失败
     *
     * @throws XMPPException
     * @throws IOException
     * @throws SmackException
     */
    @Test
    public void getAllGroup() throws XMPPException, IOException, SmackException {

        login();

        Roster roster = Roster.getInstanceFor(conn);


        Collection<RosterGroup> entriesGroup = roster.getGroups();
        for (RosterGroup group : entriesGroup) {
            group(group);
        }

    }

    @Test
    public void getGroup() throws XMPPException, IOException, SmackException {
        login();
        String frindName = "friends";
        Roster roster = Roster.getInstanceFor(conn);
        roster.getEntries();
        //roster.addRosterListener(new Rost());
        List<Presence> li = roster.getAllPresences(username + "@126xmpp");
        RosterGroup group = roster.getGroup(frindName);
        /*Collection<RosterGroup> entriesGroup = roster.getGroups();
        for(RosterGroup group: entriesGroup){
            group(group);
        }*/
        group(group);
    }

    public void group(RosterGroup group) throws XMPPException, IOException, SmackException {
        login();
        Collection<RosterEntry> entries = group.getEntries();
        logger.info("---" + group.getName());
        for (RosterEntry entry : entries) {
            logger.info("---user: " + entry.getUser());
            logger.info("---name: " + entry.getName());
            logger.info("---tyep: " + entry.getType());
            logger.info("---status: " + entry.getStatus());
            logger.info("---groups: " + entry.getGroups());
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
        roster.createEntry(username + "@126xmpp", username, new String[]{"friends"});
        System.out.print(123);
    }

    /**
     * 发送加好友信息
     * 模拟账号添加好友
     *
     * @param uname
     * @throws Exception
     */
    public void addFiends(String uname, String to) throws Exception {
        String[] groups = new String[]{"friends"};
        String user = uname + "@126xmpp";

        login();
        //createAccount(uname);

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
     * updateVcard
     */
    @Test
    public void updateVcard() throws Exception {

        // 默认插件
        String agent = "yt";
        // 客服地址
        String to = "zwc@126xmpp";
        // 待伪装名称
        String name = "zwc333";
        // 真实名称
        login();
        VCard vCard = new VCard();
        vCard.setNickName("test123");
        vCard.setFrom("yt_yt123@126xmpp");
        // vCard.setFrom("yt@126xmpp");
        vCard.setType(IQ.Type.set);
        vCard.addExtension(ex);
        conn.sendStanza(vCard);


    }

    /**
     * 发送消息好友
     */
    @Test
    public void sendMessage() throws Exception {

        // 默认插件
        String agent = "yt";
        // 客服地址
        String to = "zwc@126xmpp";
        // 待伪装名称
        String name = "zwc";
        // 真实名称
        String realName = agent + "_" + name;
        try {
            addFiends(realName, to);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = 0;
        while (true) {

            Thread.sleep(10000);

            Message msg = new Message();
            String resorce = "/A\u003cspan class\u003d\"emoji emoji1f33f\"\u003e\u003c/span\u003e 叶子\u003cspan class\u003d\"emoji emoji1f49e\"\u003e\u003c/span\u003e \u003cspan class\u003d\"emoji emoji1f33c\"\u003e\u003c";
            msg.setFrom(realName + "@126xmpp" + resorce);
            msg.setType(Message.Type.chat);
            msg.setTo(to);
            msg.setBody("__" + i++);
            conn.sendStanza(msg);
        }
    }

    @Test
    public void sendMessageGroup() throws Exception {

        login();
        // 默认插件
        // 客服地址
        // String to ="testof@126xmpp";

        String to = "zwc@126xmpp";
        String agent = "yt";
        // 待伪装名称
        String groupNum = UUID.randomUUID().toString();
        // 真实名称
        String name1 = agent + "_aaaa";
        String name2 = agent + "_bbbb";
        String name3 = agent + "_cccc";
        String name4 = agent + "_dddd";

        String realName1 = name1 + "@126xmpp/Smack";
        String realName2 = name2 + "@126xmpp/Smack";
        String realName3 = name3 + "@126xmpp/Smack";
        String realName4 = name4 + "@126xmpp/Smack";

        String groupName = "666";
        String group = groupName + "@conference.126xmpp";

        joinMultiUserChat("agent", null, groupName);
        inviteJoinMultiUserChat(to, "kf");

        /**
         *   用户必须是假的，不能注册过
         */

        JoinMultiUserChat("真实用户XX", realName1, group);
        JoinMultiUserChat("真实用户YY", realName2, group);
        JoinMultiUserChat("真实用户RR", realName3, group);
        JoinMultiUserChat("真实用户TT", realName4, group);
        /*createRoom(realName,group,"nickname");*/

        MessageListener messageListener = new UcMessageListener(null, new Visitor(), null, null, null);
        muc.addMessageListener(messageListener);

        while (true) {
            try {
                Thread.sleep(10000);
                inviteJoinMultiUserChat(to, "kf");

                Message msg1 = new Message();
                msg1.setType(Message.Type.groupchat);
                msg1.setTo(group);
                msg1.setBody("http://duobaojl.oss-cn-hangzhou.aliyuncs.com/wechat2017/2418708985930446407.mp3");
                msg1.setFrom(realName1);
                msg1.setSubject("audio");
                conn.sendStanza(msg1);

            /*  Message msg2 = new Message();
                msg2.setType(Message.Type.groupchat);
                msg2.setBody("msg2-------" + get());
                msg2.setTo(group);
                msg2.setFrom(realName2);
                conn.sendStanza(msg2);*/

                Message msg3 = new Message();
                msg3.setType(Message.Type.groupchat);
                msg3.setBody("msg3-------" + get());
                msg3.setTo(group);
                msg3.setSubject("image");
                msg3.setFrom(realName3);
                conn.sendStanza(msg3);

                Message msg4 = new Message();
                msg4.setType(Message.Type.groupchat);
                msg4.setBody("http://duobaojl.oss-cn-hangzhou.aliyuncs.com/wechat2017/2843883166980356279.mp4");
                msg4.setFrom(realName4);
                msg4.setTo(group);
                msg4.setSubject("video");
                conn.sendStanza(msg4);

                Message msg5 = new Message();
                msg5.setType(Message.Type.groupchat);
                msg5.setBody("邀请你加入群聊<br/>互粉大群(500人)<br/>1、千人互粉群<br/>https://a.meipian.me/55fyfvn<br/><br/>2、点赞互粉群<br/>https://a.meipian.me/55fx6xb<br/><br/>3、帅哥美女聊天群<br/>https://a.meipian.me/55fwbdr<br/><br/>4、辣妈互粉群<br/>https://a.meipian.me/55fucch<br/><br/>5、宝妈萌娃互粉群<br/>https://a.meipian.me/55ftevo<br/><br/>6、产品咨询代理群<br/>https://a.meipian.me/55fszvg<br/><br/>7、小白创业任务群<br/>https://a.meipian.me/55fqdxl<br/><br/>8、任务交接资源群<br/>https://a.meipian.me/55fpt78<br/><br/>9、关验证互粉一族群<br/>https://a.meipian.me/55fp9jl<br/><br/>10、宝妈互粉群<br/>https://a.meipian.me/55foqno<br/><br/>11、徽商小白交流互粉群<br/>https://a.meipian.me/55fnx9w<br/><br/>12、大学生创业互粉群<br/>https://a.meipian.me/55fjg7h<br/>  <br/><br/>互粉大群（500人）<br/>邀请你加入群聊" + get());
                msg5.setTo(group);
                msg5.setFrom(realName1);
                conn.sendStanza(msg5);

                Message msg6 = new Message();
                msg6.setType(Message.Type.groupchat);
                msg6.setBody("<span class=\"emoji emoji1f498\"></span><span class=\"emoji emoji1f498\"></span><span class=\"emoji emoji1f498\"></span>微商必备加人神器<br/> 　　全国定位：百姓活粉<br/> 　 　 强行加粉：强行被加<br/>　　　　无需验证：直接通过<br/>　　　　日加千人：不是梦想<br/>　　　　防封加密：删除僵尸<br/>　　　　全部自动：操作简单<br/> 　 苹果免越狱：转小视频<br/>　　　朋友圈点赞：强加附近人<br/>　　　强行加人版：强行倍加版<br/>　教你一天怎样加1000人先看视频http://u1994106.jisuwebapp.com/s?id=3203388　 →售4000人好友V信号 ←<br/> 未绑定 可选全女：全男<br/> 保证精粉 ：无僵尸粉<br/> 广招代理 ：加好友聊<br/> 咨询加微信 :nry4513<br/> 咨询加微信 :nry4513<br/> 咨询加微信 :nry4513<br/> ▃▃▃▃▃▃▃▃▃▃<br/> 重点 认准正版" + get());
                msg6.setTo(group);
                msg6.setFrom(realName1);
                conn.sendStanza(msg6);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化聊服务会议列表
     */
    @Test
    public void initHostRoom() throws XMPPException, IOException, SmackException {
        login("zwc");
        Collection<HostedRoom> hostrooms;

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);

        //manager.getJoinedRooms("");
        Set<String> romms = manager.getJoinedRooms();

        logger.info(romms.toString());

        logger.info(manager.getServiceNames().toString());

    }

    public int get() {
        return i++;
    }

    /**
     * 当前登录用户加入会议室
     *
     * @param user      昵称
     * @param password  会议室密码
     * @param roomsName 会议室名
     * @param
     */
    public MultiUserChat joinMultiUserChat(String user, String password, String roomsName) throws Exception {
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
    public void inviteJoinMultiUserChat(String to, String nickName) throws Exception {
        muc.invite(to, nickName);

    }

    /**
     * 伪装用户加入群
     *
     * @param
     */
    public void JoinMultiUserChat(String nickname, String realName, String room) throws Exception {
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
        joinPresence.setFrom(realName);
        conn.sendStanza(joinPresence);
        // Update the list of joined rooms

    }

    public void createRoom(String realname, String room, String nickname) throws Exception {
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

    private Presence enter(String from, String room, String nickname
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
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
            // Ensure that all callbacks are removed if there is an exception
            throw e;
        }

        return presence;
    }

}
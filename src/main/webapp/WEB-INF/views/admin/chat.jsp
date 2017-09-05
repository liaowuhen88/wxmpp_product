<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%
    String path = request.getContextPath();
%>
<head>
    <meta charset="UTF-8"/>
    <title>豆包客服</title>
    <script>
        window.base = '<%=path%>';
    </script>
    <jsp:include page="/tpl/common/commonjs.jsp"/>
    <jsp:include page="common/common.jsp"/>
    <link type="text/css" href="<%=request.getContextPath()%>/resouces/js/webuploader/css/webuploader.css"/>
    <script src="<%=request.getContextPath()%>/resouces/js/webuploader/webuploader.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/js/webuploader/uploadimg.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/js/webuploader/uploadAttachment.js"></script>

    <script src="<%=request.getContextPath()%>/resouces/ajax/common.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/scripts/laypage/laypage.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/scripts/chat.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/scripts/laypage/laypage.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/scripts/history.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/scripts/set.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/scripts/leaveMessage.js"></script>
    <script src="<%=request.getContextPath()%>/resouces/scripts/friendAndGroup.js"></script>

    <script>
        $(function () {
            window.user = ${user};
            window.currentId = window.user.id;
            var chat = new Chat();
            var history = new History();
            //var set = new Set();
            var leaveMessage = new LeaveMessage();
            //页面初始化建立连接
            xchat.connect();
            chat.init();
            history.init();
            //set.init();
            leaveMessage.init();

            var friendAndGroup = new FriendAndGroup();
            friendAndGroup.init();
        });
    </script>
</head>

<body>
<%-- 历史记录 --%>
<jsp:include page="history.jsp"/>
<%-- 个人中心设置 --%>
<jsp:include page="set.jsp"/>

<%-- 好友 --%>
<jsp:include page="friendAndGroup.jsp"/>

<%-- 离线消息 --%>
<jsp:include page="leaveMessage.jsp"/>
<%-- 等待列表 --%>

<div style="
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background: rgba(3,3,3,0.8);
    z-index: 999;
    text-align: center;
" id="onlineStatus">
    <div style="
    background: #fff;
    font-size: 25px;
    padding: 20px;
    margin: 200px auto 0;
    display: inherit;
    width: 500px;
">
        您已掉线,请刷新
    </div>
</div>


<div class="modal" data-target="holdListBtn">
    <div class="modal_backdrop"></div>
    <div class="modal_dialog">
        <div class="modal_content">
            <div class="modal_header">
                <span class="modal_close">X</span>待接入
            </div>
            <div class="modal_body">
                <ul class="holdList" id="holdListCont">
                </ul>
                <div class="pages" id="holdListPages"></div>
            </div>
        </div>
    </div>
</div>
<audio id="msgTipAudio" src="<%=request.getContextPath()%>/resouces/audio/tip.wav"></audio>
<div id="bg"></div>
<input type="hidden" value="<%=request.getContextPath()%>" id="baseUrl"/>
<%-- sidebar --%>
<jsp:include page="/tpl/common/sidebar.jsp"/>
<div class="wrapper chat">
    <%-- chat-list --%>
    <jsp:include page="left/chat-list.jsp"/>
    <%-- chat-window --%>
    <jsp:include page="window/chat-window.jsp"/>
    <%-- chat-detail --%>
    <jsp:include page="right/chat-detail.jsp"/>
</div>
</body>

</html>

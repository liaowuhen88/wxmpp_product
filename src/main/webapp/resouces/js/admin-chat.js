/**
 * xChat 事件重写
 */

//当前打开的窗口,值为当前菜单的id
var destJid;
var customerInfo = {};
var groupUsers = {};
var base = window.base;
var xchat = new xChat({
    url: base + '/sockjs/customer/chat',
    ut: 'customer',
    lu: window.currentId,
    ic: customerInfo.icon,
    nk: customerInfo.userName
});

// 统计有多少用户未回
xchat.recvMsg = [];
// 统计一个用户有多少消息未读
xchat.recvMsgOne = {};

//控件
xchat.controls = {
    chatShow: 'chatShow',
    chatTimeline: 'chatTimeline',
    holdListCont: '#holdListCont',
    defaultAvatar: window.base + '/resouces/images/default-avatar.jpg',
    defaultThumburl: window.base + '/resouces/images/no_cover.png',
    friendList: '#friendList',
    historyFriendList: '#historyFriendList',
    backupfriendList: '#backupfriendList',
    msgContainer: '#chatMsgContainer',
    turnList: 'turn-list',
    userTags: '#userTags',
    remark: '#remark',
    remarkSubmit: '#remarkSubmit',
    currentChatId: '#currentChatId',
    claimsContainer: '#claimsContainer',
    contractsContainer: '#contractsContainer',
    orderContainer: '#orderContainer',
    waitReplyPerson: '#waitReplyPerson',
    settingAll: '#setting_all',
    settingAllFrom: '#setting_all_from',
    searchConversationInput: '#searchConversationInput'


};
/*-------------------------------------------------------------*/
/*-------------------------------------------------------------*/
/*-------------------------------------------------------------*/
/*------------------------------接口---------------------------*/
/*-------------------------------------------------------------*/
/*-------------------------------------------------------------*/
/*-------------------------------------------------------------*/

xchat.interface = {
    holdList: base + '/api/queue/2',
    callIn: base + '/api/visitorUp/',
    offConnect: base + '/api/visitorOff',
    login: base + '/customerlogin',
    loadChatList: base + '/api/queue/1',
    userInfo: base + '/api/visitorDetail',
    getCustomerList: base + '/api/onlineCustomerList',
    getGroupUsers: base + '/api/getGroupUsers',
    turn: base + '/api/changeVisitorTo',
    updateUserInfo: base + '/api/upVisitorInfo',
    getTagsAll: base + '/api/getTagsAll',
    conversationInit: base + '/api/conversationInit',
    changeProfile: base + '/api/upCustomerInfo',
    closeFriendWindow: base + '/api/closeFriendWindow',
    getConversation: base + '/api/getConversation',
    getGroupUsers: base + '/api/getGroupUsers',
    getDisplay: base + '/api/getDisplay',
    msgShow: base + '/api/msgShow',
    getMaterial: base + '/api/getMaterial'
};
/*=====================================================================================初始化=====================================================================================*/
//登录成功
xchat.loginSuccessStatusHandelEvent = function () {
    this.setCustomerProfileEventBind();     //设置客服信息
    this.sendImgEventBind();    //初始化图片发送
    this.sendAttachmentEventBind();    //初始化图片发送
    this.searchConversationEventBind();    //搜索会话事件绑定

};
//初始化成功
xchat.initSuccessQueueStatusHandelEvent = function () {
    this.loadChatList();    //对话列表加载
    this.loadChatListEvent();   //对话列表事件绑定
    this.sendEventBind();   //发送消息事件绑定
    // 因为本地缓存和数据库消息对不上，所以这里去除
    // this.loadHistoryEventBind();    //加载历史消息事件绑定
    this.messageListEventBind();    //快速回复事件绑定
    this.holdListEventBind();    //接入列表事件绑定
    this.closeEventBind();  //关闭对话事件绑定
    this.callInEventBind(); //接入对话事件绑定
    //this.turnEventBind();   //转接事件绑定
    this.getCustomerList();     //获取客服列表
    //this.setUserInfoEventBind();    //设置用户详情事件绑定
    //this.customerListEventBind();//转接按钮事件
    //this.library_init();// 加载素材库
    this.groupSearchBind();//群搜索事件绑定
    this.setting_allEventBind();//扣费设置按钮

};
//初始化失败
xchat.initErrorStatusHandelEvent = function () {
};
//登录失败
xchat.loginErrorStatusHandelEvent = function () {
};
//客服个人信息设置
xchat.setCustomerProfileEventBind = function () {
    myUtils.load(window.base + "/api/getVcard", 'get', function (response) {
        customerInfo = response.data;
        if (customerInfo.icon !== null && customerInfo.icon !== '' && customerInfo.icon !== 'ic') {
            //window.user.icon = customerInfo.icon;
        }
        //window.user.username = customerInfo.username;
    }, {});
};
/*=====================================================================================初始化=====================================================================================*/
/*=====================================================================================断开连接=====================================================================================*/
//关闭当前对话
xchat.closeEventBind = function () {
    var _this = this;
    $("#offSession").on('click', function () {
        myUtils.load(_this.interface.offConnect, 'post', function () {
            $(document.getElementById(destJid)).remove();
            _this.closeFriendWindow();
        }, {"vjid": destJid})
    });
};
//关闭当前窗口
xchat.closeFriendWindow = function () {
    console.log(destJid);
    $.ajax({
        url: this.interface.closeFriendWindow + '?jid=' + destJid,
        type: 'POST',
        success: function () {
            destJid = "";
            $("#currentChatId").empty();
            $("#hasChat").hide();
            $(".chat-detail").hide();
            $("#noChat").show();
        },
        error: function () {
            alert('关闭窗口失败，请重试');
        }
    });
};
/*=====================================================================================断开连接=====================================================================================*/
/*=====================================================================================接收消息=====================================================================================*/

//接收到消息
xchat.recvMsgEvent = function (json) {
    // 是否打开当前窗口
    var _this = this;
    if (json.from != window.destJid) {
        // 未打开当前窗口
        // 统计有多少用户未回
        if (jQuery.inArray(json.from, this.recvMsg) == -1) {
            _this.recvMsg.push(json.from);
            $(_this.controls.waitReplyPerson).html(_this.recvMsg.length);
        }
        _this.initConversation(json);
    }

};

xchat.initConversation = function (json) {
    var _this = this;
    var flag = _this.changeNewMessageStatus(json);
    if (!flag) {
        $.ajax({
            url: _this.interface.getConversation + "?from=" + json.from,
            type: 'GET',
            timeout: 3000,
            async: false,
            success: function (res) {
                if (res.success) {
                    var li = document.getElementById(json.from);
                    if (li) {
                        console.log("exit");
                    } else {
                        _this.onlineQueueSuccessStatusHandelEvent(res.data);
                        _this.changeNewMessageStatus(res.data);
                    }
                }
            },
            error: function () {
                $(_this.controls.userTags).html('获取会话消息失败');
            }
        });
        console.log("not exit");
    }
};

xchat.changeNewMessageStatus = function (json) {
    // 新消息移动到表头
    var li = document.getElementById(json.from);
    if (li) {
        console.log("exit");
        $('#friendList').find('li').each(function () {
            if ($(this).attr('id') === json.from) {
                $('#friendList').prepend($(this));
            }
        });
        // 单个用户
        if (json.encrypt) {
            $(document.getElementById('m' + json.from)).attr("class", "new-message encrypt");
        } else {
            $(document.getElementById('m' + json.from)).attr("class", "new-message");

        }
        var from = json.from;

        var count = parseInt(myUtils.get_unread(from));
        if (count) {
            count = count + 1;
        } else {
            count = 1;
        }
        myUtils.storage_unread(from, count);
        $(document.getElementById('m' + from)).html(count);
        return true;
    } else {
        return false;
    }


}

//接收到文本消息
xchat.recvTextMsgHandelEvent = function (json) {
    document.getElementById("msgTipAudio").play();
    json.time = myUtils.formatDate(new Date(json.ct));
    json.src = json.from;
    json.icon = json.icon || this.controls.defaultAvatar;
    json.content = wechatFace.faceToHTML(json.content, window.base); //表情字符转换对象的图片

    if (json.from == window.destJid) {
        if ("system" == json.fromType || "synchronize" == json.fromType) {
            myUtils.renderDivAdd('mright', json, 'chatMsgContainer');
        } else {
            myUtils.renderDivAdd('mleft', json, 'chatMsgContainer');
        }
    }
    myUtils.storage(json);
    xchat.goBottom();
};
//接收到图片消息
xchat.recvImageMsgHandelEvent = function (json) {
    document.getElementById("msgTipAudio").play();
    json.src = json.from;
    json.icon = json.icon || this.controls.defaultAvatar;
    json.time = myUtils.formatDate(new Date(json.ct));
    json.dev_content = json.content;
    if (json.from == window.destJid) {
        if ("system" == json.fromType || "synchronize" == json.fromType) {
            myUtils.renderDivAdd('imgRight', json, 'chatMsgContainer');
        } else {
            myUtils.renderDivAdd('imgLeft', json, 'chatMsgContainer');
        }
    }
    myUtils.storage(json);
    xchat.goBottom();
};

//接收到微信分享信息
xchat.recvWxShareHandelEvent = function (json) {
    document.getElementById("msgTipAudio").play();
    json.src = json.from;
    json.icon = json.icon || this.controls.defaultAvatar;
    json.time = myUtils.formatDate(new Date(json.ct));
    json.content = eval('(' + json.content + ')');
    if (!json.content.thumburl) {
        json.content.thumburl = this.controls.defaultThumburl;
    }
    if (json.from == window.destJid) {
        if ("system" == json.fromType || "synchronize" == json.fromType) {
            myUtils.renderDivAdd('wx_share_Left', json, 'chatMsgContainer');
        } else {
            myUtils.renderDivAdd('wx_share_Left', json, 'chatMsgContainer');
        }
    }
    myUtils.storage(json);
    xchat.goBottom();
};

//接收到音频信息
xchat.recvAudioMsgHandelEvent = function (json) {
    document.getElementById("msgTipAudio").play();
    json.src = json.from;
    json.icon = json.icon || this.controls.defaultAvatar;
    json.time = myUtils.formatDate(new Date(json.ct));
    if (json.from == window.destJid) {
        if ("system" == json.fromType || "synchronize" == json.fromType) {
            myUtils.renderDivAdd('audioRight', json, 'chatMsgContainer');
        } else {
            myUtils.renderDivAdd('audioLeft', json, 'chatMsgContainer');
        }
    }
    myUtils.storage(json);
    xchat.goBottom();
};

//接收到位置消息
xchat.recvLocationHandelEvent = function (json) {
    document.getElementById("msgTipAudio").play();
    json.src = json.from;
    json.icon = json.icon || this.controls.defaultAvatar;
    json.time = myUtils.formatDate(new Date(json.ct));
    json.content = eval('(' + json.content + ')');
    if (json.from == window.destJid) {
        myUtils.renderDivAdd('wx_location_right', json, 'chatMsgContainer');
    }
    myUtils.storage(json);
    xchat.goBottom();
};

//接收到视频信息
xchat.recvVideoMsgHandelEvent = function (json) {
    document.getElementById("msgTipAudio").play();
    json.src = json.from;
    json.icon = json.icon || this.controls.defaultAvatar;
    json.time = myUtils.formatDate(new Date(json.ct));
    if (json.from == window.destJid) {
        if ("system" == json.fromType || "synchronize" == json.fromType) {
            myUtils.renderDivAdd('videoRight', json, 'chatMsgContainer');
        } else {
            myUtils.renderDivAdd('videoLeft', json, 'chatMsgContainer');
        }
    }
    myUtils.storage(json);
    xchat.goBottom();
};

//接收到文件信息
xchat.recvFileMsgHandelEvent = function (json) {
    document.getElementById("msgTipAudio").play();
    json.src = json.from;
    json.icon = json.icon || this.controls.defaultAvatar;
    json.time = myUtils.formatDate(new Date(json.ct));
    json.content = eval('(' + json.content + ')');
    if (json.from == window.destJid) {
        if ("system" == json.fromType || "synchronize" == json.fromType) {
            myUtils.renderDivAdd('attachmentRight', json, 'chatMsgContainer');
        } else {
            myUtils.renderDivAdd('attachmentLeft', json, 'chatMsgContainer');
        }
    }
    myUtils.storage(json);
    xchat.goBottom();
};
/*=====================================================================================接收消息=====================================================================================*/
/*=====================================================================================发送信息=====================================================================================*/
xchat.sendImgEventBind = function () {
    this.imageSendInit(
        "imgUploader",
        "/" + window.currentId,
        "/api/fileUpload",
        function (o) {
            o.removeClass("sending").addClass("timeOut");
        }
    );
};

xchat.sendAttachmentEventBind = function () {
    this.attachmentSendInit(
        "attachmentUploader",
        "/" + window.currentId,
        "/api/fileUpload",
        function (o) {
            o.removeClass("sending").addClass("timeOut");
        }
    );
};

xchat.searchConversationEventBind = function () {
    var _this = this;

    $(_this.controls.searchConversationInput).bind('input propertychange', function () {
        var searchName = $(this).val().trim();
        if (searchName) {
            $('#friendList').find('li').each(function () {
                var name = $(this).attr('data-id');
                /* alert("name:"+name );
                 alert("searchName:"+searchName );
                 alert(name.indexOf(searchName) >= 0 );*/
                if (name.indexOf(searchName) >= 0) {
                    $('#friendList').prepend($(this));
                }
            });
        }

    });
};

//发送信息
xchat.sendEvent = function (msg, contentType) {
    if (msg) {
        $("#enterChat").val("");
        xchat.normalSend({
            "content": msg,
            "to": window.destJid,
            "icon": window.user.icon,
            "fromType": window.fromType,
            "from": window.currentId,
            "contentType": contentType,
            "timeOutCall": function (o) {
                o.removeClass("sending").addClass("timeOut");
            }
        });
        xchat.goBottom();
    }
};
//发送消息事件
xchat.sendMsgHandelEvent = function (data) {
    data.time = myUtils.formatDate(new Date(data.ct));
    data.src = data.to;
    if (data.icon === undefined || data.icon === '') {
        data.icon = this.controls.defaultAvatar;
    }
    if (data.contentType == 'image' || data.contentType == 'img') {
        myUtils.renderDivAdd('imgRight', data, 'chatMsgContainer');
    } else if (data.contentType == 'audio') {
        myUtils.renderDivAdd('audioRight', data, 'chatMsgContainer');
    } else if (data.contentType == 'voice') {
        myUtils.renderDivAdd('audioRight', data, 'chatMsgContainer');
    } else if (data.contentType == 'video') {
        myUtils.renderDivAdd('videoRight', data, 'chatMsgContainer');
    } else if (data.contentType == 'attachment') {
        myUtils.renderDivAdd('attachmentRight', data, 'chatMsgContainer');
    } else {
        myUtils.renderDivAdd('mright', data, 'chatMsgContainer');
    }
};
xchat.sendEventBind = function () {
    var _this = this;
    //回车发送消息
    $(document).bind('keydown', '#enterChat', function (e) {
        if (e.keyCode == 13) {
            var msg = $("#enterChat").val();
            if (msg !== '') {
                xchat.sendEvent(msg);
                return false;
            } else {
                return false;
            }
        }
    });
    //点击按钮发送
    $("#send-btn").bind("click", function () {
        var msg = $("#enterChat").val();
        _this.sendEvent(msg);
    });
};
/*=====================================================================================发送信息=====================================================================================*/
/*=====================================================================================队列事件=====================================================================================*/
//线上队列
xchat.onlineQueueSuccessStatusHandelEvent = function (json) {
    var _this = this;
    var li = document.getElementById(json.from);
    if (li) {
        console.log("exit");
    } else {
        //myUtils.renderQueue(json.from, 'waitfriendList', 'down');
        //myUtils.renderQueue(json.from, 'backupfriendList', 'down');
        //myUtils.renderQueue(json.from, 'friendList', 'down');
        myUtils.renderQueue(json.from, 'historyFriendList', 'remove');
        json.name = json.fromName;
        json.nickname = json.fromName;
        json.icon = json.icon || _this.controls.defaultAvatar;
        json.time = myUtils.formatDate(json.loginTime);

        myUtils.renderQueue(json.from, 'friendList', 'up', function () {
            myUtils.renderDivPrepend('onlinefriendListTpl', json, 'friendList');
        })
    }


};
//等待队列
xchat.waitQueueSuccessStatusHandelEvent = function (json) {
    myUtils.renderQueue(json.from, 'waitfriendList', 'down');
    myUtils.renderQueue(json.from, 'backupfriendList', 'down');
    myUtils.renderQueue(json.from, 'historyFriendList', 'down');
    myUtils.renderQueue(json.from, 'friendList', 'down');
    //如果有等待的 增加样式
    $("#holdListBtn").addClass("hasJoined");
};
//等待队列中删除
xchat.offlineWaitQueueStatusHandelEvent = function (json) {

    myUtils.renderQueue(json.from, 'waitfriendList', 'down');
    myUtils.renderQueue(json.from, 'backupfriendList', 'down');
    myUtils.renderQueue(json.from, 'historyFriendList', 'down');
    myUtils.renderQueue(json.from, 'friendList', 'down');
};

//客服下线后调用
xchat.customerOfflineStatusHandelEvent = function () {
    var _this = this;
    _this.alertShow('托管微信已下线，请你重新登录');
};


//用户下线
xchat.offlineStatusHandelEvent = function (json) {
    myUtils.renderQueue(json.from, 'friendList', 'remove');
    myUtils.renderQueue(json.from, 'historyFriendList', 'up', function () {
        json.name = json.fromName;
        json.nickname = json.fromName;
        json.icon = json.icon || this.controls.defaultAvatar;
        json.onlineStatus = 'history';
        json.time = myUtils.formatDate(json.loginTime);
        myUtils.renderDivPrepend('onlinefriendListTpl', json, 'historyFriendList');
    })
};
//在backqueue下线
xchat.offlineBackQueueStatusHandelEvent = function (json) {
    myUtils.renderQueue(json.from, 'waitfriendList', 'down');
    myUtils.renderQueue(json.from, 'historyFriendList', 'down');

    myUtils.renderQueue(json.from, 'backupfriendList', 'down');
    myUtils.renderQueue(json.from, 'friendList', 'down');
};
//backup队列
xchat.backUpStatusHandelEvent = function (json) {
    myUtils.renderQueue(json.from, 'waitfriendList', 'down');
    myUtils.renderQueue(json.from, 'backupfriendList', 'down');
    myUtils.renderQueue(json.from, 'friendList', 'down');
    myUtils.renderQueue(json.from, 'backupfriendList', 'up', function () {
        json.name = json.fromName;
        json.nickname = json.fromName;
        json.icon = json.icon || window.base + '/resouces/images/default-avatar.jpg';
        json.onlineStatus = 'online';
        myUtils.renderDivAdd('backupfriendListTpl', json, 'backupfriendList');
    })
};
//对话队列加载
xchat.loadChatList = function () {
    var _this = this;
    var friendList = $(_this.controls.friendList);
    $.get(_this.interface.loadChatList, {}, function (json) {
        if (json.success) {
            if (json.data) {
                for (var i = 0; i < json.data.length; i++) {
                    var friend = json.data[i];
                    if (friend.icon === undefined || friend.icon === '') {
                        friend.icon = _this.controls.defaultAvatar;
                    }
                    friend.time = myUtils.formatDate(friend.loginTime);
                    var li = document.getElementById(friend.from);
                    if (!li) {
                        if (friend.onlineStatus == 'online') {
                            myUtils.renderDivAdd('onlinefriendListTpl', friend, 'friendList');
                        } else if (friend.onlineStatus == 'backup') {
                            //backup状态也算是线上状态
                            myUtils.renderDivAdd('backupfriendListTpl', friend, 'backupFriendList');
                        } else if (friend.onlineStatus == 'history') {
                            myUtils.renderDivAdd('onlinefriendListTpl', friend, 'historyFriendList');
                        }
                        var count = myUtils.get_unread(friend.from);
                        if (count && count != 0) {
                            // 单个用户
                            $(document.getElementById('m' + friend.from)).attr("class", "new-message");
                            $(document.getElementById('m' + friend.from)).html(count);
                        }
                    }
                }
            }
        }
    })
};
//对话队列事件绑定
xchat.loadChatListEvent = function () {
    var _this = this;
    $(_this.controls.friendList).on("click", 'li', function () {
        $(_this.controls.friendList).find("li").removeClass("active");
        var myFriendId = $(this).attr("id");
        var openId = $(this).attr("openId");
        var fromType = $(this).attr("fromType");
        var nickname = $(this).find('.name').text();
        var isOnline = $(this).attr("class").indexOf("online");

        _this.openFriendWindow(isOnline, myFriendId, nickname, openId, fromType);
        $(this).addClass("active");
    });

    $(this.controls.backupfriendList).on("click", 'li', function () {
        $(_this.controls.friendList).find("li").removeClass("active");
        var myFriendId = $(this).attr("id");
        var openId = $(this).attr("openId");
        var nickname = $(this).find('.name').text();
        var isOnline = $(this).attr("class").indexOf("online");
        xchat.openFriendWindow(isOnline, myFriendId, nickname, openId);
        $(this).addClass("active");
    });

    $(this.controls.historyFriendList).on("click", 'li', function () {
        $(_this.controls.friendList).find("li").removeClass("active");
        var myFriendId = $(this).attr("id");
        var openId = $(this).attr("openId");
        var nickname = $(this).find('.name').text();
        var isOnline = $(this).attr("class").indexOf("online");
        xchat.openFriendWindow(isOnline, myFriendId, nickname, openId);
        $(this).addClass("active");
    });
    $(".chat-source-detail-btn").click(function () {
        _this.visitorProperties();
    });


};
/*=====================================================================================队列事件=====================================================================================*/
//ws关闭后的处理方式
xchat.wsClose = function () {
    // window.location.href = this.interface.login;
};

//本地缓存历史数据
xchat.unEncryptLocalHistory = function (id, list) {
    var _this = this;
    //console.log(list);
    //console.log(id);
    var map = {};
    if (list) {
        $.each(list, function (index, val) {
            //console.log(val);
            map[val.messageid] = val;
        });
    }
    //console.log(map);
    var dataList = myUtils.getStorage(id);
    if (dataList) {
        dataList = eval("(" + dataList + ")");
        dataList.map(function (val) {
            var li = map[val.id];
            //console.log(li);
            if (li) {
                val.content = li.content;
                val.contentType = li.contentType;
                val.dev_content = li.content;

                console.log(val);
            }
        });
        localStorage.setItem("msg:" + id, JSON.stringify(dataList));
    }
};

//本地缓存历史数据
xchat.getLocalHistory = function (id) {
    var _this = this;
    var dataList = myUtils.getStorage(id);
    if (dataList) {
        dataList = eval("(" + dataList + ")");
        dataList.map(function (val) {
            if (val.icon === undefined || val.icon === '') {
                val.icon = _this.controls.defaultAvatar;
            }
        });
        myUtils.cacheRenderDiv(window.currentId, dataList, 'chatMsgContainer', function () {
            _this.goBottom();
        });
    }
};
xchat.getRemoteHistory = function (customerPage, cacheLastId, fn) {
    myUtils.customerHistory(customerPage, cacheLastId, fn);
};
//滚动加载历史消息
xchat.loadHistoryEventBind = function () {
    var _this = this;
    var i = 0;
    var customerPage = new myUtils.Page({
        pageNumber: 0,
        pageSize: 10
    });

    $('#chatTimeline').on('mousewheel', function (e) {
        var delta = -e.originalEvent.wheelDelta || e.originalEvent.detail;
        if (delta > 0) {
            i--;
        }
        //上滚
        if (delta < 0) {
            i++;
        }
        if (this.scrollTop === 0 && i >= 2) {
            var currentHeight = this.scrollHeight;

            var timer = setTimeout(function () {
                i = 0;
                clearTimeout(timer);
            }, 300);

            // 获取最早的本地缓存信息id
            var dataList = myUtils.getStorage(window.destJid);
            var cacheLastId;
            if (dataList) {
                dataList = eval("(" + dataList + ")");
                cacheLastId = dataList[0].id;
            }
            _this.getRemoteHistory(customerPage, cacheLastId, function () {
                this.scrollTop = this.scrollHeight - currentHeight;
            });
            i = 0;
        }
    });
};
//获取当前访客的浏览器信息
xchat.visitorProperties = function () {
    myUtils.load(window.base + "/api/propertiesApi?destJid=" + destJid, "get", function (json) {
        if (json.success) {
            var data = json.data;
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    if (data[i].key == '登录时间' || data[i].key == '上次登出时间') {
                        data[i].value = myUtils.formatDate(new Date(parseInt(data[i].value)));
                    }
                }
                data.push({
                    key: '访问来源',
                    value: myUtils.isPc()
                });
                myUtils.renderDiv('visitorPropertiesTpl', data, 'visitorProperties');
            }
        }
    }, {});
};
/*=====================================================================================打开对话=====================================================================================*/
//打开对话的窗口
xchat.openFriendWindow = function (isOnline, id, nickname, openId, fromType) {
    var _this = this;
    window.destJid = id;
    window.destJid_nickName = nickname;
    window.fromType = fromType;
    //$(id).addClass("active").siblings().removeClass("active");  //设置当前的好友为激活 且 把消息变成已阅读
    $("#currentChatId").empty().append("您正在和" + nickname + "聊天 ").data('id', id); //设置聊天标题
    $("#noChat").hide();  //中间位置消失
    $("#hasChat").show();   //把聊天窗口显示出来
    $(".chat-detail").show();   //把用户详情显示出来
    if (id.indexOf("@conference") > 0) {
        $("#groupListBtn").attr("value", id);   //如果是群，显示群查看
        $("#groupListBtn").show();   //如果是群，显示群查看
    } else {
        $("#groupListBtn").hide();   //如果是群，显示群查看
    }

    $(_this.controls.msgContainer).empty(); //清空当前的聊天容器内容
    // 判断是否在线，是否开启聊天窗口
    if (isOnline >= 0) {
        $('#chatInput').removeClass('chat-input-disabled');
    } else {
        $('#chatInput').addClass('chat-input-disabled');
    }
    _this.getLocalHistory(id);
    $(document.getElementById('m' + id)).attr("class", "");     //清空有新消息的提示
    myUtils.storage_unread(id, 0);
    $(document.getElementById('m' + id)).html("");


    // 去除提示
    this.recvMsg.splice(jQuery.inArray(id, this.recvMsg), 1);
    $(this.controls.waitReplyPerson).html(this.recvMsg.length);

    _this.conversationInit(id);

};

//设置当前用户详情
xchat.setUserInfo = function (cjid, remark, tags) {
    var _this = this;
    remark = remark || '';
    //tags = tags || [];
    $.ajax({
        url: _this.interface.updateUserInfo + '?cjid=' + cjid + '&remark=' + remark + '&tags=' + JSON.stringify(tags),
        type: 'POST',
        success: function (res) {
            if (res.success) {
                alert('修改成功');
            }
        },
        error: function () {
            alert('修改失败');
        }
    });
};
//获取用户标签
xchat.getUserLabel = function () {
    var _this = this;
    $.ajax({
        url: _this.interface.getTagsAll,
        type: 'GET',
        timeout: 3000,
        success: function (res) {
            if (res.success) {
                _this.userLabelComb(res.data);
            }
        },
        error: function () {
            $(_this.controls.userTags).html('获取标签失败');
        }
    });
};

//获取用户标签
xchat.conversationInit = function (vjid) {
    var _this = this;
    $.ajax({
        url: _this.interface.conversationInit + "?vjid=" + vjid,
        type: 'GET',
        timeout: 3000,
        success: function (res) {
            if (res.success) {
                console.log("conversationInit success");
            }
        },
        error: function () {
            console.log("conversationInit error");
        }
    });
};

//用户标签拼接
xchat.userLabelComb = function (data) {
    var _this = this;
    var html = '';
    data.map(function (val, index) {
        html += '<li class="tag"><input type="checkbox" name="tags" id="tag_' + val.id + '"><label for="tag_' + val.id + '" data-id="' + val.id + '">' + val.tagname + '</label></li>';
    });
    $(_this.controls.userTags).html(html);
    return html;
};
//用户标签设置
xchat.setUserLabel = function (data) {
    if (data) {
        data.map(function (val) {
            if (val.state) {
                $('#tag_' + val.id).prop('checked', true);
            }
        });
    }
};
//设置用户详情事件绑定
xchat.setUserInfoEventBind = function () {
    var _this = this;

    $(this.controls.remarkSubmit).on('click', function () {
        var cjid = $(_this.controls.currentChatId).data('id');
        var remark = $(_this.controls.remark).val();
        _this.setUserInfo(cjid, remark, []);
    });
    /*$(this.controls.remark).on('focusout', function () {
     var cjid = $(_this.controls.currentChatId).data('id');
     var remark = $(this).val();
     _this.setUserInfo(cjid, remark, []);
     });*/
    $(this.controls.userTags).on('click', 'input', function (e) {
        var cjid = $(_this.controls.currentChatId).data('id');
        var tags = [];
        var $tags = $('[name="tags"]');
        $tags.map(function (index, val) {
            var id = $(val).attr('id');
            var obj = {
                id: id.replace('tag_', ''),
                name: $(val).next('label').text(),
                state: $('#' + id).prop('checked')
            };
            tags.push(obj);
        });
        _this.setUserInfo(cjid, '', tags);
        e.stopPropagation();
    });
};
//理赔拼接
xchat.claimsComb = function (data) {
    //var tabHeader = '<tr><th>理赔单号</th><th>承保公司</th><th>申请人姓名</th><th>受益人姓名</th><th>受益人证件号</th><th>申请时间</th><th>完结时间</th><th>申请金额</th><th>理赔状态</th><th>处理状态</th></tr>';
    //var tabBody = '';
    var html = '';
    var itemHtml = '';
    data.map(function (val, index) {
        //tabBody += '<tr><td>' + val.applycode + '</td><td>' + val.insuranceCompany + '</td><td>' + val.applyPerson + '</td><td>' + val.dangerPerson + '</td><td>' + val.dangerIdCard + '</td><td>' + val.applyDate + '</td><td>' + val.doneTime + '</td><td>' + val.chargeMoney + '</td><td>' + val.claimsStatus + '</td><td>' + val.handleStatus + '</td></tr>';
        if (val.applyDate) {
            val.applyDate = myUtils.formatDate(val.applyDate, 'yyyy-MM-dd');
        } else {
            val.applyDate = '';
        }
        if (val.doneTime) {
            val.doneTime = myUtils.formatDate(val.doneTime, 'yyyy-MM-dd');
        } else {
            val.doneTime = '';
        }
        html += '<li><span class="tag">理赔单号:</span>' + val.applycode + '</li>';
        html += '<li><span class="tag">承保公司:</span>' + val.insuranceCompany + '</li>';
        html += '<li><span class="tag">申请人姓名:</span>' + val.applyPerson + '</li>';
        html += '<li><span class="tag">受益人姓名:</span>' + val.dangerPerson + '</li>';
        html += '<li><span class="tag">受益人证件号:</span>' + val.dangerIdCard + '</li>';
        html += '<li><span class="tag">申请时间:</span>' + val.applyDate + '</li>';
        html += '<li><span class="tag">完结时间:</span>' + val.doneTime + '</li>';
        html += '<li><span class="tag">申请金额:</span>' + val.chargeMoney + '</li>';
        html += '<li><span class="tag">理赔状态:</span>' + val.claimsStatus + '</li>';
        html += '<li><span class="tag">处理状态:</span>' + val.handleStatus + '</li>';
        itemHtml += '<ul class="modal_ul">' + html + '</ul>';
        html = '';
    });
    $(this.controls.claimsContainer).html(itemHtml);
};
//订单拼接
xchat.orderComb = function (data) {
    var html = '';
    var itemHtml = '';
    data.map(function (val, index) {
        if (val.startTime) {
            val.startTime = myUtils.formatDate(val.startTime, 'yyyy-MM-dd');
        } else {
            val.startTime = '';
        }
        if (val.endTime) {
            val.endTime = myUtils.formatDate(val.endTime, 'yyyy-MM-dd');
        } else {
            val.endTime = '';
        }
        switch (val.gender) {
            case 1:
                val.gender = '男';
                break;
            case 2:
                val.gender = '女';
                break;
            default:
                val.gender = '保密';
        }
        switch (val.isBook) {
            case 1:
                val.isBook = '预约';
                break;
            case 0:
                val.isBook = '未预约';
                break;
            default:
                val.isBook = '未预约';
        }
        switch (val.isMarried) {
            case 1:
                val.isMarried = '已婚';
                break;
            case 2:
                val.isMarried = '未婚';
                break;
            default:
                val.isMarried = '保密';
        }
        html += '<li><span class="tag">套餐名称:</span>' + val.caseName + '</li>';
        html += '<li><span class="tag">开始日期:</span>' + val.startTime + '</li>';
        html += '<li><span class="tag">结束日期:</span>' + val.endTime + '</li>';
        html += '<li><span class="tag">性别:</span>' + val.gender + '</li>';
        html += '<li><span class="tag">是否预约:</span>' + val.isBook + '</li>';
        html += '<li><span class="tag">是否体检:</span>' + val.isCheckName + '</li>';
        html += '<li><span class="tag">婚姻状态:</span>' + val.isMarried + '</li>';
        html += '<li><span class="tag">销售模式:</span>' + val.saleModelName + '</li>';
        html += '<li><span class="tag">订单状态:</span>' + val.statusName + '</li>';
        itemHtml += '<ul class="modal_ul">' + html + '</ul>';
        html = '';
    });
    $(this.controls.orderContainer).html(itemHtml);
};
//合同拼接
xchat.contractComb = function (data) {
    var html = '';
    var itemHtml = '';
    data.map(function (val, index) {
        var item = val.enterpriseContract;
        if (item.effectivedate) {
            item.effectivedate = myUtils.formatDate(item.effectivedate, 'yyyy-MM-dd');
        } else {
            item.effectivedate = '';
        }

        if (item.expirydate) {
            item.expirydate = myUtils.formatDate(item.expirydate, 'yyyy-MM-dd');
        } else {
            item.expirydate = '';
        }
        html += '<li><span class="tag">合同名称:</span>' + item.name + '</li>';
        html += '<li><span class="tag">合同编号:</span>' + item.code + '</li>';
        html += '<li><span class="tag">生效日期:</span>' + item.effectivedate + '</li>';
        html += '<li><span class="tag">失效日期:</span>' + item.expirydate + '</li>';
        itemHtml += '<ul class="modal_ul">' + html + '</ul>';
        html = '';
    });
    $(this.controls.contractsContainer).html(itemHtml);
};
/*=====================================================================================打开对话=====================================================================================*/
//消息模版事件
xchat.messageListEventBind = function (selector, container) {
    $(selector || '#message_tpl').on('click', 'li', function () {
        $(container || '#enterChat').val($(this).text());
    });
};
/*=====================================================================================等待队列=====================================================================================*/
xchat.getHoldList = function () {
    var _this = this;
    $.get(_this.interface.holdList, {}, function (json) {
        if (json.success) {
            if (json.data) {
                var data = [];
                var html = '';
                for (var i = 0; i < json.data.length; i++) {
                    var friend = json.data[i];
                    friend.from = friend.id;
                    friend.icon = friend.icon || window.base + '/resouces/images/default-avatar.jpg';
                    data.push(friend);
                }
                if (data.length > 0) {
                    data.map(function (val) {
                        html += '<li id="' + val.id + '"><span class="username">' + val.nickname + '</span><span class="call_in">接入</span></li>';
                    });

                } else {
                    html = '无用户接入';
                }
                $('#holdListCont').html(html);
                return html;
            }
        }
    });
};
xchat.holdListEventBind = function () {
    var _this = this;
    $('#holdListBtn').on('click', function () {
        _this.getHoldList();
    });
};
xchat.customerListEventBind = function () {
    var _this = this;
    $('#turnBtn').on('click', function () {
        _this.getCustomerList();
    });
};
xchat.groupSearchBind = function () {
    var _this = this;

    $('#groupListBtn').on('click', function () {
        var value = $('#groupListBtn').attr("value");
        console.log(value);
        $.post(window.base + "/api/getGroupUsers", {username: decodeURIComponent(value)}, function (res) {
            vGroup.list = res.data;
        }, 'json');
        /*$('[data-target="groupListBtn"]').show();        //value= 'xvql518';
        $.ajax({
            url: _this.interface.getGroupUsers + "?username=" + value,
            type: 'GET',
            success: function (res) {
                console.log(res);
            },
            error: function () {
                document.getElementById(_this.controls.turnList).innerHTML = '查询失败';
            }
         })*/

        //alert("查看群成员" + value);
    });
};


xchat.setting_allEventBind = function () {
    var _this = this;
    var setting_all = $(_this.controls.settingAll);
    var setting_allConfirm = $('#setting_allConfirm');

    setting_all.modal();
    setting_all.on('click', function () {
        _this.getSetting_all(window.destJid);
    });

    setting_allConfirm.on('click', function () {
        _this.setting_allConfirm(window.destJid);
    });
};


xchat.callInEventBind = function () {
    var _this = this;
    $(_this.controls.holdListCont).on('click', '.call_in', function () {
        var joinedLength = $(_this.controls.holdListCont).find('li').length;
        var targetId = $(this).parent().attr("id");
        myUtils.load(_this.interface.callIn + targetId, 'get', function (json) {
            if (json.success) {
                $(".modal.fade").hide();
                if (joinedLength == 0) {
                    $("#holdListBtn").removeClass("hasJoined");
                }
            } else {
                alert("接入失败");
            }
        }, {});
    });
};
/*=====================================================================================等待队列=====================================================================================*/
/*=====================================================================================分配客服=====================================================================================*/
xchat.getCustomerList = function () {
    var _this = this;
    $.ajax({
        url: _this.interface.getCustomerList,
        type: 'GET',
        success: function (res) {
            _this.turnComb(res.data);
        },
        error: function () {
            document.getElementById(_this.controls.turnList).innerHTML = '查询失败';
        }
    })
};

xchat.getSetting_all = function (destJid) {
    var _this = this;
    $.ajax({
        url: _this.interface.getDisplay + '?from=' + destJid,
        type: 'post',
        success: function (res) {
            if (res.success) {
                var html = '<div>' +
                    '<label for="newPWD">' +
                    '<span class="tag" style="font-size: 18px;">' + window.destJid_nickName + '</span>' +
                    '</label>' +
                    '</div>' +
                    '<div style="margin: 10px 0 0 0; font-size:14px;">' +
                    '<label for="newPWD">' +
                    '<span class="tag">默认解密消息条数：</span>' +
                    '<select name="encryptCount" id="encryptCount">' +
                    '<option value="50" selected>50</option>' +
                    '<option value="100">100</option>' +
                    '<option value="200">200</option>' +
                    '</select>' +
                        //'<input type="text"   placeholder="默认解密消息条数" value="' + res.data.count + '" style="border: 1px solid #ccc;padding: 6px;color: #666;">' +
                    '</label>' +
                    '<div style="margin: 10px 0 0 0; font-size:14px;line-height: 26px;">' +
                    '<label for="newPWD">' +
                    '<span class="tag">是否开启计费：</span>' +
                    '</div>' +
                    '关闭' +
                    ' <div class="v-switch" id="v-switch">' +
                    ' <div class="v-switch-slider">' +
                    '</div>' +
                    '</div>' +
                    '开启' +

                        //'开启<input type="radio"  name="status" value="1" style="margin: 0 20px 0 5px;">' +
                        //'关闭<input type="radio"  name="status" value="0" style="margin: 0 20px 0 5px;">' +
                    '</label>' +
                    '</div>';
                $(_this.controls.settingAllFrom).html(html);

                document.querySelector('.v-switch').addEventListener('click', function (e) {
                    e.target.classList.toggle('opened');
                });
                /* $('#v-switch').on('click', function () {
                 });*/

                $("#encryptCount").find("option[value='" + res.data.count + "']").attr("selected", true);

                if (res.data.status == 1) {
                    if ($('.v-switch.opened').length == 0) {
                        //alert('开启');
                        $('.v-switch').addClass("opened");
                    }
                } else {
                    if ($('.v-switch.opened').length == 1) {
                        //alert('关闭');
                        $('.v-switch').removeClass("opened");
                    }
                }


            } else {
                $(_this.controls.settingAllFrom).html('查询失败');
            }

        },
        error: function () {
            $(_this.controls.settingAllFrom).html('查询失败');
        }
    })
};

xchat.setting_allConfirm = function (destJid) {
    var _this = this;
    var status = 0;
    if ($('.v-switch.opened').length > 0) {
        status = 1;
    }

    var count = $("#encryptCount").val();

    $.ajax({
        url: _this.interface.msgShow + '?username=' + destJid + "&status=" + status + "&count=" + count,
        type: 'post',
        success: function (res) {
            if (res.success) {
                $("#setting_all_modal").hide();
                // 单个用户
                if (status == 0) {
                    $(document.getElementById(destJid)).attr("class", "encrypt have-message active");
                } else {
                    $(document.getElementById(destJid)).attr("class", "online have-message active");
                    if (res.data.msg.list) {
                        _this.unEncryptLocalHistory(destJid, res.data.msg.list);
                    }
                }

            } else {
                $(_this.controls.settingAllFrom).html(res.msg);
            }
        },
        error: function () {
        }
    })
};


xchat.turnComb = function (data) {
    var html = '';
    /* data.map(function (val) {
     if (val.id !== window.currentId) {
     html += '<li data-id="' + val.id + '">' + val.loginUsername + '<span class="chooseBtn">分配</span></li>';
     }
     });*/
    $('#turn-list').html(html);
};
xchat.turn = function (vjid, toJid) {
    var _this = this;
    $.ajax({
        url: _this.interface.turn + '?vjid=' + vjid + '&fromJid=' + window.currentId + '&toJid=' + toJid,
        type: 'POST',
        success: function () {
            $('[data-target="turnBtn"]').hide();
            _this.closeFriendWindow();
        },
        error: function () {
            alert('转接失败');
        }
    });
};
xchat.turnEventBind = function () {
    var _this = this;
    $('#turn-list').on('click', 'li span', function () {
        var toJid = $(this).parent().data('id');
        var vjid = $('#currentChatId').data('id');
        _this.turn(vjid, toJid);
    });
};
/*=====================================================================================分配客服=====================================================================================*/
/*=====================================================================================工具=====================================================================================*/
//滚到地步
xchat.goBottom = function () {
    document.getElementById(this.controls.chatShow).scrollTop = document.getElementById(this.controls.chatTimeline).scrollHeight;
};
//增加提示信息
xchat.addAlert = function () {
    $(Mustache.to_html($('#alert').html())).prependTo("#chatMsgContainer");
};
/*=====================================================================================工具=====================================================================================*/
xchat.library_getData = function ($library, val, index) {
    var _this = this;
    $.ajax({
        url: _this.interface.getMaterial + '?offset=0&limit=100&type=' + val,
        type: 'post',
        success: function (res) {
            if (res.success) {
                var html = '';
                $.each(res.data.data, function (index, value) {
                    if (value.type == 'text') {
                        html += '<li class="item" value="' + value.type + '">' +
                            '<h3 class="title">' + value.title + '</h3>' +
                            '<div class="cont">' + value.content + '</div>' +
                            '</li>';
                    } else if (value.type == 'image') {
                        html += '<li class="item" value="' + value.type + '">' +
                            '<div class="cont" hidden>' + value.url + '</div>' +
                            '<image class="cont" src="' + value.url + '" style="width: 50px;height: 40px"></image>' +
                            '</li>';
                    } else if (value.type == 'audio' || value.type == 'voice') {
                        html += '<li class="item" value="audio">' +
                            '<div class="cont" hidden>' + value.url + '</div>' +
                            '<audio class="cont" src="' + value.url + '" controls="controls"></audio>' +
                            '</li>';
                    } else if (value.type == 'video') {
                        html += '<li class="item" value="' + value.type + '">' +
                            '<div class="cont" hidden>' + value.url + '</div>' +
                            ' <video width="320" height="240" controls="controls" autoplay="autoplay">' +
                            '<source src="' + value.url + '"  type="video/mp4"/>' +
                            '</video>' +
                            '</li>';
                    }
                });
                $library.find('.info .content').eq(index).find('ul').html(html);
            } else {
                $library.find('.info .content').eq(index).html(res.msg);
            }
        },
        error: function () {
        }
    })
}
xchat.library_init = function () {
    var _this = this;
    var $library = $('#library');
    var $chatInput = $('#chatInput');
    _this.library_getData($library, 'text', 0)
    // 绑定事件
    $library.on('click', '.bars li', function () {
        var index = $(this).index();
        var val = $(this).attr("value");
        _this.library_getData($library, val, index)
        $(this).addClass('active').siblings().removeClass('active')
        $library.find('.info .content').eq(index).addClass('active').siblings().removeClass('active')
    });
    $library.on('click', '.libraries .item', function () {
        var msg = $(this).find('.cont').text();
        var val = $(this).attr("value");
        xchat.sendEvent(msg, val);
        //console.log(msg,val);
        $library.removeClass('library_show').addClass('library_hide')
    })
    //
    $chatInput.on('click', '.icon-emoji', function () {
        if ($library.attr('class').indexOf('show') > 0) {
            $library.removeClass('library_show').addClass('library_hide')
        } else {
            $library.removeClass('library_hide').addClass('library_show')
        }
    })
};

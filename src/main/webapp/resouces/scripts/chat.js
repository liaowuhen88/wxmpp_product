/**
 * 对话
 * @constructor
 */
'use strict';
var Chat = function (options) {
    options = options || {};
    this.currentId = options.currentId || window.currentId;
    this.base = options.base || window.base;

    this.controls = {
        quickReplyContainer: '#message_tpl',
        quickReplyInput: '#quickReplyInput',
        addQuickReplyBtn: '#addQuickReplyBtn',
        holdListBtn: '#holdListBtn',
        turnBtn: '#turnBtn',
        historyBtn: '#historyBtn',
        friendAndGroupBtn: '#friendAndGroupBtn',
        examReportBtn: '#examReportBtn',
        chatClaimsBtn: '#chatClaimsBtn',
        chatContractsBtn: '#chatContractsBtn',
        chatOrderBtn: '#chatOrderBtn',
        signOutBtn: '.signOut'
    };

    this.interface = {
        keepOnlineApi: window.base + '/api/keepOnline',
        quickReplyGet: window.base + '/api/updateQuickReply',
        quickReplyUpdate: window.base + '/api/updateQuickReply',
        quickReplyDelete: window.base + '/api/deleteQuickReply',
        signOut: window.base + "/api/customerLogout"
    };
};
Chat.prototype = {
    /*=====================================================================================快速回复=====================================================================================*/
    //初始化
    init: function () {
        this.getQuickReplyEventBind();      //快速回复列表获取
        this.quickReplySearchEventBind();   //快速回复列表搜索事件绑定
        this.addQuickReplyEventBind();
        this.deleteQuickReplyEventBind();
        this.tabEventBind();
        this.tooltipEventBind();
        // 十分钟调用一次接口
        this.keepOnline();
        this.modalBind();
        this.signOutEventBind();

    },
    signOut: function () {
        //退出登录
        var self = this;
        window.close();
        //window.location.href =self.interface.signOut;
      /*  myUtils.load(self.interface.signOut, 'post', function () {
            //window.location.href = window.base + "/customerlogin";
        }, {});*/
    },
    signOutEventBind: function () {
        //退出登录事件绑定
        var self = this;
        $(this.controls.signOutBtn).on('click', function () {
            self.signOut();
        });
    },
    modalBind: function () {
        $(this.controls.holdListBtn).modal();
        $(this.controls.turnBtn).modal();
        $(this.controls.historyBtn).modal();
        $(this.controls.friendAndGroupBtn).modal();
        $(this.controls.examReportBtn).modal();
        $(this.controls.chatClaimsBtn).modal();
        $(this.controls.chatContractsBtn).modal();
        $(this.controls.chatOrderBtn).modal();
    },
    /*=====================================================================================快速回复=====================================================================================*/
    /*=====================================================================================快速回复=====================================================================================*/
    //快速回复快速搜索
    quickReplySearchEventBind: function () {
        holmes({
            input: '.chat-tpl-search input',
            find: '.chat-tpl-list li',
            placeholder: '没有结果'
        });
    },


    // 定时刷新，防止客服掉线
    keepOnline: function () {
        var _this = this;
        function keep() {
            $.ajax({
                url: _this.interface.keepOnlineApi,
                type: 'POST',
                success: function (res) {
                    if (res.success) {
                        console.log("keepOnline success")
                    }
                },
                error: function () {
                    console.log("keepOnline error")
                }
            });
        }

        // 十分钟调用一次接口   防止掉线
        setInterval(keep, 1000 * 60 * 5);// 注意函数名没有引号和括弧！
    },


    //快速回复字符串拼接
    quickReplyComb: function (data) {
        var _this = this;
        var html = '';
        data.map(function (val) {
            html += '<li>' + val.message + '<div class="tpl-btns"><i class="tpl-btns-icon icon-delete" data-id="' + val.id + '"></i></div></li>';
        });
        $(_this.controls.quickReplyContainer).html(html);
    },
    //获取快速回复事件绑定
    getQuickReplyEventBind: function () {
        var _this = this;
        $.ajax({
            url: _this.interface.quickReplyGet + '?cjid=' + _this.currentId,
            type: 'POST',
            success: function (res) {
                if (res.success) {
                    var data = res.data;
                    _this.quickReplyComb(data);
                }
            },
            error: function () {
                $(_this.controls.quickReplyContainer).html('获取数据失败');
            }
        });
    },
    //增加快速回复
    addQuickReply: function (message) {
        var _this = this;
        $.ajax({
            url: _this.interface.quickReplyUpdate + '?cjid=' + _this.currentId + '&message=' + message,
            type: 'POST',
            success: function (res) {
                if (res.success) {
                    var data = res.data;
                    _this.clearQuickReplyInput();
                    _this.quickReplyComb(data);
                    alert('添加成功');
                }
            },
            error: function () {
                alert('添加失败,请重试');
            }
        });
    },
    //清楚快速回复输入框
    clearQuickReplyInput: function () {
        $(this.controls.quickReplyInput).val('');
    },
    //增加快速回复事件绑定
    addQuickReplyEventBind: function () {
        var _this = this;
        $(_this.controls.addQuickReplyBtn).on('click', function () {
            console.log(1);
            var message = $(_this.controls.quickReplyInput).val();
            if (message !== '') {
                _this.addQuickReply(message);
            } else {
                // 提示信息
                alert("请输入内容");
            }
        });
    },
    //删除快速回复
    deleteQuickReply: function (id, successCallback) {
        var _this = this;
        $.ajax({
            url: _this.interface.quickReplyDelete + '?cjid=' + _this.currentId + '&id=' + id,
            type: 'POST',
            success: function (res) {
                if (res.success) {
                    if (successCallback) {
                        successCallback.call(this, res);
                    }
                    alert('删除成功');
                }
            },
            error: function () {
                alert('删除失败,请重试');
            }
        });
    },
    //删除快速回复时间绑定
    deleteQuickReplyEventBind: function () {
        var _this = this;
        $(_this.controls.quickReplyContainer).on('click', '.icon-delete', function (e) {
            var id = $(this).data('id');
            var dom = $(this).parent().parent();
            _this.deleteQuickReply(id, function () {
                dom.remove();
            });
            e.stopPropagation();
            return false;
        });
    },
    /*=====================================================================================快速回复=====================================================================================*/
    /*=====================================================================================其他工具=====================================================================================*/
    //滑动门
    tabEventBind: function () {
        //$('#chat-tab li:eq(1) a').tab('show');
    },
    //工具提示
    tooltipEventBind: function () {
        $('[data-toggle="tooltip"]').tooltip();
    }
    /*=====================================================================================其他工具=====================================================================================*/
};

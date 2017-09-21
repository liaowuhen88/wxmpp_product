/**
 * 历史记录
 * @constructor
 */
'use strict';

var FriendAndGroup = function (options) {
    options = options || {};

    this.currentId = options.currentId || window.currentId;
    this.base = options.base || window.base;
    //控件
    this.friendAndGroupBtn = $('#friendAndGroupBtn');
    this.table = $('#table');

    this.interface = {
        getFriendAndGroup: window.base + '/api/getFriendAndGroup'
    }
};
FriendAndGroup.prototype = {
    /*=====================================================================================初始化=====================================================================================*/
    init: function () {
        this.initVue();     //绑定获取好友
    },
    initVue: function () {
        function _toConsumableArray(arr) {
            if (Array.isArray(arr)) {
                for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) {
                    arr2[i] = arr[i];
                }
                return arr2;
            } else {
                return Array.from(arr);
            }
        }

        new Vue({
            el: '#v-contacts',
            data: {
                timer: -1,
                loading: false,
                //form: {nickName: ''},
                form: {},
                list: []
            },
            methods: {
                query: function query(timeout) {
                    var _this = this;

                    clearTimeout(this.timer);
                    this.timer = setTimeout(function () {
                        _this.loading = true;
                        $.post(base + '/api/getFriendAndGroup', _this.form, function (res) {
                            _this.list = (res.data || []).map(function (o) {
                                o.folded = false;
                                o.count = o.nodes.reduce(function (p, x) {
                                    return p += x.nodes.length;
                                }, 0);
                                o.nodes.forEach(function (x) {
                                    x.folded = false;
                                    x.count = x.nodes.length;
                                });
                                return o;
                            });
                            _this.loading = false;
                        });
                    }, timeout || 0);
                },
                toChat: function toChat(item) {
                    if (item.jid) {
                        var li = document.getElementById(item.jid);
                        if (li) {
                            console.log("exit");
                            $('#friendList').find('li').each(function () {
                                if ($(this).attr('id') === item.jid) {
                                    $('#friendList').prepend($(this));
                                }
                            });
                            $('[data-target="friendAndGroupBtn"]').hide();
                            xchat.openFriendWindow(1, item.jid, item.text, null, null);
                        } else {
                            $.ajax({
                                url: xchat.interface.getConversation + "?from=" + item.jid,
                                type: 'GET',
                                timeout: 3000,
                                success: function (res) {
                                    if (res.success) {
                                        var li = document.getElementById(item.jid);
                                        if (li) {
                                            $('[data-target="friendAndGroupBtn"]').hide();
                                            xchat.openFriendWindow(1, item.jid, item.text, null, null);
                                            console.log("exit");
                                        } else {
                                            xchat.onlineQueueSuccessStatusHandelEvent(res.data);
                                            $('#friendList').find('li').each(function () {
                                                if ($(this).attr('id') === item.jid) {
                                                    $('#friendList').prepend($(this));
                                                }
                                            });
                                            $('[data-target="friendAndGroupBtn"]').hide();
                                            xchat.openFriendWindow(1, item.jid, item.text, null, null);
                                        }
                                    }
                                },
                                error: function () {
                                    //$(_this.controls.userTags).html('获取会话消息失败');
                                }
                            });
                            console.log("not exit");
                        }
                    }
                }
            },
            created: function created() {
                this.query();
            },
            directives: {
                fold: {
                    componentUpdated: function componentUpdated(el, o, p) {
                        var height = (o.value ? 0 : [].concat(_toConsumableArray(el.children)).reduce(function (p, o) {
                                return p += o.offsetHeight;
                            }, 0)) + 'px';
                        if (el.style.height != height) {
                            el.style.height = height, setTimeout(function () {
                                return p.context.$forceUpdate();
                            }, 30);
                        }
                    }
                }
            }
        });
    },


};
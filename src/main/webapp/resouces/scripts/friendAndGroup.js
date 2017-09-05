/**
 * 历史记录
 * @constructor
 */
'use strict';

var FriendAndGroup = function (options) {
    options = options || {};
    this.opts = {
        levels: 99,
        expandIcon: 'glyphicon glyphicon-expand',
        collapseIcon: 'glyphicon glyphicon-collapse',
        emptyIcon: 'glyphicon',
        nodeIcon: 'glyphicon glyphicon-stop',
        selectedIcon: 'glyphicon glyphicon-selected',
        checkedIcon: 'glyphicon glyphicon-checked-icon',
        uncheckedIcon: 'glyphicon glyphicon-unchecked-icon',
        //color: 'yellow',
        //backColor: 'purple',
        borderColor: 'purple',
        onhoverColor: 'gray',
        selectedColor: 'yellow',
        selectedBackColor: 'darkorange',
        searchResultColor: 'blue',
        searchResultBackColor: 'darkorange',
        enableLinks: true,
        highlightSelected: false,
        highlightSearchResults: true,
        showBorder: false,
        showIcon: false,
        showCheckbox: true,
        showTags: true,
        multiSelect: true,
        onNodeChecked: function () {
        },
        onNodeCollapsed: function () {
        },
        onNodeDisabled: function () {
        },
        onNodeEnabled: function () {
        },
        onNodeExpanded: function () {
        },
        onNodeSelected: function (event, node) {
            var tree = $('#friendAndGroupree');
            if (node.state.expanded) {
                //处于展开状态则折叠
                tree.treeview('collapseNode', node.nodeId);
            } else {
                //展开
                tree.treeview('expandNode', node.nodeId);
            }

            if (node.jid) {
                var li = document.getElementById(node.jid);
                if (li) {
                    console.log("exit");
                    $('#friendList').find('li').each(function () {
                        if ($(this).attr('id') === node.jid) {
                            $('#friendList').prepend($(this));
                        }
                    });
                    $('[data-target="friendAndGroupBtn"]').hide();
                    xchat.openFriendWindow(1, node.jid, node.text, null, null);
                } else {
                    $.ajax({
                        url: xchat.interface.getConversation + "?from=" + node.jid,
                        type: 'GET',
                        timeout: 3000,
                        success: function (res) {
                            if (res.success) {
                                var li = document.getElementById(node.jid);
                                if (li) {
                                    $('[data-target="friendAndGroupBtn"]').hide();
                                    xchat.openFriendWindow(1, node.jid, node.text, null, null);
                                    console.log("exit");
                                } else {
                                    xchat.onlineQueueSuccessStatusHandelEvent(res.data);
                                    $('#friendList').find('li').each(function () {
                                        if ($(this).attr('id') === node.jid) {
                                            $('#friendList').prepend($(this));
                                        }
                                    });
                                    $('[data-target="friendAndGroupBtn"]').hide();
                                    xchat.openFriendWindow(1, node.jid, node.text, null, null);
                                }
                            }
                        },
                        error: function () {
                            $(_this.controls.userTags).html('获取会话消息失败');
                        }
                    });
                    console.log("not exit");
                }
            }
            ;

        },
        onNodeUnchecked: function () {
        },
        onNodeUnselected: function () {
        },
        onSearchComplete: function () {
        },
        onSearchCleared: function () {
        }
    };

    this.currentId = options.currentId || window.currentId;
    this.base = options.base || window.base;
    //控件
    this.searchfriendAndGroupInput = $('#searchfriendAndGroupInput');
    this.searchFriendAndGroupBtn = $('#searchriendAndGroupBtn');
    this.friendAndGroupBtn = $('#friendAndGroupBtn');
    this.table = $('#table');

    this.interface = {
        getFriendAndGroup: window.base + '/api/getFriendAndGroup'
    }
};
FriendAndGroup.prototype = {
    /*=====================================================================================初始化=====================================================================================*/
    init: function () {
        this.getFriendAndGroupEventBind();     //绑定获取好友
        this.searchFriendAndGroupEventBind();
    },
//获取历史记录事件绑定
    getFriendAndGroupEventBind: function () {
        var _this = this;
        $(_this.friendAndGroupBtn).on('click', function () {
            _this.getFriendAndGroupList('');
        });
    },
    //获取历史记录
    getFriendAndGroupList: function (nickName) {
        var _this = this;
        $.ajax({
            url: _this.interface.getFriendAndGroup + '?nickName=' + nickName,
            type: 'POST',
            success: function (res) {
                if (res.success) {
                    //console.log(res.data);
                    //console.log(_this.opts);
                    _this.opts.data = res.data;
                    $('#friendAndGroupree').treeview(_this.opts);
                } else {
                    alert('查询好友失败，请重试');
                }
            },
            error: function () {
                alert('查询好友失败，请重试');
            }
        });
    },

    //搜索历史记录事件绑定
    searchFriendAndGroupEventBind: function () {
        var _this = this;

        /*this.searchFriendAndGroupBtn.on('click', function () {
         var nickName = _this.searchfriendAndGroupInput.val();
         $('#friendAndGroupree').treeview('search', [ nickName, {
         ignoreCase: true,     // case insensitive
         exactMatch: false,    // like or equals
         revealResults: true,  // reveal matching nodes
         }]);
         });*/

        _this.searchFriendAndGroupBtn.on('click', function () {
            var nickName = _this.searchfriendAndGroupInput.val();
            _this.getFriendAndGroupList(nickName)
        });
    }
    /*=====================================================================================历史记录=====================================================================================*/
};
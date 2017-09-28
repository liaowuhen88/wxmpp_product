<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script src="https://vuejs.org/js/vue.min.js"></script>

<style>
    * {
        font-size: 14px;
    }

    .v-group {
        width: 350px;
        padding: 10px;
        font-size: 16px;
        display: inline-block;
        background-color: #f0f0f0;
    }

    .v-group button {
        padding: 5px;
        outline: none;
        cursor: pointer;
        color: #606060;
        line-height: 1.5;
        padding: 2px 10px;
        border-radius: 4px;
        vertical-align: middle;
        border: 1px solid #dddddd;
        background-color: #ffffff;
    }

    .v-group button[type="primary"] {
        width: 100%;
        padding: 10px;
        color: #ffffff;
        border: 1px solid #339933;
        background-color: #339933;
    }

    .v-group button[type="primary"]:hover {
        border: 1px solid #339933;
        background-color: #339933;
    }

    .v-group button[type="primary"]:active {
        border: 1px solid #117711;
        background-color: #117711;
    }

    .v-group button[type="text"] {
        color: #339933;
    }

    .v-group button[type="text"]:hover {
        color: #339933;
    }

    .v-group button[type="text"]:active {
        color: #117711;
    }

    .v-group > .search {
        display: flex;
        position: relative;
    }

    .v-group > .search > input {
        flex: 1;
        outline: none;
        padding: 6px 4px 6px 8px;
        border-radius: 4px;
        border: 1px solid #cccccc;
    }

    .v-group > .search:hover > input {
        padding-right: 25px;
    }

    .v-group > .search > input:focus {
        border: 1px solid #339933;
    }

    .v-group > .search > .clear {
        top: 0px;
        right: 0px;
        bottom: 0px;
        margin: 6px;
        display: none;
        cursor: pointer;
        font-size: 16px;
        color: #666666;
        padding: 0px 3px;
        line-height: 16px;
        font-style: normal;
        position: absolute;
    }

    .v-group > .search:hover > .clear,
    .v-group > .search > input:focus + .clear {
        display: block;
    }

    .v-group > .search:hover > .clear:hover {
        color: #339933;
    }

    .v-group .list > .item {
        display: flex;
        padding: 5px 10px;
        line-height: 30px;
    }

    .v-group .list > .item > img {
        min-width: 30px;
        min-height: 30px;
        border-radius: 4px;
    }

    .v-group .list > .item > span {
        flex: 1;
        overflow: hidden;
        padding: 0px 10px;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .v-group .list > .item > .options > button[type="primary"] {
        font-size: 12px;
        padding: 3px 6px;
    }
</style>
<%--模态框--%>
<div class="modal" data-target="groupListBtn">
    <div class="modal_backdrop"></div>
    <div class="modal_dialog">
        <div class="modal_content">
            <div class="modal_header">
                <span class="modal_close">X</span>群列表
            </div>
            <div id="v-group">
                <div class="v-group">
                    <div class="search">
                        <input placeholder="查找" v-model="filter"/>

                        <div class="clear" @click="filter=''">×</div>
                    </div>
                    <div class="list">
                        <div class="item" v-for="(item,i) in list" :key="i" v-if="item.nickname.includes(filter)">
                            <img type="user" :src="item.icon" width="30" height="30"/>
                            <span v-html="item.nickname"></span>

                            <div class="options">
                                <button type="primary" @click="addFriend(item.jid)" v-if="!item.is_friend">添加好友</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal_footer">
                <button class="btn cancel-btn" data-close="modal">关闭</button>
            </div>
        </div>
    </div>

    <script>
        'use strict';

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

        var vGroup = new Vue({
            el: '#v-group',
            data: {
                filter: '',
                //list: [{ nickname: 'aaa' }, { nickname: 'bbb' }]
                list: []
            },
            methods: {
                addFriend: function addFriend(jid) {
                    xchat.addFriend(jid);
                }
            },
            created: function created() {
                //var _this = this;
                console.log('created');
                /*var xx = "xvql356@conference.126xmpp";
                 $.post(window.base + "/api/getGroupUsers", { username: decodeURIComponent(xx) }, function (res) {
                 _this.list = res.data;
                 }, 'json');*/
            }
        });
    </script>

</div>
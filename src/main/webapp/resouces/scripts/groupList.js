/**
 * 历史记录
 * @constructor
 */
'use strict';

var groupList = function (options) {
};
groupList.prototype = {
    /*=====================================================================================初始化=====================================================================================*/
    init: function () {
        this.getGroupListEventBind();     //绑定获取好友
    },
//获取历史记录事件绑定
    getGroupListEventBind: function () {
        var _this = this;

        _this.initVue();


    },
    /*=====================================================================================历史记录=====================================================================================*/

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
                        $.post(base + '/api/getGroupUsers', _this.form, function (res) {
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
                    alert(item);
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
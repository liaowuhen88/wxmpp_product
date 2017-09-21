<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<body>

<style>
    #v-material {
        margin: 0px;
        height: 100%;
        display: flex;
        flex-direction: column;
        background-color: #f0f0f0;
    }

    .v-material {
        flex: 1;
        width: 260px;
        height: 100%;
        display: flex;
        font-size: 16px;
        position: relative;
        flex-direction: column;
        background-color: #ffffff;
    }

    .v-material[loading]::before {
        top: 0px;
        left: 0px;
        right: 0px;
        bottom: 0px;
        z-index: 1;
        content: '';
        position: absolute;
        background-color: rgba(255, 255, 255, 0.5);
    }

    .v-material[loading]::after {
        top: 50%;
        left: 50%;
        z-index: 2;
        color: #ffffff;
        padding: 5px 10px;
        position: absolute;
        border-radius: 4px;
        content: '加载中...';
        background-color: rgba(0, 0, 0, 0.5);
        transform: translate(-50%, -50%);
        -moz-transform: translate(-50%, -50%);
        -webkit-transform: translate(-50%, -50%);
    }

    .v-material select {
        width: 100%;
        outline: none;
        margin: 2px 0px;
        cursor: pointer;
        position: relative;
        padding: 1px 0px 5px 0px;
        border: 1px solid #cccccc;
    }

    .v-material > .tabs {
        display: flex;
    }

    .v-material > .tabs > .tab {
        flex: 1;
        cursor: pointer;
        line-height: 40px;
        text-align: center;
        background-color: #f8f8f8;
        border-bottom: 1px solid #eeeeee;
    }

    .v-material > .tabs > .tab.active {
        background-color: #ffffff;
        border-bottom: 1px solid #ffffff;
    }

    .v-material > .tabs > .tab:not(:first-child) {
        border-left: 1px solid #eeeeee;
    }

    .v-material .search {
        padding: 5px;
    }

    .v-material .search-input {
        display: flex;
        position: relative;
    }

    .v-material .search-input > input {
        flex: 1;
        padding: 4px;
        outline: none;
        border-radius: 2px;
        padding-right: 20px;
        border: 1px solid #cccccc;
    }

    .v-material .search-input > .icon-clear {
        top: 5px;
        right: 5px;
        width: 16px;
        height: 16px;
        cursor: pointer;
        position: absolute;
        content: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAADJElEQVRoQ+2ZgU0cMRBFvysIqSCkAkgFkApIB0AHsxUkVHBDBYEOoAKggpAKSDpIKnD0T97T5nZtj+29RScx0gmhW3v/mxl77DmHPTe35/rxBvDaEZw9AiJy7Jx7B+DAe39MQOfcLwD8YLVaPc0JPQtA13Vn3vtTAF8AHGYE/gHw6Jy7897fqyr/r7YmgK7rzr333wyiUwI5/roWpApAROjpVaPwIRSjoKp6VRqKYgAR+QqAXtuFPQP4XBINM4CIHAD4HvJ8F+L7ORkNQhAmayaAIP4BwHpXWcDMEFYAev5iAeHb6+JjLp2yACLCfGfev4Y9q+qn1IuTACLCvZ2psyv7C4BFL2VXqhrdNHIAFE+IlF2GL5lmJXYLQFjUABxlBjKV1pV826IARu9fquoNJxURrhErxK2qrtdU2CByEJvnSwBy3t+I7yc1QozEBAhumx8SkZiMwmQERITnmZdMWG9UtU+fzaMZiElPGsE7lmpTBESEucmjQs5KIFrEU8eTqo7WYywCdwDOcurD9xaIVvG9lPfbdSEG4I3i+8dSEKf9gh3OaUybbRk8YnDBb2wuAE44CTHliErxnGq0DkYAvFEB+FEYgWQkZvB8P8WoqE0BtFbfaCQaPL8oQLToLAXQkkJR8YXFLpbB+RTiSBEp3YU4LCt+BohR9Z9rF0rt8ycVFTsWAfM2WlLILEXKUuwsG5+5kFlPlhbxlmJnOcWyh8RuyH/WcpgrET8HhP0wFxYyS/ZJIq61x+lROoXiyeM7Ox8xsx+nA4CloNVeaDYQRvHs3PGEPLLclTIXBU5Ye6XkTe463LlTnue9+TDWncgBWC42lt2j5Zn6S31IJeuO1CIyNnbyEjN8ONsXChAM9/kuFCbm/MmOSHNja1D+LethLkbmPcVn+6OmCAwglogEPX9hEU9dRQALrIn7IN78q00xwKBGsN2XKnQl6fSbvzn0TbKSgVUAg5RisWOvJtcajGlirnM8Wz5mrxfvQjmPhGpKGG65ORh6m6fdR1Xl3yZrikDszQGI1bX/9I1ZtsurPB17104AmlxaOPgNoNBhsz++9xH4B6mxhEAvcquBAAAAAElFTkSuQmCC)
    }

    .v-material .search-input.empty > .icon-clear {
        display: none;
    }

    .v-material > .list {
        flex: 1;
        overflow: auto;
    }

    .v-material > .list > .item {
        padding: 10px;
        cursor: pointer;
    }

    .v-material > .list > .item:hover {
        background-color: #f0f0f0;
    }

    .v-material > .list > .item > div {
        width: 100%;
        height: 100%;
    }

    .v-material > .list > .item.text .title {
        font-size: 18px;
        margin-bottom: 5px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .v-material > .list > .item.text .content {
        color: #999999;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
    }

    .v-material > .list > .item.image,
    .v-material > .list > .item.audio,
    .v-material > .list > .item.video {
        width: 41%;
        height: 110px;
        display: inline-block;
    }

    .v-material > .list > .item .img {
        width: 100%;
        height: 100%;
        position: relative;
        border: 1px solid #dddddd;
        background-size: contain;
        background-color: #ffffff;
        background-repeat: no-repeat;
        background-position: 50% 50%;
    }

    .v-material > .list > .item .img[title]::after {
        left: 0px;
        right: 0px;
        bottom: 0px;
        color: #ffffff;
        padding: 2px 4px;
        overflow: hidden;
        position: absolute;
        white-space: nowrap;
        content: attr(title);
        text-overflow: ellipsis;
        background-color: rgba(0, 0, 0, 0.6);
    }

    .v-material > .list > .item .img[duration]::before {
        top: 0px;
        right: 0px;
        font-size: 12px;
        color: #ffffff;
        padding: 2px 4px;
        position: absolute;
        content: attr(duration);
        border-bottom-left-radius: 4px;
        background-color: rgba(0, 0, 0, 0.6);
    }

    .v-material > .list > .item.audio .img {
        background-size: 50%;
        background-image: url(data:image/png;base64,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);
    }

    .v-material > .paging {
        padding: 10px;
        text-align: center;
        user-select: none;
        -ms-user-select: none;
        -moz-user-select: none;
        -webkit-user-select: none;
        border-top: 1px solid #eeeeee;
    }

    .v-material > .paging > a {
        color: #666666;
        padding: 0px 5px;
        line-height: 20px;
        display: inline-block;
    }

    .v-material > .paging > a:first-child {
        float: left;
    }

    .v-material > .paging > a:last-child {
        float: right;
    }

    .v-material > .paging > a:hover {
        color: #44b549;
        cursor: pointer;
    }

    .v-material > .paging > a.active {
        color: white;
        border-radius: 2px;
        background-color: #44b549;
    }
</style>

<div id="v-material">
    <div class="v-material" :loading="loading">
        <div class="tabs">
            <div class="tab" :class="{active:tabIndex==0}" @click="tabChange(0)">文本</div>
            <div class="tab" :class="{active:tabIndex==1}" @click="tabChange(1)">图片</div>
            <div class="tab" :class="{active:tabIndex==2}" @click="tabChange(2)">音频</div>
            <div class="tab" :class="{active:tabIndex==3}" @click="tabChange(3)">视频</div>
        </div>
        <div class="search">
            <div class="common">
                <div class="search-input" :class="{empty:!form.title}">
                    <input placeholder="查询" v-model="form.title" @input="query(1,500)"/>
                    <i class="icon-clear" @click="form.title='',query(1)"></i>
                </div>
            </div>

            <div v-if="tabIndex==1">
                <select v-model="form.category_id" @change="query(1)">
                    <option value default>全部</option>
                    <option v-for="o in forms[1].category" :value="o.id" v-text="o.name"></option>
                </select>
            </div>
        </div>
        <div class="list">
            <div class="item" :class="['text','image','audio','video'][tabIndex]" v-for="item in list"
                 @click="select(item)">
                <div v-if="tabIndex==0">
                    <div class="title" :title="item.title" v-text="item.title"></div>
                    <div class="content" :title="item.content" v-text="item.content"></div>
                </div>
                <div v-else-if="tabIndex==1">
                    <div class="img" :style="{backgroundImage:'url('+item.url+')'}" :title="item.title"></div>
                </div>
                <div v-else-if="tabIndex==2">
                    <div class="img" :title="item.title" :duration="item.longtime"></div>
                </div>
                <div v-else-if="tabIndex==3">
                    <div class="img" :style="{backgroundImage:'url('+item.thumbnail+')'}" :title="item.title"
                         :duration="item.longtime"></div>
                </div>
            </div>
        </div>
        <div class="paging">
            <a @click="query(--index)" v-show="index>1">&lt;</a>
            <a :class="{active:o.index==index}" v-for="(o,i) in btns" :key="i" @click="query(index=o.index)"
               v-text="o.text"></a>
            <a v-if="!btns.length">暂无数据</a>
            <a @click="query(++index)" v-show="index<pageCount">&gt;</a>
        </div>
    </div>
</div>

<script>
    'use strict';

    function _defineProperty(obj, key, value) {
        if (key in obj) {
            Object.defineProperty(obj, key, {value: value, enumerable: true, configurable: true, writable: true});
        } else {
            obj[key] = value;
        }
        return obj;
    }
    new Vue({
        el: '#v-material',
        data: {
            timer: -1,
            tabIndex: 0,
            loading: false,
            url: window.base + '/api/getMaterial',
            forms: [{
                form: {title: ''},
                index: 1, size: 10,
                list: [], total: 0
            }, {
                form: {title: '', category_id: ''},
                category: [],
                index: 1, size: 10,
                list: [], total: 0
            }, {
                form: {title: ''},
                index: 1, size: 10,
                list: [], total: 0
            }, {
                form: {title: ''},
                index: 1, size: 10,
                list: [], total: 0
            }],
            paging: {
                index: 'page',
                size: 'limit',
                list: 'data',
                total: 'total',
                startIndex: 1,
                maxBtnCount: 5
            }
        },
        computed: {
            form: function form() {
                return this.forms[this.tabIndex].form;
            },

            index: {
                get: function get() {
                    return this.forms[this.tabIndex].index - this.paging.startIndex + 1;
                },
                set: function set(value) {
                    this.forms[this.tabIndex].index = Math.max(this.paging.startIndex, value + this.paging.startIndex - 1);
                }
            },
            size: {
                get: function get() {
                    return this.forms[this.tabIndex].size;
                },
                set: function set(value) {
                    this.forms[this.tabIndex].size = Math.max(1, value);
                }
            },
            total: {
                get: function get() {
                    return this.forms[this.tabIndex].total;
                },
                set: function set(value) {
                    this.forms[this.tabIndex].total = Math.max(0, value);
                }
            },
            list: {
                get: function get() {
                    return this.forms[this.tabIndex].list;
                },
                set: function set(value) {
                    this.forms[this.tabIndex].list = value instanceof Array ? value : [];
                }
            },
            pageCount: function pageCount() {
                return Math.ceil(this.total / this.size);
            },
            btns: function btns() {
                var btns = [],
                        half = Math.floor(this.paging.maxBtnCount / 2),
                        start = Math.max(1, Math.min(this.index - half, this.pageCount - this.paging.maxBtnCount + 1)),
                        end = Math.min(Math.max(this.paging.maxBtnCount, this.index + half), this.pageCount);
                if (start > 1) btns.push({index: start - 1, text: '...'});
                for (var i = start; i <= end; i++) {
                    btns.push({index: i, text: i});
                }
                if (end < this.pageCount) btns.push({index: end + 1, text: '...'});
                return btns;
            }
        },
        methods: {
            query: function query() {
                var _this = this;

                var index = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 1;
                var timeout = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

                this.index = index;
                clearTimeout(this.timer);
                this.timer = setTimeout(function () {
                    _this.loading = true;
                    $.post(_this.url, _defineProperty({
                        type: ['text', 'img', 'audio', 'video'][_this.tabIndex],
                        content: JSON.stringify(Object.assign(_defineProperty({}, _this.paging.size, _this.size), _this.form))
                    }, _this.paging.index, _this.index), function (res) {
                        _this.list = res.data[_this.paging.list];
                        _this.total = res.data[_this.paging.total];
                        _this.loading = false;
                    }, 'json');
                }, timeout);
            },
            tabChange: function tabChange(index) {
                if (this.tabIndex != index) {
                    this.tabIndex = index;
                    this.query();
                }
            },
            select: function select(item) {
                var contentType = item.type;
                var content;
                if (contentType == 'text') {
                    content = item.content;
                } else {
                    content = item.url;
                }
                xchat.sendEvent(content, contentType);
                /*------------------------------------------------*/
                console.log('选中的数据', item);
                /*------------------------------------------------*/
            }
        },
        created: function created() {
            this.query();
        }
    });
</script>

</body>

</html>


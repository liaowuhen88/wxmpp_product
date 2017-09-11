<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script src="<%=request.getContextPath()%>/resouces/js/bootstrap-treeview.js"></script>
<link type="text/css" href="<%=request.getContextPath()%>/resouces/css/bootstrap.css"/>
<script src="https://vuejs.org/js/vue.min.js"></script>

<style>
    * {
        font-size: 14px;
    }

    .v-contacts {
        width: 350px;
        padding: 10px;
        font-size: 16px;
        display: inline-block;
        background-color: #f0f0f0;
    }

    .v-contacts > .search {
        display: flex;
        position: relative;
    }

    .v-contacts > .search > input {
        flex: 1;
        outline: none;
        padding: 6px 4px 6px 8px;
        border-radius: 4px;
        border: 1px solid #cccccc;
    }

    .v-contacts > .search:hover > input {
        padding-right: 25px;
    }

    .v-contacts > .search > input:focus {
        border: 1px solid #339933;
    }

    .v-contacts > .search > .clear {
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

    .v-contacts > .search:hover > .clear,
    .v-contacts > .search > input:focus + .clear {
        display: block;
    }

    .v-contacts > .search:hover > .clear:hover {
        color: #339933;
    }

    .v-contacts .header {
        padding: 5px;
        display: flex;
        cursor: pointer;
        position: relative;
    }

    .v-contacts .header > .fold {
        width: 15px;
        margin: 2px 5px;
        font-size: 14px;
        color: #ffffff;
        line-height: 16px;
        text-align: center;
        border-radius: 4px;
        background-color: #999999;
    }

    .v-contacts .header > .title {
        flex: 1;
    }

    .v-contacts .header > .count {
        height: 16px;
        font-size: 12px;
        color: #ffffff;
        padding: 0px 5px;
        line-height: 16px;
        border-radius: 8px;
        background-color: #999999;
    }

    .v-contacts .header > .count:empty {
        display: none;
    }

    .v-contacts .account {
        padding: 10px 0px;
    }

    .v-contacts .account:not(:last-child) {
        border-bottom: 2px solid #dddddd;
    }

    .v-contacts .type {
        font-size: 14px;
        padding-left: 30px;
    }

    .v-contacts .type > .header > .title {
        font-size: 14px;
        color: #808080;
    }

    .v-contacts .item {
        padding: 5px;
        display: flex;
        cursor: pointer;
        font-size: 16px;
        margin: 5px 0px 5px 25px;
    }

    .v-contacts .item:hover {
        background-color: #e0e0e0;
    }

    .v-contacts .item:active {
        background-color: #c0c0c0;
    }

    .v-contacts .item > img {
        width: 30px;
        height: 30px;
        border: none;
        min-width: 30px;
        min-height: 30px;
        overflow: hidden;
        position: relative;
        border-radius: 4px;
        vertical-align: middle;
    }

    .v-contacts .item > img::after {
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        content: '';
        display: block;
        position: absolute;
        border-radius: 4px;
        background-size: cover;
        background-color: #ffffff;
        background-repeat: no-repeat;
        background-position: 50% 50%;
        background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAFEklEQVR4Xu2bjVUUQQzHMxUoFQgVKBWIFQgVKBWQq0CtgFCBWoFYgVCBUIFQgVjB+v7n7L3d7NfMJLuHevPePUX3Zie/fExmEgIzV/Qfj7ADsLOAnQvsYsCCMfCeiK6I6K7xwev3G58jInq21JqWCIK/iOgTPiJykyIYM78gorfx8yTlO6XPzAkAggs+IvJQskBmfkpEHD+zgJgLwC0RHZUKrmExM1zkkoiel4Ac+84cAD6LCMy3MyBICOF1VVXwc2gXgmEgJjyEEK6qqvoqIvi5NaI1wKLeeELwBnAhIjBZvXgI/A5Wkbh4BMoPIoI/9VyAcJY4z+RjngA6mo9a+0hEx5Mr6X8AZn+qXYmZ8e+vC+dsfc0LQMfno99+ISJEdMvAznHSdIsIFtZhjgleAA7UAuHb36OfW4Svv4tdBO/Y7CYR8A/r5B4AWqYftfPNQfNaNljCKwUB+YUpKHoA0Np3DVKKQivIeliBFQC2rE2A81hQgklr4KaAaAWACA0zXA9mNptkAgDtcsg5sNMUDSuAPeWTCFKzpKwN6R5EZK8BHQnVzyLpicgC4FpENokNM+PvCH5LDATDTZLEzAiQRVuiJ4D3MdtbAgCyRLyvdj3AeFnyYgsAHZG3CaB457EAcNNCgeb07lMM3xPAEjtAzcrN+iwA3LRQYAHa+opzAQsAvQuY9uNMCDr/2EoQvBORg0YkxgHIfDhJBKGzQby3vlxJnOLPYxYLwPcPmxedlv04Y9W3IrI5YscLVJw8i4YVgPbFJdxAm3/xFuhhAS03wITMjPu8ue7170WkZerMXGz+HgAwh9YIToe4CZpj6BTYbHFWF4CQfVaAi9FzZwIrFBiac1q172UBmKdvcZ6JUd+FqwtkDwuoldIyzxgPzCaqXSzO63by9ASAuwBAaNX/4jEZuXruae0aMbVnPmyBOHbjHsA8PAGs40G8wu4UQSMIWASC5NClCeqJSGtRSO0rikB4BNiipKePljcAvAOWgHv8jgD1AiIM/FgnNACGm57B6jEzAxyuvlw0X69lDgD13IjYSJSKKsMNWBAYZbVOyc1s/w6p8NQaJq1hbIJoKTB5V6033zmXBdS9AfDlTqV3ipra6+HviB2wAPcLV28AEBwRH4KbTF9DihUngMD8biA8AVxgcWOCQ4gQAm5v96uqakXyEMK6b6iqKpz2BuF59wl4AIDWj4eifqwWoZ6P5CW1UozdALsIrr56XSjuCsg2TdZgBTDYChMDWE5TxFBoGGuWMLfOWAD0Ch81jv06tRskNSYCBE6eLYuw9gqUAugVfrVanVVV1TqxpUqX+lwIgc/PzxFvNsMCoQTAkOah9d7mqFThMp7DLnPqASEXAAIe2t82KeuMDRFTPOASSLmbXSMIsvj35MCYC6B1+xOPpjiZefv7lPD1/1+KyImyhKwjeA6AVh0gCr+k2Q9B6bTmMXNynSAHgL6LzyKdqtLC5+AKOEavR06nSiqAvkYo3MbOdkjJBNHXRZZ0JZcKQGs/afJMIayPayUlVapSAGyjEaoURnYDVQoA7V+PUfs1sOwGqkkAIhLUNoOGpMfi+9pSWg1UMSCO/krQFABdAp+z6lNq9vp72mJHt8QpALr4aSpEekk4MU9W98gUAF2LQxk69Uy/kLyd19yIyGEjJxgtokwB0FH1r/gVu2bcmuofGAWgJkraV7eldvXeZMXlAHCrxy0ASbvuoOXuAIz99rhygX/SAn4DuATtH2TwkMcAAAAASUVORK5CYII=");
    }

    .v-contacts .item > span {
        overflow: hidden;
        line-height: 30px;
        margin-left: 10px;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .v-contacts .content {
        overflow: hidden;
        transition: 300ms;
        -moz-transition: 300ms;
        -webkit-transition: 300ms;
    }
</style>
<%--模态框--%>
<div class="modal" data-target="friendAndGroupBtn">
    <div class="modal_backdrop"></div>
    <div class="modal_dialog">
        <div class="modal_content">
            <div class="modal_header">
                <span class="modal_close">X</span>好友列表
            </div>
            <div id="v-contacts">
                <div class="v-contacts" :loading="loading">
                    <div class="search">
                        <input placeholder="搜索" v-model="form.nickName" @input="query(500)"/>

                        <div class="clear" @click="form.nickName='',query()">×</div>
                    </div>
                    <div class="list">
                        <div class="account" v-for="account in list">
                            <div class="header" @click="account.folded=!account.folded">
                                <span class="fold" v-text="account.folded?'+':'-'"></span>
                                <span class="title" v-text="account.text"></span>
                                <span class="count" v-text="account.count"></span>
                            </div>
                            <div class="content" v-fold="account.folded">
                                <div class="type" v-for="type in account.nodes">
                                    <div class="header" @click="type.folded=!type.folded">
                                        <span class="fold" v-text="type.folded?'+':'-'"></span>
                                        <span class="title" v-text="type.text"></span>
                                        <span class="count" v-text="type.count"></span>
                                    </div>
                                    <div class="content" v-fold="type.folded">
                                        <div class="item" v-for="item in type.nodes" @click="toChat(item)">
                                            <img :src="'http://vipkefu.oss-cn-shanghai.aliyuncs.com/vvZhuShou/'+item.jid.replace(/@.*/g,'')+'.jpg'"/>
                                            <span :title="item.text" v-text="item.text"></span>
                                        </div>
                                    </div>
                                </div>
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


    </script>
</div>
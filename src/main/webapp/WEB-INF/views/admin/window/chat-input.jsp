<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<style>
    .chat-window .chat-screen {
        position: relative;
    }

    #library {
        position: absolute;
        bottom: 190px;
        left: 0;
        right: 0;
        height: 440px;
        background: #fff;
        box-shadow: 0 0 10px rgba(0, 0, 0, .15);
    }

    .library_hide {
        display: none;
    }

    .library_show {
        display: block;
    }

    #library ul, #library li {
        margin: 0;
        padding: 0;
        list-style: none;
    }

    #library .bars {

        border-bottom: 1px solid #eee;
        overflow: hidden;
    }

    #library .bars li {
        line-height: 39px;
        float: left;
        width: 100px;
        text-align: center;
        cursor: pointer;
    }

    #library .bars .active {
        color: red;
        font-weight: bold;
    }

    #library .info .content {
        display: none;
        height: 360px;
        overflow: auto;
    }

    #library .info .active {
        display: block;
    }

    #library .libraries {
        padding: 10px;
    }

    #library .libraries .item {
        line-height: 24px;
        border-bottom: 1px solid #eee;
    }

    #library .libraries h3.title {
        font-size: 14px;
        margin: 0;
        padding: 0;
    }

    #library .libraries .cont {
        font-size: 12px;
        color: #666;
    }
</style>
<script>

</script>
<div id="library" class="library_hide">
    <ul class="bars">
        <li class="bar text active" value="text">文本</li>
        <li class="bar img" value="img">图片</li>
        <li class="bar video" value="video">视频</li>
        <li class="bar audio" value="audio">语音</li>
    </ul>
    <ul class="info">
        <li class="content active">
            <ul class="libraries">

            </ul>
            <div class="pages"></div>
        </li>
        <li class="content">
            <ul class="libraries">

            </ul>
            <div class="pages"></div>
        </li>
        <li class="content">
            <ul class="libraries">

            </ul>
            <div class="pages"></div>
        </li>
        <li class="content">
            <ul class="libraries">

            </ul>
            <div class="pages"></div>
        </li>
    </ul>
</div>
<div class="chat-input" id="chatInput">
    <ul class="tools">
        <%--<li data-toggle="tooltip" data-placement="top" title="素材库"><i class="tools-icon icon-emoji"></i></li>--%>
        <li data-toggle="tooltip" data-placement="top" title="图像"><i class="tools-icon icon-image" id="imgUploader"></i></li>
        <li data-toggle="tooltip" data-placement="top" title="文件"><i class="tools-icon icon-file"
                                                                     id="attachmentUploader"></i></li>
        <%--<li data-toggle="tooltip" data-placement="top" title="评价"><i class="tools-icon icon-invite" id="modal"></i></li>--%>
    </ul>


    <textarea placeholder="请输入" id="enterChat"></textarea>

    <div class="send-btn" >
        <button id="send-btn">发送</button>
    </div>
</div>

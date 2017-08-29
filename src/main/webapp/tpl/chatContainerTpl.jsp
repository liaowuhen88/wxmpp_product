<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
    .file-msg {
        display: block;
        width: 200px;
        height: 60px;
        margin: -8px -12px;
        position: relative;
        text-decoration: none;
    }

    .file-msg:after {
        content: 'File';
        position: absolute;
        top: 0;
        right: 5px;
        width: 35px;
        height: 35px;
        line-height: 35px;
        text-align: center;
        background-color: #fff;
        color: #999;
    }

    .file-msg h1.file-name {
        font-size: 14px;
        font-weight: bold;
        line-height: 35px;
        padding: 0 10px;
        white-space: nowrap;
        overflow: hidden;
        width: 100px;
        text-decoration: none;
    }

    .file-msg .file-size {
        background: #eee;
        font-size: 12px;
        color: #666;
        height: 25px;
        padding: 0 10px;
    }
</style>
<%--alert--%>
<script id="alert" type="text/html">
    <div class="alert" id="look_history">查看漫游历史记录</div>
</script>
<%--访客信息--%>
<script id="mleft" type="text/html">
    <div class="timeline timeline-from">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}}     {{time}}</span>
        <span class="message">{{&content}}</span>
    </div>
</script>

<%--客服信息--%>
<script id="mright" type="text/html">
    <div class="timeline timeline-go">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <span class="message">{{&content}}</span>
    </div>
</script>

<script id="imgLeft" type="text/html">
    <div class="timeline timeline-from" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">
        </div>--%>
        <span class="message"><a href="{{content}}" rel="lightbox"><img src="{{dev_content}}" alt=""></a></span>
    </div>
</script>

<script id="wx_share_Left" type="text/html">
    <div class="timeline timeline-from" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">
        </div>--%>
        <div class="message" style="max-width: 500px;">
            <a href="{{content.url}}" target="view_window"
               style="color: #323c38; text-decoration: none;display: block;">
                <div style="font-size: 16px; margin: 0 0 10px;">{{content.title}}</div>
                <span style="float: left;width: 120px;height: 120px; border-radius: 3px;"><img
                        src="{{content.thumburl}}" alt=""
                        style="width: 120px;height: 120px; border-radius: 3px;"></span>

                <p style="margin: 0 0 0 130px; font-size: 12px; line-height: 20px;">{{content.des}}</p>
            </a>
        </div>
    </div>
</script>

<script id="attachmentLeft" type="text/html">
    <div class="timeline timeline-from" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">
        </div>--%>
        <span class="message">
            <a class="file-msg" href="{{content}}">
                <h1 class="file-name">文件名:{{name}}</h1>

                <div class="file-size">大小:{{size}}</div>
            </a>
        </span>
    </div>
</script>

<script id="audioLeft" type="text/html">
    <div class="timeline timeline-from" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">
        </div>--%>
        <span class="message"><audio src="{{content}}" controls="controls"></audio></span>
    </div>
</script>

<script id="videoLeft" type="text/html">
    <div class="timeline timeline-from" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">
        </div>--%>
        <span class="message">
            <video width="320" height="240" controls="controls" autoplay="autoplay">

                <%-- <source src="http://duobaojl.oss-cn-hangzhou.aliyuncs.com/wechat2017/2843883166980356279.mp4" type="video/mp4" controls="controls"/>--%>
                <source src="{{content}}" type="video/mp4"/>

            </video>

        </span>
    </div>
</script>



<script id="imgRight" type="text/html">
    <div class="timeline timeline-go" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">--%>
        <span class="message"><a href="{{content}}" rel="lightbox"><img src="{{dev_content}}" alt=""></a></span>
    </div>
    </div>
</script>

<script id="attachmentRight" type="text/html">
    <div class="timeline timeline-go" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">--%>
        <span class="message"><a class="file-msg" href="{{content}}">
            <h1 class="file-name">文件名:{{name}}</h1>

            <div class="file-size">大小:{{size}}</div>
        </a></span>
    </div>
    </div>
</script>

<script id="audioRight" type="text/html">
    <div class="timeline timeline-go" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">
        </div>--%>
        <span class="message"><audio src="{{content}}" controls="controls"></audio></span>
    </div>
</script>

<script id="videoRight" type="text/html">
    <div class="timeline timeline-go" id="{{id}}">
        <div class="avatar"><img src="{{icon}}" alt="{{username}}"></div>
        <span class="time">{{fromName}} {{time}}</span>
        <%--<div class="message-wrapper emoji" style="width:265px;height:201px;"><img src="{{content}}" width="265">
        </div>--%>
        <span class="message">
            <video width="320" height="240" controls="controls" autoplay="autoplay">

                <%-- <source src="http://duobaojl.oss-cn-hangzhou.aliyuncs.com/wechat2017/2843883166980356279.mp4" type="video/mp4" controls="controls"/>--%>
                <source src="{{content}}" type="video/mp4"/>

            </video>

        </span>
    </div>
</script>
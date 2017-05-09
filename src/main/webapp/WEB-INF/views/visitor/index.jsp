<%@ page import="com.baodanyun.websocket.core.common.Common" %>
<%@ page import="com.baodanyun.websocket.bean.user.Visitor" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0">
    <title>图片压缩</title>
    <style>
        body {
            margin: 0;
            padding: 0;
        }

        html {
            font-size: 62.5%;
        }

        .imgzip {
            padding: 1em;
        }

        .imgzip .itm {
            padding-bottom: 1em;
            word-break: break-all;
            font-size: 1.2rem;
            line-height: 1.5em;
        }

        .imgzip .itm .tit {
            margin-bottom: .5em;
            background-color: #e71446;
            color: #FFF;
            padding: .5rem 1rem;
            border-radius: 3px;
        }

        .imgzip .itm .cnt {
            padding: 1rem;
        }

        .imgzip .itm .cnt img {
            display: block;
            max-width: 100%;
        }

        .imgzip textarea {
            width: 100%;
            height: 20em;
        }
    </style>
</head>

<body>
<%
    String base = request.getContextPath();
    Visitor visitor = (Visitor) request.getSession().getAttribute(Common.USER_KEY);
%>
<link rel="stylesheet" type="text/css"
      href="<%=request.getContextPath()%>/resouces/webuploader/css/webuploader.css"/>


<script type="text/javascript" src="<%=request.getContextPath()%>/resouces/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resouces/webuploader/webuploader.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resouces/webuploader/uploadimg.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resouces/ajax/common.js"></script>
<%--<script src="<%=base%>/resouces/js/ajaxfileupload.js"></script>--%>

<script>
    window.base = "<%=base%>";



    var uploadUrl = 'http://localhost:9080/upload';
    var downUrl = 'http://localhost:9080/xmppDownLoad/downLoad';

</script>

<div  id="imgzip">

</div>

<div  id="imags">

</div>

<input type="button" value="上一页" onclick="getNext()">

<%--
<img src='http://localhost:9080/xmppDownLoad/downLoad?key=visitor/img/2016102018/a123456_c91e307b-e8ea-4ca4-a40a-0217ab037776.png>
--%>


<script>
    $(function () {

        $.webUploader({
            "uploaderId": "imgzip",
            "isMultiple": false,
            "uploadPath": "/up/visitor/img/" + window.currentId + "?name=123123.png" ,
            "uploadServer": uploadUrl,
            callback: function (file, src) {
                alert("callback");
            },
            success: function (file, response) {//
                var endFile = response.date + '/' + response.id;
                console.log(response );
                $('#imags').append('<img src="'+downUrl+'?key='+response.src+'">');
                /* $("#img" + file.id).wrap('<a href="' + window.base + endurl + endFile + '" data-lightbox="' + sybdid + '"></a>');*/
                alert("success");
            },
            uploadFinished: function (response) {
                //所有

                alert("uploadFinished");
            }
        });
    })

    var page = 1 ;
    function getNext(){
        page ++;
        ajaxCommon(window.base + "/messageHistory/query?from="+window.currentId+"&to="+window.destJid+"&page="+page+"&count=10",
                {from:window.currentId,
                 to:window.destJid,
                 page:page,
                 count:10
                }, function (responseJson) {
            console.log(responseJson);
        }, function (responseJson) {
            $("#errorTip").show();
            $("#errorContent").html(responseJson.msg);
        }, function () {
            $("#errorTip").show();
            $("#errorContent").html("提交超时");
        });
    }


</script>
</body>
</html>
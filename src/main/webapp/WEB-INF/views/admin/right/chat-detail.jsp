<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/tpl/chatTagTpl.jsp"/>
<div class="modal" data-target="examReportBtn">
    <div class="modal_backdrop"></div>
    <div class="modal_dialog">
        <div class="modal_content">
            <div class="modal_header">
                <span class="modal_close">X</span>体检报告
            </div>
            <div class="modal_body modal_scroll" style="max-height: 700px;">
                <div class="iframe-info">
                    <iframe src="<%=request.getContextPath()%>/resouces/exam/index.html" frameborder="0"></iframe>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal" data-target="chatClaimsBtn">
    <div class="modal_backdrop"></div>
    <div class="modal_dialog">
        <div class="modal_content">
            <div class="modal_header">
                <span class="modal_close">X</span>理赔
            </div>
            <div class="modal_body modal_scroll" id="claimsContainer">
            </div>
        </div>
    </div>
</div>
<div class="modal" data-target="chatContractsBtn">
    <div class="modal_backdrop"></div>
    <div class="modal_dialog">
        <div class="modal_content">
            <div class="modal_header">
                <span class="modal_close">X</span>合同
            </div>
            <div class="modal_body modal_scroll" id="contractsContainer">
            </div>
        </div>
    </div>
</div>
<div class="modal" data-target="chatOrderBtn">
    <div class="modal_backdrop"></div>
    <div class="modal_dialog">
        <div class="modal_content">
            <div class="modal_header">
                <span class="modal_close">X</span>订单
            </div>
            <div class="modal_body modal_scroll" id="orderContainer">
            </div>
        </div>
    </div>
</div>
<div class="chat-detail" style="display: none">
    <div class="chat-tab active" id="chat-tab">
        <ul class="chat-tab-bars">
           <%-- <li class="chat-tab-bar active">
                <a href="#home" data-toggle="tab">详细信息</a></li>--%>
            <li class="chat-tab-bar ">
                <a href="#quickchat" data-toggle="tab">快捷回复</a>
            </li>
        </ul>
        <ul class="chat-tab-content">
            <%--<li class="chat-tab-content-info active" id="home" style="overflow: auto">
                <section class="chat-tag">
                    <div class="section-title">用户详情</div>
                    <dl class="type" id="userDetail">
                    </dl>
                </section>
                <section class="chat-tag">
                    <div class="section-title">备注</div>
                    <textarea class="remark" name="" id="remark" cols="30" rows="10"></textarea>

                    <input class="btn" type="button" id="remarkSubmit" value="保存"/>

                </section>
                <section class="chat-tag">
                    <div class="section-title">快捷按钮</div>
                    <div class="btn" id="examReportBtn">体检报告</div>
                    <div class="btn" id="chatClaimsBtn">理赔</div>
                    <div class="btn" id="chatContractsBtn">合同</div>
                    <div class="btn" id="chatOrderBtn">订单</div>
                </section>
                <section class="chat-tag">
                    <div class="section-title">用户标签</div>
                    <ul class="tags" id="userTags">
                    </ul>
                </section>

            </li>--%>
            <li class="chat-tab-content-info  active"  id="quickchat">
                <jsp:include page="chat-tpl.jsp"/>
            </li>
        </ul>
    </div>
</div>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script src="<%=request.getContextPath()%>/resouces/js/bootstrap-treeview.js"></script>
<link type="text/css" href="<%=request.getContextPath()%>/resouces/css/bootstrap.css"/>

<style>

    .treeview {
        font-size: 20px
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
            <div class="modal_body">
                <div class="history" id="friendAndGroup">
                    <div class="search">
                        <label for="searchfriendAndGroupInput"><input type="text" name="searchHistory"
                                                                      id="searchfriendAndGroupInput"
                                                                      placeholder="请输入好友昵称"></label>
                        <button id="searchriendAndGroupBtn">查找</button>
                    </div>
                    <div id="friendAndGroupree"></div>
                </div>
            </div>
            <div class="modal_footer">
                <button class="btn cancel-btn" data-close="modal">关闭</button>
            </div>
        </div>
    </div>
</div>
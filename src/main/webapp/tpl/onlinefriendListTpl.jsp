<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script id="onlinefriendListTpl" type="text/html">
    <%--<li ><span  style="display: none" id="{{from}}_tip"></span></li>--%>
    <li class="{{onlineStatus}} have-message" fromType="{{fromType}}" id="{{from}}" openId="{{openId}}"
        data-id="{{fromName}}">
        <div class="avatar"><img src="{{icon}}" alt=""><span class="" id="m{{from}}"></span></div>
        <div style="margin: 0 0 0 60px;">
            <div class="name">{{&fromName}}</div>
            <div class="time">{{&loginUsername}}</div>
            <div class="time">{{time}}</div>
            <div class="last-message"></div>
            <!--   <span class="new-message" id="new-message"></span>
            -->
        </div>
    </li>
</script>
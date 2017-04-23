<#assign content>
<script src="/js/createJoin.js"></script>
<div id="createJoin">
<form action="/create" method="post" id="createJoinForm">
<input type="submit" class="createJoin" value="Start a sesh">
</form>
<form action="/join" method="post">
<input type="submit" class="createJoin" value="Join a sesh">
</form>
</#assign>
<#include "main.ftl">
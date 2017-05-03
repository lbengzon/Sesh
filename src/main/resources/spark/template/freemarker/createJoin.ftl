<#assign content>
<script>
	let userId;
</script>
<script src="/js/createJoin.js"></script>
<script>
	userId = "${userId}";
</script>

<div id="createJoin">
<form action="/create" method="post" id="createJoinForm">
<input type="text" style="display: none;" id="createUserId" name="createUserId">
<input type="submit" class="createJoin" id="createSesh" value="Start a sesh">
</form>
<form action="/join" method="post">
<input type="text" style="display: none;" id="joinUserId" name="joinUserId">
<input type="text" style="display: none;" id="joinlat" name="joinlat">
<input type="text" style="display: none;" id="joinlon" name="joinlon">
<input type="submit" class="createJoin" id="joinSesh" value="Join a sesh">
</form>
</#assign>
<#include "main.ftl">
<#assign content>

<div class="invisible">
<#if userId??>
${userId}
</#if>
</div>

<script>
	var userId;
</script>
<script src="/js/join.js"></script>
<script>
<#if userId??>
	userId = "${userId}";	
</#if>
</script>

<div id="party-list"></div>
<form method="POST" action="/join/party">
<input type="text" id="userId" name="userId" style="display: none;">
<input type="submit" class="footerButtons" id="partySubmit" value="Join">
</form>

</#assign>
<#include "main.ftl">
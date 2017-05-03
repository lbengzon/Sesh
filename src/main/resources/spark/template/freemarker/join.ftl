<#assign content>

<div class="invisible">
<#if userId??>
${userId}
</#if>


</div>

<script>
	let userId;
	let parties;
</script>
<script src="/js/join.js"></script>
<script>
<#if userId??>
	userId = "${userId}";	
</#if>

</script>

<div id="party-list">
<ul style="list-style-type:none"></ul>
</div>

<form method="POST" action="/join/party">
<input type="text" id="partyId" name="partyId" style="display: none;">
<input type="text" id="userId" name="userId" style="display: none;">
<input type="submit" class="footerButtons" id="partySubmit" value="Join">
</form>

</#assign>
<#include "main.ftl">
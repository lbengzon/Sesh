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

<div id="party-list">
<ul style="list-style-type:none">
<li id="1" onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 1 hosted by Host 1</li>
<li id="2" onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 2 hosted by Host 2</li>
<li id="3" onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 3 hosted by Host 3</li>
<li id="1" onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 1 hosted by Host 1</li>
<li id="2" onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 2 hosted by Host 2</li>
<li id="3"onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 3 hosted by Host 3</li>
<li id="1" onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 1 hosted by Host 1</li>
<li id="2" onmouseover="hoverOn(this)" onmouseout="hoverOff(this)">Party 2 hosted by Host 2</li>
</ul>
</div>

<form method="POST" action="/join/party">
<input type="text" id="partyId" name="partyId" style="display: none;">
<input type="text" id="userId" name="userId" style="display: none;">
<input type="submit" class="footerButtons" id="partySubmit" value="Join">
</form>

</#assign>
<#include "main.ftl">
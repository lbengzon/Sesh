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
<!-- <script src="/js/createJoin.js"></script> -->
<script src="/js/join.js"></script>
<script>
<#if userId??>
	userId = "${userId}";	
</#if>

</script>

<div id="party-list">
<ul></ul>
<h1 id="loadingParties"> Loading Nearby Seshes <span>.</span><span>.</span><span>.</span> </h1>
<h2 id="noActiveSesh" style="display: none;"> There don't seem to be any active seshes nearby. Why don't you try creating your own! </h2>
</div>


<input type="button" class="footerButtons" id="partySubmit" value="Join">

</#assign>
<#include "main.ftl">
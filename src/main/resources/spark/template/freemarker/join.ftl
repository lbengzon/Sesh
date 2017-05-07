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
<ul style="list-style-type:none"></ul>
<h2 id="loadingParties"> Loading Nearby Seshes ... </h2>
</div>


<input type="button" class="footerButtons" id="partySubmit" value="Join">

</#assign>
<#include "main.ftl">
<#assign content>
<script>
	var userId;
</script>
<script src="/js/partySettings.js"></script>
<script>
	userId = "${userId}";
</script>

<div id="form-wrapper">
<div id="create_sesh">
<form id="formId" method="POST" action="/create/party">

<label>Sesh Name:</label><input type="text" id="sesh_name" name="sesh_name" autocomplete="off" required>
<label>Host Name:</label><input type="text" id="host_name" name="host_name" autocomplete="off">
<div id="privacy_settings">
<label class="radioLabel">Private</label><input type="radio" name="privacy_setting" value="private" id="private_sesh" required>
<label class="radioLabel">Public</label><input type="radio" name="privacy_setting" value="public" id="public_sesh" required>
</div>

<input type="text" id="lat" name="lat" style="display: none;">
<input type="text" id="lon" name="lon" style="display: none;">

<input type="text" id="userId" name="userId" style="display: none;">
<input type="submit" value="Loading..." id="formSubmit" formmethod="post" formaction="/create/party" >
</form>
</div>
</div>


</#assign>
<#include "main.ftl">
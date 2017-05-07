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
<form id="formId">

<label>Sesh Name:</label><input type="text" id="sesh_name" name="sesh_name" autocomplete="off" required>
<label>Host Name:</label><input type="text" id="host_name" name="host_name" autocomplete="off">
<div id="privacy_settings">
<div id="private">
<label class="radioLabel">Private</label><input type="radio" name="privacy_setting" value="PRIVATE" id="private_sesh" checked="checked" required>
</div>
<div id="public">
<label class="radioLabel">Public</label><input type="radio" name="privacy_setting" value="PUBLIC" id="public_sesh" required>
</div>
<div id ="passwordDiv">
<label>Access Code:</label>
<input type="text" id="accessCode" autocomplete="off" required>
</div>
</div>
<ul style="list-style-type:none" id="deviceList">
<label> Select an Available Device: </label>
<p id="loadingDevices"> Loading... </p>
</ul>

<input type="text" id="lat" name="lat" style="display: none;">
<input type="text" id="lon" name="lon" style="display: none;">
<input type="text" id="deviceId" name="deviceId" style="display: none;">

<input type="text" id="userId" name="userId" style="display: none;">
<input type="button" value="Loading..." id="formSubmit">
</form>
</div>

<div>
<p> If you don't see your device, make sure spotify is open on a device and press refresh </p>
<input type="button" value="Refresh" id="refresh">
</div>
</div>


</#assign>
<#include "main.ftl">
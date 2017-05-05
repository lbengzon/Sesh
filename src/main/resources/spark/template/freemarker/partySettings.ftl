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
<!-- <div id="privacy_settings">
<label class="radioLabel">Private</label><input type="radio" name="privacy_setting" value="private" id="private_sesh" required>
<label class="radioLabel">Public</label><input type="radio" name="privacy_setting" value="public" id="public_sesh" required>
</div> -->
<ul style="list-style-type:none" id="deviceList">
<label> Select an Available Device </label>
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

</div>
</div>


</#assign>
<#include "main.ftl">
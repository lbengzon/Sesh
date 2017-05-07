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
<p class="errorRequired" id="seshNameError" style="display: none;">*Please enter the name of your sesh here. This is a required field.</p>
<label>Host Name:</label><input type="text" id="host_name" name="host_name" autocomplete="off">
<p class="errorRequired" id="hostNameError" style="display: none;">*Please enter the name of the host here. This is a required field.</p>
<p class="errorRequired" id="privacyError" style="display: none;">*Please select whether you would like to start a private sesh or public sesh. This is a required field.</p>
<div id="helpPrivacy">
<div class="help-tip">
	<p>You can determine who is allowed to join your sesh. Selecting a private sesh will prompt you to create an access token for your guests to join. Anyone can join a public sesh.</p>
</div>
<label>Privacy Setting:</label>
</div>
<div id="privacy_settings">
<div id="private">
<label class="radioLabel">Private</label><input type="radio" name="privacy_setting" value="PRIVATE" id="private_sesh" checked="checked" required>
</div>
<div id="public">
<label class="radioLabel">Public</label><input type="radio" name="privacy_setting" value="PUBLIC" id="public_sesh" required>
</div>
</div>
<div id ="passwordDiv">
<label>Access Code:</label>
<input type="text" id="accessCode" autocomplete="off" required>
</div>
<p class="errorRequired" id="accessCodeError" style="display: none;">*Please supply a password for your private sesh. This is a required field.</p>

<ul id="deviceList">
<label> Select an Available Device: </label>
<div id="deviceRefresh">
<p> If you don't see the device you're looking for, ensure Spotify is open and hit refresh </p>
<input type="button" value="Refresh" id="refresh">
</div>
<p id="loadingDevices"> Loading... </p>
<p class="errorRequired" id="deviceError" style="display: none;">*Please select a device to play from. This is a required field.</p>
</ul>

<input type="text" id="lat" name="lat" style="display: none;">
<input type="text" id="lon" name="lon" style="display: none;">
<input type="text" id="deviceId" name="deviceId" style="display: none;">

<input type="text" id="userId" name="userId" style="display: none;">
<input type="button" value="Loading..." id="formSubmit">
</form>
</div>

</div>


</#assign>
<#include "main.ftl">
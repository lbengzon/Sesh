<#assign content>
<script src="/js/geolocation-create.js"></script>
<div id="form-wrapper">
<div id="create_sesh">
<form method="POST" action="/create/party">
<label>Sesh Name:</label><input type="text" id="sesh_name" name="sesh_name" autocomplete="off" required>
<label>Host Name:</label><input type="text" id="host_name" name="host_name" autocomplete="off">
<div id="privacy_settings">
<label class="radioLabel">Private</label><input type="radio" name="privacy_setting" value="private" id="private_sesh" required>
<label class="radioLabel">Public</label><input type="radio" name="privacy_setting" value="public" id="public_sesh" required>
</div>

<input type="submit" value="Submit" id="formSubmit">
</form>
</div>
</div>


</#assign>
<#include "main.ftl">
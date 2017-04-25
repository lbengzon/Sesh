<#assign content>
<script>
	var userId;
	var partyId;
</script>
<script src="/js/createParty.js"></script>
<script>
	userId = "${userId}";
	partyId = "${partyId}";
	console.log("user id: " + userId);
	console.log("party id: " + partyId);
</script>
<div id="embedHider">
    <iframe src="" width="300" height="380" frameborder="0" allowtransparency="true"></iframe>
</div>
<div id="titles">
<h2 class="titles">Playlist | ${partyName} </h2>
<h2 class="titles">Requests | ${partyName} </h2>
</div>
<div class="list-wrapper">
<div class="tabContentPlaylist" id="playlist-list">
<ul style="list-style-type:none" class="sortable"></ul>
</div>

<div class="tabContentPlaylist" id="request-list">
<ul style="list-style-type:none" class="sortable"></ul>
</div>

<div class="tabContentSearch">
	<input type="text" class="search">
	<div class="results">
	<ul style="list-style: none;" class="searchResults"></ul>
	</div>
</div>

<div class="tabContentOptions"></div>

</div>

<div id="footer">
<button class="footerButtons" id="playlist-dj">Playlist</button>
<button class="footerButtons" id="search-dj">Search</button>
<button class="footerButtons" id="options-dj">Options</button>
</div>

</#assign>
<#include "main.ftl">
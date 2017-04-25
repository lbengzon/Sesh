<#assign content>
<script>
	var userId;
	var partyId;
</script>
<script src="/js/joinParty.js"></script>
<script>
	userId = "${userId}";
	partyId = "${partyId}";
</script>

<div id="titles">
<h2 id="playlist-title">Playlist</h2>
<h2 id="request-title">Requests</h2>
</div>
<div class="list-wrapper">
<div class="tabContentRequestGuest" id="request-list">
<ul style="list-style-type:none"></ul>
</div>

<div class="tabContentPlaylistGuest" id="playlist-list">
<ul style="list-style-type:none"></ul>
</div>

<div class="tabContentSearchGuest">
	<input type="text" class="search" name="search">
	<div class="results">
	<ul style="list-style: none;" class="searchResults"></ul>
	</div>
</div>

<div class="tabContentOptionsGuest"></div>
</div>

<div id="footer">
<button class="footerButtons" id="request-guest">Requests</button>
<button class="footerButtons" id="playlist-guest">Playlist</button>
<button class="footerButtons" id="search-guest">Search</button>
<button class="footerButtons" id="options-guest">Options</button>
</div>
</#assign>
<#include "main.ftl">
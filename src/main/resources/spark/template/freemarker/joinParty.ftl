<#assign content>
<script>
	let userId;
	let partyId;
	let currSongId;
	let showPlayed = false;
	let isPaused = true;
</script>
<script src="/js/joinParty.js"></script>
<script>
	userId = "${userId}";
	partyId = "${partyId}";
</script>
<div id="musicPlayer">
	<h4 id="songTitle"></h4>
	<h4 id="artistName"></h4>
	<h4 id="albumTitle"></h4>
	<img id="songArt" height="168" width="168">
	<button id="playButton" class="playerButton" type="button">Play</button>
	<button id="pauseButton" class="playerButton" type="button" style="display: none">Pause</button>
	<progress id="progressbar" style="width:60%"></progress>
</div>
<div id="titles">
<h2 id="playlist-title">Playlist</h2>
<h2 id="request-title">Requests</h2>
</div>
<div class="list-wrapper">
<div class="tabContentRequestGuest" id="request-list">
<ul style="list-style-type:none"></ul>
</div>

<div class="tabContentPlaylistGuest" id="playlist-list">
<ul style="list-style-type:none" id="ulPlaylist"></ul>
</div>

<div class="tabContentSearchGuest">
	<input type="text" class="search" name="search">
	<div class="results">
	<ul style="list-style: none;" class="searchResults"></ul>
	</div>
</div>

<div class="tabContentFavoritesGuest">
	<input type="text" class="favoritesSearchGuest" name="favoritesSearchGuest">
	<div class="favorites">
	<ul style="list-style: none;" class="favoritesList"></ul>
	</div>
</div>

<div class="tabContentOptionsGuest"></div>
</div>

<div id="footer">
<button class="footerButtons" id="request-guest">Requests</button>
<button class="footerButtons" id="playlist-guest">Playlist</button>
<button class="footerButtons" id="search-guest">Search</button>
<button class="footerButtons" id="favorites-guest">Favorites</button>
<button class="footerButtons" id="options-guest">Options</button>
</div>
</#assign>
<#include "main.ftl">
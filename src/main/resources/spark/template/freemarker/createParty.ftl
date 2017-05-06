<#assign content>
<script>
	let userId;
	let partyId;
	let currSongId;
	let showPlayed = false;
	let isPaused = true;
	let timePassed;
	const isHost = true;
</script>
<script src="/js/createParty.js"></script>
<script>
	userId = "${userId}";
	partyId = "${partyId}";
	console.log("user id: " + userId);
	console.log("party id: " + partyId);
</script>
<!-- <div id="embedHider">
    <iframe id="playback" width="300" height="380" frameborder="0" allowtransparency="true"></iframe>
</div> -->

<div id="musicPlayer">
	<h4 id="songTitle"></h4>
	<h4 id="artistName"></h4>
	<h4 id="albumTitle"></h4>
	<img id="songArt" height="168" width="168">
	<button id="prevButton" class="playerButton" type="button">Previous</button>
	<button id="playButton" class="playerButton" type="button">Play</button>
	<button id="pauseButton" class="playerButton" type="button" style="display: none">Pause</button>
	<button id="nextButton" class="playerButton" type="button">Next</button>
	<progress id="progressbar" style="width:60%"></progress>
</div>
<div id="titles">
<h2 class="titles">Playlist | ${partyName} </h2>
<h2 class="titles">Requests | ${partyName} </h2>
</div>
<div class="list-wrapper">
<div class="tabContentPlaylist" id="playlist-list">
<ul style="list-style-type:none" class="sortable" id="ulPlaylist">
</ul>
</div>

<div class="tabContentPlaylist" id="request-list">
<div class="switchDiv">
	<label id="switchLabel">Show My Requests</label>
	<label class="switch">
		<input type="checkbox">
		<div class="slider round"></div>
	</label>
</div>
<ul style="list-style-type:none" class="sortable" id="ulRequest">
</ul>
</div>

<div class="tabContentSearch">
	<input type="text" class="search">
	<div class="results">
	<ul style="list-style: none;" class="searchResults"></ul>
	</div>
</div>

<div class="tabContentFavorites">
	<input type="text" class="favoritesSearch" name="favoritesSearch">
	<div class="favorites">
	<ul style="list-style: none;" class="favoritesList"></ul>
	</div>
</div>

<div class="tabContentOptions">
	<input type="button" id="endButton" value="End Party">
</div>

</div>

<div id="footer">
<button class="footerButtons" id="playlist-dj">Playlist</button>
<button class="footerButtons" id="search-dj">Search</button>
<button class="footerButtons" id="favorites-dj">Favorites</button>
<button class="footerButtons" id="options-dj">Options</button>
</div>

</#assign>
<#include "main.ftl">
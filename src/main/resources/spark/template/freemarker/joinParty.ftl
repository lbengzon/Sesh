<#assign content>
<script>
	let userId;
	let partyId;
	let currSongId;
	let showPlayed = false;
	let isPaused = true;
	const isHost = false;
</script>
<script src="/js/joinParty.js"></script>
<script>
	userId = "${userId}";
	partyId = "${partyId}";
</script>
<!-- <div id="musicPlayer">
	<div id="musicTitles">
		<h4 id="songTitle"></h4>
		<h4 id="artistName"></h4>
		<h4 id="albumTitle"></h4>
	</div>
	<img id="songArt" height="168" width="168">
	<button id="playButton" class="playerButton" type="button">Play</button>
	<button id="pauseButton" class="playerButton" type="button" style="display: none">Pause</button>
	<progress id="progressbar" style="width:60%"></progress>
</div> -->
<div id="musicPlayer">
	<div id="songDataDiv">
		<div id="albumArtDiv"><img id="songArt" height="168" width="168"></div>
		<div id="songTitles">
			<h4 id="songTitle"></h4>
			<h4 id="artistName"></h4>
			<h4 id="albumTitle"></h4>
		</div>
	</div>

	<div id="progressBarDiv">
		<p class="elapsed">0:00</p>
		<progress id="progressbar" style="width:60%"></progress>
		<p class="duration">0:00</p>
	</div>

	<div id="controlsDiv">
		<button id="playButton" class="playerButton" type="button" class="guestControl">
			<i class="material-icons">play_circle_outline</i>
		</button>
		<button id="pauseButton" class="playerButton" type="button" style="display: none" class="guestControl">
			<i class="material-icons">pause_circle_outline</i>
		</button>
	</div>
</div>

<div id="titles">
<h2 id="playlist-title">Playlist | ${partyName}</h2>
<h2 id="request-title">Requests | ${partyName}</h2>
</div>
<div class="list-wrapper">
<div class="tabContentRequestGuest" id="request-list">
<div class="switchDiv">
	<label id="switchLabel">My Requests</label>
	<label class="switch" id="guestSwitch">
		<input type="checkbox">
		<div class="slider round"></div>
	</label>
</div>
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
	<div class="favHeaderButtonsDiv">
		<button class="favHeaderButtons" id="seshFavs">Sesh</button>
		<button class="favHeaderButtons" id="spotFavs">Spotify</button>
	</div>
	<input type="text" class="favoritesSearchGuest" name="favoritesSearchGuest">
	<div class="favorites">
	<ul style="list-style: none;" class="favoritesList"></ul>
	</div>
</div>

<div class="tabContentOptionsGuest">

	<input type="button" id="leaveButton" value="Leave Sesh">

</div>
</div>

<div id="footer">
<button class="footerButtons" id="request-guest">Requests</button>
<button class="footerButtons" id="playlist-guest">Playlist</button>
<button class="footerButtons" id="search">Search</button>
<button class="footerButtons" id="favorites">Favorites</button>
<button class="footerButtons" id="options-guest">Options</button>
</div>
</#assign>
<#include "main.ftl">
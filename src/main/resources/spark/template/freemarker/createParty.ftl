<#assign content>
<script>
	let userId;
	let partyId;
	let currSongId;
	let showPlayed = false;
	let isPaused = true;
	let timePassed;
	const isHost = true;
	let justRefreshed = true;
</script>
<script src="/js/createParty.js"></script>
<script>
	userId = "${userId}";
	partyId = "${partyId}";
	console.log("user id: " + userId);
	console.log("party id: " + partyId);
</script>

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
		<button id="prevButton" class="playerButton" type="button">
			<i class="material-icons" onmouseover="enlarge(this)" onmouseout="minimize(this)">skip_previous</i>
		</button>
		<button id="playButton" class="playerButton" type="button">
			<i class="material-icons" onmouseover="enlarge(this)" onmouseout="minimize(this)">play_circle_outline</i>
		</button>
		<button id="pauseButton" class="playerButton" type="button" style="display: none">
			<i class="material-icons" onmouseover="enlarge(this)" onmouseout="minimize(this)">pause_circle_outline</i>
		</button>
		<button id="nextButton" class="playerButton" type="button">
			<i class="material-icons" onmouseover="enlarge(this)" onmouseout="minimize(this)">skip_next</i>
		</button>

	</div>
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
	<label id="switchLabel">My Requests</label>
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
	<div class="favHeaderButtonsDiv">
		<button class="favHeaderButtons" id="seshFavs">Sesh</button>
		<button class="favHeaderButtons" id="spotFavs">Spotify</button>
	</div>
	<input type="text" class="favoritesSearch" name="favoritesSearch">
	<div class="favorites">
	<ul style="list-style: none;" class="favoritesList"></ul>
	<ul style="list-style: none;" class="spotifyFavoritesList"></ul>
	<h3 id="noTopTracks" style="display: none;"> It looks like you don't have any top tracks from Spotify to display </h3>
	</div>
</div>

<div class="tabContentOptions">
	<input type="button" id="endButton" value="End Sesh">
</div>

</div>

<div id="footer">
<button class="footerButtons" id="playlist-dj">Playlist</button>
<button class="footerButtons" id="search">Search</button>
<button class="footerButtons" id="favorites">Favorites</button>
<button class="footerButtons" id="options-dj">Options</button>
</div>

</#assign>
<#include "main.ftl">
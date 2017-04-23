<#assign content>
<script src="/js/joinParty.js"></script>
<div id="titles">
<h2 id="playlist-title">Playlist</h2>
<h2 id="request-title">Requests</h2>
</div>
<div class="list-wrapper">
<div class="tabContentRequestGuest">
<ul style="list-style-type:none">
<li>
	<div id="songdiv">Song 1 
		<div id="vote">
			<button type="button" class="voteButton"> <i class="material-icons">thumb_up</i> </button>
			<button type="button" class="voteButton"> <i class="material-icons">thumb_down</i> </button>
		</div>
	</div>
</li>

<li>
	<div id="songdiv">Song 1 
		<div id="vote">
			<button type="button" class="voteButton"> <i class="material-icons">thumb_up</i> </button>
			<button type="button" class="voteButton"> <i class="material-icons">thumb_down</i> </button>
		</div>
	</div>
</li>


</ul>
</div>

<div class="tabContentPlaylistGuest">
<ul style="list-style-type:none">
<li>Song 1</li>
<li>Song 2</li>
<li>Song 3</li>
<li>Song 4</li>
<li>Song 1</li>
<li>Song 2</li>
<li>Song 3</li>
<li>Song 4</li>
<li>Song 1</li>
<li>Song 2</li>
<li>Song 3</li>
<li>Song 4</li>
<li>Song 1</li>
<li>Song 2</li>
<li>Song 3</li>
<li>Song 4</li>
</ul>
</div>

<div class="tabContentSearchGuest">
	<input type="text" class="search">
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
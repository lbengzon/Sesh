<#assign content>
<script src="/js/createParty.js"></script>
<div id="titles">
<h2 class="titles">Playlist | ${partyName} </h2>
<h2 class="titles">Requests | ${partyName} </h2>
</div>
<div class="list-wrapper">
<div class="tabContentPlaylist">
<ul style="list-style-type:none" class="sortable">
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
<li>Song 1</li>
<li>Song 2</li>
<li>Song 3</li>
<li>Song 4</li>
</ul>
</div>

<div class="tabContentPlaylist">
<ul style="list-style-type:none" class="sortable">
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
<li>Song 1</li>
<li>Song 2</li>
<li>Song 3</li>
<li>Song 4</li>
</ul>
</div>

<div class="tabContentSearch">
	<input type="text" class="search">
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
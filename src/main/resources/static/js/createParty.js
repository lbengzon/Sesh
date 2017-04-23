function hoverOn(x) {
	x.className = 'selected';
}

function hoverOff(x) {
	x.classList.remove('selected');
}

function showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper) {
	$listview.addClass("active");
	$search.removeClass("active");
	$options.removeClass("active");
	$tabContentSearch.hide();
	$tabContentPlaylist.show();
	$tabContentOptions.hide();
	$titles.show();
	$listWrapper.height("50%");
}

function showSearch($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper) {
	$search.addClass("active");
	$listview.removeClass("active");
	$options.removeClass("active");
	$tabContentSearch.show();
	$tabContentPlaylist.hide();
	$tabContentOptions.hide();
	$titles.hide();
	$listWrapper.height("56%");
}

function showOptions($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper) {
	$options.addClass("active");
	$listview.removeClass("active");
	$search.removeClass("active");
	$tabContentSearch.hide();
	$tabContentPlaylist.hide();
	$tabContentOptions.show();
	$titles.hide();
	$listWrapper.height("56%");
}

$(document).ready(() => {
	//dj tab content
	const $tabContentPlaylist = $(".tabContentPlaylist");
	const $tabContentSearch = $(".tabContentSearch");
	const $tabContentOptions = $(".tabContentOptions");
	const $titles = $(".titles");
	const $listWrapper = $('.list-wrapper');

	//dj tabs
	const $listview = $("#playlist-dj");
	const $search = $("#search-dj");
	const $options = $("#options-dj");

	const $sortable = $(".sortable");

	$sortable.sortable({
		connectWith: '.sortable'
	});

	const $userInput = $(".search");
	const $results = $(".searchResults");
	const $playlist = $("#tabContentPlaylist ul");

	$userInput.keyup(function() {
		const postParameters = {userInput: $userInput.val()};
		$.post("/search", postParameters, responseJSON => {
			const responseObject = JSON.parse(responseJSON);
			const suggestions = responseObject.results;
			const songIds = responseObject.songIds;

			$results.empty();

			for (var i = 0; i < suggestions.length; i++) {
				$results.append("<li " + "id=\"" + songIds[i] + "\"" + "onmouseover=\"hoverOn(this)\"" + "onmouseout=\"hoverOff(this)\">" + suggestions[i] + "</li>");
			}
		});
	});

	$results.on("click", event => {
		$listItems = $("li");
		$selected = $listItems.filter('.selected');
		$playlist.append("<li>" + $selected.text() + "</li>");
		showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper);
		//send post request to add song to playlist in db
	});

	//default initial views
	$listview.addClass("active");
	$tabContentSearch.hide();
	$tabContentOptions.hide();

	$search.click(function() {
		showSearch($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper);
	});

	$listview.click(function() {
		showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper);
	});

	$options.click(function() {
		showOptions($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper);
	});
});
function hoverOn(x) {
	x.className = 'hover';
}

function hoverOff(x) {
	x.classList.remove('hover');
}

function showPlaylist($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest,$tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.addClass("active");
	$requestsGuest.removeClass("active");
	$searchGuest.removeClass("active");
	$optionsGuest.removeClass("active");
	$tabContentRequestGuest.hide();
	$tabContentOptionsGuest.hide();
	$tabContentFavoritesGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentPlaylistGuest.show();
	$requestTitle.hide();
	$playlistTitle.show();
	$listWrapper.height("50%");
}

function showRequests($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.removeClass("active");
	$requestsGuest.addClass("active");
	$searchGuest.removeClass("active");
	$optionsGuest.removeClass("active");
	$tabContentRequestGuest.show();
	$tabContentOptionsGuest.hide();
	$tabContentFavoritesGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentPlaylistGuest.hide();
	$playlistTitle.hide();
	$requestTitle.show();
	$listWrapper.height("50%");
}

function showSearch($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.removeClass("active");
	$requestsGuest.removeClass("active");
	$searchGuest.addClass("active");
	$optionsGuest.removeClass("active");
	$tabContentRequestGuest.hide();
	$tabContentOptionsGuest.hide();
	$tabContentFavoritesGuest.hide();
	$tabContentSearchGuest.show();
	$tabContentPlaylistGuest.hide();
	$playlistTitle.hide();
	$requestTitle.hide();
	$listWrapper.height("56%");
}

function showOptions($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.removeClass("active");
	$requestsGuest.removeClass("active");
	$searchGuest.removeClass("active");
	$optionsGuest.addClass("active");
	$tabContentRequestGuest.hide();
	$tabContentOptionsGuest.show();
	$tabContentFavoritesGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentPlaylistGuest.hide();
	$playlistTitle.hide();
	$requestTitle.hide();
	$listWrapper.height("56%");
}

function showFavorites($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.removeClass("active");
	$requestsGuest.removeClass("active");
	$searchGuest.removeClass("active");
	$optionsGuest.addClass("active");
	$tabContentRequestGuest.hide();
	$tabContentOptionsGuest.hide();
	$tabContentFavoritesGuest.show();
	$tabContentSearchGuest.hide();
	$tabContentPlaylistGuest.hide();
	$playlistTitle.hide();
	$requestTitle.hide();
	$listWrapper.height("56%");
}


$(document).ready(() => {
	setupWebsockets();
	//make post request to get party lists


	const $userInput = $(".search");
	const $results = $(".searchResults");
	const $requests = $(".tabContentRequestGuest ul");
	
	/*setting invisible form */
	$("#userId").val(userId);
	$("#partyId").val(partyId);

	$userInput.keyup(function() {
		console.log("userId" + userId);
		const postParameters = {userInput: $userInput.val()};
		$.post("/search", postParameters, responseJSON => {
			const responseObject = JSON.parse(responseJSON);
			const suggestions = responseObject.results;
			const songIds = responseObject.songIds;

			$results.empty();
			for (var i = 0; i < suggestions.length; i++) {
				$results.append("<li " + "id=\"" + songIds[i] + "\"" + 
					"onmouseover=\"hoverOn(this)\"" + "onmouseout=\"hoverOff(this)\">" 
					+ suggestions[i] + "</li>");
			}
		});
	});


	/* HANDLES ADDING A REQUEST FROM SEARCH */
	$results.on("click", event => {
		$listItems = $('li');
		$selected = $listItems.filter('.hover');
		console.log($selected);
		addRequest(partyId, userId, $selected.attr("id"));
		showRequests($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});


	//guest tab content
	const $tabContentRequestGuest = $(".tabContentRequestGuest");
	const $tabContentPlaylistGuest = $(".tabContentPlaylistGuest");
	const $tabContentSearchGuest = $(".tabContentSearchGuest");
	const $tabContentOptionsGuest = $(".tabContentOptionsGuest");
	const $tabContentFavoritesGuest = $(".tabContentFavoritesGuest");
	const $playlistTitle = $("#playlist-title");
	const $requestTitle = $("#request-title");
	const $listWrapper = $('.list-wrapper');
	// const $leavebutton = $("#leaveButton");


	//guest tabs
	const $requestsGuest = $("#request-guest");
	const $playlistGuest = $("#playlist-guest");
	const $searchGuest = $("#search-guest");
	const $optionsGuest = $("#options-guest");
	const $favoritesGuest = $("#favorites-guest");

	$requestsGuest.addClass("active");
	$tabContentPlaylistGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentOptionsGuest.hide();
	$tabContentFavoritesGuest.hide();
	$playlistTitle.hide();


	$playlistGuest.click(function() {
		showPlaylist($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	$requestsGuest.click(function() {
		showRequests($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	$searchGuest.click(function() {
		showSearch($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	$optionsGuest.click(function() {
		showOptions($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	$favoritesGuest.click(function(){
    	showFavorites($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentFavoritesGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
    });


});
function hoverOn(x) {
	x.className = 'selected';
}

function hoverOff(x) {
	x.classList.remove('selected');
}

function showPlaylist($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.addClass("active");
	$requestsGuest.removeClass("active");
	$searchGuest.removeClass("active");
	$optionsGuest.removeClass("active");
	$tabContentRequestGuest.hide();
	$tabContentOptionsGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentPlaylistGuest.show();
	$requestTitle.hide();
	$playlistTitle.show();
	$listWrapper.height("50%");
}

function showRequests($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.removeClass("active");
	$requestsGuest.addClass("active");
	$searchGuest.removeClass("active");
	$optionsGuest.removeClass("active");
	$tabContentRequestGuest.show();
	$tabContentOptionsGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentPlaylistGuest.hide();
	$playlistTitle.hide();
	$requestTitle.show();
	$listWrapper.height("50%");
}

function showSearch($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.removeClass("active");
	$requestsGuest.removeClass("active");
	$searchGuest.addClass("active");
	$optionsGuest.removeClass("active");
	$tabContentRequestGuest.hide();
	$tabContentOptionsGuest.hide();
	$tabContentSearchGuest.show();
	$tabContentPlaylistGuest.hide();
	$playlistTitle.hide();
	$requestTitle.hide();
	$listWrapper.height("56%");
}

function showOptions($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper) {
	$playlistGuest.removeClass("active");
	$requestsGuest.removeClass("active");
	$searchGuest.removeClass("active");
	$optionsGuest.addClass("active");
	$tabContentRequestGuest.hide();
	$tabContentOptionsGuest.show();
	$tabContentSearchGuest.hide();
	$tabContentPlaylistGuest.hide();
	$playlistTitle.hide();
	$requestTitle.hide();
	$listWrapper.height("56%");
}


$(document).ready(() => {

	const $userInput = $(".search");
	const $results = $(".searchResults");
	const $requests = $(".tabContentRequestGuest ul");

	$userInput.keyup(function() {
		const postParameters = {userInput: $userInput.val()};
		$.post("/search", postParameters, responseJSON => {
			const responseObject = JSON.parse(responseJSON);
			const suggestions = responseObject.results;

			$results.empty();

			for (var i = 0; i < suggestions.length; i++) {
				$results.append("<li onmouseover=\"hoverOn(this)\"" + 
					"onmouseout=\"hoverOff(this)\">" + suggestions[i] + "</li>");
			}
		});
	});

	$results.on("click", event => {
		$listItems = $('li');
		$selected = $listItems.filter('.selected');
		//append to request list
		$requests.append("<li><div id=\"songdiv\">" + $selected.text() + "<div id=\"vote\"> <button type=\"button\" class=\"voteButton\"> <i class=\"material-icons\">thumb_up</i></button> <button type=\"button\" class=\"voteButton\"> <i class=\"material-icons\">thumb_down</i> </button> </div> </div> </li>")
		//send post request to create request object and add to db
		showRequests($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	//guest tab content
	const $tabContentRequestGuest = $(".tabContentRequestGuest");
	const $tabContentPlaylistGuest = $(".tabContentPlaylistGuest");
	const $tabContentSearchGuest = $(".tabContentSearchGuest");
	const $tabContentOptionsGuest = $(".tabContentOptionsGuest");
	const $playlistTitle = $("#playlist-title");
	const $requestTitle = $("#request-title");
	const $listWrapper = $('.list-wrapper');


	//guest tabs
	const $requestsGuest = $("#request-guest");
	const $playlistGuest = $("#playlist-guest");
	const $searchGuest = $("#search-guest");
	const $optionsGuest = $("#options-guest");

	$requestsGuest.addClass("active");
	$tabContentPlaylistGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentOptionsGuest.hide();
	$playlistTitle.hide();


	$playlistGuest.click(function() {
		showPlaylist($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	$requestsGuest.click(function() {
		showRequests($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	$searchGuest.click(function() {
		showSearch($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});

	$optionsGuest.click(function() {
		showOptions($playlistGuest, $requestsGuest, $searchGuest, $optionsGuest, $tabContentRequestGuest, $tabContentOptionsGuest, $tabContentSearchGuest, $tabContentPlaylistGuest, $requestTitle, $playlistTitle, $listWrapper);
	});


});
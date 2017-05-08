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
	$("#favorites").removeClass("active");
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
	$("#favorites").removeClass("active");
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
	$("#favorites").removeClass("active");
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
	$("#favorites").removeClass("active");
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
	$optionsGuest.removeClass("active");
	$("#favorites").addClass("active");
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
	const $userInput = $(".search");
	const $results = $(".searchResults");
	const $requests = $(".tabContentRequestGuest ul");
	const $userInputFavs = $(".favoritesSearchGuest");
	

    $userInput.keyup(function() {
        const postParameters = {userInput: $userInput.val()};
        $.post("/search", postParameters, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            const suggestions = responseObject.results;

            $results.empty();

            for (let i = 0; i < suggestions.length; i++) {
                $results.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + suggestions[i].spotifyId + "\" >"
                    + "<div class=\"fav\" >"
                        + "<button class=\"favButton\" id=\"" + suggestions[i].spotifyId + "\" type=\"button\"> " 
                          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
                        + "</button>"
                      //end of fav div
                      + "</div>"
                    + "<div id=\"songtitle\">" + suggestions[i].title 
                    //end of song title div
                    + "</div>"
                    + "<div id=\"songartist\">" + suggestions[i].artist
                    + "</div>"
                    + "</li>"); 
            }
            
            favorite();
            highlightSearchFavorites(favIds);
        });
    });

    $userInputFavs.keyup(function() {
        $(".favoritesList").find("li").each(function(index, value) {
            let text = $(this)[0].innerText.replace("grade", "");
            text = text.toLowerCase();
            if (!text.includes($userInputFavs.val())) {
                $(this).hide();
            } else {
                $(this).show();
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

    $(".switch input").click(function() {
        console.log("USER REQUESTED " + userRequests.length + "SONGS");
        if ($(".switch input").is(":checked")) {
            if (userRequests.length > 0) {
                $("#request-list ul").find("li").hide();
                for (let i in userRequests) {
                    let id = userRequests[i].requestId;
                    $("#request-list ul").find("#" + id).show();
                }
                /* show only user requests */
            } else {
                /* show nothing */
                $("#request-list ul").find("li").hide();
            }

        } else {
            /* show all songs in request list */
            $("#request-list ul").find("li").show();
        }
    });

    $("#seshFavs").addClass("favSelected");

    $("#seshFavs").click(function() {
        $("#seshFavs").addClass("favSelected");
        $("#spotFavs").removeClass("favSelected");
        $(".favoritesSearch").show();
        $(".favoritesList").show();
    });

    $("#spotFavs").click(function() {
        $("#spotFavs").addClass("favSelected");
        $("#seshFavs").removeClass("favSelected");
        $(".favoritesSearch").hide();
        $(".favoritesList").hide();
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

	//guest tabs
	const $requestsGuest = $("#request-guest");
	const $playlistGuest = $("#playlist-guest");
	const $searchGuest = $("#search");
	const $optionsGuest = $("#options-guest");
	const $favoritesGuest = $("#favorites");

	//leave button
	const $leaveButton = $("#leaveButton");


	$requestsGuest.addClass("active");
	$tabContentPlaylistGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentOptionsGuest.hide();
	$tabContentFavoritesGuest.hide();
	$playlistTitle.hide();

    $("#favorites").click(populateFavoritesTab);


	$leaveButton.click(function() {
		guestLeaveParty(false);
    });


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

    setupWebsockets();


});
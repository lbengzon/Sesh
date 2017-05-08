function hoverOn(x) {
    x.className = 'hover';
}

function hoverOff(x) {
    x.classList.remove('hover');
}

function enlarge(x) {
    x.style.color = "#488c7e";
}

function minimize(x) {
    x.style.color = "white";
}

//HANNAH PLEASE FILL THIS OUT. It should get the index of the song being currently played
function getIndexOfCurrentSong(){
    alert("woah");
    return 1;
}

function playHandler() {
    //FILL IN
    //TODO get the current index of the song and use that
    if(timePassed === undefined || timePassed === null){
        playPlaylist(partyId, userId);
    } 
    else{
        resumeSong(partyId, userId, timePassed);
    }
    // $("#playButton").hide();
    // $("#pauseButton").show();
}

function pauseHandler(){
    pauseSong(partyId, userId);
    // $("#playButton").show();
    // $("#pauseButton").hide();
}

function previousSongHandler() {
    prevSong(partyId, userId);
}

function nextSongHandler() {
    nextSong(partyId, userId);
}

function progressBarHandler(e){
    if(currSongId != -1 && currSongId != undefined && currSongId != null){ 
        console.log("logging e", e)
        let x = e.pageX - this.offsetLeft; // or e.offsetX (less support, though)
        let y = e.pageY - this.offsetTop;  // or e.offsetY
        let clickedValue = x * this.max / this.offsetWidth;

        console.log("clicked value", clickedValue);
        seekSong(partyId, userId, clickedValue);
        console.log(x, y);
    }
}

function showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentFavorites, $tabContentOptions, $titles, $listWrapper) {
    $('.list-wrapper').height("50%");
    $listview.addClass("active");
    $search.removeClass("active");
    $options.removeClass("active");
    $("#favorites").removeClass("active");
    $tabContentSearch.hide();
    $tabContentPlaylist.show();
    $tabContentFavorites.hide();
    $tabContentOptions.hide();
    $titles.show();
}

function showSearch($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentFavorites, $tabContentOptions, $titles, $listWrapper) {
    $search.addClass("active");
    $listview.removeClass("active");
    $options.removeClass("active");
    $("#favorites").removeClass("active");
    $tabContentSearch.show();
    $tabContentPlaylist.hide();
    $tabContentFavorites.hide();
    $tabContentOptions.hide();
    $titles.hide();
    $('.list-wrapper').height("56%");
}

function showOptions($search, $listview, $options, $tabContentSearch, $tabContentPlaylist,$tabContentFavorites,$tabContentOptions, $titles, $listWrapper) {
    $options.addClass("active");
    $listview.removeClass("active");
    $search.removeClass("active");
    $("#favorites").removeClass("active");
    $tabContentSearch.hide();
    $tabContentPlaylist.hide();
    $tabContentFavorites.hide();
    $tabContentOptions.show();
    $titles.hide();
    $('.list-wrapper').height("56%");
}

function showFavorites($search, $listview, $options, $tabContentSearch, $tabContentPlaylist,$tabContentFavorites,$tabContentOptions, $titles, $listWrapper) {
    $options.removeClass("active");
    $listview.removeClass("active");
    $search.removeClass("active");
    $("#favorites").addClass("active");
    $tabContentSearch.hide();
    $tabContentPlaylist.hide();
    $tabContentFavorites.show();
    $tabContentOptions.hide();
    $titles.hide();
    $('.list-wrapper').height("56%");
}



$(document).ready(() => {

    //dj tab content
    const $tabContentPlaylist = $(".tabContentPlaylist");
    const $tabContentSearch = $(".tabContentSearch");
    const $tabContentOptions = $(".tabContentOptions");
    const $tabContentFavorites = $(".tabContentFavorites");
    const $titles = $(".titles");
    const $listWrapper = $('.list-wrapper');

    //dj tabs
    const $listview = $("#playlist-dj");
    const $search = $("#search");
    const $options = $("#options-dj");
    const $favorites = $("#favorites");


    const $userInput = $(".search");
    const $userInputFavs = $(".favoritesSearch");
    const $resultsFavs = $(".favoritesList");
    const $results = $(".searchResults");
    const $playlist = $("#tabContentPlaylist ul");

    //music player buttons
    const $prevButton = $("#prevButton");
    const $playButton = $("#playButton");
    const $pauseButton = $("#pauseButton");
    const $nextButton = $("#nextButton");
    const $progressBar = $("#progressbar");

    //end button
    const $endButton = $("#endButton");

    let startPlaylistIndex;
    let startList;

    $(".sortable").sortable({
        dropOnEmpty: true,
        connectWith: ".sortable",
        containment: $(".list-wrapper"),
        start: function(event, ui) {
            // console.log("start playlist index: " + ui.item.index());
            // console.log("the song started in list: " + ui.item.parent().attr("id"));
            if (ui.item.parent().attr("id") === "ulPlaylist") {
                startPlaylistIndex = ui.item.index();
                startList = ui.item.parent().attr("id");
            } else {
                startList = ui.item.parent().attr("id");
            }
        },
        beforeStop: function(event, ui) {
            if ($(this).attr('id') === "ulRequest") {
                $(this).sortable("option", "selfDrop", $(ui.placeholder).parent()[0] == this);
            }
        },
        stop: function(event, ui) {
            if ($(this).attr('id') === "ulRequest") {
                var $sortable = $(this);
                if ($sortable.sortable("option", "selfDrop")) {
                    $sortable.sortable('cancel');
                } 
            }
        },

        receive: function(event, ui) {
            //dropped into request list
            if (ui.item.parent().attr("id") === "ulRequest") {
                moveFromQueueToRequest(partyId, userId, ui.item.attr("id"));
            } else {
                moveRequestToQueue(partyId, userId, ui.item.attr("id"), ui.item.index());
            }
        },
        update: function(event, ui) {
            if (ui.item.parent().attr("id") === "ulPlaylist" && startList === "ulPlaylist") {
                console.log("reordering within playlist");
                console.log("starting at: " + startPlaylistIndex);
                endIndex = ui.item.index();
                if(startPlaylistIndex < endIndex){
                    endIndex = endIndex + 1
                }
                console.log("ending at: " + endIndex);
                console.log("==========================");

                reorderPlaylistTrack(partyId, userId, ui.item.attr("id"), startPlaylistIndex, endIndex);
            }
            // if (ui.item.parent().attr("id") === "ulPlaylist") {
            //     console.log("here!!!!");
            // }
            //console.log("update parent id: " + ui.item.parent().attr("id"));

        }
    }).disableSelection();

    setupWebsockets();

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

    //search favorites
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

    //search for songs
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

    setInterval(constantlyUpdateCurrentSong, 500);

    function constantlyUpdateCurrentSong(){
        if(!constantUpdateLocked){
            updatePartyCurrentSong(partyId, userId);
        }
    }

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


    
    $("#favorites").click(populateFavoritesTab);


    $("#ulPlaylist").dblclick(function() {
        $listItems = $("li"); 
        $selected = $listItems.filter('.hover');
        console.log("index :" + $selected.index());
        playPlaylist(partyId, userId, $selected.index());
    });

    $("#ulRequest").dblclick(function() {
        $listItems = $("li"); 
        $selected = $listItems.filter('.hover');
        if ($selected.attr("id")!== undefined) {
            moveRequestToQueue(partyId, userId, $selected.attr("id"), $("#ulPlaylist li").length);
        }
    });

    $results.click(function() {
        $listItems = $("li");
        $selected = $listItems.filter('.hover');
        addToPlaylist(partyId, userId, $selected.attr("id"));
        showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper);
    });

    //default initial views
    $listview.addClass("active");
    $tabContentSearch.hide();
    $tabContentOptions.hide();
    $tabContentFavorites.hide();

    $endButton.click(function() {
        var v = confirm("Would you like to keep this Sesh as a Spotify playlist?");
        var deleteBool;
        if (v === true) {
            deleteBool = false;
        } else {
            deleteBool = true;
        }
        endParty(partyId, userId, deleteBool);
        const params = {userId: userId};
        post("/createjoin", params);
    });


    $search.click(function() {
        showSearch($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentFavorites, $tabContentOptions, $titles, $listWrapper);
        highlightSearchFavorites(favIds);
    });

    $listview.click(function() {
        showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentFavorites, $tabContentOptions, $titles, $listWrapper);
    });

    $options.click(function() {
        showOptions($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentFavorites, $tabContentOptions, $titles, $listWrapper);
    });

    $favorites.click(function(){
    	showFavorites($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentFavorites, $tabContentOptions, $titles, $listWrapper);
    });

    $prevButton.click(previousSongHandler);

    $progressBar.click(progressBarHandler);

    $playButton.click(playHandler);

    $pauseButton.click(pauseHandler);

    $nextButton.click(nextSongHandler);

});
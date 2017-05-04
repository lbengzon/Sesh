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


    const $userInput = $(".search");
    const $results = $(".searchResults");
    const $playlist = $("#tabContentPlaylist ul");

    //music player buttons
    const $prevButton = $("#prevButton");
    const $playPauseButton = $("#playButton");
    const $pauseButton = $("#pauseButton");
    const $nextButton = $("#nextButton");


    let startPlaylistIndex;
    let startList;

    $(".sortable").sortable({
        dropOnEmpty: true,
        connectWith: ".sortable",
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
                console.log("moving song from playlist to request");
                moveFromQueueToRequest(partyId, userId, ui.item.attr("id"));
            } else {
                console.log("moving song from request to playlist");
                moveRequestToQueue (partyId, userId, ui.item.attr("id"), ui.item.index());
            }
        },
        update: function(event, ui) {
            if (ui.item.parent().attr("id") === "ulPlaylist" && startList === "ulPlaylist") {
                console.log("reordering within playlist");
                console.log("starting at: " + startPlaylistIndex);
                console.log("ending at: " + ui.item.index());
                console.log("==========================");
                reorderPlaylistTrack(partyId, userId, ui.item.attr("id"), startPlaylistIndex, ui.item.index());
            }
            // if (ui.item.parent().attr("id") === "ulPlaylist") {
            //     console.log("here!!!!");
            // }
            //console.log("update parent id: " + ui.item.parent().attr("id"));

        }
    }).disableSelection();

    setupWebsockets();

    //search for songs
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

    // function getCurrentSong() {
    //     const postParameters = {partyId: partyId};
    //     $.post("/currentSong", postParameters, responseJSON => {
    //         const responseObject = JSON.parse(responseJSON);
    //         if (currSongId !== responseObject.currentSong) {
    //             //console.log("Reloading playlist: the current song changed");
    //             currSongId = responseObject.currentSong;
    //         }

    //         for (let i = 0; i < $("#ulPlaylist li").length; i++) {
    //             console.log("current playing song is at index: " + $("#ulPlaylist").find("#" + currSongId).index());
    //             if ($("#ulPlaylist").find("#" + currSongId).index() > i) {
    //                 $("#ulPlaylist li").eq(i).hide();
    //             }
    //         }
    //     });
    // }

    //setInterval(getCurrentSong, 500);

    setInterval(function(){updatePartyCurrentSong(partyId, userId);}, 1000);


    $("#ulPlaylist").dblclick(function() {
        $listItems = $("li"); 
        $selected = $listItems.filter('.selected');
        alert("you double clicked on song with id " + $selected.index());
    });

    //HANNAH PLEASE FILL THIS OUT. It should get the index of the song being currently played
    function getIndexOfCurrentSong(){
        return 1;
    }

    function playHandler() {
        //FILL IN
        console.log("ping!");
        //TODO get the current index of the song and use that
        playPlaylist(partyId, userId, 0);
    }

    function pauseHandler(){
        pauseSong(partyId, userId);
    }

    function previousSongHandler() {
        prevSong(partyId, userId);
    }

    function nextSongHandler() {
        nextSong(partyId, userId);
    }

    $results.on("click", event => {
        $listItems = $("li");
        $selected = $listItems.filter('.selected');
        addToPlaylist(partyId, userId, $selected.attr("id"));
        showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper);
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

    $prevButton.click(previousSongHandler);

    $playPauseButton.click(playHandler);

    $pauseButton.click(pauseHandler);

    $nextButton.click(nextSongHandler);

});
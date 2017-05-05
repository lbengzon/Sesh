function hoverOn(x) {
    x.className = 'hover';
}

function hoverOff(x) {
    x.classList.remove('hover');
}

//HANNAH PLEASE FILL THIS OUT. It should get the index of the song being currently played
function getIndexOfCurrentSong(){
    alert("woah");
    return 1;
}

function playHandler() {
    //FILL IN
    //TODO get the current index of the song and use that
    playPlaylist(partyId, userId);
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
    $tabContentSearch.hide();
    $tabContentPlaylist.hide();
    $tabContentFavorites.hide();
    $tabContentOptions.show();
    $titles.hide();
    $('.list-wrapper').height("56%");
}

function showFavorites($search, $listview, $options, $tabContentSearch, $tabContentPlaylist,$tabContentFavorites,$tabContentOptions, $titles, $listWrapper) {
    $options.addClass("active");
    $listview.removeClass("active");
    $search.removeClass("active");
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
    const $search = $("#search-dj");
    const $options = $("#options-dj");
    const $favorites = $("#favorites-dj");


    const $userInput = $(".search");
    const $results = $(".searchResults");
    const $playlist = $("#tabContentPlaylist ul");

    //music player buttons
    const $prevButton = $("#prevButton");
    const $playButton = $("#playButton");
    const $pauseButton = $("#pauseButton");
    const $nextButton = $("#nextButton");
    const $progressBar = $("#progressbar");


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


    setInterval(function(){updatePartyCurrentSong(partyId, userId);}, 1000);


    $("#ulPlaylist").dblclick(function() {
        $listItems = $("li"); 
        $selected = $listItems.filter('.hover');
        console.log("index :" + $selected.index());
        playPlaylist(partyId, userId, $selected.index())
        // alert("you double clicked on song with id " + $selected.index());
    });

    $("#ulRequest").dblclick(function() {
        $listItems = $("li"); 
        $selected = $listItems.filter('.hover');
        console.log($selected.attr("id"));
        if ($selected.attr("id")!== undefined) {
            moveRequestToQueue(partyId, userId, $selected.attr("id"), $("#ulPlaylist li").length);
        }
    });




    $results.click(function() {
        $listItems = $("li");
        $selected = $listItems.filter('.hover');
        addToPlaylist(partyId, userId, $selected.attr("id"));
        console.log("WHAT IS HAPPENING")
        console.log("LIST WRAPPER ===========", $listWrapper)
        showPlaylists($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentOptions, $titles, $listWrapper);
    });

    //default initial views
    $listview.addClass("active");
    $tabContentSearch.hide();
    $tabContentOptions.hide();
    $tabContentFavorites.hide();


    $search.click(function() {
        showSearch($search, $listview, $options, $tabContentSearch, $tabContentPlaylist, $tabContentFavorites, $tabContentOptions, $titles, $listWrapper);
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
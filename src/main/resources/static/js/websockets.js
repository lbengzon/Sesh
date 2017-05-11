const MESSAGE_TYPE = {
CONNECT: 0, 
SET_PARTY_ID: 1, 
ADD_REQUEST: 2, 
UPVOTE_REQUEST: 3, 
DOWNVOTE_REQUEST: 4, 
MOVE_REQUEST_TO_QUEUE: 5, 
MOVE_FROM_QUEUE_TO_REQUEST: 6, 
ADD_SONG_DIRECTLY_TO_PLAYLIST: 7, 
UPDATE_ADD_REQUEST: 8, 
UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST: 9, 
UPDATE_VOTE_REQUESTS: 10, 
UPDATE_AFTER_REQUEST_TRANSFER: 11,
UPDATE_ENTIRE_PARTY: 12,
UPDATE_REARRANGE_PLAYLIST: 13, 
REORDER_PLAYLIST_TRACK: 14,
PLAY_PLAYLIST: 15,
PAUSE_SONG: 16,
UPDATE_PLAYER: 17,
SONG_MOVED_TO_NEXT: 18,
UPDATE_NEXT_CURR_SONG_REQUEST: 19,
SEEK_SONG: 20,
RESUME_SONG: 21,
END_PARTY: 22,
UPDATE_GUESTS_END_PARTY: 23,
UPDATE_NEW_USER_JOINED: 24,
UPDATE_SEND_USER_TO_LOGIN: 25,
PREV_SONG: 26,
NEXT_SONG: 27
};

let conn;
let timeoutCheckForNewCurrSong;
let myId = -1;

let userRequests = [];
let favIds = [];
let favObjs;

let constantUpdateLocked = false;
let votedId;
let changedTrackManual = false;

function hoverOn(x) {
  x.className = 'selected';
}

function hoverOff(x) {
  x.classList.remove('selected');
}


// Setup the WebSocket connection for live updating of scores.
function setupWebsockets() {
  const $requests = $("#request-list ul");
  const $playlist = $("#playlist-list ul");
  const $player = $("#playback");
  let requestId;
  // TODO Create the WebSocket connection and assign it to `conn`
<<<<<<< HEAD
  conn = new WebSocket("ws://localhost:4567/update");
=======
  conn = new WebSocket("wss://sesh.cloud/update");
>>>>>>> ef01fde1ed2349bed9aabe20a2c8bc52e58350c6

  conn.onclose = function(){
    setTimeout(setupWebsockets, 1000)
  }

  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data); 
    //console.log("DATA TYPE: " , data.type)
    // console.log("DATA OBJECT: " , data);
    if(data.success){
      switch (data.type) {
        default:
          console.log('Unknown message type!', data.type);
          break;

        case MESSAGE_TYPE.CONNECT:
          console.log("connected");
          setPartyId(partyId, userId);
          break;

        case MESSAGE_TYPE.UPDATE_ADD_REQUEST:
          console.log("adding new request");
          appendToRequests($requests, data);
          vote();
          favorite();
          console.log("==================");
          highlightFavorites();
          console.log("HERE!!!");
          $.notify(data.payload.newRequest.song.title + " by " + data.payload.newRequest.song.artist + " has been requested!", "info");
          $requests.sortable("refresh");

          break;

        case MESSAGE_TYPE.UPDATE_VOTE_REQUESTS:
          clearAndPopulateRequests(data.payload.requestList, $requests, data.payload.votedUser);
          requestId = data.payload.requestIdVotedOn;
          let requestVotedOn = data.payload.requestList[requestId];
          let voteType = data.payload.voteType;
          if (requestId === undefined ||requestVotedOn === undefined || voteType === undefined) {
            break;
          }
          if (requestVotedOn.userRequestId === userId) {
            if (voteType === "upvote") {
              $.notify("Your request for '" + requestVotedOn.song.title + "'" + " by " + requestVotedOn.song.artist + " was " + voteType + "d", "success");
            } else {
              $.notify("Your request for '" + requestVotedOn.song.title + "'" + " by " + requestVotedOn.song.artist + " was " + voteType + "d", "error");
            }
          }
          break;

        case MESSAGE_TYPE.UPDATE_REARRANGE_PLAYLIST:
          
          console.log("update rearrange playlist");
          clearAndPopulatePlaylist(data.payload.playlist, $playlist, isHost, false);
          if(constantUpdateLocked != undefined){
            constantUpdateLocked = false;
          }
          break;

        case MESSAGE_TYPE.UPDATE_AFTER_REQUEST_TRANSFER:
          console.log("update after request transfer");
          console.log(data.payload);
          requestId = data.payload.requestIdTransferred;
          console.log("requestId transfered", requestId);
          let transferType = data.payload.transferType;
          let requestTransferred;
          
          if (transferType === "REQUEST_TO_PLAYLIST") {
            requestTransferred = data.payload.playlist[requestId];
            console.log("request transferred: ",requestTransferred);
            $.notify("The song '" + requestTransferred.song.title + "' by " + requestTransferred.song.artist + " was added to the queue!", "success");
          } else {
            requestTransferred = data.payload.requestList[requestId];
            console.log("request transferred: ",requestTransferred);
            if (requestTransferred.userRequestId === userId) {
              $.notify("Your request '" + requestTransferred.song.title + "' by " + requestTransferred.song.artist + " was removed from the playlist!", "error");
            }
          }
          clearAndPopulatePlaylist(data.payload.playlist, $playlist, isHost, true);
          clearAndPopulateRequests(data.payload.requestList, $requests);
          if(constantUpdateLocked != undefined){
            constantUpdateLocked = false;
          }
          break;

        case MESSAGE_TYPE.UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST:
          
          console.log("adding song directly to playlist");
          console.log(data.payload);
          clearAndPopulatePlaylist(data.payload.playlist, $playlist, isHost, true);
          $playlist.sortable("refresh");
          //$player.attr("src", $player.attr("src"));
          if(constantUpdateLocked != undefined){
            constantUpdateLocked = false;
          }
          break;

        case MESSAGE_TYPE.UPDATE_ENTIRE_PARTY:
          console.log("updating whole party");

          let favorites = data.payload.favorites;
          favIds = [];
          for(let key in favorites){
            favIds.push(key);
          }
          favObjs = favorites;
          clearAndPopulatePlaylist(data.payload.party.playlist, $playlist, isHost, false);
          clearAndPopulateRequests(data.payload.party.requests, $requests);

          //$player.attr("src", data.payload.party.playlistUrl);
          break;
        case MESSAGE_TYPE.UPDATE_PLAYER:
          //console.log("Got update player")
          updatePlayer(data);
          break;
        case MESSAGE_TYPE.UPDATE_NEXT_CURR_SONG_REQUEST:
          //console.log("got update next curr song request")
          //console.log("UNLOCKED")
          updatePlayer(data);
          constantUpdateLocked = false;
          break;
        case MESSAGE_TYPE.UPDATE_GUESTS_END_PARTY:
          alert("The host has ended the party.");
          guestLeaveParty(true);
          break;
        case MESSAGE_TYPE.UPDATE_NEW_USER_JOINED:
          $.notify(data.payload.newUser.firstName + " " + data.payload.newUser.lastName + " has joined the sesh!", "info");
          break;
        case MESSAGE_TYPE.UPDATE_SEND_USER_TO_LOGIN:
          //alert("you gotta redirect to login bro");
          post("/", {}, "get");
          break;

      }
    } else{
      if (data.type === MESSAGE_TYPE.ADD_SONG_DIRECTLY_TO_PLAYLIST) {
        console.log(data);
        if (data.location === "requests") {
          if (confirm("This song has already been requested, would you like to move it to the queue?")) {
            moveRequestToQueue(partyId, userId, data.requestId, $("#ulPlaylist li").length);
          }
        } else {
          alert("This song is already in the queue");
        }
      } else if (data.type === MESSAGE_TYPE.ADD_REQUEST) {
        alert("Someone has already requested this song!");
      }
      console.log("SERVER SIDE WEBSOCKET ERROR MESSAGE: " + data.message);
    }
    
  };
}

function convertTime(s) {
  var ms = s % 1000;
  s = (s - ms) / 1000;
  var secs = s % 60;
  s = (s - secs) / 60;
  var mins = s % 60;
  var hrs = (s - mins) / 60;

  return mins + ':' + secs;
}

function updatePlayer(data){
  isPaused = !data.payload.isPlaying;
  timePassed = data.payload.timePassed;
  if (currSongId !== data.payload.currentSongId) {
      currSongId = data.payload.currentSongId;
      $("#songArt").attr("src", data.payload.imageUrl);
      $("#progressbar").attr("max", data.payload.duration);
      $("#songTitle").html(data.payload.songTitle);
      $("#albumTitle").html(data.payload.albumTitle);
      $("#artistName").html(data.payload.artist);
  }
  //console.log("isplaying", data.payload.isPlaying)
  if(data.payload.isPlaying === true){
    $("#playButton").hide();
    $("#pauseButton").show();
  } else{
    $("#playButton").show();
    $("#pauseButton").hide();
  }
  hideSongsNotPlaying();
  
  $("#progressbar").attr("value", data.payload.timePassed);
  //console.log("TIME PASSED: " + timePassed);
  $(".elapsed").text(convertTime(timePassed));
  $(".duration").text(convertTime(data.payload.duration));
}

function hideSongsNotPlaying(){
  currSongIndex = getCurrentSongIndex()
  //console.log("current playing song is at index: " + currSongIndex)
  for (let i = 0; i < $("#ulPlaylist li").length; i++) {
      $("#ulPlaylist li").eq(i).show();
      if (currSongIndex >= i) {
          //console.log("hiding song", i)
          $("#ulPlaylist li").eq(i).hide();
      }
  }
}

function getCurrentSongIndex(){
  index = $("#ulPlaylist").find("#" + currSongId).index();
  if(index < 0){
    return 0;
  }
  return index;
}

function vote() {
  //unbind TODO
  $(".upvote").click(function(x) {
    votedId = x.currentTarget.id;
    upvoteRequest(partyId, userId, x.currentTarget.id);

  });

  $(".downvote").click(function(x) {
    votedId = x.currentTarget.id;
    downvoteRequest(partyId, userId, x.currentTarget.id);
  });
}

function getRequestId(songId){
  return partyId + "-" + songId;
}

function highlightFavorites() {
  $("#request-list ul").find("li").each(function(index, value) {

    if (jQuery.inArray(value.id, favIds) >= 0) {
      $(this).find("i#ifav").attr("style", "color: yellow;");
    } else {
      $(this).find("i#ifav").attr("style", "color: grey;");
    }
  });

  $("#playlist-list ul").find("li").each(function(index, value) {
    console.log(favIds);
      if (jQuery.inArray(value.id, favIds) >= 0) {
        $(this).find("i#ifav").attr("style", "color: yellow;");
      } else {
        $(this).find("i#ifav").attr("style", "color: grey;");
      }
  });
}

function highlightSearchFavorites(favIds){
  $(".searchResults").find("li").each(function(index, value) {
      if (jQuery.inArray(getRequestId(value.id), favIds) >= 0) {
        $(this).find("i#ifav").attr("style", "color: yellow;");
      } else {
        $(this).find("i#ifav").attr("style", "color: grey;");
      }
  });
}

function populateFavoritesTab(){
  console.log("populateFavoritesTab");
  let favorites = $(".favoritesList");
    favorites.empty();
    for(let key in favObjs){
        song = favObjs[key];
        favorites.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
          + "id=\"" + song.spotifyId + "\" >"
          + "<div class=\"fav\" >"
            + "<button class=\"favButton\" id=\"" + song.spotifyId + "\" type=\"button\"> " 
              + "<i id=\"ifav\" class=\"material-icons\" style=\"color: yellow;\">grade</i>"
            + "</button>"
          //end of fav div
          + "</div>"
          + "<div id=\"songtitle\">" + song.title 
          + "</div>"
          + "<div id=\"songartist\">" + song.artist 
          + "</div>"
          + "</li>");
    }
    favorite();
}

function onStarClick(x){
  //console.log($(this).find("i#ifav"));
  //console.log($(this).find("i#ifav").css("color"));
  //console.log($(this).find("i#ifav").css("color") === "rgb(181, 181, 181)");
  //add fav
  if ($(this).find("i#ifav").css("color") === "rgb(128, 128, 128)") {
    const postParams = {userId: userId, songId: x.currentTarget.id, add: true, partyId: partyId};
    $.post("/addSongToFavorites", postParams, responseJSON => {
      const responseObject = JSON.parse(responseJSON);
      const favList = responseObject.favorites;
      favObjs = favList;
      favIds = [];
      for (let i in favList) {
        favIds.push(i);
      }    
      // console.log("FAV LIST", favList);
      console.log("FAV IDS in on start click", favIds);

      highlightFavorites();
      if($("#favorites").hasClass("active")){
        populateFavoritesTab();
      } else if($("#search").hasClass("active")){
        highlightSearchFavorites(favIds);
      } 

    });
  } else {
    const postParameters = {userId: userId, songId: x.currentTarget.id, add: false, partyId: partyId};
    $.post("/addSongToFavorites", postParameters, responseJSON => {
      const resObject = JSON.parse(responseJSON);
      const favList = resObject.favorites;
      favObjs = favList;

      favIds = [];
      for (let i in favList) {
        favIds.push(i);
      }

      highlightFavorites();
      if($("#favorites").hasClass("active")){
        populateFavoritesTab();
      } else if($("#search").hasClass("active")){
        highlightSearchFavorites(favIds);
      } 
    });

  }
  x.stopPropagation();

}

function favorite() {
  $(".favButton").unbind("click");
  $(".favButton").click(onStarClick);
}

function appendToRequests($requests, data) {
  if (data.payload.newRequest.userRequestId === userId) {
      $requests.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
    + "id=\"" + data.payload.newRequest.requestId + "\" >"
    + "<div class=\"fav\" >"
        + "<button class=\"favButton\" id=\"" + data.payload.newRequest.song.spotifyId + "\" type=\"button\"> " 
          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
        + "</button>"
      //end of fav div
      + "</div>"
    + "<div id=\"songtitle\">" + data.payload.newRequest.song.title 
    + "<div id=\"scorediv\">" + data.payload.newRequest.score + "</div>"
    + "<div id=\"vote\" > "
      + "<button class=\"upvote\" id=\"" + data.payload.newRequest.requestId + "\" type=\"button\"> "
        + "<i class=\"material-icons\" style=\"color: green;\">thumb_up</i>"
      + "</button>"
      + "<button class=\"downvote\" id=\"" + data.payload.newRequest.requestId + "\" type=\"button\"> "
        + "<i class=\"material-icons\">thumb_down</i>"
      + "</button>"
    //end of vote div
    + "</div>"
    //end of song title div
    + "</div>"
    + "<div id=\"songartist\">" + data.payload.newRequest.song.artist 
    + "</div>"
    + "</li>");
} else {
    $requests.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
    + "id=\"" + data.payload.newRequest.requestId + "\" >"
    + "<div class=\"fav\" >"
        + "<button class=\"favButton\" id=\"" + data.payload.newRequest.song.spotifyId + "\" type=\"button\"> " 
          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
        + "</button>"
      //end of fav div
      + "</div>"
    + "<div id=\"songtitle\">" + data.payload.newRequest.song.title 
    + "<div id=\"scorediv\">" + data.payload.newRequest.score + "</div>"
    + "<div id=\"vote\" > "
      + "<button class=\"upvote\" id=\"" + data.payload.newRequest.requestId + "\" type=\"button\"> "
        + "<i class=\"material-icons\">thumb_up</i>"
      + "</button>"
      + "<button class=\"downvote\" id=\"" + data.payload.newRequest.requestId + "\" type=\"button\"> "
        + "<i class=\"material-icons\">thumb_down</i>"
      + "</button>"
    //end of vote div
    + "</div>"
    //end of song title div
    + "</div>"
    + "<div id=\"songartist\">" + data.payload.newRequest.song.artist 
    + "</div>"
    + "</li>");
}


  if (data.payload.newRequest.userRequestId === userId) {
    userRequests.push(data.payload.newRequest);
  }

}

function updateRequestVotes($requests, key, requestList, upvote, downvote, user) {
  if (upvote === true) {
    $requests.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
      + "id=\"" + key + "\" >"
      + "<div class=\"fav\" >"
        + "<button class=\"favButton\" id=\"" + requestList[key].song.spotifyId + "\" type=\"button\"> " 
          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
        + "</button>"
      //end of fav div
      + "</div>"
      + "<div id=\"songtitle\">" + requestList[key].song.title 
      + "<div id=\"scorediv\">" + requestList[key].score + "</div>"
      + "<div id=\"vote\" > "
        + "<button class=\"upvote\" id=\"" + key + "\" type=\"button\"> "
          + "<i style=\"color:green;\" class=\"material-icons\">thumb_up</i>"
        + "</button>"
        + "<button class=\"downvote\" id=\"" + key + "\" type=\"button\"> "
          + "<i class=\"material-icons\">thumb_down</i>"
        + "</button>"
      //end of vote div
      + "</div>"
      //end of song title div
      + "</div>"
      + "<div id=\"songartist\">" + requestList[key].song.artist 
      + "</div>"
      + "</li>");

      if (requestList[key].userRequestId === userId) {
        userRequests.push(requestList[key]);
      }

      if (key === votedId && user !== undefined && user === userId) {
        $("#"+key).effect( "highlight",{color:'#26a82b',easing:'swing'},2500 );
        $("#request-list").animate({
          scrollTop: $("#"+key).position().top - $("#request-list ul").first().position().top
        }, 1000);
      }

  } else if (downvote === true) {
    $requests.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
      + "id=\"" + key + "\" >"
      + "<div class=\"fav\" >"
        + "<button class=\"favButton\" id=\"" + requestList[key].song.spotifyId + "\" type=\"button\"> " 
          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
        + "</button>"
      //end of fav div
      + "</div>"
      + "<div id=\"songtitle\">" + requestList[key].song.title 
      + "<div id=\"scorediv\">" + requestList[key].score + "</div>"
      + "<div id=\"vote\" > "
        + "<button class=\"upvote\" id=\"" + key + "\" type=\"button\"> "
          + "<i class=\"material-icons\">thumb_up</i>"
        + "</button>"
        + "<button class=\"downvote\" id=\"" + key + "\" type=\"button\"> "
          + "<i style=\"color:red;\" class=\"material-icons\">thumb_down</i>"
        + "</button>"
      //end of vote div
      + "</div>"
      //end of song title div
      + "</div>"
      + "<div id=\"songartist\">" + requestList[key].song.artist 
      + "</div>"
      + "</li>");

      if (requestList[key].userRequestId === userId) {
        userRequests.push(requestList[key]);
      }

      if (key === votedId && user !== undefined && user === userId) {
        $("#"+key).effect( "highlight",{color:'#e21818',easing:'swing'},1500 );
        $("#request-list").animate({
          scrollTop: $("#"+key).position().top - $("#request-list ul").first().position().top
        }, 1000);
      } 

  } else {
     $requests.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
      + "id=\"" + key + "\" >"
      + "<div class=\"fav\" >"
        + "<button class=\"favButton\" id=\"" + requestList[key].song.spotifyId + "\" type=\"button\"> " 
          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
        + "</button>"
      //end of fav div
      + "</div>"
      + "<div id=\"songtitle\">" + requestList[key].song.title 
      + "<div id=\"scorediv\">" + requestList[key].score + "</div>"
      + "<div id=\"vote\" > "
        + "<button class=\"upvote\" id=\"" + key + "\" type=\"button\"> "
          + "<i class=\"material-icons\">thumb_up</i>"
        + "</button>"
        + "<button class=\"downvote\" id=\"" + key + "\" type=\"button\"> "
          + "<i class=\"material-icons\">thumb_down</i>"
        + "</button>"
      //end of vote div
      + "</div>"
      //end of song title div
      + "</div>"
      + "<div id=\"songartist\">" + requestList[key].song.artist 
      + "</div>"
      + "</li>"); 

      if (requestList[key].userRequestId === userId) {
        userRequests.push(requestList[key]);
      }

      if (key === votedId && user !== undefined && user === userId) {
        $("#"+key).effect( "highlight",{color:'#39aeb2',easing:'swing'},2500 );
        $("#request-list").animate({
          scrollTop: $("#"+key).position().top - $("#request-list ul").first().position().top
        }, 1000);
      } 
  }
}

function appendToPlaylist($playlist, newRequest, startShowing) {
  if (!startShowing) {
    $playlist.append("<li style=\"display:none;\" onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
      + "id=\"" + newRequest.requestId + "\" >"
      + "<div class=\"fav\" >"
        + "<button class=\"favButton\" id=\"" + newRequest.song.spotifyId + "\" type=\"button\"> " 
          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
        + "</button>"
      //end of fav div
      + "</div>"
      + "<div id=\"songtitle\">" + newRequest.song.title 
      + "<div id=\"scorediv\">" + newRequest.score + "</div>"
      + "</div>"
      + "<div id=\"songartist\">" + newRequest.song.artist 
      + "</div>"
      + "</li>");
  } else {
    $playlist.append("<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
      + "id=\"" + newRequest.requestId + "\" >"
      + "<div class=\"fav\" >"
        + "<button class=\"favButton\" id=\"" + newRequest.song.spotifyId + "\" type=\"button\"> " 
          + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
        + "</button>"
      //end of fav div
      + "</div>"
      + "<div id=\"songtitle\">" + newRequest.song.title 
      + "<div id=\"scorediv\">" + newRequest.score + "</div>"
      + "</div>"
      + "<div id=\"songartist\">" + newRequest.song.artist 
      + "</div>"
      + "</li>");
  }
}

function clearAndPopulateRequests(requests, $requests, user){
  $requests.empty();
  for (let key in requests) {
    let downvote = false;
    let upvote = false;
    if (requests.hasOwnProperty(key)) {
      for (let i in requests[key].upvotes) {
        if (userId === requests[key].upvotes[i]) {
          upvote = true;
        }
      }

      for (let j in requests[key].downvotes) {
        if (userId === requests[key].downvotes[j]) {
          downvote = true;
        }
      }
      updateRequestVotes($requests, key, requests, upvote, downvote, user);
    }
  }

  vote();
  favorite();
  highlightFavorites();
}


function clearAndPopulatePlaylist(playlist, $playlist, isHost, play){
  let startShowing = false
  $playlist.empty();
  for (let key in playlist) {
    if (playlist.hasOwnProperty(key)) {
        appendToPlaylist($playlist, playlist[key], startShowing);
        if(key === currSongId){
          startShowing = true;
        }//If a song is not playing, and you just added the first song. Play that song 
        else if ((currSongId === -1 || currSongId === undefined) && startShowing == false && play){
          startShowing = true;

          if(isHost){
            //playPlaylist(partyId, userId)
            //pauseSong(partyId, userId);
          }
        }
      }   
    }
    favorite();
    highlightFavorites();
  }

function setPartyId (partyId, userId) {
  console.log("send party id");
  let message = {
    type: MESSAGE_TYPE.SET_PARTY_ID, 
    payload:{
      userId: userId,
      partyId: partyId
    }
  }
  conn.send(JSON.stringify(message));
}

function addRequest (partyId, userId, songId) {
  let message = {
    type: MESSAGE_TYPE.ADD_REQUEST, 
    payload:{
      userId: userId,
      partyId: partyId,
      songId: songId
    }
  }
  conn.send(JSON.stringify(message));
}

function upvoteRequest (partyId, userId, requestId) {
  let message = {
    type: MESSAGE_TYPE.UPVOTE_REQUEST, 
    payload:{
      userId: userId,
      partyId: partyId,
      requestId: requestId
    }
  }
  conn.send(JSON.stringify(message));
}

function downvoteRequest (partyId, userId, requestId) {
  let message = {
    type: MESSAGE_TYPE.DOWNVOTE_REQUEST, 
    payload:{
      userId: userId,
      partyId: partyId,
      requestId: requestId
    }
  }
  conn.send(JSON.stringify(message));
}

function moveRequestToQueue (partyId, userId, requestId, index) {
  constantUpdateLocked = true;

  let message = {
    type: MESSAGE_TYPE.MOVE_REQUEST_TO_QUEUE, 
    payload:{
      userId: userId,
      partyId: partyId,
      requestId: requestId,
      insertIndex: index
    }
  }
  conn.send(JSON.stringify(message));
}

function moveFromQueueToRequest (partyId, userId, requestId) {
  let message = {
    type: MESSAGE_TYPE.MOVE_FROM_QUEUE_TO_REQUEST, 
    payload:{
      userId: userId,
      partyId: partyId,
      requestId: requestId
    }
  }
  conn.send(JSON.stringify(message));
}

function reorderPlaylistTrack(partyId, userId, requestId, oldIndex, newIndex){
  let message = {
    type: MESSAGE_TYPE.REORDER_PLAYLIST_TRACK, 
    payload:{
      userId: userId,
      partyId: partyId,
      requestId: requestId,
      startIndex: oldIndex,
      endIndex: newIndex
    }
  }
  conn.send(JSON.stringify(message));
}

function addToPlaylist(partyId, userId, songId) {
  constantUpdateLocked = true;
  let message = {
    type: MESSAGE_TYPE.ADD_SONG_DIRECTLY_TO_PLAYLIST, 
    payload:{
      userId: userId,
      partyId: partyId,
      songId: songId
    }
  }
  conn.send(JSON.stringify(message));
}


// function resumeSong (partyId, userId) {
//   let message = {
//     type: MESSAGE_TYPE.RESUME_SONG, 
//     payload:{
//       userId: userId,
//       partyId: partyId
//     }
//   }
//   conn.send(JSON.stringify(message));
// }

function playPlaylist(partyId, userId, index){
  console.log("playplalist sent ", partyId, userId)
  //IF you should play the current song (i.e it was paused) if you dont go into his if statement it means 
  //the host double clicked on a song to play it.
  console.log("LOCKED PLAY PLAYLIST")

  constantUpdateLocked = true;
  if(index === undefined || index === null){
      index = getCurrentSongIndex();
  }
  let message = {
    type: MESSAGE_TYPE.PLAY_PLAYLIST, 
    payload:{
      userId: userId,
      partyId: partyId,
      index: index
    }
  }
  conn.send(JSON.stringify(message));
}

function pauseSong (partyId, userId) {
  console.log("LOCKED PLAY PAUSE SONG")

  constantUpdateLocked = true;

  let message = {
    type: MESSAGE_TYPE.PAUSE_SONG, 
    payload:{
      userId: userId,
      partyId: partyId
    }
  }
  conn.send(JSON.stringify(message));
}


function nextSong (partyId, userId) {
    console.log("LOCKED NEXT SONG")

  constantUpdateLocked = true;

  index = getCurrentSongIndex() + 1;
  //TODO add check to see if the index is greater than the current size of the list
  let message = {
    type: MESSAGE_TYPE.NEXT_SONG, 
    payload:{
      userId: userId,
      partyId: partyId,
      index: index
    }
  }
  conn.send(JSON.stringify(message));
}

function prevSong (partyId, userId) {
    
  index = getCurrentSongIndex() - 1;
  if(index < 0){
    return;
  }
  console.log("LOCKED PREV SONG")

  constantUpdateLocked = true;

  let message = {
    type: MESSAGE_TYPE.PREV_SONG, 
    payload:{
      userId: userId,
      partyId: partyId,
      index: index
    }
  }
  conn.send(JSON.stringify(message));
}

function seekSong (partyId, userId, position) {
    console.log("LOCKED SEEK SONG")

  constantUpdateLocked = true;
  let message = {
    type: MESSAGE_TYPE.SEEK_SONG, 
    payload:{
      userId: userId,
      partyId: partyId,
      seekPosition: position
    }
  }
  conn.send(JSON.stringify(message));
}

function resumeSong(partyId, userId, position){
    console.log("LOCKED RESUME SONG")

  constantUpdateLocked = true;
  index = getCurrentSongIndex();
  //IF you should play the current song (i.e it was paused) if you dont go into his if statement it means 
  //the host double clicked on a song to play it.
  let message = {
    type: MESSAGE_TYPE.RESUME_SONG, 
    payload:{
      userId: userId,
      partyId: partyId,
      index: index,
      seekPosition: position
    }
  }
  conn.send(JSON.stringify(message));
}

function post(path, params, method) {
  method = method || "post"; // Set method to post by default if not specified.

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    let form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
            let hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
         }
    }

    document.body.appendChild(form);
    form.submit();
}

function updatePartyCurrentSong (partyId, userId) {
  index = getCurrentSongIndex();
  let message = {
    type: MESSAGE_TYPE.SONG_MOVED_TO_NEXT, 
    payload:{
      userId: userId,
      partyId: partyId,
      oldSongId: currSongId,
      index: index,
      isPaused: isPaused,
      timePassed: timePassed
    }
  }
  conn.send(JSON.stringify(message));
}

function endParty(partyId, userId, shouldUnfollow) {
  let message = {
    type: MESSAGE_TYPE.END_PARTY, 
    payload:{
      userId: userId,
      partyId: partyId,
      unfollow: shouldUnfollow
    }
  }
  conn.send(JSON.stringify(message));
}

function guestLeaveParty(partyEndedBool) {
  var v = confirm("Would you like to keep this Sesh as a Spotify playlist?");
    var deleteBool;
        if (v === true) {
            deleteBool = false;
        } else {
            deleteBool = true;
        }
        const params = {userId: userId, partyId: partyId, deleteBool: deleteBool, partyEndedBool: partyEndedBool};
        post("/leaveparty",params, "get");
}
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
RESUME_SONG: 21
};

let conn;
let timeoutCheckForNewCurrSong;
let myId = -1;

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
  // TODO Create the WebSocket connection and assign it to `conn`
  conn = new WebSocket("ws://localhost:4567/update");

  conn.onclose = function(){
    setTimeout(setupWebsockets, 1000)
  }

  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    console.log("DATA TYPE: " , data.type)
    console.log("DATA OBJECT: " , data);
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

          $requests.sortable("refresh");
          break;

        case MESSAGE_TYPE.UPDATE_VOTE_REQUESTS:
          console.log("updating request vote");
          clearAndPopulateRequests(data.payload.requestList, $requests);
          break;

        case MESSAGE_TYPE.UPDATE_REARRANGE_PLAYLIST:
          console.log("update rearrange playlist");
          clearAndPopulatePlaylist(data.payload.playlist, $playlist);
          break;

        case MESSAGE_TYPE.UPDATE_AFTER_REQUEST_TRANSFER:
          console.log("update after request transfer");
          clearAndPopulatePlaylist(data.payload.playlist, $playlist);
          clearAndPopulateRequests(data.payload.requestList, $requests);
          break;

        case MESSAGE_TYPE.UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST:
          console.log("adding song directly to playlist");
          clearAndPopulatePlaylist(data.payload.playlist, $playlist);


          $playlist.sortable("refresh");
          $player.attr("src", $player.attr("src"));
          break;

        case MESSAGE_TYPE.UPDATE_ENTIRE_PARTY:
          console.log("updating whole party");
          clearAndPopulatePlaylist(data.payload.party.playlist, $playlist);
          clearAndPopulateRequests(data.payload.party.requests, $requests);

          $player.attr("src", data.payload.party.playlistUrl);
          break;
        case MESSAGE_TYPE.UPDATE_PLAYER:
          console.log("Got update player")
          updatePlayer(data)
          break;
        case MESSAGE_TYPE.UPDATE_NEXT_CURR_SONG_REQUEST:
          console.log("got update next curr song request")
          updatePlayer(data)
          break;

      }
    } else{
      console.log("SERVER SIDE WEBSOCKET ERROR MESSAGE: " + data.message);
    }
    
  };
}

function updatePlayer(data){
  if (currSongId !== data.payload.currentSongId) {
      currSongId = data.payload.currentSongId;
      $("#songArt").attr("src", data.payload.imageUrl);
      $("#progressbar").attr("max", data.payload.duration);
      $("#songTitle").html(data.payload.songTitle);
      $("#albumTitle").html(data.payload.albumTitle);
      $("#artistName").html(data.payload.artist);
  }
  console.log("isplaying", data.payload.isPlaying)
  if(data.payload.isPlaying === true){
    $("#playButton").hide();
    $("#pauseButton").show();
  } else{
    $("#playButton").show();
    $("#pauseButton").hide();
  }
  hideSongsNotPlaying();

  timePassed = data.payload.timePassed;
  $("#progressbar").attr("value", data.payload.timePassed);
}

function hideSongsNotPlaying(){
  currSongIndex = getCurrentSongIndex()
  console.log("current playing song is at index: " + currSongIndex)
  for (let i = 0; i < $("#ulPlaylist li").length; i++) {
      $("#ulPlaylist li").eq(i).show();
      if (currSongIndex >= i) {
          console.log("hiding song", i)
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
  $(".upvote").click(function(x) {
    upvoteRequest(partyId, userId, x.currentTarget.id);
  });

  $(".downvote").click(function(x) {
    downvoteRequest(partyId, userId, x.currentTarget.id);
  });
}

function appendToRequests($requests, data) {
  $requests.append("<li " + "id=\"" + data.payload.newRequest.requestId + 
    "\" onmouseover=\"hoverOn(this)\"" + " onmouseout=\"hoverOff(this)\"><div id=\"songdiv\">" 
    + data.payload.newRequest.song.title + " - " + data.payload.newRequest.song.artist 
    + " " + data.payload.newRequest.score + "<div id=\"vote\"> <button class=\"upvote\" id=\"" 
    + data.payload.newRequest.requestId 
    + "\" type=\"button\"> <i class=\"material-icons\">thumb_up</i></button><button class=\"downvote\" id=\"" 
    + data.payload.newRequest.requestId 
    + "\" type=\"button\"> <i class=\"material-icons\">thumb_down</i> </button> </div> </div> </li>");
}

function updateRequestVotes($requests, key, requestList) {
  $requests.append("<li " + "id=\"" + key + "\" onmouseover=\"hoverOn(this)\"" + 
    " onmouseout=\"hoverOff(this)\"><div id=\"songdiv\">" + requestList[key].song.title + 
    " - " + requestList[key].song.artist + " " + requestList[key].score + 
    "<div id=\"vote\"> <button class=\"upvote\" id=\"" + key + 
    "\" type=\"button\"> <i class=\"material-icons\">thumb_up</i></button><button class=\"downvote\" id=\"" + 
    key + "\" type=\"button\"> <i class=\"material-icons\">thumb_down</i> </button> </div> </div> </li>");
}

function appendToPlaylist($playlist, newRequest, startShowing) {
  if (!startShowing) {
    $playlist.append("<li style=\"display:none;\"" + "id=\"" + newRequest.requestId + "\" onmouseover=\"hoverOn(this)\"" + 
    " onmouseout=\"hoverOff(this)\"><div id=\"songdiv\">" + newRequest.song.title + 
    " - " + newRequest.song.artist + " " + newRequest.score + 
    "<div id=\"vote\"> <button class=\"upvote\" id=\"" + newRequest.requestId + 
    "\" type=\"button\"> <i class=\"material-icons\">thumb_up</i></button><button class=\"downvote\" id=\"" + 
    newRequest.requestId + "\" type=\"button\"> <i class=\"material-icons\">thumb_down</i> </button> </div> </div> </li>");
  } else {
    $playlist.append("<li " + "id=\"" + newRequest.requestId + "\" onmouseover=\"hoverOn(this)\"" + 
    " onmouseout=\"hoverOff(this)\"><div id=\"songdiv\">" + newRequest.song.title + 
    " - " + newRequest.song.artist + " " + newRequest.score + 
    "<div id=\"vote\"> <button class=\"upvote\" id=\"" + newRequest.requestId + 
    "\" type=\"button\"> <i class=\"material-icons\">thumb_up</i></button><button class=\"downvote\" id=\"" + 
    newRequest.requestId + "\" type=\"button\"> <i class=\"material-icons\">thumb_down</i> </button> </div> </div> </li>");
  }
}

function clearAndPopulateRequests(requests, $requests){
  $requests.empty();
  for (let key in requests) {
    if (requests.hasOwnProperty(key)) {
      console.log("populating request");
      updateRequestVotes($requests, key, requests);
    }
  }

  vote();
}


function clearAndPopulatePlaylist(playlist, $playlist){
  console.log("curr song id: " + currSongId);
  let startShowing = false

  $playlist.empty();
  for (let key in playlist) {
    if (playlist.hasOwnProperty(key)) {
        appendToPlaylist($playlist, playlist[key], startShowing);
        if(key === currSongId){
          startShowing = true;
        }//If a song is not playing, and you just added the first song. Play that song 
        else if ((currSongId === -1 || currSongId === undefined) && startShowing == false){
          startShowing = true;
          playPlaylist(partyId, userId)
          pauseSong(partyId, userId);
        }
      }   
    }
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
  index = getCurrentSongIndex() + 1;
  //TODO add check to see if the index is greater than the current size of the list
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

function prevSong (partyId, userId) {
  index = getCurrentSongIndex() - 1;
  if(index < 0){
    return;
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

function seekSong (partyId, userId, position) {
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

function updatePartyCurrentSong (partyId, userId) {
  let message = {
    type: MESSAGE_TYPE.SONG_MOVED_TO_NEXT, 
    payload:{
      userId: userId,
      partyId: partyId,
    }
  }
  conn.send(JSON.stringify(message));
}
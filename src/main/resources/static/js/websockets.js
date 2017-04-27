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
MOVE_PLAYLIST_TRACK: 14
};

let conn;
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

  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    console.log(data);
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
          $requests.append("<li " + "id=\"" + data.payload.newRequest.requestId + "\" onmouseover=\"hoverOn(this)\"" + " onmouseout=\"hoverOff(this)\"><div id=\"songdiv\">" + data.payload.newRequest.song.title + " - " + data.payload.newRequest.song.artist + " " + data.payload.newRequest.score + "<div id=\"vote\"> <button class=\"upvote\" id=\"" + data.payload.newRequest.requestId + "\" type=\"button\"> <i class=\"material-icons\">thumb_up</i></button><button class=\"downvote\" id=\"" + data.payload.newRequest.requestId + "\" type=\"button\"> <i class=\"material-icons\">thumb_down</i> </button> </div> </div> </li>");
          
          $(".upvote").click(function(x) {
            upvoteRequest(partyId, userId, x.currentTarget.id);
          });

          $(".downvote").click(function(x) {
            downvoteRequest(partyId, userId, x.currentTarget.id);
          });
          break;
        case MESSAGE_TYPE.UPDATE_VOTE_REQUESTS:
          console.log("adding song to request list");
          $requests.empty();
          const requestList = data.payload.requestList;
          for (var key in requestList) {
            if (requestList.hasOwnProperty(key)) {
              $requests.append("<li " + "id=\"" + key + "\" onmouseover=\"hoverOn(this)\"" + " onmouseout=\"hoverOff(this)\"><div id=\"songdiv\">" + requestList[key].song.title + " - " + requestList[key].song.artist + " " + requestList[key].score + "<div id=\"vote\"> <button class=\"upvote\" id=\"" + key + "\" type=\"button\"> <i class=\"material-icons\">thumb_up</i></button><button class=\"downvote\" id=\"" + key + "\" type=\"button\"> <i class=\"material-icons\">thumb_down</i> </button> </div> </div> </li>");
            }
          }

          $(".upvote").click(function(x) {
            upvoteRequest(partyId, userId, x.currentTarget.id);
          });

          $(".downvote").click(function(x) {
            downvoteRequest(partyId, userId, x.currentTarget.id);
          });
          break;
        case MESSAGE_TYPE.UPDATE_REARRANGE_PLAYLIST:
          //TODO reload the entire playlist list
          break;
        case MESSAGE_TYPE.UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST:
          console.log("adding song directly to playlist");
          $playlist.append("<li " + "id=\"" + data.payload.newRequest.requestId + "\" onmouseover=\"hoverOn(this)\"" + " onmouseout=\"hoverOff(this)\"><div id=\"songdiv\">" + data.payload.newRequest.song.title + " - " + data.payload.newRequest.song.artist + " " + data.payload.newRequest.score + "</div> </li>");
          // $listItems = $("#playlist-list li");
          // $last = $listItems.last();
          // $last.attr("class", "sortable");
          break;
        case MESSAGE_TYPE.UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST:
          console.log("adding song directly to playlist");
          break;
        case MESSAGE_TYPE.UPDATE_ENTIRE_PARTY:
        console.log("updating whole party");
          const playlist = data.payload.party.playlistQueue;
          const requests = data.payload.party.requests;

          for (var key in playlist) {
            console.log("populating playlist");
            if (playlist.hasOwnProperty(key)) {
              $playlist.append("<li " + "id=\"" + key + "\">" + playlist[key].song.title + " - " + playlist[key].song.artist + " " + playlist[key].score + "</li>");
            }
          }

          for (var key in requests) {
            if (requests.hasOwnProperty(key)) {
              console.log("populating request");
              $requests.append("<li " + "id\"" + key + "\">" + requests[key].song.title + " - " + requests[key].song.artist + " " + requests[key].score + "</li>");
            }
          }
          console.log("finished");

          $player.attr("src", data.payload.party.playlistUrl);
          break;
      }
    } else{
      console.log("SERVER SIDE WEBSOCKET ERROR MESSAGE: " + data.message);
    }
    
  };
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
      requestId: requestId
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
    type: MESSAGE_TYPE.MOVE_FROM_QUEUE_TO_REQUEST, 
    payload:{
      userId: userId,
      partyId: partyId,
      requestId: requestId,
      oldIndex: oldIndex,
      newIndex: newIndex
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


const MESSAGE_TYPE = {
  CONNECT: 0,
  SET_PARTY_ID: 1,
  ADD_REQUEST: 2, 
  UPVOTE_REQUEST: 3, 
  DOWNVOTE_REQUEST: 4, 
  MOVE_REQUEST_TO_QUEUE: 5, 
  MOVE_FROM_QUEUE_TO_REQUEST: 6, 
  UPDATE_ADD_REQUEST: 7, 
  UPDATE_VOTE_REQUESTS: 8, 
  UPDATE_AFTER_REQUEST_TRANSFER: 9
};

let conn;
let myId = -1;

// Setup the WebSocket connection for live updating of scores.
const setup_live_scores = () => {
  // TODO Create the WebSocket connection and assign it to `conn`
  conn = new WebSocket("ws://localhost:4567/update");

  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    if(data.success){
      console.log(data)
      switch (data.type) {
        default:
          console.log('Unknown message type!', data.type);
          break;
        case MESSAGE_TYPE.UPDATE_ADD_REQUEST:
          //receive new request and append to list
          break;
        case MESSAGE_TYPE.UPDATE_VOTE_REQUESTS:
          break;
        case MESSAGE_TYPE.UPDATE_AFTER_REQUEST_TRANSFER:
          break;
      }
    } else{
      console.log(data.message);
    }
    
  };
}

function setPartyId (partyId, userId) {
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

function moveRequestToQueue (partyId, userId, requestId) {
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


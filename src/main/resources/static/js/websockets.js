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
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.CONNECT:
        break;
      case MESSAGE_TYPE.SET_PARTY_ID:
        break;
      case MESSAGE_TYPE.ADD_REQUEST:
        break;
      case MESSAGE_TYPE.UPVOTE_REQUEST:
        break;
      case MESSAGE_TYPE.DOWNVOTE_REQUEST:
        break;
      case MESSAGE_TYPE.MOVE_REQUEST_TO_QUEUE:
        break;
      case MESSAGE_TYPE.MOVE_FROM_QUEUE_TO_REQUEST:
        break;
      case MESSAGE_TYPE.UPDATE_ADD_REQUEST:
        break;
      case MESSAGE_TYPE.UPDATE_VOTE_REQUESTS:
        break;
      case MESSAGE_TYPE.UPDATE_AFTER_REQUEST_TRANSFER:
        break;
    }
  };
}



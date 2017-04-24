package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Request.VoteType;

@WebSocket
public class PartyWebsocket {
  private static final Gson GSON = new Gson();
  private static final Multimap<Integer, Session> partyIdToSessions = HashMultimap
      .create();
  private static final Map<Session, Integer> sessionToPartyId = new HashMap<>();

  private static enum MESSAGE_TYPE {
    CONNECT, SET_PARTY_ID, ADD_REQUEST, UPVOTE_REQUEST, DOWNVOTE_REQUEST, MOVE_REQUEST_TO_QUEUE, MOVE_FROM_QUEUE_TO_REQUEST, UPDATE_ADD_REQUEST, UPDATE_VOTE_REQUESTS, UPDATE_AFTER_REQUEST_TRANSFER
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    System.out.println("received");
    JsonObject payload = new JsonObject();
    payload.addProperty("success", true);
    JsonObject message = new JsonObject();
    message.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    message.add("payload", payload);
    session.getRemote().sendString(message.toString());
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    Integer partyId = sessionToPartyId.get(session);
    // If they have set their party id
    if (partyId != null) {
      // Remove from both lists so you dont try and set updates to a closed
      // session
      partyIdToSessions.remove(partyId, session);
      sessionToPartyId.remove(session);
    }
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    JsonObject received = GSON.fromJson(message, JsonObject.class);
    int typeInt = received.get("type").getAsInt();
    MESSAGE_TYPE messageType = MESSAGE_TYPE.values()[typeInt];
    JsonObject payload = received.get("payload").getAsJsonObject();
    String userId = payload.get("userId").getAsString();
    int partyId = payload.get("partyId").getAsInt();
    Party party = Party.of(partyId);
    User user = User.of(userId);
    switch (messageType) {
      case SET_PARTY_ID:
        partyIdToSessions.put(partyId, session);
        sessionToPartyId.put(session, partyId);
        break;
      case ADD_REQUEST:

        sendAddRequestUpdate(payload, user, party, session);
        break;
      case UPVOTE_REQUEST:
        sendVoteRequestUpdate(payload, user, party, session,
            Request.VoteType.upvote);
        break;
      case DOWNVOTE_REQUEST:
        sendVoteRequestUpdate(payload, user, party, session,
            Request.VoteType.downvote);
        break;
      case MOVE_REQUEST_TO_QUEUE:
        // sendAfterRequestTransferUpdate(partyId);
        break;
      case MOVE_FROM_QUEUE_TO_REQUEST:
        // sendAfterRequestTransferUpdate(partyId);
        break;
      default:
        assert false : "you should never get here!!!";
    }
  }

  private void sendAddRequestUpdate(JsonObject payload, User user, Party party,
      Session session) throws IOException {
    String songId = payload.get("songId").getAsString();
    String time = payload.get("time").getAsString();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      Request newRequest = party.requestSong(Song.of(songId), user);
      if (newRequest == null) {
        throw new RuntimeException("ERROR: could not request song");
      }
      updatePayload.addProperty("newRequest", newRequest.toJson());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_ADD_REQUEST.ordinal());
      updateMessage.addProperty("success", true);
      updateMessage.add("payload", updatePayload);
      for (Session sesh : partyIdToSessions.get(party.getPartyId())) {
        sesh.getRemote().sendString(updateMessage.toString());
      }
    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void sendVoteRequestUpdate(JsonObject payload, User user, Party party,
      Session session, Request.VoteType voteType) throws IOException {
    String requestId = payload.get("requestId").getAsString();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      boolean success;
      if (voteType.equals(VoteType.upvote)) {
        success = party.upvoteSong(user, requestId);
      } else {
        assert voteType.equals(VoteType.downvote);
        success = party.downvoteSong(user, requestId);
      }
      updatePayload.addProperty("requestList", party.getRequestsAsJson());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_VOTE_REQUESTS.ordinal());
      if (success == false) {
        throw new RuntimeException("ERROR: Cannot vote on request");
      }
      updateMessage.addProperty("success", true);
      updateMessage.add("payload", updatePayload);
      for (Session sesh : partyIdToSessions.get(party.getPartyId())) {
        sesh.getRemote().sendString(updateMessage.toString());
      }
    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void sendAfterRequestTransferUpdate(JsonObject payload) {

  }
}

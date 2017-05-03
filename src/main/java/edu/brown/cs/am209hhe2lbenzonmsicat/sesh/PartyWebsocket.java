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

  private static enum TRANSFER_TYPE {
    REQUEST_TO_PLAYLIST,
    PLAYLIST_TO_REQUEST
  }

  private static enum MESSAGE_TYPE {
    CONNECT,
    SET_PARTY_ID,
    ADD_REQUEST,
    UPVOTE_REQUEST,
    DOWNVOTE_REQUEST,
    MOVE_REQUEST_TO_QUEUE,
    MOVE_FROM_QUEUE_TO_REQUEST,
    ADD_SONG_DIRECTLY_TO_PLAYLIST,
    UPDATE_ADD_REQUEST,
    UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST,
    UPDATE_VOTE_REQUESTS,
    UPDATE_AFTER_REQUEST_TRANSFER,
    UPDATE_ENTIRE_PARTY,
    UPDATE_REARRANGE_PLAYLIST,
    REORDER_PLAYLIST_TRACK,
    PLAY_PLAYLIST,
    RESUME_SONG,
    PAUSE_SONG,
    NEXT_SONG,
    PREVIOUS_SONG,
    UPDATE_PLAYER

  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    System.out.println("received");
    JsonObject payload = new JsonObject();
    JsonObject message = new JsonObject();
    message.addProperty("success", true);
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
      System.out.println("Closed Connection");
    }
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    try {
      JsonObject received = GSON.fromJson(message, JsonObject.class);
      int typeInt = received.get("type").getAsInt();
      MESSAGE_TYPE messageType = MESSAGE_TYPE.values()[typeInt];
      System.out.println("=================================");
      System.out.println(typeInt);
      System.out.println("Message Recieved" + messageType);
      JsonObject payload = received.get("payload").getAsJsonObject();
      String userId = payload.get("userId").getAsString();
      int partyId = payload.get("partyId").getAsInt();
      Party party = Party.of(partyId);
      User user = User.of(userId);
      switch (messageType) {
        case SET_PARTY_ID:
          sendUpdateEntireParty(partyId, session);
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
          sendAfterRequestTransferUpdate(payload, user, party, session,
              TRANSFER_TYPE.REQUEST_TO_PLAYLIST);
          break;
        case MOVE_FROM_QUEUE_TO_REQUEST:
          sendAfterRequestTransferUpdate(payload, user, party, session,
              TRANSFER_TYPE.PLAYLIST_TO_REQUEST);
          break;
        case ADD_SONG_DIRECTLY_TO_PLAYLIST:
          sendAddSongDirectlyToPlaylistUpdate(payload, user, party, session);
          break;
        case REORDER_PLAYLIST_TRACK:
          sendReorderPlaylistTrackUpdate(payload, user, party, session);
          break;
        case PLAY_PLAYLIST:
          playPlaylistAndUpdate(payload, user, party, session);
          break;
        case RESUME_SONG:
          resumeSongAndUpdate(payload, user, party, session);
          break;
        case PAUSE_SONG:
          pauseSongAndUpdate(payload, user, party, session);
          break;
        case NEXT_SONG:
          nextSongAndUpdate(payload, user, party, session);
          break;
        case PREVIOUS_SONG:
          previousSongAndUpdate(payload, user, party, session);
          break;
        default:
          assert false : "you should never get here!!!";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void previousSongAndUpdate(JsonObject payload, User user, Party party,
      Session session) throws IOException {
    try {
      party.prevSong();
      updatePartiesCurrentSong(party);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void nextSongAndUpdate(JsonObject payload, User user, Party party,
      Session session) throws IOException {
    try {
      party.nextSong();
      updatePartiesCurrentSong(party);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void pauseSongAndUpdate(JsonObject payload, User user, Party party,
      Session session) throws IOException {
    try {
      party.pause();
      updatePartiesCurrentSong(party);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void resumeSongAndUpdate(JsonObject payload, User user, Party party,
      Session session) throws IOException {
    try {
      party.resume();
      updatePartiesCurrentSong(party);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void playPlaylistAndUpdate(JsonObject payload, User user, Party party,
      Session session) throws IOException {
    try {
      int index = payload.get("index").getAsInt();
      party.playPlaylist(index);
      updatePartiesCurrentSong(party);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void updatePartiesCurrentSong(Party party) {
    try {
      JsonObject updatePayload = new JsonObject();
      JsonObject updateMessage = new JsonObject();
      updatePayload.add("currentSong",
          party.getSongBeingCurrentlyPlayed().toJson());
      updateMessage.add("message", updatePayload);
      updateMessage.addProperty("success", true);
      for (Session sesh : partyIdToSessions.get(party.getPartyId())) {
        sesh.getRemote().sendString(updateMessage.toString());
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private void sendReorderPlaylistTrackUpdate(JsonObject payload, User user,
      Party party, Session session) throws IOException {
    int startIndex = payload.get("startIndex").getAsInt();
    int endIndex = payload.get("endIndex").getAsInt();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      boolean success = party.reorderSong(startIndex, endIndex);
      if (success == false) {
        throw new RuntimeException("ERROR: could not add song");
      }
      updatePayload.add("playlist", party.getPlaylistQueueAsJson());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_REARRANGE_PLAYLIST.ordinal());
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

  private void sendUpdateEntireParty(int partyId, Session session)
      throws IOException {
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      partyIdToSessions.put(partyId, session);
      sessionToPartyId.put(session, partyId);
      Party party = Party.of(partyId);
      updatePayload.add("party", party.toJson());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_ENTIRE_PARTY.ordinal());
      updateMessage.addProperty("success", true);
      updateMessage.add("payload", updatePayload);
    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
    }
    session.getRemote().sendString(updateMessage.toString());
  }

  private void sendAddSongDirectlyToPlaylistUpdate(JsonObject payload,
      User user, Party party, Session session) throws IOException {
    String songId = payload.get("songId").getAsString();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      Request newRequest = party.requestSong(Song.of(songId), user);
      boolean success = party.approveSong(newRequest.getId());

      if (newRequest == null || success == false) {
        throw new RuntimeException("ERROR: could not add song");
      }
      updatePayload.add("playlist", party.getPlaylistQueueAsJson());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST.ordinal());
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

  private void sendAddRequestUpdate(JsonObject payload, User user, Party party,
      Session session) throws IOException {
    String songId = payload.get("songId").getAsString();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      Request newRequest = party.requestSong(Song.of(songId), user);
      if (newRequest == null) {
        throw new RuntimeException("ERROR: could not request song");
      }
      updatePayload.add("newRequest", newRequest.toJson());
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
      if (success == false) {
        throw new RuntimeException("ERROR: Cannot vote on request");
      }
      updatePayload.add("requestList", party.getRequestsAsJson());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_VOTE_REQUESTS.ordinal());
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

  private void sendAfterRequestTransferUpdate(JsonObject payload, User user,
      Party party, Session session, TRANSFER_TYPE transferType)
      throws IOException {
    String requestId = payload.get("requestId").getAsString();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      boolean success;
      if (transferType.equals(TRANSFER_TYPE.REQUEST_TO_PLAYLIST)) {
        int insertIndex = payload.get("insertIndex").getAsInt();
        success = party.approveSong(requestId, insertIndex);
      } else {
        assert transferType.equals(TRANSFER_TYPE.PLAYLIST_TO_REQUEST);
        success = party.removeFromPlaylist(requestId);

      }
      System.out.println("success = " + success);
      if (success == false) {
        throw new RuntimeException("ERROR: Cannot vote on request");
      }
      System.out.println("calling get playlist queue as json");
      System.out.println(party.getPlaylistQueueAsJson());
      updatePayload.add("requestList", party.getRequestsAsJson());
      updatePayload.add("playlist", party.getPlaylistQueueAsJson());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_AFTER_REQUEST_TRANSFER.ordinal());

      updateMessage.addProperty("success", true);
      updateMessage.add("payload", updatePayload);
      for (Session sesh : partyIdToSessions.get(party.getPartyId())) {
        sesh.getRemote().sendString(updateMessage.toString());
      }

    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      session.getRemote().sendString(updateMessage.toString());
      e.printStackTrace();
    }
  }
}

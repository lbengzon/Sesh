package edu.brown.cs.am209hhe2lbenzonmsicat.utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.am209hhe2lbenzonmsicat.models.CurrentSongPlaying;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Request;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Request.VoteType;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Song;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyOutOfSyncException;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;

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
    PAUSE_SONG,
    UPDATE_PLAYER,
    SONG_MOVED_TO_NEXT,
    UPDATE_NEXT_CURR_SONG_REQUEST,
    SEEK_SONG,
    RESUME_SONG,
    END_PARTY,
    UPDATE_GUESTS_END_PARTY,
    UPDATE_NEW_USER_JOINED,
    UPDATE_SEND_USER_TO_LOGIN,
    PREV_SONG,
    NEXT_SONG
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
      // Party p = Party.of(partyId);
      System.out.println("Closed Connection");
    }
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    try {
      JsonObject received = GSON.fromJson(message, JsonObject.class);
      int typeInt = received.get("type").getAsInt();
      MESSAGE_TYPE messageType = MESSAGE_TYPE.values()[typeInt];
      System.out.println("Message Recieved" + messageType);
      JsonObject payload = received.get("payload").getAsJsonObject();
      System.out.println(payload);
      String userId = payload.get("userId").getAsString();
      int partyId = payload.get("partyId").getAsInt();
      Party party = Party.of(partyId);
      User user = User.of(userId);
      switch (messageType) {
        case SET_PARTY_ID:
          sendUpdateEntireParty(partyId, user, session,
              MESSAGE_TYPE.SET_PARTY_ID);
          break;
        case ADD_REQUEST:
          sendAddRequestUpdate(payload, user, party, session,
              MESSAGE_TYPE.ADD_REQUEST);
          break;
        case UPVOTE_REQUEST:
          sendVoteRequestUpdate(payload, user, party, session,
              Request.VoteType.upvote, MESSAGE_TYPE.UPVOTE_REQUEST);
          break;
        case DOWNVOTE_REQUEST:
          sendVoteRequestUpdate(payload, user, party, session,
              Request.VoteType.downvote, MESSAGE_TYPE.DOWNVOTE_REQUEST);
          break;
        case MOVE_REQUEST_TO_QUEUE:
          sendAfterRequestTransferUpdate(payload, user, party, session,
              TRANSFER_TYPE.REQUEST_TO_PLAYLIST,
              MESSAGE_TYPE.MOVE_REQUEST_TO_QUEUE);
          break;
        case MOVE_FROM_QUEUE_TO_REQUEST:
          sendAfterRequestTransferUpdate(payload, user, party, session,
              TRANSFER_TYPE.PLAYLIST_TO_REQUEST,
              MESSAGE_TYPE.MOVE_FROM_QUEUE_TO_REQUEST);
          break;
        case ADD_SONG_DIRECTLY_TO_PLAYLIST:
          sendAddSongDirectlyToPlaylistUpdate(payload, user, party, session,
              MESSAGE_TYPE.ADD_SONG_DIRECTLY_TO_PLAYLIST);
          break;
        case REORDER_PLAYLIST_TRACK:
          sendReorderPlaylistTrackUpdate(payload, user, party, session,
              MESSAGE_TYPE.REORDER_PLAYLIST_TRACK);
          break;
        case PLAY_PLAYLIST:
          playPlaylistAndUpdate(payload, user, party, session,
              MESSAGE_TYPE.PLAY_PLAYLIST, false);
          break;
        case NEXT_SONG:
          playPlaylistAndUpdate(payload, user, party, session,
              MESSAGE_TYPE.PLAY_PLAYLIST, false);
          break;
        case PREV_SONG:
          playPlaylistAndUpdate(payload, user, party, session,
              MESSAGE_TYPE.PLAY_PLAYLIST, false);
          break;
        case PAUSE_SONG:
          pauseSongAndUpdate(payload, user, party, session,
              MESSAGE_TYPE.PAUSE_SONG);
          break;
        case SEEK_SONG:
          seekSong(payload, user, party, session, MESSAGE_TYPE.SEEK_SONG);
          break;
        case RESUME_SONG:
          resumeSong(payload, user, party, session, MESSAGE_TYPE.RESUME_SONG);
          break;
        case SONG_MOVED_TO_NEXT:
          updatePartiesCurrentSong(payload, party, session,
              MESSAGE_TYPE.SONG_MOVED_TO_NEXT, true);
          break;
        case END_PARTY:
          endPartyUpdateGuests(payload, user, party, session,
              MESSAGE_TYPE.END_PARTY);
          ;
          break;
        default:
          assert false : "you should never get here!!!";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendRedirectLoginUpdate(Session session) throws IOException {
    JsonObject updateMessage = new JsonObject();
    updateMessage.addProperty("type",
        MESSAGE_TYPE.UPDATE_SEND_USER_TO_LOGIN.ordinal());
    updateMessage.addProperty("success", true);
    session.getRemote().sendString(updateMessage.toString());
  }

  private void endPartyUpdateGuests(JsonObject payload, User user, Party party,
      Session session, MESSAGE_TYPE messageType) throws IOException {
    try {
      boolean shouldUnfollow = payload.get("unfollow").getAsBoolean();

      party.endParty();
      if (shouldUnfollow) {
        party.deletePlaylist();
      }
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_GUESTS_END_PARTY.ordinal());
      updateMessage.addProperty("success", true);

      sendUpdateToEntirePartyExceptSender(session, updateMessage,
          party.getPartyId());

    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void resumeSong(JsonObject payload, User user, Party party,
      Session session, MESSAGE_TYPE messageType) throws IOException {
    try {
      int index = payload.get("index").getAsInt();
      long seekPosition = payload.get("seekPosition").getAsLong();
      party.playPlaylist(index);
      party.seekSong(seekPosition);
      updatePartiesCurrentSong(payload, party, session, messageType, false);
    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void seekSong(JsonObject payload, User user, Party party,
      Session session, MESSAGE_TYPE messageType) throws IOException {
    try {

      long position = payload.get("seekPosition").getAsLong();
      party.seekSong(position);
      updatePartiesCurrentSong(payload, party, session, messageType, false);
    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      e.printStackTrace();
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void pauseSongAndUpdate(JsonObject payload, User user, Party party,
      Session session, MESSAGE_TYPE messageType) throws IOException {
    try {
      party.pause();
      updatePartiesCurrentSong(payload, party, session, messageType, false);
    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void playPlaylistAndUpdate(JsonObject payload, User user, Party party,
      Session session, MESSAGE_TYPE messageType, boolean fixSync)
      throws IOException {
    try {
      int index = payload.get("index").getAsInt();
      if (index >= party.getPlaylist().getSetOfSongs().size()) {
        index = 0;
      }
      party.playPlaylist(index);
      CurrentSongPlaying curr = party.getSongBeingCurrentlyPlayed();
      while (!curr.getSong()
          .equals(party.getPlaylist().getSongs().get(index).getSong())) {
        curr = party.getSongBeingCurrentlyPlayed();
      }
      updatePartiesCurrentSong(payload, party, session, messageType, false);
    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void updatePartiesCurrentSong(JsonObject payload, Party party,
      Session sender, MESSAGE_TYPE messageType, boolean checkSync)
      throws IOException {
    try {
      JsonObject updatePayload = new JsonObject();
      JsonObject updateMessage = new JsonObject();
      CurrentSongPlaying curr = null;
      try {
        curr = party.getSongBeingCurrentlyPlayed();
        System.out.println("===============================");

        if (curr == null) {
          System.out.println("Curr is Null");
          return;
        }
        System.out.println("Real Time passed " + curr.getTimePassed());
        System.out
            .println("Real CurrentSong is playing = " + curr.getIsPlaying());
        String newSongIdPlaying = Request.getId(party.getPartyId(),
            curr.getSong().getSpotifyId());
        System.out.println("SongId currently playing:" + newSongIdPlaying);

        // If you should check for out of sync and you are provided the old song
        // id
        if (checkSync && payload.get("oldSongId") != null) {
          String oldSongId = payload.get("oldSongId").getAsString();
          boolean isPaused = payload.get("isPaused").getAsBoolean();
          long timePassed = payload.get("timePassed").getAsLong();

          int index = payload.get("index").getAsInt();
          // This is the id of the song that is currently playing on spotify

          System.out.println("SongId sent from front end " + oldSongId);
          System.out.println("isPaused front end" + isPaused);
          System.out.println("time passed front end" + timePassed);

          // If the front end thinks the playlist should still be playing
          // and the current song isn't playing it means that the playlist
          // looped
          // around the beginning because it thinks the playlist is over
          if (!curr.getIsPlaying() && !isPaused && curr.getTimePassed() == 0
              && curr.getTimePassed() < timePassed) {
            System.out.println(
                "**********************RELOOPED AROUND?***********************");
            int newIndex = 0;
            // Figure out if infact the playlist really is over. If it isn't,
            // set the new index to the index that should be playing. If not,
            // start from the beginnning
            System.out.println("oldIndex" + index);
            System.out.println(
                "playlist size" + party.getPlaylist().getSetOfSongs().size());
            if (index + 1 < party.getPlaylist().getSetOfSongs().size()) {
              newIndex = index + 1;
            }
            System.out.println("Playing playlist at index " + newIndex);
            party.playPlaylist(newIndex);
            curr = party.getSongBeingCurrentlyPlayed();
            // while (!curr.getSong().equals(
            // party.getPlaylist().getSongs().get(newIndex).getSong())) {
            // curr = party.getSongBeingCurrentlyPlayed();
            // }
            System.out
                .println("new song being played" + curr.getSong().getTitle());

          } // if the playlist hasn't looped around and there are still more
            // songs to be played
          else if (index + 1 < party.getPlaylist().getSetOfSongs().size()) {
            List<Request> playlistSongs = party.getPlaylist().getSongs();
            // Set the new index to be the old index plus one
            int newIndex = index + 1;
            Request realNextSong = playlistSongs.get(newIndex);
            // Check if there was a song change and if what should be playing is
            // what is actually being played
            if ((!oldSongId.equals(newSongIdPlaying)
                && !realNextSong.getId().equals(newSongIdPlaying))) {
              // If there is a mismatch, play the new index of the playlist.
              System.out.println(
                  "**********************WENT TO NEXT SONG and out of sync?***********************");
              System.out.println("Playing playlist at index " + newIndex);
              party.playPlaylist(newIndex);
              curr = party.getSongBeingCurrentlyPlayed();
              // while (!curr.getSong().equals(
              // party.getPlaylist().getSongs().get(newIndex).getSong())) {
              // curr = party.getSongBeingCurrentlyPlayed();
              // }
              System.out
                  .println("new song being played" + curr.getSong().getTitle());
            }
          }
        }
      } catch (SpotifyUserApiException e) {
        sendRedirectLoginUpdate(sender);
      } catch (SpotifyOutOfSyncException e) {
        // Throws an exception if the song being played does not exist in the
        // playlist.
        // We then correct this by playing the next index in the playlist.
        System.out.println("Trying to sync playlist because of exception");
        int index = payload.get("index").getAsInt();
        Set<Request> playlistSongs = party.getPlaylist().getSetOfSongs();
        int newIndex = 0;
        if (index + 1 < playlistSongs.size()) {
          newIndex = index + 1;
        }
        party.playPlaylist(newIndex);
        curr = party.getSongBeingCurrentlyPlayed();
        // while (!curr.getSong()
        // .equals(party.getPlaylist().getSongs().get(newIndex).getSong())) {
        // curr = party.getSongBeingCurrentlyPlayed();
        // }
      }
      if (curr == null) {

        return;
      }
      System.out.println("Finally playing = " + curr.getSong().getTitle());
      String requestId = Request.getId(party.getPartyId(),
          curr.getSong().getSpotifyId());
      System.out.println("===============================");

      updatePayload.addProperty("currentSongId", requestId);
      updatePayload.addProperty("timePassed", curr.getTimePassed());
      updatePayload.addProperty("duration", curr.getDuration());
      updatePayload.addProperty("imageUrl", curr.getImageURL());
      updatePayload.addProperty("songTitle", curr.getSong().getTitle());
      updatePayload.addProperty("albumTitle", curr.getSong().getAlbum());
      updatePayload.addProperty("artist", curr.getSong().getArtist());

      updatePayload.addProperty("isPlaying", curr.getIsPlaying());
      updateMessage.add("payload", updatePayload);
      updateMessage.addProperty("type", MESSAGE_TYPE.UPDATE_PLAYER.ordinal());
      updateMessage.addProperty("success", true);

      JsonObject senderUpdateMessage = new JsonObject();
      senderUpdateMessage.add("payload", updatePayload);
      senderUpdateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_NEXT_CURR_SONG_REQUEST.ordinal());
      senderUpdateMessage.addProperty("success", true);
      sendUpdateToEntireParty(sender, updateMessage, senderUpdateMessage,
          party.getPartyId());

    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(sender);
      return;
    } catch (SpotifyOutOfSyncException e) {
      e.printStackTrace();
      return;
    }
  }

  /**
   * Sends the updatemessage to everyone in the party except the sender. Sends
   * the sender the senderUpdateMessage
   * @param sender
   * @param updateMessage
   * @param senderUpdateMessage
   * @param partyId
   * @throws IOException
   */
  private void sendUpdateToEntireParty(Session sender, JsonObject updateMessage,
      JsonObject senderUpdateMessage, int partyId) throws IOException {
    for (Session sesh : partyIdToSessions.get(partyId)) {
      if (sesh.equals(sender)) {
        sesh.getRemote().sendString(senderUpdateMessage.toString());
      } else {
        sesh.getRemote().sendString(updateMessage.toString());
      }
    }
  }

  /**
   * Sends the updatemessage to everyone in the party except the sender. Sends
   * the sender the senderUpdateMessage
   * @param sender
   * @param updateMessage
   * @param senderUpdateMessage
   * @param partyId
   * @throws IOException
   */
  private void sendUpdateToEntirePartyExceptSender(Session sender,
      JsonObject updateMessage, int partyId) throws IOException {
    for (Session sesh : partyIdToSessions.get(partyId)) {
      if (!sesh.equals(sender)) {
        sesh.getRemote().sendString(updateMessage.toString());
      }
    }
  }

  private void sendReorderPlaylistTrackUpdate(JsonObject payload, User user,
      Party party, Session session, MESSAGE_TYPE messageType)
      throws IOException {
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
    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void sendUpdateEntireParty(int partyId, User user, Session session,
      MESSAGE_TYPE messageType) throws IOException {
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      partyIdToSessions.put(partyId, session);
      sessionToPartyId.put(session, partyId);
      Party party = Party.of(partyId);
      List<Song> favorites = DbHandler
          .GetUserFavoritedSongs(user.getSpotifyId());
      Map<String, Map<String, Object>> favoriteRequestIdToSongMaps = new HashMap<>();
      for (Song song : favorites) {
        String requestId = Request.getId(partyId, song.getSpotifyId());
        favoriteRequestIdToSongMaps.put(requestId, song.toMap());
      }
      updatePayload.add("party", party.toJson());
      updatePayload.add("favorites",
          GSON.toJsonTree(favoriteRequestIdToSongMaps));
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_ENTIRE_PARTY.ordinal());
      updateMessage.addProperty("success", true);
      updateMessage.add("payload", updatePayload);

      JsonObject updatePartyPayload = new JsonObject();
      JsonObject updatePartyMessage = new JsonObject();
      updatePartyPayload.add("newUser", user.toJson());
      updatePartyMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_NEW_USER_JOINED.ordinal());
      updatePartyMessage.addProperty("success", true);
      updatePartyMessage.add("payload", updatePartyPayload);
      sendUpdateToEntirePartyExceptSender(session, updatePartyMessage, partyId);

    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
    }
    session.getRemote().sendString(updateMessage.toString());
  }

  private void sendAddSongDirectlyToPlaylistUpdate(JsonObject payload,
      User user, Party party, Session session, MESSAGE_TYPE messageType)
      throws IOException {
    String songId = payload.get("songId").getAsString();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      Request newRequest = party.requestSong(Song.of(songId), user);
      boolean success = party.approveSong(newRequest.getId());
      if (party.getPlaylist().getSetOfSongs().size() == 1) {
        party.playPlaylist(0);
        party.pause();
      }
      if (newRequest == null || success == false) {
        throw new RuntimeException("ERROR: could not add song");
      }
      updatePayload.add("playlist", party.getPlaylistQueueAsJson());
      updatePayload.addProperty("requestId", newRequest.getId());
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_ADD_SONG_DIRECTLY_TO_PLAYLIST.ordinal());
      updateMessage.addProperty("success", true);
      updateMessage.add("payload", updatePayload);
      for (Session sesh : partyIdToSessions.get(party.getPartyId())) {
        sesh.getRemote().sendString(updateMessage.toString());
      }
    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      String requestId = Request.getId(party.getPartyId(), songId);
      Request request = Request.of(requestId);
      updateMessage.addProperty("requestId", requestId);
      updateMessage.addProperty("type", messageType.ordinal());
      boolean isInRequests = party.getRequestedSongs().contains(request);
      updateMessage.addProperty("location",
          isInRequests ? "requests" : "playlist");
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void sendAddRequestUpdate(JsonObject payload, User user, Party party,
      Session session, MESSAGE_TYPE messageType) throws IOException {
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
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void sendVoteRequestUpdate(JsonObject payload, User user, Party party,
      Session session, Request.VoteType voteType, MESSAGE_TYPE messageType)
      throws IOException {
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
      updatePayload.addProperty("votedUser", user.getSpotifyId());

      Request r = Request.of(requestId);

      if (r.getDownvotes().contains(user) || r.getUpvotes().contains(user)) {
        updatePayload.addProperty("requestIdVotedOn", requestId);
        updatePayload.addProperty("voteType", voteType.toString());
      }
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
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
    }
  }

  private void sendAfterRequestTransferUpdate(JsonObject payload, User user,
      Party party, Session session, TRANSFER_TYPE transferType,
      MESSAGE_TYPE messageType) throws IOException {
    String requestId = payload.get("requestId").getAsString();
    JsonObject updatePayload = new JsonObject();
    JsonObject updateMessage = new JsonObject();
    try {
      boolean success;
      if (transferType.equals(TRANSFER_TYPE.REQUEST_TO_PLAYLIST)) {
        int insertIndex = payload.get("insertIndex").getAsInt();
        success = party.approveSong(requestId, insertIndex);
        if (party.getPlaylist().getSetOfSongs().size() == 1) {
          party.playPlaylist(0);
          party.pause();
        }
      } else {
        assert transferType.equals(TRANSFER_TYPE.PLAYLIST_TO_REQUEST);
        success = party.removeFromPlaylist(requestId);

      }
      if (success == false) {
        throw new RuntimeException("ERROR: Cannot vote on request");
      }

      updatePayload.add("requestList", party.getRequestsAsJson());
      updatePayload.add("playlist", party.getPlaylistQueueAsJson());

      updatePayload.addProperty("transferType", transferType.toString());
      updatePayload.addProperty("requestIdTransferred", requestId);
      updateMessage.addProperty("type",
          MESSAGE_TYPE.UPDATE_AFTER_REQUEST_TRANSFER.ordinal());
      updateMessage.addProperty("success", true);
      updateMessage.add("payload", updatePayload);
      for (Session sesh : partyIdToSessions.get(party.getPartyId())) {
        sesh.getRemote().sendString(updateMessage.toString());
      }

    } catch (SpotifyUserApiException e) {
      sendRedirectLoginUpdate(session);
    } catch (Exception e) {
      updateMessage.addProperty("success", false);
      updateMessage.addProperty("message", e.getMessage());
      updateMessage.addProperty("type", messageType.ordinal());
      session.getRemote().sendString(updateMessage.toString());
      e.printStackTrace();
    }
  }
}

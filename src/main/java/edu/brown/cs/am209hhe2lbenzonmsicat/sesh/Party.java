package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Abstract party class.
 */
public abstract class Party implements Jsonable {

  /**
   * Models party status.
   */
  public enum Status {
    ongoing, stopped
  }

  /**
   * Models attendee type.
   */
  public enum AttendeeType {
    host, guest
  }

  /**
   * @return party id
   */
  public abstract int getPartyId();

  /**
   * @return requested songs
   */
  public abstract Set<Request> getRequestedSongs();

  /**
   * @return requested songs
   */
  public abstract List<Request> getRequestedSongsOrderedByRank();

  /**
   * @return playlist
   */
  public abstract Playlist getPlaylist();

  /**
   * @return guests
   */
  public abstract Set<User> getGuests();

  /**
   * @return The guests and the hosts
   */
  public abstract Set<User> getAttendees();

  /**
   * @return host
   */
  public abstract User getHost();

  /**
   * @return party name
   */
  public abstract String getName();

  /**
   * @return time of party
   */
  public abstract LocalDateTime getTime();

  /**
   * @return party location
   */
  public abstract Coordinate getLocation();

  /**
   * @return Returns the request list as a stringified JSON object that will be
   *         sent to the front end to display.
   */
  public abstract JsonElement getRequestsAsJson();

  /**
   * @return Returns the request list as a stringified JSON object that will be
   *         sent to the front end to display.
   */
  public abstract JsonElement getPlaylistQueueAsJson();

  /**
   * @return party status
   */
  public abstract Status getStatus();

  /**
   * Upvote song. <<<<<<< HEAD ======= >>>>>>>
   * d50b8f626056187d3c69cea53817244af4c7c6f8 <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param user
   *          - to upvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean upvoteSong(User user, String requestId);

  /**
   * Downvote song. <<<<<<< HEAD ======= >>>>>>>
   * d50b8f626056187d3c69cea53817244af4c7c6f8 <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param user
   *          - to downvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean downvoteSong(User user, String requestId);

  /**
   * Approve song. <<<<<<< HEAD ======= >>>>>>>
   * d50b8f626056187d3c69cea53817244af4c7c6f8 <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean approveSong(String requestId);

  /**
   * Approve song.
   *
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean approveSong(String requestId, int index);

  /**
   * Approve song.
   *
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean reorderSong(int startIndex, int endIndex);

  /**
   * @return If a song is being played by the host, this will return the song
   *         being played. If not, this will return null.
   */
  public abstract CurrentSongPlaying getSongBeingCurrentlyPlayed();

  /**
   * Remove from playlist.
   *
   * @param req
   *          - request
   * @return boolean if successful.
   */
  public abstract boolean removeFromPlaylist(String requestId);

  /**
   * Request song.
   *
   * @param song
   *          - request
   * @param user
   *          TODO
   * @return The newly created request
   */
  public abstract Request requestSong(Song song, User user);

  /**
   * Add a guest to party. <<<<<<< HEAD ======= >>>>>>>
   * d50b8f626056187d3c69cea53817244af4c7c6f8 <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param guest
   *          - guest to add
   * @return boolean if successful.
   */
  public abstract boolean addGuest(User guest);

  public abstract String getDeviceId();

  /**
   * Removes a guest from the party <<<<<<< HEAD ======= >>>>>>>
   * d50b8f626056187d3c69cea53817244af4c7c6f8 <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param guest
   *          -Guest to remove
   * @return boolean if successful
   */
  public abstract boolean removeGuest(User guest);

  /**
   * Gets the distance from the party to the coordinate. <<<<<<< HEAD =======
   * >>>>>>> d50b8f626056187d3c69cea53817244af4c7c6f8 <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param coordinate
   *          The coordinate to get the distance from.
   * @return The distance from the coordinate.
   */
  public abstract double getDistance(Coordinate coordinate);

  public abstract boolean playPlaylist(int index);

  public abstract boolean pause();

  // public abstract boolean nextSong();
  //
  // public abstract boolean prevSong();

  /**
   * Ends the party
   */
  public abstract void endParty();

  public boolean isActive() {
    return getStatus().equals(Status.ongoing);
  }

  /**
   * Retrieve party data. <<<<<<< e41b659867a347536c9fbd24f352ea417f562c49
   * ======= >>>>>>> fixed bug related to moving request from playlist to
   * request list
   *
   * @param partyId
   *          - id
   * @param name
   *          - name
   * @param host
   *          - host
   * @param playlist
   *          - playlist
   * @param location
   *          - loc
   * @param time
   *          - time
   * @param status
   *          - status
   * @return party
   */
  public static Party of(int partyId, String name, String playlistId,
      Coordinate location, LocalDateTime time, Status status, String deviceId) {
    if (name == null || playlistId == null || location == null || time == null
        || status == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an part from a null id");
    }
    return new PartyProxy(partyId, name, playlistId, location, time, status,
        deviceId);
  }

  /**
   * Gets the party object with the party id passed in.
   *
   * @param partyId
   *          The id of the party
   * @return The party object representing the party.
   */
  public static Party of(int partyId) {
    try {
      return DbHandler.getPartyFromId(partyId);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Gets the parties within the distance. <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param location
   *          The location of the user.
   * @param distance
   *          The distance within which all parties should be within.
   * @return The list of parties within the distance.
   */
  public static List<Party> getActivePartiesWithinDistance(Coordinate location,
      double distance) {
    try {
      List<Party> parties = DbHandler.getAllActiveParties();
      List<Party> partiesWithinDistance = new ArrayList<>();
      for (Party p : parties) {
        double distanceFromParty = p.getDistance(location);
        if (distanceFromParty <= distance) {
          partiesWithinDistance.add(p);
        }
      }
      // TODO: change this back to partiesWthinDistance when issue is resolved
      return parties;
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Create a party and add to db.
   *
   * @param name
   *          - name
   * @param host
   *          - host
   * @param location
   *          - location
   * @param time
   *          - time
   * @return party
   * @throws SQLException
   *           - exception
   */
  // TODO: Modify parameters to take in Date object for time
  public static Party create(String name, User host, Coordinate location,
      LocalDateTime time, String deviceId) throws SQLException {
    if (name == null || host == null || location == null || time == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an party from a null id");
    }
    String newPlaylistId = Playlist.getNewPlaylistId(host);
    return DbHandler.addParty(newPlaylistId, name, location, time, host,
        deviceId);
  }

  /**
   * Returns the active party of the user if he has any. <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param user
   *          The user you want to get the active party of
   * @return The active party of the user or null if there is no active party.
   */
  public static Party getActivePartyOfUser(User user) {
    try {
      return DbHandler.getActivePartyOfUser(user);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Gets all (active and stopped) parties of a user. <<<<<<< HEAD =======
   * >>>>>>> d50b8f626056187d3c69cea53817244af4c7c6f8 <<<<<<<
   * e41b659867a347536c9fbd24f352ea417f562c49 ======= >>>>>>> fixed bug related
   * to moving request from playlist to request list
   *
   * @param user
   *          The user you want to get the parties of.
   * @return The parties of a user.
   */
  public static List<Party> getAllPartiesOfUser(User user) {
    try {
      return DbHandler.getUsersParties(user);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public boolean equals(Object o) {
    try {
      Party a = (Party) o;
      if (getPartyId() == a.getPartyId()) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getPartyId());
  }

  @Override
  public String toString() {
    return getPartyId() + "";
  }

  public static Gson GSON = new Gson();

}

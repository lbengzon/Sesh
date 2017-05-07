package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import edu.brown.cs.am209hhe2lbenzonmsicat.models.User.Type;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Jsonable;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;

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

  public static enum AccessType {
    PRIVATE, PUBLIC
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
   * @throws SpotifyUserApiException
   */
  public abstract JsonElement getPlaylistQueueAsJson()
      throws SpotifyUserApiException;

  /**
   * @return party status
   */
  public abstract Status getStatus();

  public abstract void deletePlaylist() throws SpotifyUserApiException;

  public abstract void followPlaylist(String userId)
      throws SpotifyUserApiException;

  /**
   * Upvote song.
   *
   * @param user
   *          - to upvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean upvoteSong(User user, String requestId);

  /**
   * Downvote song.
   *
   * @param user
   *          - to downvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean downvoteSong(User user, String requestId);

  /**
   * Approve song.
   *
   * @param req
   *          - request
   * @return boolean if successful
   * @throws SpotifyUserApiException
   */
  public abstract boolean approveSong(String requestId)
      throws SpotifyUserApiException;

  /**
   * Approve song.
   *
   * @param req
   *          - request
   * @return boolean if successful
   * @throws SpotifyUserApiException
   */
  public abstract boolean approveSong(String requestId, int index)
      throws SpotifyUserApiException;

  /**
   * Approve song.
   *
   * @param req
   *          - request
   * @return boolean if successful
   * @throws SpotifyUserApiException
   */
  public abstract boolean reorderSong(int startIndex, int endIndex)
      throws SpotifyUserApiException;

  /**
   * @return If a song is being played by the host, this will return the song
   *         being played. If not, this will return null.
   * @throws SpotifyUserApiException
   */
  public abstract CurrentSongPlaying getSongBeingCurrentlyPlayed()
      throws SpotifyUserApiException;

  /**
   * Remove from playlist.
   *
   * @param req
   *          - request
   * @return boolean if successful.
   * @throws SpotifyUserApiException
   */
  public abstract boolean removeFromPlaylist(String requestId)
      throws SpotifyUserApiException;

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
   * Attempts to add the guest to the party.
   *
   * @param guest
   *          The guest to add to the party.
   * @param accessCode
   *          The access code to the party. Ignored of the party is public
   * @return Whether or not the add guest was successful.
   */
  public abstract boolean addGuest(User guest, String accessCode);

  public abstract String getDeviceId();

  /**
   * Removes a guest from the party.
   *
   * @param guest
   *          -Guest to remove
   * @return boolean if successful
   */
  public abstract boolean removeGuest(User guest);

  /**
   * Gets the distance from the party to the coordinate.
   *
   * @param coordinate
   *          The coordinate to get the distance from.
   * @return The distance from the coordinate.
   */
  public abstract double getDistance(Coordinate coordinate);

  public abstract boolean playPlaylist(int index)
      throws SpotifyUserApiException;

  public abstract boolean pause() throws SpotifyUserApiException;

  public abstract boolean seekSong(long seekPosition)
      throws SpotifyUserApiException;

  public abstract boolean checkAccessCode(String accessCodeAttempt);

  public abstract AccessType getAccessType();

  /**
   * Ends the party
   */
  public abstract void endParty();

  public boolean isActive() {
    return getStatus().equals(Status.ongoing);
  }

  /**
   * Retrieve party data.
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
      Coordinate location, LocalDateTime time, Status status, String deviceId,
      AccessType accessType, String accessCode) {
    if (name == null || playlistId == null || location == null || time == null
        || status == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an part from a null id");
    }
    return new PartyProxy(partyId, name, playlistId, location, time, status,
        deviceId, accessType, accessCode);
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
   * Gets the parties within the distance.
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
   * @throws SpotifyUserApiException
   */
  // TODO: Modify parameters to take in Date object for time
  public static Party create(String name, User host, Coordinate location,
      LocalDateTime time, String deviceId, String seshName,
      AccessType accessType, String accessCode)
      throws SQLException, SpotifyUserApiException {
    if (name == null || host == null || location == null || time == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an party from a null id");
    }
    if (!host.getType().equals(Type.premium)) {
      throw new IllegalArgumentException(
          "ERROR: Can't host a party if you're not premium!");
    }
    String newPlaylistId = Playlist.getNewPlaylistId(host, seshName);
    if (accessType.equals(AccessType.PUBLIC)) {
      accessCode = "";
    } else {
      assert accessCode != null : "cant have a null access code for a private party";
    }
    return DbHandler.addParty(newPlaylistId, name, location, time, host,
        deviceId, accessType, accessCode);
  }

  /**
   * Returns the active party of the user if he has any.
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
   * Gets all (active and stopped) parties of a user.
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

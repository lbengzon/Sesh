package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract party class.
 */
public abstract class Party {

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
  public abstract String getTime();

  /**
   * @return party location
   */
  public abstract Coordinate getLocation();

  /**
   * @return party status
   */
  public abstract Status getStatus();

  /**
   * Upvote song.
   *
   * @param user
   *          - to upvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean upvoteSong(User user, Request req);

  /**
   * Downvote song.
   *
   * @param user
   *          - to downvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean downvoteSong(User user, Request req);

  /**
   * Approve song.
   *
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean approveSong(Request req);

  /**
   * Remove from playlist.
   *
   * @param req
   *          - request
   * @return boolean if successful.
   */
  public abstract boolean removeFromPlaylist(Request req);

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
   * Add a guest to party.
   *
   * @param guest
   *          - guest to add
   * @return boolean if successful.
   */
  public abstract boolean addGuest(User guest);

  /**
   * Removes a guest from the party
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
      Coordinate location, String time, Status status) {
    if (name == null || playlistId == null || location == null || time == null
        || status == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an part from a null id");
    }
    return new PartyProxy(partyId, name, playlistId, location, time, status);
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
        if (p.getDistance(location) <= distance) {
          partiesWithinDistance.add(p);
        }
      }
      return partiesWithinDistance;
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
  public static Party create(String name, User host, Coordinate location,
      String time) throws SQLException {
    if (name == null || host == null || location == null || time == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an party from a null id");
    }
    String newPlaylistId = Playlist.getNewPlaylistId(host);
    return DbHandler.addParty(newPlaylistId, name, location, time, host);
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
}

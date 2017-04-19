package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
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
   * @param user
   *          - to upvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean upvoteSong(User user, Request req);

  /**
   * Downvote song.
   * @param user
   *          - to downvote
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean downvoteSong(User user, Request req);

  /**
   * Approve song.
   * @param req
   *          - request
   * @return boolean if successful
   */
  public abstract boolean approveSong(Request req);

  /**
   * Remove from playlist.
   * @param req
   *          - request
   * @return boolean if successful.
   */
  public abstract boolean removeFromPlaylist(Request req);

  /**
   * Request song.
   * @param song
   *          - request
   * @param user
   *          TODO
   * @return The newly created request
   */
  public abstract Request requestSong(Song song, User user);

  /**
   * Add a guest to party.
   * @param guest
   *          - guest to add
   * @return boolean if successful.
   */
  public abstract boolean addGuest(User guest);

  /**
   * Ends the party
   */
  public abstract void endParty();

  public boolean isActive() {
    return getStatus().equals(Status.ongoing);
  }

  /**
   * Retrieve party data.
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
  public static Party of(int partyId, String name, User host, Playlist playlist,
      Coordinate location, String time, Status status) {
    if (name == null || host == null || playlist == null || location == null
        || time == null || status == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return new PartyProxy(partyId, name, host, playlist, location, time,
        status);
  }

  /**
   * Create a party and add to db.
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
          "ERROR: Trying to create an mapnode from a null id");
    }
    Playlist newPlaylist = Playlist.create(host);
    return DbHandler.addParty(newPlaylist, name, location, time, host);
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

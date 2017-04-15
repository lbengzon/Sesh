package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

public abstract class Party {
  public static enum Status {
    ongoing, stopped
  }

  public static enum AttendeeType {
    host, guest
  }

  public abstract int getPartyId();

  public abstract Set<Request> getRequestedSongs();

  public abstract Playlist getPlaylist();

  public abstract Set<User> getGuests();

  public abstract User getHost();

  public abstract String getName();

  public abstract String getTime();

  public abstract Coordinate getLocation();

  public abstract Status getStatus();

  public abstract boolean upvoteSong(User user, Request req);

  public abstract boolean downvoteSong(User user, Request req);

  public abstract boolean approveSong(Request req);

  public abstract boolean removeFromPlaylist(Request req);

  public abstract boolean requestSong(Request req);

  public abstract boolean addGuest(User guest);

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

  public static Party create(String name, User host, Playlist playlist,
      Coordinate location, String time) throws SQLException {
    if (name == null || host == null || playlist == null || location == null
        || time == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return DbHandler.addParty(playlist.getId(), name, location, time, host);

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

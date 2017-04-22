package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Models a party.
 * @author Matt
 */
public class PartyBean extends Party {
  private int partyId;
  private Playlist playlist;
  private User host;
  private Set<User> guests;
  private Set<Request> requestedSongs;
  private Coordinate location;
  private String name;
  private String time;
  private Status status;

  /**
   * Constructor.
   * @param partyId
   *          - id
   * @param name
   *          - name
   * @param host
   *          - host
   * @param playlist
   *          - playlist
   * @param location
   *          - location
   * @param time
   *          - time
   * @param requestedSongs
   *          - requested songs
   * @param guests
   *          - guests
   * @param status
   *          -status
   */
  public PartyBean(int partyId, String name, User host, Playlist playlist,
      Coordinate location, String time, Set<Request> requestedSongs,
      Set<User> guests, Status status) {
    this.partyId = partyId;
    this.host = host;
    this.guests = guests;
    this.requestedSongs = requestedSongs;
    this.playlist = playlist;
    this.location = location;
    this.name = name;
    this.time = time;
    this.status = status;
  }

  @Override
  public int getPartyId() {
    return partyId;
  }

  @Override
  public boolean upvoteSong(User user, Request req) {
    assert isActive() == true;
    try {
      if (getRequestedSongs().contains(req) && getAttendees().contains(user)) {
        req.upvote(user);
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public boolean downvoteSong(User user, Request req) {
    assert isActive() == true;
    try {
      if (getRequestedSongs().contains(req) && getAttendees().contains(user)) {
        req.downvote(user);
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;

  }

  @Override
  public boolean approveSong(Request req) {
    assert isActive() == true;

    if (!requestedSongs.contains(req)) {
      return false;
    }
    playlist.addSong(req);
    requestedSongs.remove(req);
    return true;
  }

  @Override
  public boolean removeFromPlaylist(Request req) {
    assert isActive() == true;

    if (!playlist.getSongs().contains(req)) {
      return false;
    }
    playlist.removeSong(req);
    requestedSongs.add(req);
    return true;
  }

  @Override
  public Request requestSong(Song song, User user) {
    assert isActive() == true;

    try {
      if (getAttendees().contains(user)) {
        Request newRequest = Request.create(song, user, partyId, "testTime");
        requestedSongs.add(newRequest);
        return newRequest;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    throw new IllegalArgumentException(
        "Error: User who requested song is not a guest");
  }

  @Override
  public Set<Request> getRequestedSongs() {
    return new HashSet<>(requestedSongs);
  }

  @Override
  public Playlist getPlaylist() {
    return playlist;
  }

  @Override
  public Set<User> getGuests() {
    return new HashSet<>(guests);
  }

  @Override
  public User getHost() {
    return host;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getTime() {
    return time;
  }

  @Override
  public Coordinate getLocation() {
    return location;
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public boolean addGuest(User guest) {
    assert isActive() == true;

    if (host.equals(guest)) {
      return false;
    }
    return guests.add(guest);
  }

  @Override
  public void endParty() {
    // TODO Auto-generated method stub
    status = Status.stopped;
  }

  @Override
  public boolean removeGuest(User guest) {
    assert isActive() == true;
    return guests.remove(guest);
  }

  @Override
  public Set<User> getAttendees() {
    Set<User> attendees = getGuests();
    attendees.add(getHost());
    return attendees;
  }

  @Override
  public double getDistance(Coordinate coordinate) {
    return location.getDistanceFrom(coordinate);
  }

}

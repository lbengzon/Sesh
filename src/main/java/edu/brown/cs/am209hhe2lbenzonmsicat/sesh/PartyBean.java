package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;

/**
 * Models a party.
 * @author Matt
 */
public class PartyBean extends Party {
  private int partyId;
  private Playlist playlist;
  private User host;
  private Set<User> guests;
  private Map<String, Request> requestIdToRequest;
  private Coordinate location;
  private String name;
  private LocalDateTime time;
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
      Coordinate location, LocalDateTime time, Set<Request> requestedSongs,
      Set<User> guests, Status status) {
    this.partyId = partyId;
    this.host = host;
    this.guests = guests;
    this.requestIdToRequest = new HashMap<>();
    for (Request request : requestedSongs) {
      requestIdToRequest.put(request.getId(), request);
    }
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
  public boolean upvoteSong(User user, String requestId) {
    assert isActive() == true;
    try {
      Request request = requestIdToRequest.get(requestId);
      if (request != null) {
        request.upvote(user);
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public boolean downvoteSong(User user, String requestId) {
    assert isActive() == true;
    try {
      Request request = requestIdToRequest.get(requestId);
      if (request != null) {
        request.downvote(user);
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;

  }

  @Override
  public boolean approveSong(String requestId) {
    assert isActive() == true;
    Request request = requestIdToRequest.get(requestId);
    if (request == null) {
      return false;
    }
    playlist.addSong(request);
    requestIdToRequest.remove(requestId);
    return true;
  }

  @Override
  public boolean removeFromPlaylist(String requestId) {
    assert isActive() == true;
    Request req = playlist.removeSong(requestId);
    if (req != null) {
      requestIdToRequest.put(req.getId(), req);
    }
    return true;
  }

  @Override
  public Request requestSong(Song song, User user) {
    assert isActive() == true;

    try {
      if (getAttendees().contains(user)) {
        Request newRequest = Request.create(song, user, partyId,
            LocalDateTime.now());
        requestIdToRequest.put(newRequest.getId(), newRequest);
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
    return new HashSet<>(requestIdToRequest.values());
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
  public LocalDateTime getTime() {
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

  @Override
  public JsonElement getRequestsAsJson() {
    Map<String, Object> requestsMap = new HashMap<>();
    for (Request r : requestIdToRequest.values()) {
      requestsMap.put(r.getId(), r.toMap());
    }
    return GSON.toJsonTree(requestsMap);
  }

  @Override
  public JsonElement getPlaylistQueueAsJson() {
    Map<String, Object> requestsMap = new HashMap<>();
    for (Request r : playlist.getSongs()) {
      requestsMap.put(r.getId(), r.toMap());
    }
    return GSON.toJsonTree(requestsMap);
  }

}

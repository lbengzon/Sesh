package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
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
  private Multiset<User> userToNumApprovedRequests;
  private Multiset<User> userToNumTotalRequests;

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

    this.userToNumApprovedRequests = HashMultiset.create();
    this.userToNumTotalRequests = HashMultiset.create();
    for (Request request : requestedSongs) {
      userToNumTotalRequests.add(request.getUserRequestedBy());
      requestIdToRequest.put(request.getId(), request);
    }
    for (Request request : playlist.getSetOfSongs()) {
      userToNumApprovedRequests.add(request.getUserRequestedBy());
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
      if (request != null && getAttendees().contains(user)) {
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
      if (request != null && getAttendees().contains(user)) {
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
    userToNumApprovedRequests.add(request.getUserRequestedBy());
    return true;
  }

  @Override
  public boolean removeFromPlaylist(String requestId) {
    assert isActive() == true;
    Request req = playlist.removeSong(requestId);
    if (req != null) {
      requestIdToRequest.put(req.getId(), req);
      userToNumApprovedRequests.remove(req.getUserRequestedBy());
      return true;
    }
    return false;

  }

  @Override
  public Request requestSong(Song song, User user) {
    assert isActive() == true;

    try {
      if (getAttendees().contains(user)) {
        Request newRequest = Request.create(song, user, partyId,
            LocalDateTime.now());
        requestIdToRequest.put(newRequest.getId(), newRequest);
        userToNumTotalRequests.add(newRequest.getUserRequestedBy());
        return newRequest;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    throw new IllegalArgumentException(
        "Error: User who requested song is not a guest");
  }

  @Override
  public List<Request> getRequestedSongsOrderedByRank() {
    RequestComparator comp = new RequestComparator();
    List<Request> rankedRequests = new ArrayList<>(requestIdToRequest.values());
    Collections.sort(rankedRequests, comp);
    return rankedRequests;
  }

  private class RequestComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
      int numApprovedRequestsOfUser1 = userToNumApprovedRequests
          .count(r1.getUserRequestedBy());
      int numTotalRequestOfUser1 = userToNumTotalRequests
          .count(r1.getUserRequestedBy());
      int numApprovedRequestsOfUser2 = userToNumApprovedRequests
          .count(r2.getUserRequestedBy());
      int numTotalRequestOfUser2 = userToNumTotalRequests
          .count(r2.getUserRequestedBy());
      // double r1Multiplier = numApprovedRequestsOfUser1 /
      // numTotalRequestOfUser1;
      // double r2Multiplier = numApprovedRequestsOfUser2 /
      // numTotalRequestOfUser2;
      Double r1Rank = r1.getRanking();
      Double r2Rank = r2.getRanking();
      return r1Rank.compareTo(r2Rank);
    }

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
    for (Request r : getRequestedSongsOrderedByRank()) {
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

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> partyMap = new HashMap<>();
    partyMap.put("partyId", partyId);
    partyMap.put("playlistQueue", getPlaylistQueueAsJson());
    partyMap.put("requests", getRequestsAsJson());
    partyMap.put("playlistUrl", playlist.getUrl());
    partyMap.put("host", host.toJson());
    partyMap.put("location", GSON.toJsonTree(location));
    partyMap.put("name", name);
    partyMap.put("time", time.toString());
    partyMap.put("status", status);
    return partyMap;
  }

  @Override
  public Set<Request> getRequestedSongs() {
    return new HashSet<>(requestIdToRequest.values());
  }

}

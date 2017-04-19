package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

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
    req.upvote(user);
    return true;
  }

  @Override
  public boolean downvoteSong(User user, Request req) {
    req.downvote(user);
    return true;
  }

  @Override
  public boolean approveSong(Request req) {
    if (!requestedSongs.contains(req)) {
      System.out.println("ERROR: Cannot approve song not in requested list");
      return false;
    }
    playlist.addSong(req);
    requestedSongs.remove(req);
    return true;

  }

  @Override
  public boolean removeFromPlaylist(Request req) {
    if (!playlist.getSongs().contains(req)) {
      System.out.println("ERROR: Cannot remove song not in playlist");
      return false;
    }
    playlist.removeSong(req);
    requestedSongs.add(req);
    return true;
  }

  @Override
  public boolean requestSong(Request req) {
    if (requestedSongs.contains(req)) {
      return false;
    }
    requestedSongs.add(req);
    return true;
  }

  @Override
  public Set<Request> getRequestedSongs() {
    return requestedSongs;
  }

  @Override
  public Playlist getPlaylist() {
    return playlist;
  }

  @Override
  public Set<User> getGuests() {
    return guests;
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
    return false;
  }

  @Override
  public void endParty() {
    // TODO Auto-generated method stub
    status = Status.stopped;
  }

}

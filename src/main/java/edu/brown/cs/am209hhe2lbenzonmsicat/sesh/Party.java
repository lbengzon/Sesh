package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.HashSet;
import java.util.Set;

/**
 * Models a party.
 *
 * @author Matt
 *
 */
public class Party {
  private String id;
  private Playlist playlist;
  private User host;
  private Set<User> guests;
  private Set<Request> requestedSongs;
  private Set<Request> playlistSet;
  // private Location location; Google api stuff?

  public Party(String id, User host) {
    this.id = id;
    this.host = host;
    this.guests = new HashSet<User>();
    this.requestedSongs = new HashSet<Request>();
    this.playlistSet = new HashSet<Request>();
  }

  public boolean upvoteSong(User user, Request req) {
    req.upvote(user);
    return true;
  }

  public boolean downvoteSong(User user, Request req) {
    req.downvote(user);
    return true;

  }

  public boolean approveSong(Request req) {
    if (!requestedSongs.contains(req)) {
      System.out.println("ERROR: Cannot approve song not in requested list");
      return false;
    }
    requestedSongs.remove(req);
    playlistSet.add(req);
    return true;

  }

  public boolean removeFromPlaylist(Request req) {
    if (!playlistSet.contains(req)) {
      System.out.println("ERROR: Cannot remove song not in playlist");
      return false;
    }
    playlistSet.remove(req);
    requestedSongs.add(req);
    return true;

  }

  public boolean requestSong(Request req) { // maybe this method should be in
                                            // User?
    if (requestedSongs.contains(req)) {
      return false;
    }
    requestedSongs.add(req);
    return true;
  }

}

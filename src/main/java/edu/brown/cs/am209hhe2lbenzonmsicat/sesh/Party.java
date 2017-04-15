package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.HashSet;
import java.util.Set;

/**
 * Models a party.
 * @author Matt
 */
public class Party {
  private int id;
  private Playlist playlist;
  private User host;
  private Set<User> guests;
  private Set<Request> requestedSongs;
  // private Location location; Google api stuff?

  public Party(int id, User host, Playlist playlist) {
    this.id = id;
    this.host = host;
    this.guests = new HashSet<User>();
    this.requestedSongs = new HashSet<Request>();
    this.playlist = playlist;
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
    return true;

  }

  public boolean removeFromPlaylist(Request req) {
    if (!playlist.getSongs().contains(req)) {
      System.out.println("ERROR: Cannot remove song not in playlist");
      return false;
    }
    playlist.removeSong(req.getSong());
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

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.server.Authentication.User;

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
  /*
   * TODO: Fill methods in
   */

  public boolean upvoteSong(User user, Request req) {
	  return true;
  }

  public boolean downvoteSong(User user, Request req) {
	  return true;
  }

  public boolean approveSong(Request req) {
	  return true;
  }

  public boolean removeFromPlaylist(Request req) {
	  return true;
  }

  public boolean requestSong(Request req) {
	  return true;
  }

}

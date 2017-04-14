package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.HashSet;
import java.util.Set;

/**
 * Models a song request.
 *
 * @author Matt
 *
 */
public class Request implements Comparable<Request> {
  private String id;
  private Song song;
  private double requestTime;
  private Set<User> upvotes;
  private Set<User> downvotes;

  public Request(String id, double requestTime, Song song) {
    this.id = id;
    this.requestTime = requestTime;
    this.song = song;
    upvotes = new HashSet<User>();
    downvotes = new HashSet<User>();
  }

  /**
   * Upvotes the request.
   *
   * @param user
   *          - user voting
   */
  public void upvote(User user) {
    if (upvotes.contains(user)) {
      upvotes.remove(user);
    } else {
      upvotes.add(user);
      if (downvotes.contains(user)) {
        downvotes.remove(user);
      }
    }
  }

  /**
   * Downvotes the request.
   *
   * @param user
   *          - user voting
   */
  public void downvote(User user) {
    if (downvotes.contains(user)) {
      downvotes.remove(user);
    } else {
      downvotes.add(user);
      if (upvotes.contains(user)) {
        upvotes.remove(user);
      }
    }

  }

  /**
   * Gets the Request's vote count.
   *
   * @return - vote count
   */
  public int voteCount() {
    return upvotes.size() - downvotes.size();
  }

  /**
   * Gets request time.
   *
   * @return - request time
   */
  public double getTime() {
    return requestTime;
  }

  /**
   * Gets song object.
   *
   * @return - song
   */
  public Song getSong() {
    return song;
  }

  @Override
  public int compareTo(Request req) { // may want to modify to a better model so
                                      // we can order
                                      // requests better (divide votecount by
                                      // time elapsed?)
    if (this.voteCount() > req.voteCount()) {
      return 1;
    }
    if (this.voteCount() < req.voteCount()) {
      return -1;
    }
    if (this.getTime() < req.getTime()) {
      return 1;
    }
    if (this.getTime() > req.getTime()) {
      return -1;
    }

    return 0;
  }

  @Override
  public boolean equals(Object o) {
    try {
      Request otherRequest = (Request) o;
      return getSong().equals(otherRequest.getSong());
    } catch (ClassCastException cce) {
      return false;
    }
  }

}

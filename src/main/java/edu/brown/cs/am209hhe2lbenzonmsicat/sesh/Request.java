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

  public void upvote(User user) {
    upvotes.add(user);
  }

  public void downvote(User user) {
    downvotes.add(user);
  }

  public int voteCount() {
    return upvotes.size() - downvotes.size();
  }

  public double getTime() {
    return requestTime;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public int compareTo(Request req) {
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

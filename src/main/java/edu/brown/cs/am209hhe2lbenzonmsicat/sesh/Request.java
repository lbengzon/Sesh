package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Objects;

public abstract class Request implements Comparable<Request> {
  public enum VoteType {
    upvote, downvote
  }

  public abstract int getId();

  public abstract void upvote(User user);

  public abstract void downvote(User user);

  public abstract int voteCount();

  public abstract String getRequestTime();

  public abstract User getUserRequestedBy();

  public abstract Song getSong();

  @Override
  public int compareTo(Request req) { // may want to modify to a better
                                      // model so
    // we can order
    // requests better (divide votecount by
    // time elapsed?)
    if (this.voteCount() > req.voteCount()) {
      return 1;
    }
    if (this.voteCount() < req.voteCount()) {
      return -1;
    }
    // if (this.getTime() < req.getTime()) {
    // return 1;
    // }
    // if (this.getTime() > req.getTime()) {
    // return -1;
    // }

    return 0;
  }

  public static Request of(int id, Song song, User user, String requestTime) {
    if (song == null || user == null || requestTime == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return new RequestProxy(id, song, user, requestTime);
  }

  public static Request create(Song song, User user, String partyId,
      String requestTime) throws SQLException {
    if (song == null || user == null || requestTime == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return DbHandler.requestSong(song.getSpotifyId(), partyId, user,
        requestTime);
  }

  @Override
  public boolean equals(Object o) {
    try {
      Request a = (Request) o;
      if (getId() == a.getId()) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public String toString() {
    return getId() + "";
  }
}

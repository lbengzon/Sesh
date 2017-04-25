package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Request abstract class.
 */
public abstract class Request implements Comparable<Request>, Jsonable {

  /**
   * Vote type enum.
   */
  public enum VoteType {
    upvote, downvote
  }

  /**
   * @return a combination of the song id and the party id which creates a
   *         unique id
   */
  public String getId() {
    return getId(getPartyId(), getSong().getSpotifyId());
  }

  public static String getId(int partyId, String spotifySongId) {
    return partyId + "-" + spotifySongId;
  }

  /**
   * @return the party id
   */
  public abstract int getPartyId();

  /**
   * @param user
   *          - user who is upvoting
   */
  public abstract void upvote(User user);

  /**
   * @param user
   *          - user who is downvoting
   */
  public abstract void downvote(User user);

  /**
   * @param user
   *          - user of vote
   */
  public abstract void removeVote(User user);

  /**
   * @return vote count on this request
   */
  public abstract int voteCount();

  /**
   * @return time of request
   */
  public abstract LocalDateTime getRequestTime();

  /**
   * @return user of request
   */
  public abstract User getUserRequestedBy();

  /**
   * @return requested song
   */
  public abstract Song getSong();

  /**
   * @return set of user upvotes
   */
  public abstract Set<User> getUpvotes();

  /**
   * @return set of user downvotes
   */
  public abstract Set<User> getDownvotes();

  @Override
  public int compareTo(Request req) {
    // may want to modify to a better
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

  /**
   * Get partial request data.
   * @param id
   *          - id
   * @param song
   *          - song
   * @param user
   *          - user
   * @param requestTime
   *          - request time
   * @return requestproxy
   */
  public static Request of(int partyId, Song song, User user,
      LocalDateTime requestTime) {
    if (song == null || user == null || requestTime == null) {
      throw new NullPointerException(
          "ERROR: Trying to create a request from a null id");
    }
    return new RequestProxy(partyId, song, user, requestTime);
  }

  /**
   * Get full request data.
   * @param requestId
   *          - id
   * @param time
   *          - time of request
   * @param song
   *          - song
   * @param user
   *          - user
   * @param upvotes
   *          - set of upvotes
   * @param downvotes
   *          - set of downvotes
   * @return - requestproxy
   */
  public static Request of(int partyId, LocalDateTime time, Song song,
      User user, HashSet<User> upvotes, HashSet<User> downvotes) {
    // TODO Auto-generated method stub
    return new RequestProxy(partyId, time, song, user, upvotes, downvotes);
  }

  /**
   * Create a new request and add to database.
   * @param song
   *          - requested song
   * @param user
   *          - user who made request
   * @param partyId
   *          - party id
   * @param requestTime
   *          - time of request
   * @return the created request
   * @throws SQLException
   *           - exception
   */
  public static Request create(Song song, User user, int partyId,
      LocalDateTime requestTime) throws SQLException {
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
      if (getId().equals(a.getId())) {
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
    return getId();
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Set;

/**
 * Models a song request.
 * @author Matt
 */
public class RequestBean extends Request {
  private int id;
  private Song song;
  private String requestTime;
  private Set<User> upvotes;
  private Set<User> downvotes;
  private User userRequestedBy;

  public RequestBean(int id, String requestTime, Song song,
      User userRequestedBy, Set<User> upvotes, Set<User> downvotes) {
    this.id = id;
    this.requestTime = requestTime;
    this.song = song;
    this.userRequestedBy = userRequestedBy;
    this.upvotes = upvotes;
    this.downvotes = downvotes;
  }

  @Override
  public int getId() {
    return id;
  }

  /**
   * Upvotes the request.
   * @param user
   *          - user voting
   */
  @Override
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
   * @param user
   *          - user voting
   */
  @Override
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
   * Gets the RequestBean's vote count.
   * @return - vote count
   */
  @Override
  public int voteCount() {
    return upvotes.size() - downvotes.size();
  }

  /**
   * Gets request time.
   * @return - request time
   */

  /**
   * Gets song object.
   * @return - song
   */
  @Override
  public Song getSong() {
    return song;
  }

  @Override
  public User getUserRequestedBy() {
    // TODO Auto-generated method stub
    return userRequestedBy;
  }

  @Override
  public String getRequestTime() {
    // TODO Auto-generated method stub
    return requestTime;
  }

  @Override
  public Set<User> getUpvotes() {
    // TODO Auto-generated method stub
    return upvotes;
  }

  @Override
  public Set<User> getDownvotes() {
    // TODO Auto-generated method stub
    return downvotes;
  }

  @Override
  public void removeVote(User user) {
    if (upvotes.contains(user)) {
      upvotes.remove(user);
    } else if (downvotes.contains(user)) {
      downvotes.remove(user);
    }

  }

}

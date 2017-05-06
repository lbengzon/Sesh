package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Models a song request.
 * @author Matt
 */
public class RequestBean extends Request {
  private int partyId;
  private Song song;
  private LocalDateTime requestTime;
  private Set<User> upvotes;
  private Set<User> downvotes;
  private User userRequestedBy;

  /**
   * Constructor.
   * @param id
   *          - id
   * @param requestTime
   *          - time of request
   * @param song
   *          - song associated with request
   * @param userRequestedBy
   *          - which user made the request
   * @param upvotes
   *          - set of users who upvoted this request
   * @param downvotes
   *          - set of users who downvoted this request
   */
  public RequestBean(int partyId, LocalDateTime requestTime, Song song,
      User userRequestedBy, Set<User> upvotes, Set<User> downvotes) {
    this.partyId = partyId;
    this.requestTime = requestTime;
    this.song = song;
    this.userRequestedBy = userRequestedBy;
    this.upvotes = upvotes;
    this.downvotes = downvotes;
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
   * Ranks the request based on the votes and the time it was posted.
   * @return - vote count
   */
  @Override
  public Double getRanking() {
    int x = getVotes();
    long t = getRequestTimeSeconds();
    int y;
    if (x > 0) {
      y = 1;
    } else if (x < 0) {
      y = -1;
    } else {
      y = 0;
    }
    int z = Math.max(Math.abs(x), 1);

    double finalRank = Math.log10(z) + (y * t) / 45000.0;
    return finalRank;
  }

  /**
   * Returns the time in seconds that this request was made
   * @return
   */
  public long getRequestTimeSeconds() {
    ZoneId zoneId = ZoneId.systemDefault();
    long requestTimeSeconds = requestTime.atZone(zoneId).toEpochSecond();
    return requestTimeSeconds;
  }

  /**
   * Returns the difference in votes
   * @return
   */
  public int getVotes() {
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
  public LocalDateTime getRequestTime() {
    // TODO Auto-generated method stub
    return requestTime;
  }

  @Override
  public Set<User> getUpvotes() {
    // TODO Auto-generated method stub
    return new HashSet<>(upvotes);
  }

  @Override
  public Set<User> getDownvotes() {
    // TODO Auto-generated method stub
    return new HashSet<>(downvotes);
  }

  @Override
  public void removeVote(User user) {
    if (upvotes.contains(user)) {
      upvotes.remove(user);
    } else if (downvotes.contains(user)) {
      downvotes.remove(user);
    }

  }

  @Override
  public int getPartyId() {
    return partyId;
  }

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> rMap = new HashMap<>();
    rMap.put("requestId", getId());
    Set<String> upvoteIds = new HashSet<>();
    upvotes.forEach((upvote) -> upvoteIds.add(upvote.getSpotifyId()));
    Set<String> downvoteIds = new HashSet<>();
    downvotes.forEach((downvote) -> downvoteIds.add(downvote.getSpotifyId()));
    rMap.put("upvotes", upvoteIds);
    rMap.put("downvotes", downvoteIds);
    rMap.put("score", getVotes());
    rMap.put("time", requestTime);
    try {
      rMap.put("song", song.toMap());
    } catch (Exception e) {
      rMap.put("song", "DEMO SONG JSON");
    }
    return rMap;
  }

}

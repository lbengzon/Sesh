package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Constants;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;

/**
 * The actor proxy class. Deals with the data base to fetch the data about the
 * actor.
 * @author leandro
 */
public class RequestProxy extends Request implements Proxy {
  private static Map<String, RequestBean> idToRequestCache = new ConcurrentHashMap<>();
  private RequestBean requestBean;
  private int partyId;
  private Song song;
  private LocalDateTime requestTime;
  private User userRequestedBy;

  // private Location location; Google api stuff?

  /**
   * Constructor.
   * @param id
   *          - request id
   * @param song
   *          - song associated with request
   * @param userRequestedBy
   *          - user that made request
   * @param requestTime
   *          - time of request
   */
  public RequestProxy(int partyId, Song song, User userRequestedBy,
      LocalDateTime requestTime) {
    this.partyId = partyId;
    this.song = song;
    this.userRequestedBy = userRequestedBy;
    this.requestTime = requestTime;
  }

  /**
   * Constructor.
   * @param id
   *          - request id
   * @param song
   *          - song associated with request
   * @param userRequestedBy
   *          - user that made request
   * @param requestTime
   *          - time of request
   * @param upvotes
   *          - set of upvotes
   * @param downvotes
   *          - set of downvotes
   */
  public RequestProxy(int partyId, LocalDateTime requestTime, Song song,
      User userRequestedBy, HashSet<User> upvotes, HashSet<User> downvotes) {
    this.partyId = partyId;
    this.song = song;
    this.userRequestedBy = userRequestedBy;
    this.requestTime = requestTime;
    requestBean = new RequestBean(partyId, requestTime, song, userRequestedBy,
        upvotes, downvotes);
  }

  @Override
  public void fillBean() {
    assert requestBean == null;
    // if the actor exists in the cache just use that
    RequestBean request = RequestProxy.idToRequestCache.get(getId());
    if (request != null) {
      requestBean = request;
      return;
    }
    try {
      requestBean = DbHandler.getFullRequest(partyId, song, userRequestedBy,
          requestTime);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idToRequestCache.size() > Constants.MAX_CACHE_SIZE) {
      idToRequestCache.clear();
    }
    assert !idToRequestCache.containsKey(getId());
    idToRequestCache.put(getId(), requestBean);
  }

  @Override
  public boolean isBeanNull() {
    return requestBean == null;
  }

  @Override
  public void upvote(User user) {
    if (requestBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }

    if (!getDownvotes().contains(user) && !getUpvotes().contains(user)) {
      try {
        DbHandler.upvoteRequest(this, user);
      } catch (SQLException e1) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e1.getMessage());
      }
      requestBean.upvote(user);
    } else if (getDownvotes().contains(user)) {
      removeVote(user);
      upvote(user);
    } else if (getUpvotes().contains(user)) {
      removeVote(user);
    }
  }

  @Override
  public void downvote(User user) {
    if (requestBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    if (!getDownvotes().contains(user) && !getUpvotes().contains(user)) {
      try {
        DbHandler.downvoteRequest(this, user);
      } catch (SQLException e1) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e1.getMessage());
      }
      requestBean.downvote(user);
    } else if (getDownvotes().contains(user)) {
      removeVote(user);
    } else if (getUpvotes().contains(user)) {
      removeVote(user);
      downvote(user);
    }

  }

  @Override
  public void removeVote(User user) {
    if (requestBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      DbHandler.removeVote(this, user);
    } catch (SQLException e1) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e1.getMessage());
    }
    requestBean.removeVote(user);
  }

  @Override
  public Double getRanking() {
    if (requestBean != null) {
      return requestBean.getRanking();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return requestBean.getRanking();
  }

  @Override
  public LocalDateTime getRequestTime() {
    // TODO Auto-generated method stub
    return requestTime;
  }

  @Override
  public Song getSong() {
    // TODO Auto-generated method stub
    return song;
  }

  @Override
  public User getUserRequestedBy() {
    // TODO Auto-generated method stub
    return userRequestedBy;
  }

  @Override
  public Set<User> getUpvotes() {
    if (requestBean != null) {
      return requestBean.getUpvotes();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return requestBean.getUpvotes();
  }

  @Override
  public Set<User> getDownvotes() {
    if (requestBean != null) {
      return requestBean.getDownvotes();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return requestBean.getDownvotes();
  }

  /**
   * This method clears the cache.
   */
  public static void clearCache() {
    idToRequestCache.clear();
  }

  @Override
  public int getPartyId() {
    return partyId;
  }

  @Override
  public Map<String, Object> toMap() {
    if (requestBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return requestBean.toMap();
  }

}

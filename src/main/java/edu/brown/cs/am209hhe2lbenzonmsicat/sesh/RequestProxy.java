package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The actor proxy class. Deals with the data base to fetch the data about the
 * actor.
 * @author leandro
 */
public class RequestProxy extends Request implements Proxy {
  private static Map<Integer, RequestBean> idToRequestCache = new ConcurrentHashMap<>();
  private RequestBean requestBean;
  private int id;
  private Song song;
  private String requestTime;
  private User userRequestedBy;

  // private Location location; Google api stuff?

  public RequestProxy(int id, Song song, User userRequestedBy,
      String requestTime) {
    this.id = id;
    this.song = song;
    this.userRequestedBy = userRequestedBy;
    this.requestTime = requestTime;
  }

  @Override
  public void fillBean() {
    assert requestBean == null;
    // if the actor exists in the cache just use that
    RequestBean request = RequestProxy.idToRequestCache.get(id);
    if (request != null) {
      requestBean = request;
      return;
    }
    try {
      requestBean = DbHandler.getFullRequest(id, song, userRequestedBy,
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
    if (idToRequestCache.size() > MAX_CACHE_SIZE) {
      idToRequestCache.clear();
    }
    assert !idToRequestCache.containsKey(id);
    idToRequestCache.put(id, requestBean);
  }

  @Override
  public boolean isBeanNull() {
    return requestBean == null;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void upvote(User user) {
    if (requestBean != null) {
      requestBean.upvote(user);
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    requestBean.upvote(user);
  }

  @Override
  public void downvote(User user) {
    if (requestBean != null) {
      requestBean.downvote(user);
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    requestBean.downvote(user);
  }

  @Override
  public int voteCount() {
    if (requestBean != null) {
      return requestBean.voteCount();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return requestBean.voteCount();
  }

  @Override
  public String getRequestTime() {
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

}

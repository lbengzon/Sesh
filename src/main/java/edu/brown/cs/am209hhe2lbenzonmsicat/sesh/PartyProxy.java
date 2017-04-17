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
public class PartyProxy extends Party implements Proxy {
  private static Map<Integer, PartyBean> idToPartyCache = new ConcurrentHashMap<>();
  private PartyBean partyBean;
  private int partyId;
  private Playlist playlist;
  private User host;
  private Coordinate location;
  private String name;
  private String time;
  private Status status;

  // private Location location; Google api stuff?

  public PartyProxy(int partyId, String name, User host, Playlist playlist,
      Coordinate location, String time, Status status) {
    this.partyId = partyId;
    this.name = name;
    this.host = host;
    this.playlist = playlist;
    this.playlist.setPartyId(partyId);
    this.location = location;
    this.time = time;
    this.status = status;
  }

  public static void clearCache() {
    idToPartyCache.clear();
  }

  @Override
  public void fillBean() {
    assert partyBean == null;
    // if the actor exists in the cache just use that
    PartyBean party = PartyProxy.idToPartyCache.get(partyId);
    if (party != null) {
      partyBean = party;
      return;
    }

    try {
      partyBean = DbHandler.getFullParty(partyId, playlist, name, location,
          time, status);
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
    if (idToPartyCache.size() > MAX_CACHE_SIZE) {
      idToPartyCache.clear();
    }
    assert !idToPartyCache.containsKey(partyId);
    idToPartyCache.put(partyId, partyBean);
  }

  @Override
  public boolean isBeanNull() {
    return partyBean == null;
  }

  @Override
  public int getPartyId() {
    return partyId;
  }

  @Override
  public Set<Request> getRequestedSongs() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getRequestedSongs();
  }

  @Override
  public Playlist getPlaylist() {
    return playlist;
  }

  @Override
  public Set<User> getGuests() {
    // TODO Auto-generated method stub
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getGuests();

  }

  @Override
  public User getHost() {
    // TODO Auto-generated method stub
    return host;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return name;
  }

  @Override
  public String getTime() {
    // TODO Auto-generated method stub
    return time;
  }

  @Override
  public Coordinate getLocation() {
    // TODO Auto-generated method stub
    return location;
  }

  @Override
  public Status getStatus() {
    // TODO Auto-generated method stub
    return status;
  }

  @Override
  public boolean upvoteSong(User user, Request req) {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.upvoteSong(user, req);
  }

  @Override
  public boolean downvoteSong(User user, Request req) {
    if (partyBean != null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.downvoteSong(user, req);
  }

  @Override
  public boolean approveSong(Request req) {
    if (partyBean != null) {
      return partyBean.approveSong(req);
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.approveSong(req);
  }

  @Override
  public boolean removeFromPlaylist(Request req) {
    // try {
    // // DbHandler.requestSong(req);
    // } catch (SQLException e1) {
    // throw new RuntimeException(e1.getMessage());
    // }
    if (partyBean != null) {
      return partyBean.removeFromPlaylist(req);
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.removeFromPlaylist(req);
  }

  @Override
  public boolean requestSong(Request req) {
    if (partyBean != null) {
      return partyBean.requestSong(req);
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.requestSong(req);
  }

  @Override
  public boolean addGuest(User guest) {
    if (partyBean != null) {
      return partyBean.addGuest(guest);
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.addGuest(guest);
  }

}

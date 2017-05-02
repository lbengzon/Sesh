package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;

/**
 * The actor proxy class. Deals with the data base to fetch the data about the
 * actor.
 * @author leandro
 */
public class PartyProxy extends Party implements Proxy {
  private static Map<Integer, PartyBean> idToPartyCache = new ConcurrentHashMap<>();
  private PartyBean partyBean;
  private int partyId;
  private String playlistId;
  private Coordinate location;
  private String name;
  private LocalDateTime time;
  private Status status;

  // private Location location; Google api stuff?

  /**
   * Constructor.
   * @param partyId
   *          - id
   * @param name
   *          - name
   * @param host
   *          - host
   * @param playlist
   *          - playlist
   * @param location
   *          - location
   * @param time
   *          - time
   * @param status
   *          - status
   */
  public PartyProxy(int partyId, String name, String playlistId,
      Coordinate location, LocalDateTime time, Status status) {
    this.partyId = partyId;
    this.name = name;
    this.playlistId = playlistId;
    // this.playlist.setPartyId(partyId);
    this.location = location;
    this.time = time;
    this.status = status;
  }

  /**
   * Clears cache.
   */
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
      partyBean = DbHandler.getFullParty(partyId, playlistId, name, location,
          time, status);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idToPartyCache.size() > Constants.MAX_CACHE_SIZE) {
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
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getPlaylist();
  }

  @Override
  public Set<User> getGuests() {
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
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getHost();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public LocalDateTime getTime() {
    return time;
  }

  @Override
  public Coordinate getLocation() {
    return location;
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public boolean upvoteSong(User user, String requestId) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.upvoteSong(user, requestId);
  }

  @Override
  public boolean downvoteSong(User user, String requestId) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.downvoteSong(user, requestId);
  }

  @Override
  public boolean approveSong(String requestId) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.approveSong(requestId);
  }

  @Override
  public boolean removeFromPlaylist(String requestId) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.removeFromPlaylist(requestId);
  }

  @Override
  public Request requestSong(Song song, User user) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.requestSong(song, user);
  }

  @Override
  public boolean addGuest(User guest) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      DbHandler.addPartyGuest(partyId, guest);
      return partyBean.addGuest(guest);

    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public void endParty() {
    if (partyBean != null) {
      partyBean.endParty();
    }
    try {
      DbHandler.endParty(partyId);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    status = Status.stopped;
  }

  @Override
  public boolean removeGuest(User guest) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      DbHandler.removePartyGuest(partyId, guest);
      return partyBean.removeGuest(guest);
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public Set<User> getAttendees() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getAttendees();
  }

  @Override
  public double getDistance(Coordinate coordinate) {
    return location.getDistanceFrom(coordinate);
  }

  @Override
  public JsonElement getRequestsAsJson() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getRequestsAsJson();
  }

  @Override
  public JsonElement getPlaylistQueueAsJson() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getPlaylistQueueAsJson();
  }

  @Override
  public Map<String, Object> toMap() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.toMap();
  }

  @Override
  public List<Request> getRequestedSongsOrderedByRank() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getRequestedSongsOrderedByRank();
  }

  @Override
  public boolean approveSong(String requestId, int index) {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.approveSong(requestId, index);
  }

  @Override
  public boolean reorderSong(int startIndex, int endIndex) {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.reorderSong(startIndex, endIndex);
  }

  @Override
  public Song getSongBeingCurrentlyPlayed() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getSongBeingCurrentlyPlayed();
  }
}

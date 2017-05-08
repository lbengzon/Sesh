package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Constants;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyOutOfSyncException;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator;

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
  private String deviceId;
  private AccessType accessType;
  private String accessCode;

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
      Coordinate location, LocalDateTime time, Status status, String deviceId,
      AccessType accessType, String accessCode) {
    this.partyId = partyId;
    this.name = name;
    this.playlistId = playlistId;
    // this.playlist.setPartyId(partyId);
    this.location = location;
    this.time = time;
    this.status = status;
    this.deviceId = deviceId;
    this.accessType = accessType;
    this.accessCode = accessCode;
  }

  @Override
  public String getDeviceId() {
    return deviceId;
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
          time, status, deviceId, accessType, accessCode);
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
  public boolean approveSong(String requestId) throws SpotifyUserApiException {
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
  public boolean removeFromPlaylist(String requestId)
      throws SpotifyUserApiException {
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
  public boolean addGuest(User guest, String accessCodeAttempt) {
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
      // If its a private party, check the accesscode
      System.out.println("Access Type = " + accessType);
      if (getAccessType().equals(AccessType.PRIVATE)) {
        System.out.println("ATTEMPT = " + accessCodeAttempt);
        System.out.println("REAL = " + accessCode);

        if (checkAccessCode(accessCodeAttempt)) {
          DbHandler.addPartyGuest(partyId, guest);
          return partyBean.addGuest(guest, accessCodeAttempt);
        }
        return false;
      } else {
        assert getAccessType().equals(AccessType.PUBLIC);
        DbHandler.addPartyGuest(partyId, guest);
        return partyBean.addGuest(guest, accessCodeAttempt);
      }

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
  public JsonElement getPlaylistQueueAsJson() throws SpotifyUserApiException {
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
  public boolean approveSong(String requestId, int index)
      throws SpotifyUserApiException {
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
  public boolean reorderSong(int startIndex, int endIndex)
      throws SpotifyUserApiException {
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
  public CurrentSongPlaying getSongBeingCurrentlyPlayed()
      throws SpotifyUserApiException, SpotifyOutOfSyncException {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getSongBeingCurrentlyPlayed();
  }

  @Override
  public boolean playPlaylist(int index) throws SpotifyUserApiException {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.playPlaylist(index);
  }

  @Override
  public boolean pause() throws SpotifyUserApiException {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.pause();
  }

  @Override
  public boolean seekSong(long seekPosition) throws SpotifyUserApiException {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.seekSong(seekPosition);
  }

  @Override
  public void deletePlaylist() throws SpotifyUserApiException {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    SpotifyCommunicator.unfollowPlaylist(partyBean.getHost().getSpotifyId(),
        partyBean.getHost().getSpotifyId(), playlistId, true);
  }

  @Override
  public void followPlaylist(String userId) throws SpotifyUserApiException {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    SpotifyCommunicator.followPlaylist(userId,
        partyBean.getHost().getSpotifyId(), playlistId, true);
  }

  @Override
  public boolean checkAccessCode(String accessCodeAttempt) {
    return accessCode.equals(accessCodeAttempt);
  }

  @Override
  public AccessType getAccessType() {
    return accessType;
  }

  // @Override
  // public boolean nextSong() {
  // if (partyBean == null) {
  // try {
  // fill();
  // } catch (SQLException e) {
  // throw new RuntimeException(e.getMessage());
  // }
  // }
  // return partyBean.nextSong();
  // }
  //
  // @Override
  // public boolean prevSong() {
  // if (partyBean == null) {
  // try {
  // fill();
  // } catch (SQLException e) {
  // throw new RuntimeException(e.getMessage());
  // }
  // }
  // return partyBean.prevSong();
  // }
}

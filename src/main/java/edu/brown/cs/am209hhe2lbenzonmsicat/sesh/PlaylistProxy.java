package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Playlist proxy class.
 */
public class PlaylistProxy extends Playlist implements Proxy {
  private static Map<String, PlaylistBean> idToPlaylistCache = new ConcurrentHashMap<>();
  private PlaylistBean playlistBean;
  private String spotifyId;
  private int partyId;
  private User host;

  /**
   * Constructor.
   *
   * @param spotifyId
   *          - playlist id
   * @param partyId
   *          - party id
   * @param host
   *          the host
   */
  public PlaylistProxy(String spotifyId, int partyId, User host) {
    this.spotifyId = spotifyId;
    this.partyId = partyId;
    this.host = host;
  }

  // /**
  // * Constructor.
  // * @param spotifyId
  // * - id
  // */
  // public PlaylistProxy(String spotifyId, User host) {
  // this.spotifyId = spotifyId;
  // this.host = host;
  // }
  //
  // @Override
  // public void setPartyId(int partyId) {
  // this.partyId = partyId;
  // }

  /**
   * Clears the cache.
   */
  public static void clearCache() {
    idToPlaylistCache.clear();
  }

  @Override
  public String getId() {
    return spotifyId;
  }

  @Override
  public String getUrl() {
    if (playlistBean != null) {
      return playlistBean.getUrl();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return playlistBean.getUrl();
  }

  @Override
  public List<Request> getSongs() {

    if (playlistBean != null) {
      return new ArrayList<>(playlistBean.getQueuedRequests().values());
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    // TODO ADD API REQUEST HERE
    // TODO make a request to the spotify api to get the list of songs, then
    // sort that requsts the same way that the songs in the spotify playlists
    // are
    List<String> songs = SpotifyCommunicator
        .getPlaylistTracks(host.getSpotifyId(), this.spotifyId);
    List<Request> results = new ArrayList<Request>();
    return new ArrayList<>(playlistBean.getQueuedRequests().values());
  }

  @Override
  public boolean removeSong(Request request) {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      // TODO ADD API REQUEST HERE

      DbHandler.moveSongRequestOutOfQueue(request);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e.getMessage());
    }
    return playlistBean.removeSong(request);
  }

  @Override
  public boolean addSong(Request request) {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      // TODO ADD API REQUEST HERE

      DbHandler.moveSongRequestToQueue(request);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    return playlistBean.addSong(request);
  }

  @Override
  public void fillBean() {
    assert playlistBean == null;
    // if the actor exists in the cache just use that
    PlaylistBean playlist = idToPlaylistCache.get(spotifyId);
    if (playlist != null) {
      playlistBean = playlist;
      return;
    }
    try {
      playlistBean = DbHandler.getQueuedSongsForParty(spotifyId, partyId);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idToPlaylistCache.size() > Constants.MAX_CACHE_SIZE) {
      idToPlaylistCache.clear();
    }
    assert !idToPlaylistCache.containsKey(spotifyId);
    idToPlaylistCache.put(spotifyId, playlistBean);
  }

  @Override
  public boolean isBeanNull() {
    return playlistBean == null;
  }

}

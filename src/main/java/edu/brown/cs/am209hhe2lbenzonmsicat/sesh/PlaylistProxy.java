package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wrapper.spotify.models.PlaylistTrackPosition;

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
    List<Request> results = new ArrayList<Request>();
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
    List<Song> songs = SpotifyCommunicator
        .getPlaylistTracks(host.getSpotifyId(), this.spotifyId);
    Map<Song, Request> map = playlistBean.getQueuedRequests();
    for (Song s : songs) {
      results.add(map.get(s));
    }

    return results;
  }

  @Override
  public Request removeSong(String requestId) {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      // TODO ADD API REQUEST HERE
      int[] positions = new int[10];
      List<Request> reqs = this.getSongs();
      Request request = getRequest(requestId);
      assert request != null;
      int pos = reqs.indexOf(request);
      positions[0] = pos;
      StringBuilder sb = new StringBuilder();
      sb.append("spotify:track:");
      sb.append(request.getSong().getSpotifyId());
      PlaylistTrackPosition ptp = new PlaylistTrackPosition(sb.toString(),
          positions);
      List<PlaylistTrackPosition> listOfTrackPositions = new ArrayList<PlaylistTrackPosition>();
      listOfTrackPositions.add(ptp);
      SpotifyCommunicator.removeTrack(host.getSpotifyId(), this.spotifyId,
          listOfTrackPositions);
      DbHandler.moveSongRequestOutOfQueue(request);
      return playlistBean.removeSong(requestId);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public Request getRequest(String requestId) {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return playlistBean.getRequest(requestId);
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
      StringBuilder sb = new StringBuilder();
      sb.append("spotify:track:");
      sb.append(request.getSong().getSpotifyId());
      List<String> uris = new ArrayList<String>();
      uris.add(sb.toString());
      System.out.println("adding track and the id is " + this.spotifyId);
      SpotifyCommunicator.addTrack(host.getSpotifyId(), this.spotifyId, uris);
      DbHandler.moveSongRequestToQueue(request);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    return playlistBean.addSong(request);
  }

  @Override
  public void fillBean() {
    assert playlistBean == null;
    // if the playlist exists in the cache just use that
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

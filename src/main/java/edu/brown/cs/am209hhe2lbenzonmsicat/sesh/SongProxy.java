package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wrapper.spotify.models.Track;

/**
 * The actor proxy class. Deals with the data base to fetch the data about the
 * actor.
 * @author leandro
 */
public class SongProxy extends Song implements Proxy {
  private static Map<String, SongBean> idSongCache = new ConcurrentHashMap<>();
  private SongBean songBean;
  private String spotifyId;

  /**
   * Constructor.
   * @param spotifyId
   *          - spotify id
   */
  public SongProxy(String spotifyId) {
    this.spotifyId = spotifyId;
  }

  public SongProxy(String spotifyId, String name, String album, String artist,
      double length) {
    this.spotifyId = spotifyId;
    this.fillBean();
  }

  @Override
  public void fillBean() {
    assert songBean == null;
    // if the song exists in the cache just use that
    SongBean song = idSongCache.get(spotifyId);
    if (song != null) {
      songBean = song;
      return;
    }
    try {
      // TODO songBean = some api call here
      Track t = SpotifyCommunicator.getTrack(this.spotifyId);
      String id = t.getId();
      String title = t.getName();
      String album = t.getAlbum().getName();
      String artist = t.getArtists().get(0).getName();
      double length = t.getDuration();
      songBean = new SongBean(id, title, album, artist, length);

    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idSongCache.size() > Constants.MAX_CACHE_SIZE) {
      idSongCache.clear();
    }
    assert !idSongCache.containsKey(spotifyId);
    idSongCache.put(spotifyId, songBean);
  }

  @Override
  public boolean isBeanNull() {
    return songBean == null;
  }

  @Override
  public String getSpotifyId() {
    return spotifyId;
  }

  @Override
  public String getTitle() {
    if (songBean != null) {
      return songBean.getTitle();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return songBean.getTitle();
  }

  @Override
  public String getAlbum() {
    if (songBean != null) {
      return songBean.getAlbum();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return songBean.getAlbum();
  }

  @Override
  public String getArtist() {
    if (songBean != null) {
      return songBean.getArtist();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return songBean.getArtist();
  }

  @Override
  public double getLength() {
    if (songBean != null) {
      return songBean.getLength();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return songBean.getLength();
  }

  @Override
  public Map<String, Object> toMap() {
    if (songBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return songBean.toMap();
  }

}

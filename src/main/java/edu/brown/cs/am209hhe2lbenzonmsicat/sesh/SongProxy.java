package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The actor proxy class. Deals with the data base to fetch the data about the
 * actor.
 *
 * @author leandro
 */
public class SongProxy extends Song implements Proxy {
  private static Map<String, SongBean> idToSongCache = new ConcurrentHashMap<>();
  private SongBean songBean;
  private String spotifyId;

  /**
   * Constructor.
   *
   * @param spotifyId
   *          - spotify id
   */
  public SongProxy(String spotifyId) {
    this.spotifyId = spotifyId;
  }

  @Override
  public void fillBean() {
    assert songBean == null;
    // if the actor exists in the cache just use that
    SongBean song = idToSongCache.get(spotifyId);
    if (song != null) {
      songBean = song;
      return;
    }
    try {
      // TODO: songBean = some api call here
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idToSongCache.size() > MAX_CACHE_SIZE) {
      idToSongCache.clear();
    }
    assert !idToSongCache.containsKey(spotifyId);
    idToSongCache.put(spotifyId, songBean);
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

}

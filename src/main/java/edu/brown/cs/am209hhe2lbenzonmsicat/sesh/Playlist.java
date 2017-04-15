package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.List;

/**
 * Wrapper for a Spotify playlist.
 * @author Matt
 */
public class Playlist {
  private String id; // youtube/spotify id
  private String url;

  public Playlist(String id) {
    this.setId(id);
    this.setUrl("find out the actual structure " + id);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<SongBean> getSongs() {
    // TODO:MAKE API CALL
    return null;
  }

  public boolean removeSong(Song song) {
    return false;
  }

  public boolean addSong(Song song) {
    return false;
  }

}

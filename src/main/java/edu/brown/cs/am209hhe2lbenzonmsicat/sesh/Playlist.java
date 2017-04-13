package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

/**
 * Wrapper for a Spotify playlist.
 *
 * @author Matt
 *
 */
public class Playlist {
  private String id; // youtube/spotify id
  private String url;

  public Playlist(String id, String url) {
    this.setId(id);
    this.setUrl(url);
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

}

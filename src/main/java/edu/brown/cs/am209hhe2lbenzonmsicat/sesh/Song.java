package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Objects;

/**
 * Song abstract class.
 */
public abstract class Song implements Jsonable {

  /**
   * @return spotify id
   */
  public abstract String getSpotifyId();

  /**
   * @return title
   */
  public abstract String getTitle();

  /**
   * @return album
   */
  public abstract String getAlbum();

  /**
   * @return artist
   */
  public abstract String getArtist();

  /**
   * @return length
   */
  public abstract double getLength();

  /**
   * @param spotifyId
   *          - spotify id
   * @return song, null if id doesn't exist.
   */
  public static Song of(String spotifyId) {
    if (spotifyId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create a song from a null id");
    }
    return new SongProxy(spotifyId);
  }

  public static Song of(String spotifyId, String name, String album,
      String artist, double length) {
    if (spotifyId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create a song from a null id");
    }
    return new SongProxy(spotifyId, name, album, artist, length);
  }

  @Override
  public boolean equals(Object o) {
    try {
      Song a = (Song) o;
      if (getSpotifyId() == a.getSpotifyId()) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getSpotifyId());
  }

  @Override
  public String toString() {
    return getSpotifyId() + "";
  }
}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Objects;

public abstract class Song {

  public abstract String getSpotifyId();

  public abstract String getTitle();

  public abstract String getAlbum();

  public abstract String getArtist();

  public abstract double getLength();

  public static Song of(String spotifyId) {
    if (spotifyId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return new SongProxy(spotifyId);
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

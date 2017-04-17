package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.List;
import java.util.Objects;

public abstract class Playlist {

  public abstract String getId();

  public abstract String getUrl();

  public abstract List<Request> getSongs();

  public abstract boolean removeSong(Request request);

  public abstract boolean addSong(Request request);

  public abstract void setPartyId(int partyId);

  public static Playlist of(String spotifyId) {
    return new PlaylistProxy(spotifyId);
  }

  public static Playlist Create(User user) {
    String spotifyId = "test";// TODO: MAKE API CALL TO CREATE NEW PLAYLIST and
                              // get the spotify id

    return new PlaylistProxy(spotifyId);
  }

  @Override
  public boolean equals(Object o) {
    try {
      Playlist a = (Playlist) o;
      if (getId() == a.getId()) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public String toString() {
    return getId() + "";
  }

}

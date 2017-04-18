package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.List;
import java.util.Objects;

/**
 * Abstract playlist class.
 *
 * @author HE23
 *
 */
public abstract class Playlist {

  /**
   * @return id
   */
  public abstract String getId();

  /**
   * @return url
   */
  public abstract String getUrl();

  /**
   * @return requested songs
   */
  public abstract List<Request> getSongs();

  /**
   * @param request
   *          - to remove
   * @return boolean if successful
   */
  public abstract boolean removeSong(Request request);

  /**
   * @param request
   *          - to add
   * @return boolean if successful
   */
  public abstract boolean addSong(Request request);

  /**
   * @param partyId
   *          - to set
   */
  public abstract void setPartyId(int partyId);

  /**
   * This should only be used for testing!
   *
   * @param spotifyId
   *          - spotify id
   * @param partyId
   *          - party id
   * @param host
   *          the host user
   * @return Playlist
   */
  public static Playlist of(String spotifyId, int partyId, User host) {
    return new PlaylistProxy(spotifyId, partyId, host);
  }

  /**
   * @param spotifyId
   *          - id
   * @return playlist of that id
   */
  public static Playlist of(String spotifyId) {
    return new PlaylistProxy(spotifyId);
  }

  /**
   * Add playlist to database.
   *
   * @param user
   *          - user
   * @return playlist
   */
  public static Playlist create(User user) {
    String spotifyId = "test";
    // TODO MAKE API CALL TO CREATE NEW PLAYLIST and
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

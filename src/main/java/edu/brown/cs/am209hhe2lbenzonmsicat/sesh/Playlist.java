package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract playlist class.
 *
 * @author HE23
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
   * @return ordered list of requested songs
   */
  public abstract List<Request> getSongs();

  /**
   * @return the set of songs
   */
  public abstract Set<Request> getSetOfSongs();

  /**
   * @param request
   *          - to remove
   * @return boolean if successful
   */
  public abstract Request removeSong(String requestId);

  public abstract Request getRequest(String requestId);

  public abstract CurrentSongPlaying getCurrentSong();

  public abstract void play(int offset);

  public abstract void pause();

  public abstract void nextSong();

  public abstract void prevSong();

  public abstract void seek(int position_ms, String deviceId);

  /**
   * @param request
   *          - to add
   * @return boolean if successful
   */
  public abstract boolean addSong(Request request);

  public abstract boolean addSongInPosition(Request request, int pos);

  public abstract void reorderPlaylist(int rangeStart, int insertBefore);

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
   * Add playlist to database.
   *
   * @param user
   *          - user
   * @return playlist
   */
  public static Playlist create(int partyId, User user) {
    String spotifyId = "test";
    // TODO MAKE API CALL TO CREATE NEW PLAYLIST and
    // get the spotify id

    return new PlaylistProxy(spotifyId, partyId, user);
  }

  public static String getNewPlaylistId(User user) {
    // TODO MAKE API CALL TO CREATE NEW PLAYLIST and
    // get the spotify id
    String id = SpotifyCommunicator.createPlaylist(user.getSpotifyId(),
        "testTitle", true);
    return id;
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

package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator;

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
   * @throws SpotifyUserApiException
   */
  public abstract List<Request> getSongs() throws SpotifyUserApiException;

  /**
   * @return the set of songs
   */
  public abstract Set<Request> getSetOfSongs();

  /**
   * @param request
   *          - to remove
   * @return boolean if successful
   * @throws SpotifyUserApiException
   */
  public abstract Request removeSong(String requestId)
      throws SpotifyUserApiException;

  public abstract Request getRequest(String requestId);

  public abstract CurrentSongPlaying getCurrentSong()
      throws SpotifyUserApiException;

  public abstract void play(int offset, String deviceId)
      throws SpotifyUserApiException;

  public abstract void pause(String deviceId) throws SpotifyUserApiException;

  // public abstract void nextSong(String deviceId);
  //
  // public abstract void prevSong(String deviceId);

  public abstract void seek(long position_ms, String deviceId)
      throws SpotifyUserApiException;

  /**
   * @param request
   *          - to add
   * @return boolean if successful
   * @throws SpotifyUserApiException
   */
  public abstract boolean addSong(Request request)
      throws SpotifyUserApiException;

  public abstract boolean addSongInPosition(Request request, int pos)
      throws SpotifyUserApiException;

  public abstract void reorderPlaylist(int rangeStart, int insertBefore)
      throws SpotifyUserApiException;

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

  public static String getNewPlaylistId(User user, String seshName)
      throws SpotifyUserApiException {
    // TODO MAKE API CALL TO CREATE NEW PLAYLIST and
    // get the spotify id
    String id = SpotifyCommunicator.createPlaylist(user.getSpotifyId(),
        seshName, true);
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

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Playlist bean class.
 */
public class PlaylistBean extends Playlist {
  private String id; // youtube/spotify id
  private String url;
  private Map<Song, Request> queuedRequests;
  private Map<String, Request> requestIdToRequest;

  private int partyId;

  /**
   * Constructor.
   *
   * @param id
   *          - playlist id
   * @param partyId
   *          - party id
   * @param queuedRequests
   *          - queued requests
   */
  public PlaylistBean(String id, int partyId, List<Request> queuedRequests,
      User host) {
    this.setId(id);
    StringBuilder sb = new StringBuilder();
    sb.append("https://embed.spotify.com/?uri=spotify%3Auser%3A");
    sb.append(host.getSpotifyId());
    sb.append("%3Aplaylist%3A");
    sb.append(id);
    // String link = "https://embed.spotify,com/?uri=spotify:user:"
    this.setUrl(sb.toString());
    this.partyId = partyId;
    this.queuedRequests = new HashMap<>();
    requestIdToRequest = new HashMap<>();
    for (Request req : queuedRequests) {
      this.queuedRequests.put(req.getSong(), req);
      this.requestIdToRequest.put(req.getId(), req);
    }
  }

  @Override
  public String getId() {
    return id;
  }

  /**
   * Set id.
   *
   * @param id
   *          - id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getUrl() {
    return url;
  }

  /**
   * @param url
   *          - to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public List<Request> getSongs() {
    return new ArrayList<>(queuedRequests.values());
  }

  /**
   * @return queued requests
   */
  public Map<Song, Request> getQueuedRequests() {
    return queuedRequests;
  }

  @Override
  public Request removeSong(String requestId) {
    Request toRemove = requestIdToRequest.remove(requestId);
    return queuedRequests.remove(toRemove.getSong());
  }

  @Override
  public boolean addSongInPosition(Request request, int pos) {
    queuedRequests.put(request.getSong(), request);

    requestIdToRequest.put(request.getId(), request);
    return true;
  }

  @Override
  public Request getRequest(String requestId) {
    return requestIdToRequest.get(requestId);
  }

  @Override
  public boolean addSong(Request request) {
    queuedRequests.put(request.getSong(), request);

    requestIdToRequest.put(request.getId(), request);
    return true;
  }

  @Override
  public Set<Request> getSetOfSongs() {
    return new HashSet<>(queuedRequests.values());
  }

  @Override
  public void reorderPlaylist(int rangeStart, int insertBefore) {

  }

  @Override
  public Song getCurrentSong() {
    return null;
  }

}

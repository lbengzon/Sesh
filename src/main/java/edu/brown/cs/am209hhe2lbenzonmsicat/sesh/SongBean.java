package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.HashMap;
import java.util.Map;

/**
 * Models a song.
 * @author Matt
 */
public class SongBean extends Song {
  private String spotifyId;
  private String title;
  private String album;
  private String artist;
  private double length;

  /**
   * Constructor.
   * @param spotifyId
   *          - spotify id
   * @param title
   *          - title
   * @param album
   *          - album
   * @param artist
   *          - artist
   * @param length
   *          - length
   */
  public SongBean(String spotifyId, String title, String album, String artist,
      double length) {
    this.spotifyId = spotifyId;
    this.title = title;
    this.album = album;
    this.artist = artist;
    this.length = length;
  }

  @Override
  public String getSpotifyId() {
    return spotifyId;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getAlbum() {
    return album;
  }

  @Override
  public String getArtist() {
    return artist;
  }

  @Override
  public double getLength() {
    return length;
  }

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> songMap = new HashMap<>();
    songMap.put("spotifyId", spotifyId);
    songMap.put("title", title);
    songMap.put("album", album);
    songMap.put("artist", artist);
    songMap.put("length", length);
    return songMap;
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

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
    // TODO Auto-generated method stub
    return title;
  }

  @Override
  public String getAlbum() {
    // TODO Auto-generated method stub
    return album;
  }

  @Override
  public String getArtist() {
    // TODO Auto-generated method stub
    return artist;
  }

  @Override
  public double getLength() {
    // TODO Auto-generated method stub
    return length;
  }

}

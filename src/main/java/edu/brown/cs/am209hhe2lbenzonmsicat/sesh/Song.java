package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

public class Song {
  private String id; // id to song (spotify id? or youtube lmk)
  private String songName;
  private double length;

  public Song(String id, String songName, double length) {
    this.id = id;
    this.songName = songName;
    this.length = length;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    try {
      Song otherSong = (Song) o;
      return getId().equals(otherSong.getId());
    } catch (ClassCastException cce) {
      return false;
    }
  }

}

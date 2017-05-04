package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

public class CurrentSongPlaying {

  private Song song;
  private long timeLeft;
  private String imageURL;
  private long duration;

  public CurrentSongPlaying(Song s, long d, long t, String image) {
    song = s;
    timeLeft = t;
    imageURL = image;
    duration = d;
  }

  public Song getSong() {
    return song;
  }

  public long getTimeLeft() {
    return timeLeft;
  }

  public String getImageURL() {
    return imageURL;
  }

  public long getDuration() {
    return duration;
  }

}

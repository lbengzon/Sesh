package edu.brown.cs.am209hhe2lbenzonmsicat.models;

public class CurrentSongPlaying {

  private Song song;
  private long timePassed;
  private String imageURL;
  private long duration;
  private boolean isPlaying;

  public CurrentSongPlaying(Song s, long d, long t, String image,
      boolean isPlaying) {
    song = s;
    timePassed = t;
    imageURL = image;
    duration = d;
    this.isPlaying = isPlaying;
  }

  public Song getSong() {
    return song;
  }

  public long getTimeLeft() {
    return duration - timePassed;
  }

  public long getTimePassed() {
    return timePassed;
  }

  public String getImageURL() {
    return imageURL;
  }

  public long getDuration() {
    return duration;
  }

  public boolean getIsPlaying() {
    return isPlaying;
  }

}

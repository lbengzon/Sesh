package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.Serializable;

public class Coordinate implements Serializable {
  private double lat;
  private double lon;

  public Coordinate(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.Serializable;

/**
 * Models a coordinate.
 *
 *
 */
public class Coordinate implements Serializable {
  private double lat;
  private double lon;

  /**
   * Constructor.
   *
   * @param lat
   *          - lat
   * @param lon
   *          - lon
   */
  public Coordinate(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  /**
   * @return lat
   */
  public double getLat() {
    return lat;
  }

  /**
   * @return lon
   */
  public double getLon() {
    return lon;
  }

}

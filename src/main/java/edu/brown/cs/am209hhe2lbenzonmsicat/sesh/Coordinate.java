package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.Serializable;

/**
 * Models a coordinate.
 */
public class Coordinate implements Serializable {
  private double lat;
  private double lon;

  private static final int EARTH_RADIUS = 6371;

  /**
   * Constructor.
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

  /**
   * Returns the distance between this location and the location passed in.
   * @param location
   *          The other location.
   * @return Distance in Meters.
   */
  public double getDistanceFrom(Coordinate location) {
    double latDistance = Math.toRadians(location.getLat() - lat);
    double lonDistance = Math.toRadians(location.getLon() - lon);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat))
            * Math.cos(Math.toRadians(location.getLat()))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = EARTH_RADIUS * c * 1000;
    distance = Math.pow(distance, 2);
    return Math.sqrt(distance);
  }

}

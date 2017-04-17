package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Objects;

public abstract class User {

  public abstract String getSpotifyId();

  public abstract String getEmail();

  public abstract String getFirstName();

  /**
   * This method gets the user's last name.
   * @return user's last name.
   */
  public abstract String getLastName();

  /**
   * This method gets the user's full name.
   * @return the user's full name.
   */
  public abstract String getFullName();

  public static User of(String spotifyId) {
    if (spotifyId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return new UserProxy(spotifyId);
  }

  public static User of(String spotifyId, String email, String name) {
    if (spotifyId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return new UserProxy(spotifyId, email, name);
  }

  public static User create(String userId, String email, String name)
      throws SQLException {
    if (userId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return DbHandler.addUser(userId, email, name);
  }

  @Override
  public boolean equals(Object o) {
    try {
      User a = (User) o;
      if (getSpotifyId().equals(a.getSpotifyId())) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getSpotifyId());
  }

  @Override
  public String toString() {
    return getSpotifyId() + "";
  }

}

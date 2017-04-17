package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Objects;

/**
 * User abstract class.
 *
 * @author HE23
 *
 */
public abstract class User {

  /**
   * @return spotify id.
   */
  public abstract String getSpotifyId();

  /**
   * @return email
   */
  public abstract String getEmail();

  /**
   * @return first name
   */
  public abstract String getFirstName();

  /**
   * This method gets the user's last name.
   *
   * @return user's last name.
   */
  public abstract String getLastName();

  /**
   * This method gets the user's full name.
   *
   * @return the user's full name.
   */
  public abstract String getFullName();

  /**
   * User of.
   *
   * @param spotifyId
   *          - id
   * @return User, null if id doesn't exist.
   */
  public static User of(String spotifyId) {
    if (spotifyId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create a user from a null id");
    }
    return new UserProxy(spotifyId);
  }

  /**
   * Retrieve user data.
   *
   * @param spotifyId
   *          - id
   * @param email
   *          - email
   * @param name
   *          - name
   * @return User
   */
  public static User of(String spotifyId, String email, String name) {
    if (spotifyId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create an mapnode from a null id");
    }
    return new UserProxy(spotifyId, email, name);
  }

  /**
   * Create user.
   *
   * @param userId
   *          - id
   * @param email
   *          - email
   * @param name
   *          - name
   * @return User or null if id doesn't exist
   * @throws SQLException
   *           - SQLException
   */
  public static User create(String userId, String email, String name)
      throws SQLException {
    if (userId == null) {
      throw new NullPointerException(
          "ERROR: Trying to create a user from a null id");
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

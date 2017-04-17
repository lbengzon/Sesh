package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

/**
 * Models an arbitrary Sesh user.
 * @author Matt
 */
public class UserBean extends User {
  private String spotifyId; // spotify username?
  private String email;
  private String firstName;
  private String lastName;

  /**
   * This is the constructor for the User class.
   * @param id
   *          user id
   * @param email
   *          user email
   * @param firstName
   *          user's first name
   * @param lastName
   *          user's last name
   */
  public UserBean(String spotifyId, String email, String name) {
    this.spotifyId = spotifyId;
    this.email = email;
    String[] names = name.split(" ");
    this.firstName = names[0];
    this.lastName = names[1];

  }

  /**
   * This method gets the email of the user.
   * @return the user's email
   */
  @Override
  public String getEmail() {
    return email;
  }

  /**
   * This method gets the id of the user.
   * @return the user's id
   */
  @Override
  public String getSpotifyId() {
    return spotifyId;
  }

  /**
   * This method gets the user's first name.
   * @return user's first name.
   */
  @Override
  public String getFirstName() {
    return firstName;
  }

  /**
   * This method gets the user's last name.
   * @return user's last name.
   */
  @Override
  public String getLastName() {
    return lastName;
  }

  /**
   * This method gets the user's full name.
   * @return the user's full name.
   */
  @Override
  public String getFullName() {
    return firstName + " " + lastName;
  }

  public static User ofId(String id) {
    return null;
  }

}

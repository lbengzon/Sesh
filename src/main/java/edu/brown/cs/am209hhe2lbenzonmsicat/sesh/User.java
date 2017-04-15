package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

/**
 * Models an arbitrary Sesh user.
 * @author Matt
 */
public class User {
  private String id; // spotify username?
  private String email;
  private String[] name;

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
  public User(String id, String email, String firstName, String lastName) {
    this.id = id;
    this.email = email;
    this.name[0] = firstName;
    this.name[1] = lastName;
  }

  /**
   * This method gets the email of the user.
   * @return the user's email
   */
  public String getEmail() {
    return email;
  }

  /**
   * This method gets the id of the user.
   * @return the user's id
   */
  public String getId() {
    return id;
  }

  /**
   * This method gets the user's first name.
   * @return user's first name.
   */
  public String getFirstName() {
    return name[0];
  }

  /**
   * This method gets the user's last name.
   * @return user's last name.
   */
  public String getLastName() {
    return name[1];
  }

  /**
   * This method gets the user's full name.
   * @return the user's full name.
   */
  public String getFullName() {
    return name[0] + " " + name[1];
  }

  public static User ofId(String id) {
    return null;
  }

}

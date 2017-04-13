package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

public class User {
  private String id; // spotify username?
  private String email;
  private String[] name;

  public User(String id, String email, String firstName, String lastName) {
    this.id = id;
    this.email = email;
    this.name[0] = firstName;
    this.name[1] = lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return name[0];
  }

  public String getLastName() {
    return name[1];
  }

  public String getFullName() {
    return name[0] + " " + name[1];
  }

}

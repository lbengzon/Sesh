package edu.brown.cs.am209hhe2lbenzonmsicat.models;

public class Device {

  private String id;
  private String type;
  private String name;
  private boolean isActive;

  public Device(String i, String t, String n, boolean active) {
    id = i;
    type = t;
    name = n;
    isActive = active;
  }

  public String getId() {
    return id;

  }

  public String getType() {
    return type;

  }

  public String getName() {
    return name;

  }

  public boolean getActive() {
    return isActive;
  }

}

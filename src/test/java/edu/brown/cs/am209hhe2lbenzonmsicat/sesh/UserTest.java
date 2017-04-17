package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.junit.Test;

public class UserTest {

  @Test
  public void testGetSpotifyId() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();

    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    UserProxy.clearCache();

    assert l.getSpotifyId().equals("lbengzon");
  }

  @Test
  public void testGetEmail() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();

    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    UserProxy.clearCache();

    assert l.getEmail().equals("leandro.bengzon@gmail.com");
  }

  @Test
  public void testGetFirstName() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();

    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    UserProxy.clearCache();
    assert l.getFirstName().equals("Leandro");
  }

  @Test
  public void testGetLastName() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();

    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    UserProxy.clearCache();

    assert l.getLastName().equals("Bengzon");
  }

  @Test
  public void testGetFullName() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();

    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    UserProxy.clearCache();

    assert l.getFullName().equals("Leandro Bengzon");
  }

  @Test
  public void testOfString() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();
    User.create("lbengzon", "leandro.bengzon@gmail.com", "Leandro Bengzon");
    UserProxy.clearCache();
    User l = User.of("lbengzon");
    assert l.getSpotifyId().equals("lbengzon");
    assert l.getFirstName().equals("Leandro");
    assert l.getEmail().equals("leandro.bengzon@gmail.com");
    assert l.getFullName().equals("Leandro Bengzon");
    assert l.getLastName().equals("Bengzon");
  }

  @Test(expected = RuntimeException.class)
  public void testOfWithNoCreate() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();
    User l = User.of("lbengzon");
    assert l.getSpotifyId().equals("lbengzon");
    assert l.getFirstName().equals("Leandro");
    assert l.getEmail().equals("leandro.bengzon@gmail.com");
    assert l.getFullName().equals("Leandro Bengzon");
    assert l.getLastName().equals("Bengzon");
  }

  @Test
  public void testOfStringStringString()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    UserProxy.clearCache();
    User l = User.of("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    assert l.getSpotifyId().equals("lbengzon");
    assert l.getFirstName().equals("Leandro");
    assert l.getEmail().equals("leandro.bengzon@gmail.com");
    assert l.getFullName().equals("Leandro Bengzon");
    assert l.getLastName().equals("Bengzon");
  }

  @Test
  public void testEqualsObject() throws SQLException, FileNotFoundException {
    UserProxy.clearCache();
    assert User.of("lbengzon").equals(User.of("lbengzon"));
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.junit.Test;

public class RequestTest {

  @Test
  public void testGetId() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, "playlistId", new Coordinate(1, 1),
        "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    RequestProxy.clearCache();
    assert r.getSong().equals(Song.of("song1"));
    assert r.getRequestTime().equals("testTime");
    assert r.getUserRequestedBy().equals(l);
  }

  @Test
  public void testUpvote() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, "playlistId", new Coordinate(1, 1),
        "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.upvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    System.out.println(r1.getUpvotes());
    assert r1.getUpvotes().contains(l);
    // this shouldn't be working?

  }

  @Test
  public void testDownvote() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testVoteCount() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testGetRequestTime() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testGetUserRequestedBy()
      throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testGetSong() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testGetUpvotes() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testGetDownvotes() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testOfIntSongUserString()
      throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testOfIntStringSongUserHashSetOfUserHashSetOfUser()
      throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testCreate() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

}

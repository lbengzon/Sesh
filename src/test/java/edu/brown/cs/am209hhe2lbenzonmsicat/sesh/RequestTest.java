package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class RequestTest {

  @Test
  public void testGetId() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    assert r.getSong().equals(Song.of("song1"));
    assert r.getRequestTime().equals("testTime");
    assert r.getUserRequestedBy().equals(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    assert r1.getSong().equals(Song.of("song1"));
    assert r1.getRequestTime().equals("testTime");
    assert r1.getUserRequestedBy().equals(l);
  }

  @Test
  public void testUpvote() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.upvote(l);
    assert r.getUpvotes().contains(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    assert r1.getUpvotes().contains(l);
    // TODO: Do this type of thing for every proxy
  }

  @Test
  public void testUpvoteBySameUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    try {
      r.upvote(l);
      r.upvote(l);
      assert (false);
    } catch (Exception e) {
      RequestProxy.clearCache();
      Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
      assert r1.getUpvotes().contains(l);
      assert r1.getUpvotes().size() == 1;
    }
  }

  @Test
  public void testDownvote() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.downvote(l);
    assert r.getDownvotes().contains(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    assert r1.getDownvotes().contains(l);
  }

  @Test
  public void testDownvoteBySameUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    try {
      r.downvote(l);
      r.downvote(l);
      assert (false);

    } catch (Exception e) {
      RequestProxy.clearCache();
      Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
      assert r1.getDownvotes().contains(l);
      assert r1.getDownvotes().size() == 1;
    }
  }

  @Test
  public void testUpDownvoteBySameUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    try {
      r.downvote(l);
      r.upvote(l);
      assert (false);
    } catch (Exception e) {
      RequestProxy.clearCache();
      Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
      assert r1.getDownvotes().contains(l);
      assert r1.getDownvotes().size() == 1;
      assert r1.getUpvotes().size() == 0;
    }
  }

  @Test
  public void testRemoveVote() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.downvote(l);
    r.removeVote(l);
    r.upvote(l);
    assert r.getDownvotes().size() == 0;
    assert r.getUpvotes().size() == 1;
    assert r.getUpvotes().contains(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    assert r1.getDownvotes().size() == 0;
    assert r1.getUpvotes().size() == 1;
    assert r1.getUpvotes().contains(l);
  }

  @Test
  public void testVoteCount() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    System.out.println(r1.voteCount());
    assert r1.voteCount() == -1;
  }

  @Test
  public void testGetRequestTime() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    assert r1.getRequestTime().equals("testTime");
  }

  @Test
  public void testGetUserRequestedBy()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    assert r1.getUserRequestedBy().equals(l);
  }

  @Test
  public void testGetSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    assert r1.getSong().equals(Song.of("song1"));
  }

  @Test
  public void testOfIntStringSongUserHashSetOfUserHashSetOfUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = Request.of(1, "testTime", Song.of("song1"), l,
        new HashSet<User>(Arrays.asList(l)), new HashSet<User>());
    r1.getUpvotes().contains(l);
  }

}

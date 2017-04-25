package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.sqlite.SQLiteException;

/**
 * This class tests the request class.
 *
 * @author Ali
 */
public class RequestTest {

  /**
   * This test the getID function.
   *
   * @throws SQLException
   *           if db messes up
   * @throws FileNotFoundException
   *           if db not found
   */
  @Test
  public void testGetId() throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    assert r.getSong().equals(Song.of("7AQAlklmptrrkBSeujkXsD"));
    assert r.getRequestTime().equals(LocalDateTime.now());
    assert r.getUserRequestedBy().equals(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getSong().equals(Song.of("7AQAlklmptrrkBSeujkXsD"));
    assert r1.getRequestTime().equals(LocalDateTime.now());
    assert r1.getUserRequestedBy().equals(l);
  }

  @Test(expected = SQLiteException.class)
  public void testRequestSameSongToParty()
      throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    Request r1 = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
  }

  @Test
  public void testRequestSameSongToDifferentParty()
      throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    User l1 = User.create("1185743437", "hannahhe97@yahoo.com.au", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Party p1 = Party.create("Dope Party", l1, new Coordinate(1, 1),
        LocalDateTime.now());

    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    Request r1 = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p1.getPartyId(), LocalDateTime.now());
  }

  @Test
  public void testUpvote() throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.upvote(l);
    assert r.getUpvotes().contains(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getUpvotes().contains(l);
    // TODO: Do this type of thing for every proxy
  }

  @Test
  public void testUpvoteBySameUser()
      throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.upvote(l);
    r.upvote(l);
    assert r.getUpvotes().size() == 0;
    assert r.getDownvotes().size() == 0;

    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("song1"), l,
        LocalDateTime.now());
    assert r1.getUpvotes().size() == 0;
    assert r1.getDownvotes().size() == 0;

  }

  @Test
  public void testDownvote() throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    assert r.getDownvotes().contains(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getDownvotes().contains(l);
  }

  @Test
  public void testDownvoteBySameUser()
      throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    r.downvote(l);
    assert r.getDownvotes().size() == 0;

    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getDownvotes().size() == 0;

  }

  @Test
  public void testUpDownvoteBySameUser()
      throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    r.upvote(l);
    assert r.getDownvotes().size() == 0;
    assert r.getUpvotes().size() == 1;
    assert r.getUpvotes().contains(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getDownvotes().size() == 0;
    assert r1.getUpvotes().size() == 1;
    assert r1.getUpvotes().contains(l);

  }

  @Test
  public void testRemoveVote() throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    r.removeVote(l);
    r.upvote(l);
    assert r.getDownvotes().size() == 0;
    assert r.getUpvotes().size() == 1;
    assert r.getUpvotes().contains(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getDownvotes().size() == 0;
    assert r1.getUpvotes().size() == 1;
    assert r1.getUpvotes().contains(l);
  }

  @Test
  public void testVoteCount() throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.voteCount() == -1;
  }

  @Test
  public void testGetRequestTime() throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getRequestTime().equals(LocalDateTime.now());
  }

  @Test
  public void testGetUserRequestedBy()
      throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getUserRequestedBy().equals(l);
  }

  @Test
  public void testGetSong() throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getSong().equals(Song.of("7AQAlklmptrrkBSeujkXsD"));
  }

  @Test
  public void testOfIntStringSongUserHashSetOfUserHashSetOfUser()
      throws SQLException, FileNotFoundException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now());
    Request r1 = Request.of(1, LocalDateTime.now(),
        Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        new HashSet<User>(Arrays.asList(l)), new HashSet<User>());
    r1.getUpvotes().contains(l);
  }

}

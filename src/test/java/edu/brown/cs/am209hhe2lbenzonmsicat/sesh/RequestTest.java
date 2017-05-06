package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.sqlite.SQLiteException;

import edu.brown.cs.am209hhe2lbenzonmsicat.models.Coordinate;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.PartyProxy;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Request;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.RequestProxy;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Song;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator;

/***
 * This
 *
 * class tests the request class.*
 *
 * @author Ali
 */
public class RequestTest {

  /**
   * This test the getID function.
   *
   * @throws SQLException
   *           if db messes up
   * @throws FileNotFoundException,
   *           SpotifyUserApiException if db not found
   */
  @Test
  public void testGetId()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    assert r.getSong().equals(Song.of("7AQAlklmptrrkBSeujkXsD"));
    assert r.getUserRequestedBy().equals(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getSong().equals(Song.of("7AQAlklmptrrkBSeujkXsD"));
    assert r1.getUserRequestedBy().equals(l);
  }

  @Test(expected = SQLiteException.class)
  public void testRequestSameSongToParty()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    //
    Request r1 = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
  }

  @Test
  public void testRequestSameSongToDifferentParty()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    User l1 = User.create("1185743437", "hannahhe97@yahoo.com.au", "Hannah He",
        "deviceId");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
    Party p1 = Party.create("Dope Party", l1, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");

    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    Request r1 = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p1.getPartyId(), LocalDateTime.now());
  }

  @Test
  public void testUpvote()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
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
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
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
  public void testDownvote()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
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
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
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
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
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
  public void testRemoveVote()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
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
  public void testVoteCount() throws SQLException, FileNotFoundException,
      SpotifyUserApiException, InterruptedException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now().minusMinutes(100));
    r.downvote(l);

    Request r1 = Request.create(Song.of("7zye9v6B785eFWEFYs13C2"), l,
        p.getPartyId(), LocalDateTime.now());
    r1.downvote(l);
    RequestProxy.clearCache();
    Request r2 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, r.getRequestTime());
    Request r3 = Request.of(r1.getPartyId(), Song.of("7zye9v6B785eFWEFYs13C2"),
        l, r1.getRequestTime());
    assert r2.getRanking() > r3.getRanking();
    // assert r1.getRanking() == -1;
  }

  @Test
  public void testGetRequestTime()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    RequestProxy.clearCache();
    LocalDateTime now = LocalDateTime.now();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, now);
    assert r1.getRequestTime().equals(now);
  }

  @Test
  public void testGetUserRequestedBy()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    r.downvote(l);
    RequestProxy.clearCache();
    Request r1 = Request.of(r.getPartyId(), Song.of("7AQAlklmptrrkBSeujkXsD"),
        l, LocalDateTime.now());
    assert r1.getUserRequestedBy().equals(l);
  }

  @Test
  public void testGetSong()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
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
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpPublicApi();
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "deviceId");

    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId");
    Request r1 = Request.of(1, LocalDateTime.now(),
        Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        new HashSet<User>(Arrays.asList(l)), new HashSet<User>());
    r1.getUpvotes().contains(l);
  }

}

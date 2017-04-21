package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.junit.Test;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Party.Status;

public class PartyTest {

  @Test
  public void testGetHost() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 2), "time");
    assert p.getHost().equals(l);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getHost().equals(l);
  }

  @Test
  public void testGetName() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 2), "time");
    assert p.getName().equals("Dope Party");

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getName().equals("Dope Party");
  }

  @Test
  public void testGetTime() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 2), "time");
    assert p.getTime().equals("time");

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getTime().equals("time");
  }

  @Test
  public void testGetLocation() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 2), "time");
    assert p.getLocation().getLat() == 1;
    assert p.getLocation().getLon() == 2;

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getLocation().getLat() == 1;
    assert p1.getLocation().getLon() == 2;
  }

  @Test
  public void testGetStatus() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.getStatus() == Status.ongoing;

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getStatus() == Status.ongoing;
  }

  @Test
  public void testEndParty() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.endParty();
    assert p.getStatus() == Status.stopped;

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getStatus() == Status.stopped;
  }

  @Test
  public void testEndPartyTwice() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.endParty();
    p.endParty();
    assert p.getStatus() == Status.stopped;

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getStatus() == Status.stopped;
  }

  @Test
  public void testUpvoteDownvoteSongBySameUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(a);
    p.addGuest(m);
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.upvoteSong(a, r1) == true;
    assert p.downvoteSong(a, r1) == true;

    assert ((Request) (p.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 0;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
        .contains(a);
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 1;

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 0;
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
        .contains(a);
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 1;
  }

  @Test
  public void testUpvoteSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(a);
    p.addGuest(m);
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.upvoteSong(a, r1) == true;
    assert p.upvoteSong(m, r1) == true;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 2;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getUpvotes()
        .contains(m);
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getUpvotes()
        .contains(a);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 2;
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getUpvotes()
        .contains(m);
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getUpvotes()
        .contains(a);
  }

  @Test
  public void testUpvoteSongSameUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(a);
    p.addGuest(m);
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.upvoteSong(a, r1) == true;
    assert p.upvoteSong(a, r1) == true;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 0;
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 0;
  }

  @Test
  public void testUpvoteSongByNonGuest()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.upvoteSong(a, r1) == false;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 0;
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getUpvotes()
        .size() == 0;
  }

  @Test
  public void testUpvoteSongAfterTransfer()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    assert p.upvoteSong(a, r1) == false;
    assert p.getRequestedSongs().isEmpty();

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getPlaylist().getSongs().get(0).getUpvotes().isEmpty();
  }

  @Test
  public void testUpvoteSongPartyEnded()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.endParty();
    try {
      p.upvoteSong(a, r1);
      assert false;
    } catch (IllegalStateException e) {
      assert ((Request) (p.getRequestedSongs().toArray()[0])).getUpvotes()
          .isEmpty();
      RequestProxy.clearCache();
      PartyProxy.clearCache();
      Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
          p.getLocation(), p.getTime(), p.getStatus());
      assert ((Request) (p1.getRequestedSongs().toArray()[0])).getUpvotes()
          .isEmpty();
    }
  }

  @Test
  public void testDownvoteSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(a);
    p.addGuest(m);
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.downvoteSong(a, r1) == true;
    assert p.downvoteSong(m, r1) == true;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 2;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
        .contains(m);
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
        .contains(a);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 2;
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
        .contains(m);
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
        .contains(a);
  }

  @Test
  public void testDownvoteSongSameUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(a);
    p.addGuest(m);
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.downvoteSong(a, r1) == true;
    assert p.downvoteSong(a, r1) == true;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 0;
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 0;
  }

  @Test
  public void testDownvoteSongByNonGuest()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.downvoteSong(a, r1) == false;
    assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 0;
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
        .size() == 0;
  }

  @Test
  public void testDownvoteSongAfterTransfer()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    assert p.downvoteSong(a, r1) == false;
    assert p.getRequestedSongs().isEmpty();

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getPlaylist().getSongs().get(0).getDownvotes().isEmpty();
  }

  @Test
  public void testDownvoteSongPartyEnded()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.endParty();
    try {
      p.downvoteSong(a, r1);
      assert false;
    } catch (IllegalStateException e) {
      assert ((Request) (p.getRequestedSongs().toArray()[0])).getDownvotes()
          .isEmpty();
      RequestProxy.clearCache();
      PartyProxy.clearCache();
      Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
          p.getLocation(), p.getTime(), p.getStatus());
      assert ((Request) (p1.getRequestedSongs().toArray()[0])).getDownvotes()
          .isEmpty();
    }
  }

  @Test
  public void testRemoveFromPlaylist()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    p.removeFromPlaylist(r1);
    assert p.getRequestedSongs().contains(r1);
    assert !p.getPlaylist().getSongs().contains(r1);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getRequestedSongs().contains(r1);
    assert !p1.getPlaylist().getSongs().contains(r1);
  }

  @Test
  public void testRemoveFromPlaylistPartyEnded()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    p.endParty();
    try {
      p.removeFromPlaylist(r1);
      assert false;
    } catch (IllegalStateException e) {
      assert p.getRequestedSongs().isEmpty();
      assert p.getPlaylist().getSongs().size() == 1;
      assert p.getPlaylist().getSongs().contains(r1);
      RequestProxy.clearCache();
      PartyProxy.clearCache();
      Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
          p.getLocation(), p.getTime(), p.getStatus());
      assert p1.getRequestedSongs().isEmpty();
      assert p1.getPlaylist().getSongs().size() == 1;
      assert p1.getPlaylist().getSongs().contains(r1);
    }
  }

  @Test
  public void testRemovePlaylistButInRequest()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.removeFromPlaylist(r1) == false;
    assert p.getRequestedSongs().contains(r1);
    assert !p.getPlaylist().getSongs().contains(r1);
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getRequestedSongs().contains(r1);
    assert !p1.getPlaylist().getSongs().contains(r1);
  }

  @Test
  public void testRemovePlaylistButNonExistentRequest()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    PlaylistProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    Party p1 = Party.create("Dope Party1", m, new Coordinate(1, 1), "time");
    assert p1.getRequestedSongs().size() == 0;
    assert p1.removeFromPlaylist(r1) == false;
    assert p.getRequestedSongs().contains(r1);
    assert !p.getPlaylist().getSongs().contains(r1);
    assert p1.getRequestedSongs().size() == 0;
    assert p1.getPlaylist().getSongs().size() == 0;

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p2 = Party.of(p1.getPartyId(), p1.getName(), p1.getPlaylist().getId(),
        p1.getLocation(), p1.getTime(), p1.getStatus());
    assert p2.getRequestedSongs().size() == 0;
    assert p2.getPlaylist().getSongs().size() == 0;
  }

  @Test
  public void testRequestSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    p.addGuest(m);
    p.addGuest(a);
    Request r1 = p.requestSong(Song.of("song1"), h);
    Request r2 = p.requestSong(Song.of("song2"), m);
    Request r3 = p.requestSong(Song.of("song3"), a);
    assert p.getRequestedSongs().contains(r1);
    assert p.getRequestedSongs().contains(r2);
    assert p.getRequestedSongs().contains(r3);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getRequestedSongs().contains(r1);
    assert p1.getRequestedSongs().contains(r2);
    assert p1.getRequestedSongs().contains(r3);
  }

  @Test
  public void testRequestSongPartyEnded()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    p.endParty();
    try {
      p.requestSong(Song.of("song1"), h);
      assert false;
    } catch (IllegalStateException e) {
      assert p.getRequestedSongs().isEmpty();
      RequestProxy.clearCache();
      PartyProxy.clearCache();
      Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
          p.getLocation(), p.getTime(), p.getStatus());
      assert p1.getRequestedSongs().isEmpty();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRequestSongByNonGuest()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.requestSong(Song.of("song1"), h);
  }

  @Test(expected = RuntimeException.class)
  public void testRequestSameSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.requestSong(Song.of("song1"), h);
    p.requestSong(Song.of("song1"), m);
  }

  @Test
  public void testApproveSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    assert !p.getRequestedSongs().contains(r1);
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert !p1.getRequestedSongs().contains(r1);
    assert p1.getPlaylist().getSongs().contains(r1);
  }

  @Test
  public void testApproveSongPartyEnded()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    PlaylistProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.endParty();

    try {
      p.approveSong(r1);
      assert false;
    } catch (IllegalStateException e) {
      assert p.getRequestedSongs().size() == 1;
      assert p.getRequestedSongs().contains(r1);
      assert p.getPlaylist().getSongs().isEmpty();
      RequestProxy.clearCache();
      PartyProxy.clearCache();
      Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
          p.getLocation(), p.getTime(), p.getStatus());
      assert p1.getRequestedSongs().size() == 1;
      assert p1.getRequestedSongs().contains(r1);
      assert p1.getPlaylist().getSongs().isEmpty();
    }
  }

  @Test(expected = RuntimeException.class)
  public void testApproveReRequestSong()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    p.requestSong(Song.of("song1"), m);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddHostAsGuest() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");

    assert p.addGuest(l) == false;
  }

  @Test
  public void testAddGuest() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");

    assert p.addGuest(h) == true;
    assert p.addGuest(a) == true;
    assert p.addGuest(m) == true;
    assert p.getGuests().contains(h);
    assert p.getGuests().contains(a);
    assert p.getGuests().contains(m);
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getGuests().contains(h);
    assert p1.getGuests().contains(a);
    assert p1.getGuests().contains(m);
  }

  @Test
  public void testAddGuestPartyEnded()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.endParty();
    try {
      p.addGuest(h);
      assert false;
    } catch (IllegalStateException e) {
      assert p.getGuests().isEmpty();
      RequestProxy.clearCache();
      PartyProxy.clearCache();
      Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
          p.getLocation(), p.getTime(), p.getStatus());
      assert p1.getGuests().isEmpty();
    }
  }

  @Test
  public void testRemoveGuest() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");

    assert p.addGuest(h) == true;
    assert p.addGuest(a) == true;
    assert p.addGuest(m) == true;
    assert p.removeGuest(m) == true;
    assert p.getGuests().contains(h);
    assert p.getGuests().contains(a);
    assert !p.getGuests().contains(m);
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getGuests().contains(h);
    assert p1.getGuests().contains(a);
    assert !p1.getGuests().contains(m);
  }

  @Test
  public void testRemoveGuestPartyEnded()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    p.addGuest(h);
    p.endParty();
    try {
      p.removeGuest(h);
      assert false;
    } catch (IllegalStateException e) {
      assert p.getGuests().size() == 1;
      assert p.getGuests().contains(h);
      RequestProxy.clearCache();
      PartyProxy.clearCache();
      Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
          p.getLocation(), p.getTime(), p.getStatus());
      assert p1.getGuests().size() == 1;
      assert p1.getGuests().contains(h);
    }
  }

  @Test
  public void testRemoveNonExistentGuest()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");

    assert p.addGuest(h) == true;
    assert p.addGuest(a) == true;
    assert p.removeGuest(m) == false;
    assert p.getGuests().contains(h);
    assert p.getGuests().contains(a);
    assert !p.getGuests().contains(m);
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getPlaylist().getId(),
        p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getGuests().contains(h);
    assert p1.getGuests().contains(a);
    assert !p1.getGuests().contains(m);
    assert p1.getGuests().size() == 2;
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSameGuest() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.addGuest(h) == true;
    assert p.addGuest(a) == true;
    assert p.addGuest(m) == true;
    assert p.addGuest(m) == false;
    assert p.getGuests().contains(h);
    assert p.getGuests().contains(a);
    assert p.getGuests().contains(m);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddGuestOfAlreadyActiveParty()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.addGuest(h) == true;
    Party p1 = Party.create("Dope Party", a, new Coordinate(1, 1), "time");
    p1.addGuest(h);
  }

  @Test
  public void testAddGuestOfAlreadyActivePartyEndParty()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.addGuest(h) == true;
    p.endParty();
    Party p1 = Party.create("Dope Party", a, new Coordinate(1, 1), "time");
    p1.addGuest(h);
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p2 = Party.of(p1.getPartyId(), p1.getName(), p1.getPlaylist().getId(),
        p1.getLocation(), p1.getTime(), p1.getStatus());
    assert p2.getGuests().size() == 1;
    assert p2.getGuests().contains(h);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddGuestOfButAlreadyHostOfActiveParty()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.addGuest(h) == true;
    Party p1 = Party.create("Dope Party", a, new Coordinate(1, 1), "time");
    p1.addGuest(l);
  }

  @Test
  public void testAddGuestOfButAlreadyHostOfActivePartyEndParty()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.addGuest(h) == true;
    Party p1 = Party.create("Dope Party", a, new Coordinate(1, 1), "time");
    p.endParty();
    p1.addGuest(l);
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p2 = Party.of(p1.getPartyId(), p1.getName(), p1.getPlaylist().getId(),
        p1.getLocation(), p1.getTime(), p1.getStatus());
    assert p2.getGuests().size() == 1;
    assert p2.getGuests().contains(l);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddHostButAlreadyActiveGuest()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.addGuest(h) == true;
    Party.create("Dope Party", h, new Coordinate(1, 1), "time");
  }

  @Test
  public void testAddHostButAlreadyActiveGuestEndParty()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    assert p.addGuest(h) == true;
    p.endParty();
    Party.create("Dope Party", h, new Coordinate(1, 1), "time");
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.junit.Test;

public class PartyTest {

  //
  // @Test
  // public void testGetHost() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testGetName() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testGetTime() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testGetLocation() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }

  // @Test
  // public void testGetStatus() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testUpvoteSong() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testDownvoteSong() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }

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
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    p.removeFromPlaylist(r1);
    assert p.getRequestedSongs().contains(r1);
    assert !p.getPlaylist().getSongs().contains(r1);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getRequestedSongs().contains(r1);
    assert !p1.getPlaylist().getSongs().contains(r1);
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
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = p.requestSong(Song.of("song1"), h);
    assert p.removeFromPlaylist(r1) == false;
    assert p.getRequestedSongs().contains(r1);
    assert !p.getPlaylist().getSongs().contains(r1);
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
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
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    User h = User.create("hhe", "hannahhe@brown.edu", "Hannah He");
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = p.requestSong(Song.of("song1"), h);
    Party p1 = Party.create("Dope Part1", m, new Coordinate(1, 1), "time");

    assert p1.removeFromPlaylist(r1) == false;
    assert p.getRequestedSongs().contains(r1);
    assert !p.getPlaylist().getSongs().contains(r1);
    assert p1.getRequestedSongs().size() == 0;
    assert p1.getPlaylist().getSongs().size() == 0;

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p2 = Party.of(p1.getPartyId(), p1.getName(), p1.getHost(),
        p1.getPlaylist(), p1.getLocation(), p1.getTime(), p1.getStatus());
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
    Request r1 = p.requestSong(Song.of("song1"), h);
    Request r2 = p.requestSong(Song.of("song2"), m);
    Request r3 = p.requestSong(Song.of("song3"), a);
    assert p.getRequestedSongs().contains(r1);
    assert p.getRequestedSongs().contains(r2);
    assert p.getRequestedSongs().contains(r3);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getRequestedSongs().contains(r1);
    assert p1.getRequestedSongs().contains(r2);
    assert p1.getRequestedSongs().contains(r3);
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
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = p.requestSong(Song.of("song1"), h);
    Request r2 = p.requestSong(Song.of("song1"), m);
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
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    assert !p.getRequestedSongs().contains(r1);
    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
    assert !p1.getRequestedSongs().contains(r1);
    assert p1.getPlaylist().getSongs().contains(r1);
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
    User a = User.create("ali", "ali@gmail.com", "Ali Mir");
    User m = User.create("msicat", "mattsicat@gmail.com", "Matt Sicat");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Request r1 = p.requestSong(Song.of("song1"), h);
    p.approveSong(r1);
    p.requestSong(Song.of("song1"), m);
  }

  @Test
  public void testAddHostAsGuest() throws SQLException, FileNotFoundException {
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
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getGuests().contains(h);
    assert p1.getGuests().contains(a);
    assert p1.getGuests().contains(m);
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
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getGuests().contains(h);
    assert p1.getGuests().contains(a);
    assert !p1.getGuests().contains(m);
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
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getGuests().contains(h);
    assert p1.getGuests().contains(a);
    assert !p1.getGuests().contains(m);
    assert p1.getGuests().size() == 2;
  }

  @Test
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

}

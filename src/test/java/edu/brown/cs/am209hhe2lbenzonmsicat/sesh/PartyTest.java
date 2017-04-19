package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.junit.Test;

public class PartyTest {

  // @Test
  // public void testGetRequestedSongs()
  // throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testGetPlaylist() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testGetGuests() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }
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
  //
  // @Test
  // public void testApproveSong() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }

  // @Test
  // public void testRemoveFromPlaylist()
  // throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }

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

  @Test
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
    assert r2 == null;
    assert p.getRequestedSongs().contains(r1);

    RequestProxy.clearCache();
    PartyProxy.clearCache();
    Party p1 = Party.of(p.getPartyId(), p.getName(), p.getHost(),
        p.getPlaylist(), p.getLocation(), p.getTime(), p.getStatus());
    assert p1.getRequestedSongs().contains(r1);
    assert p1.getRequestedSongs().size() == 1;
  }

  // @Test
  // public void testAddGuest() throws SQLException, FileNotFoundException {
  // fail("Not yet implemented");
  // }

}

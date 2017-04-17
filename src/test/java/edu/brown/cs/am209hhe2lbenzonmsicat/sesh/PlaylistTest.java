package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.junit.Test;

public class PlaylistTest {

  @Test
  public void testGetId() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.ClearAllTables();
    PartyProxy.clearCache();
    PlaylistProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Playlist playlist = p.getPlaylist();
    // assert playlist.get()
    // PlaylistProxy.clearCache();
    // Request r1 = Request.of(r.getId(), Song.of("song1"), l, "testTime");
    // assert r1.getSong().equals(Song.of("song1"));
  }

  @Test
  public void testGetUrl() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");

  }

  @Test
  public void testGetSongs() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testRemoveSong() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testAddSong() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

  @Test
  public void testOf() throws SQLException, FileNotFoundException {
    fail("Not yet implemented");
  }

}

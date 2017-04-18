package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import org.junit.Test;

/**
 * This class tests the playlist class.
 *
 * @author Ali
 *
 */
public class PlaylistTest {

  /**
   * This method tests the getID method.
   *
   * @throws SQLException
   *           throws a SQL exception if the db is bad
   * @throws FileNotFoundException
   *           if the db is not found
   */
  // @Test
  public void testGetId() throws SQLException, FileNotFoundException {

  }

  // @Test
  public void testGetUrl() throws SQLException, FileNotFoundException {
    // fail("Not yet implemented");

  }

  @Test
  public void testRemoveSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    PartyProxy.clearCache();
    PlaylistProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Playlist playlist = p.getPlaylist();
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    playlist.addSong(r);
    Request r1 = Request.create(Song.of("song2"), l, p.getPartyId(),
        "testTime");
    playlist.addSong(r1);
    assert playlist.getSongs().contains(r);
    assert playlist.getSongs().contains(r1);
    playlist.removeSong(r);
    PlaylistProxy.clearCache();
    Playlist playlist1 = Playlist.of(playlist.getId(), p.getPartyId(), l);
    assert !playlist1.getSongs().contains(r);
    assert playlist1.getSongs().contains(r1);
  }

  @Test
  public void testAddSong() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    PartyProxy.clearCache();
    PlaylistProxy.clearCache();
    User l = User.create("lbengzon", "leandro.bengzon@gmail.com",
        "Leandro Bengzon");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1), "time");
    Playlist playlist = p.getPlaylist();
    Request r = Request.create(Song.of("song1"), l, p.getPartyId(), "testTime");
    p.requestSong(r);
    playlist.addSong(r);
    Request r1 = Request.create(Song.of("song2"), l, p.getPartyId(),
        "testTime");
    p.requestSong(r1);
    playlist.addSong(r1);
    assert playlist.getSongs().contains(r);
    assert playlist.getSongs().contains(r1);
    PlaylistProxy.clearCache();
    int id = p.getPartyId();
    Playlist playlist1 = Playlist.of(playlist.getId(), id, l);
    System.out.println(playlist1.getSongs());
    assert playlist1.getSongs().contains(r);
    assert playlist1.getSongs().contains(r1);
  }

  @Test
  public void testOf() throws SQLException, FileNotFoundException {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetSongs() throws MalformedURLException, IOException {
    System.out.println("GET SONGS");
    SpotifyCommunicator comm = new SpotifyCommunicator();
    comm.createAuthorizeURL();
    // try {
    User host = User.of("alimiraculous", "ali.ahmed." + "mir@gmail.com",
        "Ali Mir");
    Playlist plist = Playlist.of(" ", 1, host);
    // } catch (SQLException e) {
    // // ERROR
    // }

  }

}

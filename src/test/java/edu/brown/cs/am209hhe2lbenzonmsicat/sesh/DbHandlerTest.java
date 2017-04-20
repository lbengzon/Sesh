package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.sqlite.SQLiteException;

/**
 * This class tests the db handler class.
 * @author Ali
 */
public class DbHandlerTest {

  /**
   * This tests the add get user function.
   * @throws FileNotFoundException
   *           if db isn't there
   * @throws SQLException
   *           if db messes up
   */
  @Test
  public void testAddGetUser() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User user = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    assert user.getEmail().equals("leandro_bengzon@brown.edu");
    assert user.getFullName().equals("Leandro Bengzon");
    User same = DbHandler.getUserWithId("lbengzon");
    assert user.getEmail().equals(same.getEmail());
    assert user.getSpotifyId().equals(same.getSpotifyId());
    assert user.getFullName().equals(same.getFullName());
  }

  /**
   * This method tests the adding of the same user.
   * @throws FileNotFoundException
   *           if the db isn't there
   * @throws SQLException
   *           if the db messes up
   */
  @Test(expected = SQLiteException.class)
  public void testAddSameUser() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User user = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User user1 = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
  }

  /**
   * This tests adding the same party.
   * @throws FileNotFoundException
   *           if db isn't there
   * @throws SQLException
   *           if the db messes up.
   */
  @Test
  public void testAddParty() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);
    assert party.getHost().equals(host);
    assert party.getGuests().isEmpty();
    assert party.getRequestedSongs().isEmpty();
    assert party.getName().equals("My Party");
    assert party.getPlaylist().getId().equals("testPlaylistId");

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus());

    assert partyFull.getHost().equals(host);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getRequestedSongs().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");
  }

  @Test
  public void testEndParty() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);
    assert party.getHost().equals(host);
    assert party.getGuests().isEmpty();
    assert party.getRequestedSongs().isEmpty();
    assert party.getName().equals("My Party");
    assert party.getPlaylist().getId().equals("testPlaylistId");
    assert party.isActive() == true;
    DbHandler.endParty(party.getPartyId());

    List<Party> listparties = DbHandler.getUsersParties(host);
    Party partyFull = listparties.get(0);
    assert partyFull.getHost().equals(host);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getRequestedSongs().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");
    assert partyFull.isActive() == false;
  }

  @Test
  public void testremoveParty() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);
    List<Party> parties = DbHandler.getUsersParties(host);
    assert parties.contains(party);
    DbHandler.removeParty(party);

    List<Party> parties2 = DbHandler.getUsersParties(host);
    assert parties2.isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddHostOfTwoParties()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);
    Party party2 = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

  }

  @Test
  public void testAddGuests() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");
    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    DbHandler.addPartyGuest(party.getPartyId(), hannah);
    DbHandler.addPartyGuest(party.getPartyId(), matt);
    DbHandler.addPartyGuest(party.getPartyId(), ali);

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus());

    assert partyFull.getHost().equals(host);
    assert partyFull.getGuests().contains(hannah);
    assert partyFull.getGuests().contains(ali);
    assert partyFull.getGuests().contains(matt);

    assert partyFull.getRequestedSongs().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");
  }

  @Test
  public void testRemoveGuests() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");
    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    DbHandler.addPartyGuest(party.getPartyId(), hannah);
    DbHandler.addPartyGuest(party.getPartyId(), matt);
    DbHandler.addPartyGuest(party.getPartyId(), ali);

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus());

    assert partyFull.getHost().equals(host);
    assert partyFull.getGuests().contains(hannah);
    assert partyFull.getGuests().contains(ali);
    assert partyFull.getGuests().contains(matt);

    DbHandler.removePartyGuest(party.getPartyId(), ali);
    DbHandler.removePartyGuest(party.getPartyId(), matt);

    partyFull = DbHandler.getFullParty(party.getPartyId(), party.getPlaylist(),
        party.getName(), party.getLocation(), party.getTime(),
        party.getStatus());

    assert partyFull.getGuests().contains(hannah);
    assert partyFull.getGuests().size() == 1;

  }

  @Test(expected = SQLiteException.class)
  public void testAddSameGuest() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);
    DbHandler.addPartyGuest(party.getPartyId(), ali);
    DbHandler.addPartyGuest(party.getPartyId(), ali);
  }

  @Test
  public void testAddSongRequests() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    Request song1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time.toString());
    Request song2 = DbHandler.requestSong("songId2", party.getPartyId(), host,
        time.toString());
    Request song3 = DbHandler.requestSong("songId3", party.getPartyId(), ali,
        time.toString());

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus());

    assert partyFull.getHost().equals(host);
    assert partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");
  }

  @Test
  public void testMoveSongRequestsToQueue()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    Request song1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time.toString());
    Request song2 = DbHandler.requestSong("songId2", party.getPartyId(), host,
        time.toString());
    Request song3 = DbHandler.requestSong("songId3", party.getPartyId(), ali,
        time.toString());

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus());

    assert partyFull.getHost().equals(host);
    assert partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");

    DbHandler.moveSongRequestToQueue(song1);
    partyFull = DbHandler.getFullParty(party.getPartyId(), party.getPlaylist(),
        party.getName(), party.getLocation(), party.getTime(),
        party.getStatus());
    PlaylistBean playlist = DbHandler.getQueuedSongsForParty(
        party.getPlaylist().getId(), party.getPartyId());

    assert playlist.getSongs().contains(song1);

    assert partyFull.getHost().equals(host);
    assert !partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");
  }

  @Test
  public void testMoveSongRequestsOutOfQueue()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    Request song1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time.toString());
    Request song2 = DbHandler.requestSong("songId2", party.getPartyId(), host,
        time.toString());
    Request song3 = DbHandler.requestSong("songId3", party.getPartyId(), ali,
        time.toString());

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus());

    assert partyFull.getHost().equals(host);
    assert partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");

    DbHandler.moveSongRequestToQueue(song1);
    partyFull = DbHandler.getFullParty(party.getPartyId(), party.getPlaylist(),
        party.getName(), party.getLocation(), party.getTime(),
        party.getStatus());

    assert partyFull.getHost().equals(host);
    assert !partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");

    DbHandler.moveSongRequestOutOfQueue(song1);

    partyFull = DbHandler.getFullParty(party.getPartyId(), party.getPlaylist(),
        party.getName(), party.getLocation(), party.getTime(),
        party.getStatus());

    PlaylistBean playlist = DbHandler.getQueuedSongsForParty(
        party.getPlaylist().getId(), party.getPartyId());

    assert playlist.getSongs().size() == 0;

    assert partyFull.getHost().equals(host);
    assert partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");
  }

  @Test
  public void testVoteRequest() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");
    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat");
    User kobe = DbHandler.addUser("kbryant", "kobe_bryant@brown.edu",
        "Kobe Bryant");

    User kanye = DbHandler.addUser("kwest", "kanye_west@brown.edu",
        "Kanye West");

    User frank = DbHandler.addUser("focean", "frank_ocean@brown.edu",
        "Frank Ocean");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    Request request1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time.toString());
    assert request1.getSong().equals(Song.of("songId1"));
    assert request1.getUserRequestedBy().equals(ali);

    DbHandler.upvoteRequest(request1, ali);
    DbHandler.upvoteRequest(request1, kobe);
    DbHandler.upvoteRequest(request1, frank);
    DbHandler.downvoteRequest(request1, kanye);
    DbHandler.downvoteRequest(request1, hannah);
    DbHandler.downvoteRequest(request1, matt);

    RequestBean fullRequest = DbHandler.getFullRequest(request1.getPartyId(),
        request1.getSong(), request1.getUserRequestedBy(),
        request1.getRequestTime());

    assert fullRequest.getSong().equals(request1.getSong());
    assert fullRequest.getDownvotes().size() == 3;
    assert fullRequest.getDownvotes().contains(kanye);
    assert fullRequest.getDownvotes().contains(matt);
    assert fullRequest.getDownvotes().contains(hannah);
    assert fullRequest.getUpvotes().contains(ali);
    assert fullRequest.getUpvotes().contains(kobe);
    assert fullRequest.getUpvotes().contains(frank);
  }

  @Test
  public void testRemoveVoteRequest()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");
    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat");
    User kobe = DbHandler.addUser("kbryant", "kobe_bryant@brown.edu",
        "Kobe Bryant");

    User kanye = DbHandler.addUser("kwest", "kanye_west@brown.edu",
        "Kanye West");

    User frank = DbHandler.addUser("focean", "frank_ocean@brown.edu",
        "Frank Ocean");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    Request request1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time.toString());
    assert request1.getSong().equals(Song.of("songId1"));
    assert request1.getUserRequestedBy().equals(ali);

    DbHandler.upvoteRequest(request1, ali);
    DbHandler.upvoteRequest(request1, kobe);
    DbHandler.upvoteRequest(request1, frank);
    DbHandler.downvoteRequest(request1, kanye);
    DbHandler.downvoteRequest(request1, hannah);
    DbHandler.downvoteRequest(request1, matt);

    RequestBean fullRequest = DbHandler.getFullRequest(request1.getPartyId(),
        request1.getSong(), request1.getUserRequestedBy(),
        request1.getRequestTime());

    assert fullRequest.getSong().equals(request1.getSong());
    assert fullRequest.getDownvotes().size() == 3;
    assert fullRequest.getDownvotes().contains(kanye);
    assert fullRequest.getDownvotes().contains(matt);
    assert fullRequest.getDownvotes().contains(hannah);
    assert fullRequest.getUpvotes().contains(ali);
    assert fullRequest.getUpvotes().contains(kobe);
    assert fullRequest.getUpvotes().contains(frank);

    DbHandler.removeVote(request1, kobe);
    DbHandler.downvoteRequest(request1, kobe);

    DbHandler.removeVote(request1, frank);
    DbHandler.downvoteRequest(request1, frank);

    DbHandler.removeVote(request1, matt);
    DbHandler.upvoteRequest(request1, matt);

    fullRequest = DbHandler.getFullRequest(request1.getPartyId(),
        request1.getSong(), request1.getUserRequestedBy(),
        request1.getRequestTime());

    assert fullRequest.getSong().equals(request1.getSong());
    assert fullRequest.getDownvotes().size() == 4;
    assert fullRequest.getDownvotes().contains(kanye);
    assert fullRequest.getDownvotes().contains(frank);
    assert fullRequest.getDownvotes().contains(hannah);
    assert fullRequest.getDownvotes().contains(kobe);
    assert fullRequest.getUpvotes().contains(matt);
    assert fullRequest.getUpvotes().contains(ali);
  }

  @Test(expected = SQLiteException.class)
  public void testDoubleVoteRequest()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty(Playlist.of("testPlaylistId", host),
        "My Party", new Coordinate(71.6, 41.8), time.toString(), host);

    Request request1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time.toString());
    assert request1.getSong().equals(Song.of("songId1"));
    assert request1.getUserRequestedBy().equals(ali);

    DbHandler.upvoteRequest(request1, ali);
    DbHandler.downvoteRequest(request1, ali);
  }

}

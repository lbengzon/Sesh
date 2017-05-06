package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.sqlite.SQLiteException;

import edu.brown.cs.am209hhe2lbenzonmsicat.models.Coordinate;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party.AccessType;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.PlaylistBean;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Request;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.RequestBean;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Song;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User.Type;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.UserProxy;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;

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
        "Leandro Bengzon", "premium");
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
        "Leandro Bengzon", "premium");
    User user1 = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
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
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    assert party.getHost().equals(host);
    assert party.getGuests().isEmpty();
    assert party.getRequestedSongs().isEmpty();
    assert party.getName().equals("My Party");
    assert party.getPlaylist().getId().equals("testPlaylistId");

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

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
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
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
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
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

    User host = DbHandler.addUser("s3shteam32", "seshteam32@gmail.com",
        "Ali Mir", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    Party party2 = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

  }

  @Test
  public void testAddGuests() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    DbHandler.addPartyGuest(party.getPartyId(), hannah);
    DbHandler.addPartyGuest(party.getPartyId(), matt);
    DbHandler.addPartyGuest(party.getPartyId(), ali);

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

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
        "Leandro Bengzon", "premium");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    DbHandler.addPartyGuest(party.getPartyId(), hannah);
    DbHandler.addPartyGuest(party.getPartyId(), matt);
    DbHandler.addPartyGuest(party.getPartyId(), ali);

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

    assert partyFull.getHost().equals(host);
    assert partyFull.getGuests().contains(hannah);
    assert partyFull.getGuests().contains(ali);
    assert partyFull.getGuests().contains(matt);

    DbHandler.removePartyGuest(party.getPartyId(), ali);
    DbHandler.removePartyGuest(party.getPartyId(), matt);

    partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

    assert partyFull.getGuests().contains(hannah);
    assert partyFull.getGuests().size() == 1;

  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddSameGuest() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    DbHandler.addPartyGuest(party.getPartyId(), ali);
    DbHandler.addPartyGuest(party.getPartyId(), ali);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddGuestOfTwoActiveParties()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    User matt = DbHandler.addUser("matt", "mattsicat@brown.edu", "Matt Sicat",
        "premium");

    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    Party party1 = DbHandler.addParty("testPlaylistId1", "My Party",
        new Coordinate(71.6, 41.8), time, matt, "deviceId", AccessType.PUBLIC,
        "");

    DbHandler.addPartyGuest(party.getPartyId(), ali);
    DbHandler.addPartyGuest(party1.getPartyId(), ali);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testActiveGuestBecomeActiveHost()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("s3shteam32", "seshteam32@gmail.com",
        "Ali Mir", "premium");

    User hannah = DbHandler.addUser("1185743437", "hannahhe97@yahoo.com.au",
        "Hannah He", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    DbHandler.addPartyGuest(party.getPartyId(), hannah);

    Party party1 = DbHandler.addParty("testPlaylistId1", "My Party",
        new Coordinate(71.6, 41.8), time, hannah, "deviceId", AccessType.PUBLIC,
        "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testActiveHostBecomeGuest()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    User matt = DbHandler.addUser("matt", "mattsicat@brown.edu", "Matt Sicat",
        "premium");

    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Party party1 = DbHandler.addParty("testPlaylistId1", "My Party",
        new Coordinate(71.6, 41.8), time, ali, "deviceId", AccessType.PUBLIC,
        "");
    DbHandler.addPartyGuest(party1.getPartyId(), host);
  }

  @Test
  public void testActiveHostEndPartyBecomeGuest()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    User matt = DbHandler.addUser("matt", "mattsicat@brown.edu", "Matt Sicat",
        "premium");

    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    DbHandler.endParty(party.getPartyId());
    Party party1 = DbHandler.addParty("testPlaylistId1", "My Party",
        new Coordinate(71.6, 41.8), time, ali, "deviceId", AccessType.PUBLIC,
        "");
    DbHandler.addPartyGuest(party1.getPartyId(), host);
    assert party1.getGuests().contains(host);
  }

  @Test
  public void testAddGuestEndPartyAddGuestOfNewParty()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    User matt = DbHandler.addUser("matt", "mattsicat@brown.edu", "Matt Sicat",
        "premium");

    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    Party party1 = DbHandler.addParty("testPlaylistId1", "My Party",
        new Coordinate(71.6, 41.8), time, matt, "deviceId", AccessType.PUBLIC,
        "");

    DbHandler.addPartyGuest(party.getPartyId(), ali);
    DbHandler.endParty(party.getPartyId());
    DbHandler.addPartyGuest(party1.getPartyId(), ali);
    assert DbHandler.getPartyHostsAndGuests(party1.getPartyId()).get(1)
        .contains(ali);
    assert DbHandler.getPartyHostsAndGuests(party.getPartyId()).get(1)
        .contains(ali);

  }

  @Test
  public void testAddSongRequests() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Request song1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time);
    Request song2 = DbHandler.requestSong("songId2", party.getPartyId(), host,
        time);
    Request song3 = DbHandler.requestSong("songId3", party.getPartyId(), ali,
        time);

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

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
        "Leandro Bengzon", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Request song1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time);
    Request song2 = DbHandler.requestSong("songId2", party.getPartyId(), host,
        time);
    Request song3 = DbHandler.requestSong("songId3", party.getPartyId(), ali,
        time);

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

    assert partyFull.getHost().equals(host);
    assert partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");

    DbHandler.moveSongRequestToQueue(song1);
    partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");
    PlaylistBean playlist = DbHandler.getQueuedSongsForParty(
        party.getPlaylist().getId(), party.getPartyId(), host);

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
        "Leandro Bengzon", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Request song1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time);
    Request song2 = DbHandler.requestSong("songId2", party.getPartyId(), host,
        time);
    Request song3 = DbHandler.requestSong("songId3", party.getPartyId(), ali,
        time);

    Party partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

    assert partyFull.getHost().equals(host);
    assert partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");

    DbHandler.moveSongRequestToQueue(song1);
    partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

    assert partyFull.getHost().equals(host);
    assert !partyFull.getRequestedSongs().contains(song1);
    assert partyFull.getRequestedSongs().contains(song2);
    assert partyFull.getRequestedSongs().contains(song3);
    assert partyFull.getGuests().isEmpty();
    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");

    DbHandler.moveSongRequestOutOfQueue(song1);

    partyFull = DbHandler.getFullParty(party.getPartyId(),
        party.getPlaylist().getId(), party.getName(), party.getLocation(),
        party.getTime(), party.getStatus(), "deviceId", AccessType.PUBLIC, "");

    PlaylistBean playlist = DbHandler.getQueuedSongsForParty(
        party.getPlaylist().getId(), party.getPartyId(), host);

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
        "Leandro Bengzon", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User kobe = DbHandler.addUser("kbryant", "kobe_bryant@brown.edu",
        "Kobe Bryant", "premium");

    User kanye = DbHandler.addUser("kwest", "kanye_west@brown.edu",
        "Kanye West", "premium");

    User frank = DbHandler.addUser("focean", "frank_ocean@brown.edu",
        "Frank Ocean", "premium");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Request request1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time);
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
        "Leandro Bengzon", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User kobe = DbHandler.addUser("kbryant", "kobe_bryant@brown.edu",
        "Kobe Bryant", "premium");

    User kanye = DbHandler.addUser("kwest", "kanye_west@brown.edu",
        "Kanye West", "premium");

    User frank = DbHandler.addUser("focean", "frank_ocean@brown.edu",
        "Frank Ocean", "premium");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Request request1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time);
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
        "Leandro Bengzon", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");

    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Request request1 = DbHandler.requestSong("songId1", party.getPartyId(), ali,
        time);
    assert request1.getSong().equals(Song.of("songId1"));
    assert request1.getUserRequestedBy().equals(ali);

    DbHandler.upvoteRequest(request1, ali);
    DbHandler.downvoteRequest(request1, ali);
  }

  @Test
  public void testGetUsersParties() throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    DbHandler.addPartyGuest(party.getPartyId(), hannah);
    DbHandler.endParty(party.getPartyId());
    Party party1 = DbHandler.addParty("testPlaylistId2", "My Party",
        new Coordinate(71.6, 41.8), time, matt, "deviceId", AccessType.PUBLIC,
        "");

    DbHandler.addPartyGuest(party1.getPartyId(), hannah);
    DbHandler.endParty(party1.getPartyId());

    Party party2 = DbHandler.addParty("testPlaylistId2", "My Party",
        new Coordinate(71.6, 41.8), time, ali, "deviceId", AccessType.PUBLIC,
        "");

    DbHandler.addPartyGuest(party2.getPartyId(), hannah);
    DbHandler.endParty(party2.getPartyId());

    List<Party> parties = DbHandler.getUsersParties(hannah);

    assert parties.size() == 3;
    assert parties.contains(party);
    assert parties.contains(party1);
    assert parties.contains(party2);
  }

  @Test
  public void testGetUsersPartiesNoParties()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");

    List<Party> parties = DbHandler.getUsersParties(host);

    assert parties.size() == 0;
  }

  @Test
  public void testGetPartyHostedByUser()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    party.endParty();

    Party party1 = DbHandler.addParty("testPlaylistId2", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Party party2 = DbHandler.getPartyHostedByUser(host);

    assert party2.equals(party1);
    assert !party2.equals(party);
  }

  @Test
  public void testGetAllActiveParties()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();

    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Party party1 = DbHandler.addParty("testPlaylistId2", "My Party",
        new Coordinate(71.6, 41.8), time, hannah, "deviceId", AccessType.PUBLIC,
        "");

    Party party2 = DbHandler.addParty("testPlaylistId2", "My Party",
        new Coordinate(71.6, 41.8), time, matt, "deviceId", AccessType.PUBLIC,
        "");

    Party party3 = DbHandler.addParty("testPlaylistId2", "My Party",
        new Coordinate(71.6, 41.8), time, ali, "deviceId", AccessType.PUBLIC,
        "");
    party3.endParty();

    List<Party> parties = DbHandler.getAllActiveParties();
    assert parties.size() == 3;
    assert parties.contains(party);
    assert parties.contains(party1);
    assert parties.contains(party2);
  }

  @Test
  public void testGetPartyHostedByUserButNotHost()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");

    User hannah = DbHandler.addUser("hhe", "hannah_he@brown.edu", "Hannah He",
        "premium");
    User matt = DbHandler.addUser("msicat", "matt_sicat@brown.edu",
        "Matt Sicat", "premium");
    User ali = DbHandler.addUser("ali", "ali_mir@brown.edu", "Ali Mir",
        "premium");
    LocalDateTime time = LocalDateTime.now();

    Party party2 = DbHandler.getPartyHostedByUser(host);

    assert party2 == null;
  }

  @Test
  public void testGetPartyFromId() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");

    Party partyFull = DbHandler.getPartyFromId(party.getPartyId());

    assert partyFull.getName().equals("My Party");
    assert partyFull.getPlaylist().getId().equals("testPlaylistId");
  }

  @Test
  public void testGetPartyFromInvalidId()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();

    Party partyFull = DbHandler.getPartyFromId(1);

    assert partyFull == null;
  }

  @Test
  public void testGetPartyGetDeviceId()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    assert party.getDeviceId().equals("deviceId");
    Party partyFull = DbHandler.getPartyFromId(party.getPartyId());

    assert partyFull.getDeviceId().equals("deviceId");
  }

  @Test
  public void testGetUserType() throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    assert host.getType().equals(Type.premium);
    UserProxy.clearCache();
    User h = User.of("lbengzon");
    assert h.getType().equals(Type.premium);
  }

  @Test
  public void testAddSongsToFavorite()
      throws SQLException, FileNotFoundException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    User host = User.of("s3shteam32", "seshteam32@gmail.com", "Sesh",
        Type.valueOf("premium"));
    Song s = Song.of("7AQAlklmptrrkBSeujkXsD");
    DbHandler.AddSongToFavorites("s3shteam32", s.getSpotifyId());
    List<Song> songs = DbHandler.GetUserFavoritedSongs("s3shteam32");
    assert songs.size() == 1;
    assert songs.get(0).getSpotifyId().equals("7AQAlklmptrrkBSeujkXsD");
  }

  @Test
  public void testGetPartyGetAccessType()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PUBLIC,
        "");
    assert party.getAccessType().equals(AccessType.PUBLIC);
    Party partyFull = DbHandler.getPartyFromId(party.getPartyId());

    assert partyFull.getAccessType().equals(AccessType.PUBLIC);
  }

  @Test
  public void testGetPartyGetAccessCode()
      throws FileNotFoundException, SQLException {
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();

    User host = DbHandler.addUser("lbengzon", "leandro_bengzon@brown.edu",
        "Leandro Bengzon", "premium");
    LocalDateTime time = LocalDateTime.now();
    Party party = DbHandler.addParty("testPlaylistId", "My Party",
        new Coordinate(71.6, 41.8), time, host, "deviceId", AccessType.PRIVATE,
        "1234");
    assert party.checkAccessCode("1234") == true;
    assert party.getAccessType().equals(AccessType.PRIVATE);

    Party partyFull = DbHandler.getPartyFromId(party.getPartyId());

    assert partyFull.checkAccessCode("1234") == true;
    assert partyFull.getAccessType().equals(AccessType.PRIVATE);

  }
}

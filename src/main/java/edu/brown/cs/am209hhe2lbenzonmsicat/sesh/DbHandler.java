package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Party.AttendeeType;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Party.Status;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Request.VoteType;

public class DbHandler {

  private static Connection guiConnection = null;
  private static String startOfPath = "jdbc:sqlite:";
  private static ThreadLocal<Connection> connections = new ThreadLocal<>();
  // private static ConcurrentHashMap<String, PreparedStatement> statementMap =
  // new ConcurrentHashMap<String, PreparedStatement>();

  /**
   * Hiding constructor
   */
  private DbHandler() {

  }

  /**
   * Gets the connection a particular thread has.
   * @return the connection to the database.
   */
  public static Connection getConnection() {
    if (connections.get() == null && guiConnection != null) {
      return guiConnection;
    }
    return connections.get();
  }

  public static void setFromUrl(String pathToDb)
      throws SQLException, FileNotFoundException {
    // System.out.println("1");
    File dbFile = new File(pathToDb);
    // System.out.println("2");

    if (dbFile.exists() && !(dbFile.isDirectory())) {
      // System.out.println("Found the file. The URL is:");
      // System.out.println(SQLITE_START + pathToDb);
      // System.out.println("3");

      // overwrite previous connection
      if (DbHandler.getConnection() != null) {
        assertNotNull(DbHandler.getConnection());
        // close all previous connections
        // for (String key : statementMap.keySet()) {
        // statementMap.get(key).close();
        // }
        DbHandler.getConnection().close();
        DbHandler.getAllConnections().set(null);
        guiConnection.close();
        guiConnection = null;
      }
      // System.out.println("4");

      Connection conn = DriverManager.getConnection(startOfPath + pathToDb);
      // now we add the new DbConnection to the ThreadLocal.
      DbHandler.setConnection(conn);
      // set gui connection.
      guiConnection = conn;
      // Turn on foreign key thingy.
      Statement stat = conn.createStatement();
      // System.out.println("5");

      stat.executeUpdate("PRAGMA foreign_keys = ON;");

    } else {
      throw new FileNotFoundException("ERROR: Not a valid database. "
          + "If a database was already linked it has not been replaced.");
    }

  }

  /**
   * Sets the connection to a particular database for the thread.
   * @param conn
   *          the connection we want to use.
   */
  private static void setConnection(Connection conn) {
    connections.set(conn);
    guiConnection = conn;
  }

  public static void ClearAllTables() throws SQLException {
    ClearUserTable();
    ClearPartyTable();
    ClearPartyAttendeeTable();
    ClearRequestVotesTable();
    ClearSongRequestTable();
  }

  public static void ClearUserTable() throws SQLException {
    String query = SqlStatements.CLEAR_USER_TABLE;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    int success = prep.executeUpdate();
  }

  public static void ClearPartyTable() throws SQLException {
    String query = SqlStatements.CLEAR_PARTY_TABLE;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    int success = prep.executeUpdate();
  }

  public static void ClearSongRequestTable() throws SQLException {
    String query = SqlStatements.CLEAR_SONG_REQUEST_TABLE;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    int success = prep.executeUpdate();
  }

  public static void ClearRequestVotesTable() throws SQLException {
    String query = SqlStatements.CLEAR_REQUEST_VOTES_TABLE;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    int success = prep.executeUpdate();
  }

  public static void ClearPartyAttendeeTable() throws SQLException {
    String query = SqlStatements.CLEAR_PARTY_ATTENDEE_TABLE;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    int success = prep.executeUpdate();
  }

  public static ThreadLocal<Connection> getAllConnections() {
    return connections;
  }

  public static User addUser(String userId, String email, String name)
      throws SQLException {
    String query = SqlStatements.ADD_NEW_USER;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setString(1, userId);
    prep.setString(2, email);
    prep.setString(3, name);

    int success = prep.executeUpdate();
    if (success >= 1) {
      return new UserBean(userId, email, name);
    } else {
      throw new SQLException(
          "ERROR: Could not insert the user with the query " + query);
    }
  }

  public static Request requestSong(String spotifySongId, int partyId,
      User user, String time) throws SQLException {
    String query = SqlStatements.ADD_SONG_REQUEST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setString(1, spotifySongId);
    prep.setInt(2, partyId);
    prep.setString(3, user.getSpotifyId());
    prep.setString(4, time);

    int success = prep.executeUpdate();
    if (success >= 1) {
      try (ResultSet generatedKeys = prep.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          int requestId = generatedKeys.getInt(1);

          return new RequestBean(requestId, time, Song.of(spotifySongId), user,
              new HashSet<>(), new HashSet<>());
        } else {
          throw new SQLException("Creating user failed, no ID obtained.");
        }
      }
    } else {
      throw new SQLException(
          "ERROR: Could not insert the song with the query " + query);
    }
  }

  public static Party addParty(String spotifyPlaylistId, String name,
      Coordinate coordinate, String time, User host) throws SQLException {
    if (getPartyHostedByUser(host) != null) {
      throw new IllegalArgumentException(
          "ERROR: Host is already a host of another active party");
    }
    String query = SqlStatements.ADD_NEW_PARTY;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setString(1, spotifyPlaylistId);
    prep.setString(2, name);
    prep.setDouble(3, coordinate.getLat());
    prep.setDouble(4, coordinate.getLon());
    prep.setString(5, time);

    int success = prep.executeUpdate();
    if (success >= 1) {
      try (ResultSet generatedKeys = prep.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          int partyId = generatedKeys.getInt(1);

          AddHost(partyId, host);

          return Party.of(partyId, name, host, new Playlist(spotifyPlaylistId),
              coordinate, time, Status.ongoing);
        } else {
          throw new SQLException("Add PartyBean failed, no ID obtained.");
        }
      }
    } else {
      throw new SQLException(
          "ERROR: Could not insert the party with the query " + query);
    }
  }

  public static void AddHost(int partyId, User host) throws SQLException {
    // TODO: if your gonna have multiple hosts, add the check that he's not
    // hosting another ongoing party here.
    String query = SqlStatements.ADD_PARTY_HOST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, partyId);
    prep.setString(2, host.getSpotifyId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not add the host with the query " + query);
    }
  }

  public static void RemoveSongRequest(Request request) throws SQLException {
    String query = SqlStatements.REMOVE_SONG_REQUEST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, request.getId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Remove song request with the query " + query);
    }
  }

  public static void RemoveParty(Party party) throws SQLException {
    String query = SqlStatements.REMOVE_PARTY;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, party.getPartyId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not remove the party with the query " + query);
    }
  }

  public static void UpvoteRequest(Request request, User user)
      throws SQLException {
    String query = SqlStatements.UPVOTE_SONG_REQUEST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, request.getId());
    prep.setString(2, user.getSpotifyId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not upvote request with the query " + query);
    }
  }

  public static void DownvoteRequest(Request request, User user)
      throws SQLException {
    String query = SqlStatements.DOWNVOTE_SONG_REQUEST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, request.getId());
    prep.setString(2, user.getSpotifyId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not downvote the request with the query " + query);
    }
  }

  public static void RemoveVote(Request request, User user)
      throws SQLException {
    String query = SqlStatements.REMOVE_VOTE_SONG_REQUEST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, request.getId());
    prep.setString(2, user.getSpotifyId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not remove request vote with the query " + query);
    }
  }

  public static void AddPartyGuest(int partyId, User guest)
      throws SQLException {
    String query = SqlStatements.ADD_PARTY_GUEST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, partyId);
    prep.setString(2, guest.getSpotifyId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not add the guest with the query " + query);
    }
  }

  public static void RemovePartyGuest(int partyId, User guest)
      throws SQLException {
    String query = SqlStatements.REMOVE_PARTY_GUEST;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, partyId);
    prep.setString(2, guest.getSpotifyId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not remove the guest with the query " + query);
    }
  }

  public static List<Request> getPartySongRequests(int partyId)
      throws SQLException {
    String query = SqlStatements.GET_PARTY_SONG_REQUESTS;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, partyId);

    ResultSet rs = prep.executeQuery();
    List<Request> requests = new ArrayList<>();
    while (rs.next()) {
      int requestId = rs.getInt(1);
      String spotifySongId = rs.getString(2);
      String userId = rs.getString(4);
      String time = rs.getString(5);
      Request r = Request.of(requestId, Song.of(spotifySongId), User.of(userId),
          time);
      requests.add(r);
    }
    return requests;
  }

  public static List<List<User>> getPartyHostsAndGuests(int partyId)
      throws SQLException {
    String query = SqlStatements.GET_ALL_PARTY_ATTENDEES;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, partyId);

    ResultSet rs = prep.executeQuery();
    List<User> hosts = new ArrayList<>();
    List<User> guests = new ArrayList<>();
    while (rs.next()) {
      String userId = rs.getString(2);
      String type = rs.getString(3);
      AttendeeType atype = AttendeeType.valueOf(type);
      if (atype.equals(AttendeeType.host)) {
        hosts.add(User.of(userId));
      } else if (atype.equals(AttendeeType.guest)) {
        guests.add(User.of(userId));
      } else {
        // should never reach here
        assert (false);
      }
    }
    return Arrays.asList(hosts, guests);
  }

  public static PartyBean getFullParty(int partyId, Playlist playlist,
      String name, Coordinate location, String time, Status status)
      throws SQLException {
    List<Request> requests = getPartySongRequests(partyId);
    List<List<User>> attendees = getPartyHostsAndGuests(partyId);
    assert attendees.size() == 2;
    return new PartyBean(partyId, name, attendees.get(0).get(0), playlist,
        location, time, new HashSet<>(requests),
        new HashSet<>(attendees.get(1)), status);

  }

  public static RequestBean getFullRequest(int id, Song song, User user,
      String requestTime) throws SQLException {
    String query = SqlStatements.GET_VOTES_FOR_SONG;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setInt(1, id);

    ResultSet rs = prep.executeQuery();
    List<User> upvotes = new ArrayList<>();
    List<User> downvotes = new ArrayList<>();
    while (rs.next()) {
      String userId = rs.getString(2);
      String type = rs.getString(3);
      VoteType voteType = VoteType.valueOf(type);
      if (voteType.equals(VoteType.upvote)) {
        upvotes.add(User.of(userId));
      } else if (voteType.equals(VoteType.downvote)) {
        downvotes.add(User.of(userId));
      } else {
        // should never reach here
        assert (false);
      }
    }

    return new RequestBean(id, requestTime, song, user, new HashSet<>(upvotes),
        new HashSet<>(downvotes));
  }

  public static UserBean getUserWithId(String spotifyId) throws SQLException {
    String query = SqlStatements.GET_USER_FROM_ID;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setString(1, spotifyId);

    ResultSet rs = prep.executeQuery();
    List<User> upvotes = new ArrayList<>();
    List<User> downvotes = new ArrayList<>();
    if (rs.next()) {
      String email = rs.getString(2);
      String name = rs.getString(3);
      return new UserBean(spotifyId, email, name);
    }

    throw new SQLException("ERROR: No User With id of  " + spotifyId);
  }

  public static List<Party> getUsersParties(User user) throws SQLException {
    String query = SqlStatements.GET_USER_PARTY;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setString(1, user.getSpotifyId());

    ResultSet rs = prep.executeQuery();
    List<Party> parties = new ArrayList<>();

    while (rs.next()) {
      int partyId = rs.getInt(1);
      String spotifyPlaylistId = rs.getString(2);
      String name = rs.getString(3);
      double lat = rs.getDouble(4);
      double lon = rs.getDouble(5);
      String time = rs.getString(6);
      String status = rs.getString(7);
      Party p = Party.of(partyId, name, user, new Playlist(spotifyPlaylistId),
          new Coordinate(lat, lon), time, Status.valueOf(status));
      parties.add(p);
    }
    return parties;
  }

  public static Party getPartyHostedByUser(User user) throws SQLException {
    String query = SqlStatements.GET_PARTY_HOSTED_BY_USER;
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    PreparedStatement prep = conn.prepareStatement(query);

    prep.setString(1, user.getSpotifyId());

    ResultSet rs = prep.executeQuery();

    if (rs.next()) {
      int partyId = rs.getInt(1);
      String spotifyPlaylistId = rs.getString(2);
      String name = rs.getString(3);
      double lat = rs.getDouble(4);
      double lon = rs.getDouble(5);
      String time = rs.getString(6);
      String status = rs.getString(7);
      Party p = Party.of(partyId, name, user, new Playlist(spotifyPlaylistId),
          new Coordinate(lat, lon), time, Status.valueOf(status));
      return p;
    }
    return null;
  }

}

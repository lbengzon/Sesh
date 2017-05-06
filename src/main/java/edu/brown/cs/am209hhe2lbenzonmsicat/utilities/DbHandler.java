package edu.brown.cs.am209hhe2lbenzonmsicat.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

import edu.brown.cs.am209hhe2lbenzonmsicat.models.Coordinate;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.PartyBean;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Playlist;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.PlaylistBean;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Request;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.RequestBean;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Song;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.UserBean;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party.AttendeeType;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party.Status;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Request.VoteType;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User.Type;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SqlStatements;

/**
 * Database handler class.
 */
public final class DbHandler {
  private static Connection guiConnection = null;
  private static String startOfPath = "jdbc:sqlite:";
  private static BasicDataSource connectionPool;
  // private static ConcurrentHashMap<String, PreparedStatement> statementMap =
  // new ConcurrentHashMap<String, PreparedStatement>();

  /**
   * Hiding constructor.
   */
  private DbHandler() {
  }

  /**
   * Gets the connection a particular thread has.
   * @return a connection to the database.
   */
  public static Connection getConnection() {
    if (connectionPool != null) {
      try {
        return connectionPool.getConnection();
      } catch (SQLException | NullPointerException e) {
        e.printStackTrace();
        System.out.println("ERROR: Null connection");
        return null;
      }
    }
    return null;
  }

  /**
   * Set from URL.
   * @param pathToDb
   *          - path
   * @throws SQLException
   *           - exception
   * @throws FileNotFoundException
   *           - exception
   */
  public static void setFromUrl(String pathToDb)
      throws SQLException, FileNotFoundException {
    File dbFile = new File(pathToDb);

    /* if database is already loaded, close connection to old one */
    if (dbFile.exists() && !(dbFile.isDirectory())) {
      if (DbHandler.getConnection() != null) {
        assert (getConnection() != null);
        connectionPool.close();
      }
      /* setting up connection pool */
      connectionPool = new BasicDataSource();
      connectionPool.setUrl(startOfPath + pathToDb);
      connectionPool.setInitialSize(10);
      Driver driver = DriverManager.getDriver(startOfPath + pathToDb);
      connectionPool.setDriver(driver);
      Statement stat = connectionPool.getConnection().createStatement();
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
    guiConnection = conn;
  }

  /**
   * Clear all tables.
   * @throws SQLException
   *           - exception
   */
  public static void clearAllTables() throws SQLException {
    clearUserTable();
    clearPartyTable();
    clearPartyAttendeeTable();
    clearRequestVotesTable();
    clearSongRequestTable();
  }

  /**
   * Clear user table.
   * @throws SQLException
   *           - exception
   */
  public static void clearUserTable() throws SQLException {
    String query = SqlStatements.CLEAR_USER_TABLE;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      int success = prep.executeUpdate();
    }
  }

  /**
   * Clear party table.
   * @throws SQLException
   *           - exception
   */
  public static void clearPartyTable() throws SQLException {
    String query = SqlStatements.CLEAR_PARTY_TABLE;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      int success = prep.executeUpdate();
    }
  }

  /**
   * Clear song request table.
   * @throws SQLException
   *           - exception
   */
  public static void clearSongRequestTable() throws SQLException {
    String query = SqlStatements.CLEAR_SONG_REQUEST_TABLE;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);
      int success = prep.executeUpdate();
    }
  }

  /**
   * Clear request votes table.
   * @throws SQLException
   *           - exception
   */
  public static void clearRequestVotesTable() throws SQLException {
    String query = SqlStatements.CLEAR_REQUEST_VOTES_TABLE;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);
      int success = prep.executeUpdate();
    }
  }

  /**
   * Clear party attendee table.
   * @throws SQLException
   *           - exception
   */
  public static void clearPartyAttendeeTable() throws SQLException {
    String query = SqlStatements.CLEAR_PARTY_ATTENDEE_TABLE;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      int success = prep.executeUpdate();
    }
  }

  /**
   * Adds user to database.
   * @param userId
   *          - id
   * @param email
   *          - email
   * @param name
   *          - name
   * @return User created
   * @throws SQLException
   *           - exception
   */
  public static User addUser(String userId, String email, String name,
      String type) throws SQLException {
    String query = SqlStatements.ADD_NEW_USER;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, userId);
      prep.setString(2, email);
      if (name == null) {
        name = userId;
        // prep.setString(3, name);
      }
      prep.setString(3, name);
      prep.setString(4, type);
      int success = prep.executeUpdate();
      if (success >= 1) {
        return User.of(userId, email, name, Type.valueOf(type));
      } else {
        throw new SQLException(
            "ERROR: Could not insert the user with the query " + query);
      }
    }
  }

  /**
   * Add song request to database.
   * @param spotifySongId
   *          - song id
   * @param partyId
   *          - party id
   * @param user
   *          - user that made request
   * @param time
   *          - time of request
   * @return Request made
   * @throws SQLException
   *           - exception
   */
  public static Request requestSong(String spotifySongId, int partyId,
      User user, LocalDateTime time) throws SQLException {
    String query = SqlStatements.ADD_SONG_REQUEST;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);
      String requestId = Request.getId(partyId, spotifySongId);
      prep.setString(1, requestId);
      prep.setString(2, spotifySongId);
      prep.setInt(3, partyId);
      prep.setString(4, user.getSpotifyId());
      prep.setString(5, time.toString());

      int success = prep.executeUpdate();
      if (success >= 1) {
        return Request.of(partyId, time, Song.of(spotifySongId), user,
            new HashSet<>(), new HashSet<>());
      } else {
        throw new SQLException(
            "ERROR: Could not insert the song with the query " + query);
      }
    }

  }

  /**
   * Add party to database.
   * @param playlist
   *          - playlist
   * @param name
   *          - name of party
   * @param coordinate
   *          - geocoordaintes of party
   * @param time
   *          - time of party
   * @param host
   *          - user host
   * @return Party created
   * @throws SQLException
   *           - exception
   */
  public static Party addParty(String playlistId, String name,
      Coordinate coordinate, LocalDateTime time, User host, String deviceId)
      throws SQLException {
    if (getActivePartyOfUser(host) != null) {
      throw new IllegalArgumentException(
          "ERROR: Host is already a host of another active party");
    }
    String query = SqlStatements.ADD_NEW_PARTY;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, playlistId);
      prep.setString(2, name);
      prep.setDouble(3, coordinate.getLat());
      prep.setDouble(4, coordinate.getLon());
      prep.setString(5, time.toString());
      prep.setString(6, deviceId);

      int success = prep.executeUpdate();
      if (success >= 1) {
        try (ResultSet generatedKeys = prep.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            int partyId = generatedKeys.getInt(1);

            addHost(partyId, host);

            return Party.of(partyId, name, playlistId, coordinate, time,
                Status.ongoing, deviceId);
          } else {
            throw new SQLException("Add PartyBean failed, no ID obtained.");
          }
        }
      } else {
        throw new SQLException(
            "ERROR: Could not insert the party with the query " + query);
      }
    }
  }

  /**
   * Add host to database.
   * @param partyId
   *          - party id
   * @param host
   *          - user host
   * @throws SQLException
   *           - exception
   */
  public static void addHost(int partyId, User host) throws SQLException {
    // TODO if your gonna have multiple hosts, add the check that he's not
    // hosting another ongoing party here.
    if (getActivePartyOfUser(host) != null) {
      throw new IllegalArgumentException(
          "ERROR: Host is already a host of another active party");
    }
    String query = SqlStatements.ADD_PARTY_HOST;
    try (Connection conn = getConnection()) {
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
  }

  /**
   * Move song request out of request list and to the playlist queue
   * @param request
   *          - request
   * @throws SQLException
   *           - exception
   */
  public static void moveSongRequestToQueue(Request request)
      throws SQLException {
    String query = SqlStatements.MOVE_SONG_REQUEST_TO_QUEUE;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, request.getId());

      int success = prep.executeUpdate();
      if (success < 1) {
        throw new SQLException(
            "ERROR: Remove song request with the query " + query);
      }
    }
  }

  public static void endParty(int partyId) throws SQLException {
    String query = SqlStatements.END_PARTY;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setInt(1, partyId);

      int success = prep.executeUpdate();
      if (success < 1) {
        throw new SQLException("ERROR: END party with this query " + query);
      }
    }
  }

  /**
   * Move song playlist to request list
   * @param request
   *          - request
   * @throws SQLException
   *           - exception
   */
  public static void moveSongRequestOutOfQueue(Request request)
      throws SQLException {
    String query = SqlStatements.MOVE_SONG_REQUEST_OUT_OF_QUEUE;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, request.getId());

      int success = prep.executeUpdate();
      if (success < 1) {
        throw new SQLException(
            "ERROR: Remove song request with the query " + query);
      }
    }
  }

  /**
   * Remove party from database.
   * @param party
   *          - party
   * @throws SQLException
   *           - exception
   */
  public static void removeParty(Party party) throws SQLException {
    String query = SqlStatements.REMOVE_PARTY;
    try (Connection conn = getConnection()) {
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
  }

  /**
   * Upvote request in database.
   * @param request
   *          - request that has been upvoted
   * @param user
   *          - user that upvoted request
   * @throws SQLException
   *           - exception
   */
  public static void upvoteRequest(Request request, User user)
      throws SQLException {
    String query = SqlStatements.UPVOTE_SONG_REQUEST;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      String reqId = request.getId();
      String userId = user.getSpotifyId();
      prep.setString(1, reqId);
      prep.setString(2, userId);

      int success = prep.executeUpdate();
      if (success < 1) {
        throw new SQLException(
            "ERROR: Could not upvote request with the query " + query);
      }
    }
  }

  /**
   * Downvote request in the database.
   * @param request
   *          - request that has been downvoted
   * @param user
   *          - user that downvoted
   * @throws SQLException
   *           - exception
   */
  public static void downvoteRequest(Request request, User user)
      throws SQLException {
    String query = SqlStatements.DOWNVOTE_SONG_REQUEST;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, request.getId());
      prep.setString(2, user.getSpotifyId());

      int success = prep.executeUpdate();
      if (success < 1) {
        throw new SQLException(
            "ERROR: Could not downvote the request with the query " + query);
      }
    }
  }

  /**
   * Remove vote from database.
   * @param request
   *          - request to remove vote from
   * @param user
   *          - user whose vote we want to remove
   * @throws SQLException
   *           - exception
   */
  public static void removeVote(Request request, User user)
      throws SQLException {
    String query = SqlStatements.REMOVE_VOTE_SONG_REQUEST;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, request.getId());
      prep.setString(2, user.getSpotifyId());

      int success = prep.executeUpdate();
      if (success < 1) {
        throw new SQLException(
            "ERROR: Could not remove request vote with the query " + query);
      }
    }
  }

  /**
   * Add guest to party in database.
   * @param partyId
   *          - party id
   * @param guest
   *          - user guest
   * @throws SQLException
   *           - exception
   */
  public static void addPartyGuest(int partyId, User guest)
      throws SQLException {
    if (getActivePartyOfUser(guest) != null) {
      throw new IllegalArgumentException(
          "ERROR: Host is already a host of another active party");
    }
    String query = SqlStatements.ADD_PARTY_GUEST;
    try (Connection conn = getConnection()) {
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
  }

  /**
   * Remove guest from party in database.
   * @param partyId
   *          - party id
   * @param guest
   *          - user guest
   * @throws SQLException
   *           - exception
   */
  public static void removePartyGuest(int partyId, User guest)
      throws SQLException {
    String query = SqlStatements.REMOVE_PARTY_GUEST;
    try (Connection conn = getConnection()) {
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
  }

  /**
   * Retrieve all requests in party.
   * @param partyId
   *          - id
   * @return list of requests in given party
   * @throws SQLException
   *           - exception
   */
  public static List<Request> getPartySongRequests(int partyId)
      throws SQLException {
    String query = SqlStatements.GET_PARTY_SONG_REQUESTS;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setInt(1, partyId);

      try (ResultSet rs = prep.executeQuery()) {
        List<Request> requests = new ArrayList<>();
        while (rs.next()) {
          int requestId = rs.getInt(1);
          String spotifySongId = rs.getString(2);
          String userId = rs.getString(4);
          String time = rs.getString(5);
          Request r = Request.of(requestId, Song.of(spotifySongId),
              User.of(userId), LocalDateTime.parse(time));
          requests.add(r);
        }
        return requests;
      }
    }
  }

  /**
   * Retrieve all party attendees.
   * @param partyId
   *          - party id
   * @return list of hosts and list of guests
   * @throws SQLException
   *           - exception
   */
  public static List<List<User>> getPartyHostsAndGuests(int partyId)
      throws SQLException {
    String query = SqlStatements.GET_ALL_PARTY_ATTENDEES;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setInt(1, partyId);

      try (ResultSet rs = prep.executeQuery()) {
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
    }
  }

  /**
   * Retrieve party from database.
   * @param partyId
   *          - id
   * @param playlist
   *          - playlist associated with party
   * @param name
   *          - name of party
   * @param location
   *          - location of party
   * @param time
   *          - time of party
   * @param status
   *          - status of party
   * @return party bean
   * @throws SQLException
   *           - exception
   */
  public static PartyBean getFullParty(int partyId, String playlistId,
      String name, Coordinate location, LocalDateTime time, Status status,
      String deviceId) throws SQLException {
    List<Request> requests = getPartySongRequests(partyId);
    List<List<User>> attendees = getPartyHostsAndGuests(partyId);
    assert attendees.size() == 2;
    User host = attendees.get(0).get(0);
    return new PartyBean(partyId, name, host,
        Playlist.of(playlistId, partyId, host), location, time,
        new HashSet<>(requests), new HashSet<>(attendees.get(1)), status,
        deviceId);

  }

  /**
   * Retrieve request from database.
   * @param id
   *          - request id
   * @param song
   *          - song associated with request
   * @param user
   *          - user of request
   * @param requestTime
   *          - request time
   * @return Request Bean
   * @throws SQLException
   *           - exception
   */
  public static RequestBean getFullRequest(int partyId, Song song, User user,
      LocalDateTime requestTime) throws SQLException {
    String query = SqlStatements.GET_VOTES_FOR_SONG;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);
      String requestId = Request.getId(partyId, song.getSpotifyId());
      prep.setString(1, requestId);

      try (ResultSet rs = prep.executeQuery()) {
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

        return new RequestBean(partyId, requestTime, song, user,
            new HashSet<>(upvotes), new HashSet<>(downvotes));
      }
    }

  }

  /**
   * Retrieve user from id.
   * @param spotifyId
   *          - id
   * @return - user of given id
   * @throws SQLException
   *           - exception
   */
  public static UserBean getUserWithId(String spotifyId) throws SQLException {
    String query = SqlStatements.GET_USER_FROM_ID;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, spotifyId);

      try (ResultSet rs = prep.executeQuery()) {
        // List<User> upvotes = new ArrayList<>();
        // List<User> downvotes = new ArrayList<>();
        if (rs.next()) {
          String email = rs.getString(2);
          String name = rs.getString(3);
          String type = rs.getString(4);

          return new UserBean(spotifyId, email, name, Type.valueOf(type));
        }

        throw new SQLException("ERROR: No User With id of  " + spotifyId);
      }
    }
  }

  /**
   * Get all parties of a user.
   * @param user
   *          - the user's parties we want
   * @return list of all parties of the given user
   * @throws SQLException
   *           - exception
   */
  public static List<Party> getUsersParties(User user) throws SQLException {
    String query = SqlStatements.GET_USER_PARTY;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, user.getSpotifyId());

      try (ResultSet rs = prep.executeQuery()) {

        List<Party> parties = new ArrayList<>();

        while (rs.next()) {
          int partyId = rs.getInt(1);
          String spotifyPlaylistId = rs.getString(2);
          String name = rs.getString(3);
          double lat = rs.getDouble(4);
          double lon = rs.getDouble(5);
          String time = rs.getString(6);
          String status = rs.getString(7);
          String deviceId = rs.getString(8);

          Party p = Party.of(partyId, name, spotifyPlaylistId,
              new Coordinate(lat, lon), LocalDateTime.parse(time),
              Status.valueOf(status), deviceId);
          parties.add(p);
        }
        return parties;
      }
    }

  }

  public static List<Party> getAllActiveParties() throws SQLException {
    String query = SqlStatements.GET_ALL_ACTIVE_PARTIES;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      try (ResultSet rs = prep.executeQuery()) {
        List<Party> parties = new ArrayList<>();

        while (rs.next()) {
          int partyId = rs.getInt(1);
          String spotifyPlaylistId = rs.getString(2);
          String name = rs.getString(3);
          double lat = rs.getDouble(4);
          double lon = rs.getDouble(5);
          String time = rs.getString(6);
          String status = rs.getString(7);
          String deviceId = rs.getString(8);
          Party p = Party.of(partyId, name, spotifyPlaylistId,
              new Coordinate(lat, lon), LocalDateTime.parse(time),
              Status.valueOf(status), deviceId);
          parties.add(p);
        }
        return parties;
      }
    }
  }

  /**
   * Gets the party from the id.
   * @param partyId
   *          The id of the party.
   * @return The party of the partyId
   * @throws SQLException
   *           If there is an issue with the database connection.
   */
  public static Party getPartyFromId(int partyId) throws SQLException {
    String query = SqlStatements.GET_PARTY_FROM_ID;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setInt(1, partyId);

      try (ResultSet rs = prep.executeQuery()) {

        if (rs.next()) {
          String spotifyPlaylistId = rs.getString(2);
          String name = rs.getString(3);
          double lat = rs.getDouble(4);
          double lon = rs.getDouble(5);
          String time = rs.getString(6);
          String status = rs.getString(7);
          String deviceId = rs.getString(8);
          Party p = Party.of(partyId, name, spotifyPlaylistId,
              new Coordinate(lat, lon), LocalDateTime.parse(time),
              Status.valueOf(status), deviceId);
          return p;
        }
        return null;
      }
    }
  }

  public static Party getActivePartyOfUser(User user) throws SQLException {
    String query = SqlStatements.GET_ACTIVE_PARTY_OF_USER;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, user.getSpotifyId());

      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          int partyId = rs.getInt(1);
          String spotifyPlaylistId = rs.getString(2);
          String name = rs.getString(3);
          double lat = rs.getDouble(4);
          double lon = rs.getDouble(5);
          String time = rs.getString(6);
          String status = rs.getString(7);
          String deviceId = rs.getString(8);

          Party p = Party.of(partyId, name, spotifyPlaylistId,
              new Coordinate(lat, lon), LocalDateTime.parse(time),
              Status.valueOf(status), deviceId);
          assert rs.next() == false;
          return p;
        }
        return null;
      }
    }
  }

  /**
   * Get party hosted by the passed in user.
   * @param user
   *          - user's party we are trying to retrieve
   * @return - party
   * @throws SQLException
   *           - exception
   */
  public static Party getPartyHostedByUser(User user) throws SQLException {
    String query = SqlStatements.GET_PARTY_HOSTED_BY_USER;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, user.getSpotifyId());

      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          int partyId = rs.getInt(1);
          String spotifyPlaylistId = rs.getString(2);
          String name = rs.getString(3);
          double lat = rs.getDouble(4);
          double lon = rs.getDouble(5);
          String time = rs.getString(6);
          String status = rs.getString(7);
          String deviceId = rs.getString(8);
          Party p = Party.of(partyId, name, spotifyPlaylistId,
              new Coordinate(lat, lon), LocalDateTime.parse(time),
              Status.valueOf(status), deviceId);
          assert rs.next() == false;
          return p;
        }
        return null;
      }
    }
  }

  /**
   * Retrieve queued songs.
   * @param playlistId
   *          - playlist id
   * @param partyId
   *          - party id
   * @return - playlist
   * @throws SQLException
   *           - exception
   */
  public static PlaylistBean getQueuedSongsForParty(String playlistId,
      int partyId, User host) throws SQLException {
    String query = SqlStatements.GET_PARTY_QUEUED_SONG_REQUESTS;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setInt(1, partyId);

      try (ResultSet rs = prep.executeQuery()) {
        List<Request> queue = new ArrayList<>();
        while (rs.next()) {
          int requestId = rs.getInt(1);
          String spotifySongId = rs.getString(2);
          String userId = rs.getString(4);
          String time = rs.getString(5);
          Request r = Request.of(requestId, Song.of(spotifySongId),
              User.of(userId), LocalDateTime.parse(time));
          queue.add(r);
        }
        return new PlaylistBean(playlistId, partyId, queue, host);
      }
    }

  }

  public static void AddSongToFavorites(String userId, String songId)
      throws SQLException {
    String query = SqlStatements.ADD_SONG_TO_FAVORITES;
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, userId);
      prep.setString(2, songId);
      int success = prep.executeUpdate();
      if (success < 1) {
        throw new SQLException(
            "ERROR: Could not upvote request with the query " + query);
      }
    }

  }

  public static List<Song> GetUserFavoritedSongs(String userId)
      throws SQLException {
    String query = SqlStatements.GET_USER_FAVORITES;
    List<Song> songs = new ArrayList<Song>();
    try (Connection conn = getConnection()) {
      if (conn == null) {
        throw new SQLException("ERROR: No database has been set.");
      }
      PreparedStatement prep = conn.prepareStatement(query);

      prep.setString(1, userId);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          String songId = rs.getString(1);
          Song s = Song.of(songId);
          songs.add(s);
        }
      }
    }
    return songs;
  }

}

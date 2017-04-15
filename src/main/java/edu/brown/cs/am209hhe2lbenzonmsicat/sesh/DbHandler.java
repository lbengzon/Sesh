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
import java.util.concurrent.ConcurrentHashMap;

public class DbHandler {

  private static Connection guiConnection = null;
  private static String startOfPath = "jdbc:sqlite:";
  private static ThreadLocal<Connection> connections = new ThreadLocal<>();
  private static ConcurrentHashMap<String, PreparedStatement> statementMap = new ConcurrentHashMap<String, PreparedStatement>();

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
        for (String key : statementMap.keySet()) {
          statementMap.get(key).close();
        }
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

  public static ThreadLocal<Connection> getAllConnections() {
    return connections;
  }

  public static User addUser(String userId, String email, String name)
      throws SQLException {
    String query = SqlStatements.ADD_NEW_USER;
    PreparedStatement prep = statementMap.get(query);
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    if (prep == null) {
      prep = conn.prepareStatement(query);
      statementMap.put(query, prep);
    }
    prep.setString(1, userId);
    prep.setString(2, email);
    prep.setString(3, name);

    int success = prep.executeUpdate();
    if (success >= 1) {
      return new User(userId, email, name, name);
    } else {
      throw new SQLException(
          "ERROR: Could not insert the user with the query " + query);
    }
  }

  public static Request requestSong(String spotifySongId, String partyId,
      String userId, String time) throws SQLException {
    String query = SqlStatements.ADD_SONG_REQUEST;
    PreparedStatement prep = statementMap.get(query);
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    if (prep == null) {
      prep = conn.prepareStatement(query);
      statementMap.put(query, prep);
    }
    prep.setString(1, spotifySongId);
    prep.setString(2, partyId);
    prep.setString(3, userId);
    prep.setString(4, time);

    int success = prep.executeUpdate();
    if (success >= 1) {
      try (ResultSet generatedKeys = prep.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          int requestId = generatedKeys.getInt(1);
          return new Request(requestId, time, Song.ofId(spotifySongId),
              User.ofId(userId));
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
    String query = SqlStatements.ADD_NEW_PARTY;
    PreparedStatement prep = statementMap.get(query);
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    if (prep == null) {
      prep = conn.prepareStatement(query);
      statementMap.put(query, prep);
    }
    prep.setString(1, spotifyPlaylistId);
    prep.setString(2, name);
    prep.setDouble(3, coordinate.getLat());
    prep.setDouble(4, coordinate.getLon());
    prep.setString(4, time);

    int success = prep.executeUpdate();
    if (success >= 1) {
      try (ResultSet generatedKeys = prep.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          int partyId = generatedKeys.getInt(1);

          AddHost(partyId, host);

          return new Party(partyId, host, new Playlist(spotifyPlaylistId));
        } else {
          throw new SQLException("Add Party failed, no ID obtained.");
        }
      }
    } else {
      throw new SQLException(
          "ERROR: Could not insert the party with the query " + query);
    }
  }

  public static void AddHost(int partyId, User host) throws SQLException {
    String query = SqlStatements.ADD_PARTY_HOST;
    PreparedStatement prep = statementMap.get(query);
    Connection conn = getConnection();
    if (conn == null) {
      throw new SQLException("ERROR: No database has been set.");
    }
    if (prep == null) {
      prep = conn.prepareStatement(query);
      statementMap.put(query, prep);
    }
    prep.setInt(1, partyId);
    prep.setString(2, host.getId());

    int success = prep.executeUpdate();
    if (success < 1) {
      throw new SQLException(
          "ERROR: Could not add the host with the query " + query);
    }
  }

}

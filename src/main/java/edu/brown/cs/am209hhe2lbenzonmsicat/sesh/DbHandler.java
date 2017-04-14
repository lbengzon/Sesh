package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

  /**
   * Gets the intersection node between two streets.
   * @param streetName1
   *          The first street name
   * @param streetName2
   *          The second street name
   * @return The node at the intersection. Returns Null if there is no
   *         intersection;
   * @throws SQLException
   *           If there is a problem accessing the database.
   */
  // public static MapNode getIntersection(String streetName1, String
  // streetName2)
  // throws SQLException {
  // String query = "SELECT a.start, a.end, b.start, b.end FROM "
  // + "(way AS a JOIN way AS b ON "
  // + "(a.start=b.start OR a.start=b.end OR "
  // + "a.end=b.start OR a.end = b.end)) WHERE a.name = ? "
  // + "AND b.name = ? LIMIT 1";
  //
  // PreparedStatement prep = statementMap.get(query);
  // if (getConnection() == null) {
  // throw new SQLException("ERROR: No database has been set.");
  // }
  // Connection conn = getConnection();
  // if (prep == null) {
  // prep = conn.prepareStatement(query);
  // statementMap.put(query, prep);
  // }
  // prep.setString(1, streetName1);
  // prep.setString(2, streetName2);
  // ResultSet rs = prep.executeQuery();
  // if (rs.next()) {
  // String start1 = rs.getString(1);
  // String end1 = rs.getString(2);
  // String start2 = rs.getString(3);
  // String end2 = rs.getString(4);
  // if (start1.equals(start2) || start1.equals(end2)) {
  // return MapNode.ofId(start1);
  // } else {
  // assert end1.equals(start2) || end1.equals(end2);
  // return MapNode.ofId(end1);
  // }
  // }
  // // there is no intersection between the two.
  // return null;
  // }

}

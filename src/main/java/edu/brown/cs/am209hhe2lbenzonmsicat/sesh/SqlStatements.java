package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

/**
 * A class that holds useful sql statements for bacon.
 * @author leandro
 */
final class SqlStatements {
  private SqlStatements() {

  }

  /**
   * Adds a new user into the user table.
   */
  public static final String ADD_NEW_USER = "INSERT INTO \"User\" "
      + "(userId, email, name) VALUES (?, ?, ?);";

  /**
   * Adds a new song request into the song request table.
   */
  public static final String ADD_SONG_REQUEST = "INSERT INTO \"SongRequest\""
      + " (spotifySongId, partyId, userId, time) VALUES (?, ?, ?, ?);";

  /**
   * Adds a new party to the party table with the status of "ongoing".
   */
  public static final String ADD_NEW_PARTY = "INSERT INTO Party "
      + "(spotifyPlaylistId, name, lat, lon, time, status) VALUES (?, ?, ?, ?, ?, 'ongoing');";

  /**
   * Removes a song from the Song Request table.
   */
  public static final String REMOVE_SONG_REQUEST = "DELETE FROM SongRequest WHERE requestId = ?;";

  /**
   * Removes a party from the party table.
   */
  public static final String REMOVE_PARTY = "DELETE FROM Party WHERE partyId = ?;";

  /**
   * Adds an entry to the Vote table with type "upvote".
   */
  public static final String UPVOTE_SONG_REQUEST = "INSERT INTO RequestVotes"
      + " (requestId, userId, type) VALUES (?, ?, 'upvote');";

  /**
   * Removes the vote for the song request. Since there can only be 1 vote per
   * user and request id, we dont need specific remove down votes and remove
   * upvotes.
   */
  public static final String REMOVE_VOTE_SONG_REQUEST = "DELETE FROM RequestVotes"
      + " WHERE requestId=? AND userId=?;";

  /**
   * Adds an entry to the vote table with the "downvote" type.
   */
  public static final String DOWNVOTE_SONG_REQUEST = "INSERT INTO RequestVotes"
      + " (requestId, userId, type) VALUES (?, ?, 'downvote');";

  /**
   * Adds an entry to the party attendee table with type host.
   */
  public static final String ADD_PARTY_HOST = "INSERT INTO PartyAttendee"
      + " (partyId, userId, type) VALUES (?, ?, 'host');";

  /**
   * Adds an entry to the party attendee table with type guest.
   */
  public static final String ADD_PARTY_GUEST = "INSERT INTO PartyAttendee"
      + " (partyId, userId, type) VALUES (?, ?, 'guest');";

  /**
   * Removes the guest from the party.
   */
  public static final String REMOVE_PARTY_GUEST = "DELETE FROM PartyAttendee"
      + " WHERE partyId=? AND userId=? AND type='guest';";

  /**
   * Gets all the attendees of a party.
   */
  public static final String GET_ALL_PARTY_ATTENDEES = "SELECT * FROM PartyAttendee"
      + " WHERE partyId=?;";

  /**
   * Gets all the song requests of a party.
   */
  public static final String GET_PARTY_SONG_REQUESTS = "SELECT * FROM SongRequest"
      + " WHERE partyId=?;";

  /**
   * Gets all the parties.
   */
  public static final String GET_ALL_PARTIES = "SELECT * FROM Party;";

  /**
   * Gets all the votes (up and down) for a song.
   */
  public static final String GET_VOTES_FOR_SONG = "SELECT * FROM RequestVotes "
      + "WHERE requestId=?;";

  /**
   * Gets the user information given the userid.
   */
  public static final String GET_USER_FROM_ID = "SELECT * FROM User WHERE userId=?;";

  /**
   * Gets all the parties that the user has attended or is hosting/attending.
   */
  public static final String GET_USER_PARTY = "SELECT Party.PartyId, spotifyPlaylistId, name, lat, lon, time, status FROM PartyAttendee, Party"
      + " WHERE PartyAntendee.partyId=Party.partyId AND userId=?;";

}

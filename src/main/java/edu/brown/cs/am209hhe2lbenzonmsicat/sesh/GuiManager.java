package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Gui Manager class.
 *
 * @author HE23
 */
public class GuiManager {
  private SpotifyCommunicator comm = new SpotifyCommunicator();
  private static final Gson GSON = new Gson();

  /**
   * Default constructor.
   *
   * @param freeMarkerEngine
   *          - freemarker engine
   */
  public GuiManager(FreeMarkerEngine freeMarkerEngine) {
    installRoutes(freeMarkerEngine);
    comm.createAuthorizeURL();
  }

  private void installRoutes(FreeMarkerEngine fme) {
    Spark.get("/login", new LoginHandler(), fme);
    Spark.get("/spotifycallback", new CallbackHandler(), fme);
    Spark.get("/sesh", new FrontHandler(), fme);
    Spark.post("/create", new PartySettingsHandler(), fme);
    Spark.post("/join", new JoinHandler(), fme);
    Spark.post("/create/party", new CreatePartyHandler(), fme);
    Spark.post("/join/party", new JoinPartyHandler(), fme);
    Spark.post("/search", new SearchHandler());
  }

  /**
   * Displays login page. Backend sends authorization URL to frontend and
   * displays that link as button. After logging in, redirects to
   * /spotifycallback.
   *
   */
  private class LoginHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Login",
          "authURL", comm.createAuthorizeURL());
      return new ModelAndView(variables, "login.ftl");
    }
  }

  /**
   * Homepage end point, where user enters in their login credentials.
   */
  private class CallbackHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String ftlPage = null;
      String code = qm.value("code");
      User user;
      List<String> userInfo = comm.getAccessToken(code);
      String userId = userInfo.get(0);
      String userEmail = userInfo.get(1);
      String userName = userInfo.get(2);
      System.out.println("USER ID: " + userId);
      try {
        user = User.create(userId, userEmail, userName);
        ftlPage = "createJoin.ftl";
      } catch (SQLException e) {
        /* user already exists */
        user = User.of(userId);
        // TODO check if user is currently attending a party, and if so must
        // redirect to that party's view
        ftlPage = "createJoin.ftl";
      }

      Map<String, Object> variables = ImmutableMap.of("title", "Sesh");
      return new ModelAndView(variables, ftlPage);
    }
  }

  /**
   * Handles request to join a sesh page.
   *
   * @author HE23
   */
  private static class JoinHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables;
      QueryParamsMap qm = req.queryMap();
      String lat = qm.value("lat");
      String lon = qm.value("lon");
      System.out.println("lat:" + lat);
      System.out.println("lon:" + lon);

      if (lat != null && lon != null) {
        Coordinate coord = new Coordinate(Double.valueOf(lat),
            Double.valueOf(lon));
        List<Party> parties = Party.getActivePartiesWithinDistance(coord,
            Constants.PARTY_JOIN_RADIUS);

        variables = ImmutableMap.of("title", "Join a Sesh", "parties", parties);
      } else {
        variables = ImmutableMap.of("title", "Join a Sesh");
      }

      return new ModelAndView(variables, "join.ftl");
    }
  }

  /**
   * Handles joining a party.
   */
  private class JoinPartyHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      /*
       * Get party id from front end (from the one that user clicks on) Add user
       * to party id that user clicks on. Add them to party, update java and db
       * Send back party info (id, name, host, etc.) to display party.
       *
       */
      // int partyId = Integer.valueOf(qm.value("party_id")); // from frontend
      // String userId = qm.value("user_id"); // from frontend
      //
      // User user = User.of(userId);
      // Party party = Party.of(partyId); // modify method later
      // party.addGuest(user);

      // Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh",
      // "userId", userId, "partyId", partyId, "partyName", party.getName(),
      // "hostId", party.getHost().getSpotifyId());
      Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh");
      return new ModelAndView(variables, "joinParty.ftl");
    }
  }

  /**
   * Handles request to create a sesh page.
   *
   * @author HE23
   */
  private static class PartySettingsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {

      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("user_id");

      Map<String, Object> variables = ImmutableMap.of("title", "Create a Sesh",
          "user_id", userId);

      return new ModelAndView(variables, "partySettings.ftl");
    }
  }

  /**
   * Handles displaying newly created party.
   */
  private class CreatePartyHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      // String userId = qm.value("user_id"); // from frontend
      String partyName = qm.value("sesh_name"); // required
      String hostName = qm.value("host_name");
      String privacyStatus = qm.value("privacy_setting");
      String time = String.valueOf(System.currentTimeMillis() / 1000);

      String lat = qm.value("lat");
      String lon = qm.value("lon");

      Coordinate coord = new Coordinate(Double.valueOf(lat),
          Double.valueOf(lon));
      Party party = null;

      // try {
      // User host = User.of(userId);
      // party = Party.create(partyName, host, coord, time);
      // } catch (SQLException e) {
      // System.out.println("Failed to add party to database");
      // }

      System.out.println("PARTY NAME: " + partyName);
      System.out.println("HOST NAME: " + hostName);
      System.out.println("PRIVACY SETTINGS: " + privacyStatus);
      System.out.println("LAT: " + lat);
      System.out.println("LON: " + lon);

      Map<String, Object> variables = ImmutableMap.of("title", "Sesh Settings",
          "partyName", partyName);
      return new ModelAndView(variables, "createParty.ftl");
    }

  }

  /**
   * Handles request to front page (join or create a sesh).
   *
   * @author HE23
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("user_id");
      Map<String, Object> variables = ImmutableMap.of("title", "Sesh");
      return new ModelAndView(variables, "createJoin.ftl");
    }
  }

  /**
   * Handles displaying search results.
   *
   * @author HE23
   *
   */
  private static class SearchHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String input = qm.value("userInput");
      List<Track> results = SpotifyCommunicator.searchTracks(input);
      List<String> names = new ArrayList<>();
      for (Track t : results) {
        StringBuilder item = new StringBuilder(t.getName());
        item.append(" - ");
        for (SimpleArtist artist : t.getArtists()) {
          item.append(artist.getName() + ", ");
        }
        item.delete(item.length() - 2, item.length() - 1);
        names.add(item.toString());
      }
      Map<String, Object> variables = ImmutableMap.of("results", names);
      return GSON.toJson(variables);
    }
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.time.LocalDateTime;
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
 * Gui Manager class. <<<<<<< HEAD ======= >>>>>>>
 * 95539040b5146fa67d5bb15373dd5c2eb0fd6ea0
 * @author HE23
 */
public class GuiManager {
  private SpotifyCommunicator comm = new SpotifyCommunicator();
  private static final Gson GSON = new Gson();

  /**
   * Default constructor. <<<<<<< HEAD ======= >>>>>>>
   * 95539040b5146fa67d5bb15373dd5c2eb0fd6ea0
   * @param freeMarkerEngine
   *          - freemarker engine
   */
  public GuiManager(FreeMarkerEngine freeMarkerEngine) {
    installRoutes(freeMarkerEngine);
    comm.createAuthorizeURL();
  }

  private void installRoutes(FreeMarkerEngine fme) {
    Spark.webSocket("/update", PartyWebsocket.class);
    Spark.get("/login", new LoginHandler(), fme);
    Spark.get("/spotifycallback", new CallbackHandler(), fme);
    Spark.post("/create", new PartySettingsHandler(), fme);
    Spark.post("/join", new JoinHandler(), fme);
    Spark.post("/join2", new JoinHandler2());
    Spark.post("/getParty", new GetPartyHandler());
    Spark.post("/create/party", new CreatePartyHandler(), fme);
    Spark.post("/join/party", new JoinPartyHandler(), fme);
    Spark.post("/search", new SearchHandler());
    Spark.post("/devices", new DevicesHandler());
    // Spark.post("/currentSong", new CurrentSongHandler());
    Spark.get("/error", new ErrorHandler(), fme);
  }

  private static class ErrorHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Error!");
      return new ModelAndView(variables, "error.ftl");
    }
  }

  /**
   * Displays login page. Backend sends authorization URL to frontend and
   * displays that link as button. After logging in, redirects to
   * /spotifycallback.
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
      String type = userInfo.get(3);
      Map<String, Object> variables = ImmutableMap.of("title", "Sesh", "userId",
          userId);
      try {
        ftlPage = "createJoin.ftl";
        user = User.create(userId, userEmail, userName, type);
      } catch (SQLException e) {
        /* user already exists */
        user = User.of(userId);
        Party p = Party.getActivePartyOfUser(user);

        /* user is not part of an active party */
        if (p == null) {
          ftlPage = "createJoin.ftl";
        } else {
          assert p != null;
          /* user is host */
          if (p.getHost().equals(user)) {
            ftlPage = "createParty.ftl";
            int partyId = p.getPartyId();
            String partyName = p.getName();
            variables = ImmutableMap.of("title", partyName, "userId", userId,
                "partyId", partyId, "partyName", partyName);
          } else {
            assert p.getGuests().contains(user);
            ftlPage = "joinParty.ftl";
            int partyId = p.getPartyId();
            String partyName = p.getName();
            variables = ImmutableMap.of("title", partyName, "userId", userId,
                "partyId", partyId, "partyName", partyName);
          }

        }

        // ftlPage = "createJoin.ftl";
      }

      return new ModelAndView(variables, ftlPage);
    }
  }

  private static class JoinHandler2 implements Route {
    @Override
    public String handle(Request req, Response res) {
      System.out.println("Running post request to get list of active parties");
      QueryParamsMap qm = req.queryMap();
      String lat = qm.value("latitude");
      String lon = qm.value("longitude");

      List<String> parties = new ArrayList<>();
      List<Integer> partyIds = new ArrayList<>();

      System.out.println("lat " + lat + " lon " + lon);

      if (lat != null && lon != null) {
        Coordinate coord = new Coordinate(Double.valueOf(lat),
            Double.valueOf(lon));
        List<Party> p = Party.getActivePartiesWithinDistance(coord,
            Constants.PARTY_JOIN_RADIUS);
        for (Party party : p) {
          parties.add(party.getName());
          partyIds.add(party.getPartyId());
        }
      }

      Map<String, Object> variables = ImmutableMap.of("parties", parties,
          "partyIds", partyIds);

      return GSON.toJson(variables);
    }
  }

  /**
   * Handles request to join a sesh page.
   * @author HE23
   */
  private static class JoinHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      try {
        QueryParamsMap qm = req.queryMap();
        String userId = qm.value("joinUserId");

        Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh");

        if (userId != null) {
          variables = ImmutableMap.of("title", "Join a Sesh", "userId", userId);
        }

        return new ModelAndView(variables, "join.ftl");
      } catch (

      Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  /**
   * Handles joining a party.
   */
  private class JoinPartyHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      // success
      String userId = qm.value("userId");
      String partyId = qm.value("partyId");
      System.out.println("partyid: " + partyId);

      User user = User.of(userId);
      Party party = Party.of(Integer.valueOf(partyId));
      if (!party.getAttendees().contains(user)) {
        party.addGuest(user);
      }

      // should probably get party name from previous page to display on guest's
      // party view

      Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh",
          "userId", userId, "partyId", partyId);
      return new ModelAndView(variables, "joinParty.ftl");
    }
  }

  /**
   * Handles request to create a sesh page.
   * @author HE23
   */
  private static class PartySettingsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("createUserId");
      Map<String, Object> variables = ImmutableMap.of("title", "Create a Sesh",
          "userId", userId);

      return new ModelAndView(variables, "partySettings.ftl");
    }
  }

  /**
   * Creates party in the backend.
   * @author HE23
   */
  private class GetPartyHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("userId");
      String partyName = qm.value("sesh_name"); // required
      String hostName = qm.value("host_name");
      String privacyStatus = qm.value("privacy_setting"); // add to Party.create
      String lat = qm.value("lat");
      String lon = qm.value("lon");
      String deviceId = qm.value("deviceId");

      Party party = null;
      int partyId = -1;
      System.out.println("lat " + lat);
      System.out.println("lon " + lon);

      Coordinate coord = new Coordinate(Double.valueOf(lat),
          Double.valueOf(lon));

      Map<String, Object> variables = ImmutableMap.of("partyId", "",
          "partyName", partyName, "userId", userId);

      try {
        User host = User.of(userId);
        System.out.println("got the user");
        party = Party.create(partyName, host, coord, LocalDateTime.now(),
            deviceId);
        System.out.println("created the party");
        partyId = party.getPartyId();
        variables = ImmutableMap.of("partyId", partyId, "partyName", partyName,
            "userId", userId);
        System.out.println("successfully created party in backend");
      } catch (SQLException e) {
        System.out.println("Failed to add party to database");
      } catch (SpotifyUserApiException e) {
        // TODO SEND USER TO THE LOGIN PAGE
        e.printStackTrace();
      }

      System.out.println("reached end!!!!!");
      return GSON.toJson(variables);

    }
  }

  /**
   * Handles displaying newly created party.
   */
  private class CreatePartyHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("userId");
      String partyId = qm.value("partyId");
      String partyName = qm.value("partyName"); // required
      System.out.println("======In create party handler======");
      System.out.println("userId: " + userId);
      System.out.println("partyId: " + partyId);
      System.out.println("partyName: " + partyName);
      Map<String, Object> variables = ImmutableMap.of("title", "Sesh Settings",
          "partyId", partyId, "partyName", partyName, "userId", userId);
      return new ModelAndView(variables, "createParty.ftl");
    }

  }

  /**
   * Handles displaying search results.
   * @author HE23
   */
  private static class SearchHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      try {
        QueryParamsMap qm = req.queryMap();
        String input = qm.value("userInput");
        List<Track> results = SpotifyCommunicator.searchTracks(input, true);
        List<String> names = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Track t : results) {
          ids.add(t.getId());
          StringBuilder item = new StringBuilder(t.getName());
          item.append(" - ");
          for (SimpleArtist artist : t.getArtists()) {
            item.append(artist.getName() + ", ");
          }
          item.delete(item.length() - 2, item.length() - 1);
          names.add(item.toString());
        }

        Map<String, Object> variables = ImmutableMap.of("results", names,
            "songIds", ids);
        return GSON.toJson(variables);
      } catch (Exception c) {
        c.printStackTrace();
      }
      Map<String, Object> variables = ImmutableMap.of("results",
          new ArrayList<String>(), "songIds", new ArrayList<String>());
      return GSON.toJson(variables);
    }
  }

  private static class DevicesHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      try {
        QueryParamsMap qm = req.queryMap();
        String userId = qm.value("userId");
        List<Device> devices = User.of(userId).getDevices();
        Map<String, Object> variables = ImmutableMap.of("success", true,
            "devices", devices);
        return GSON.toJson(variables);
      } catch (Exception c) {
        c.printStackTrace();
        Map<String, Object> variables = ImmutableMap.of("success", false,
            "message", c.getMessage());
        return GSON.toJson(variables);
      }
    }
  }

  // /**
  // * Handles returning the current song being played at the party. Will return
  // * null if no current song is playing.
  // *
  // * @author HE23
  // */
  // private static class CurrentSongHandler implements Route {
  // @Override
  // public String handle(Request req, Response res) {
  // try {
  // QueryParamsMap qm = req.queryMap();
  // int partyId = Integer.parseInt(qm.value("partyId"));
  // Party p = Party.of(partyId);
  // Song curr = p.getSongBeingCurrentlyPlayed();
  // Map<String, Object> variables;
  // if (curr != null) {
  // String requestId = edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Request
  // .getId(p.getPartyId(), curr.getSpotifyId());
  // System.out.println("CURR SONG: " + requestId);
  // variables = ImmutableMap.of("currentSong", requestId);
  // } else {
  // variables = ImmutableMap.of("currentSong", null);
  // }
  // return GSON.toJson(variables);
  // } catch (Exception c) {
  // c.printStackTrace();
  // }
  // Map<String, Object> variables = ImmutableMap.of("currentSong", null);
  // return GSON.toJson(variables);
  // }
  // }

}

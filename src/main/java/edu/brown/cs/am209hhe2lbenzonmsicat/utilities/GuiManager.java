package edu.brown.cs.am209hhe2lbenzonmsicat.utilities;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import edu.brown.cs.am209hhe2lbenzonmsicat.models.Coordinate;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Device;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party.AccessType;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Song;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Constants;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * <<<<<<< HEAD Gui Manager class. ======= <<<<<<< HEAD Gui Manager class.
 * <<<<<<< HEAD ======= >>>>>>> 95539040b5146fa67d5bb15373dd5c2eb0fd6ea0 =======
 * Gui Manager class. >>>>>>> 3c9453bc42a1b742af1d7554f4cb30503c82ca95 >>>>>>>
 * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
 * 
=======
 *
>>>>>>> finished requests highlighting
 * @author HE23
 */
public class GuiManager {
  private SpotifyCommunicator comm = new SpotifyCommunicator();
  private static final Gson GSON = new Gson();

  /**
   * <<<<<<< HEAD Default constructor. ======= <<<<<<< HEAD Default constructor.
   * <<<<<<< HEAD ======= >>>>>>> 95539040b5146fa67d5bb15373dd5c2eb0fd6ea0
   * ======= Default constructor. >>>>>>>
   * 3c9453bc42a1b742af1d7554f4cb30503c82ca95 >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * 
=======
   *
>>>>>>> finished requests highlighting
   * @param freeMarkerEngine
   *          - freemarker engine
   */
  public GuiManager(FreeMarkerEngine freeMarkerEngine) {
    installRoutes(freeMarkerEngine);
    comm.createAuthorizeURL();
  }

  private void installRoutes(FreeMarkerEngine fme) {
    Spark.webSocket("/update", PartyWebsocket.class);
    Spark.get("/", new LoginHandler(), fme);
    Spark.get("/spotifycallback", new CallbackHandler(), fme);
    Spark.post("/create", new PartySettingsHandler(), fme);
    Spark.post("/join", new JoinHandler(), fme);
    Spark.post("/join2", new GetPartiesWithinRange());
    Spark.post("/joinParty", new JoinPartyHandler());
    Spark.post("/getParty", new GetPartyHandler());
    Spark.post("/create/party", new CreatePartyHandler(), fme);
    Spark.post("/join/party", new GuestViewHandler(), fme);
    Spark.post("/search", new SearchHandler());
    Spark.post("/devices", new DevicesHandler());
    Spark.post("/getactiveparty", new ActivePartyHandler());
    Spark.get("/error", new ErrorHandler(), fme);
    Spark.get("/leaveparty", new LeavePartyHandler(), fme);
    Spark.post("/addSongToFavorites", new AddFavoriteHandler());
    Spark.get("/createjoin", new CreateJoinHandler(), fme);
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
   * Handles the homepage, where users enter their credentials.
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * 
=======
   *
>>>>>>> finished requests highlighting
   * @author Matt
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
        user = User.create(userId, userEmail, userName, type);
      } catch (SQLException e) {
        /* user already exists */
        user = User.of(userId);
      }

      return new ModelAndView(variables, "callback.ftl");
    }
  }

  /**
   * Handles the create join page.
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * 
=======
   *
>>>>>>> finished requests highlighting
   * @author Matt
   */
  private static class CreateJoinHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String ftlPage;
      String userId = qm.value("userId");
      User user = User.of(userId);
      Party p = Party.getActivePartyOfUser(user);

      Map<String, Object> variables = ImmutableMap.of("title", "Sesh", "userId",
          userId);

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

      return new ModelAndView(variables, ftlPage);

    }

  }

  private static class GetPartiesWithinRange implements Route {
    @Override
    public String handle(Request req, Response res) {
      System.out.println("Running post request to get list of active parties");
      QueryParamsMap qm = req.queryMap();
      String lat = qm.value("latitude");
      String lon = qm.value("longitude");

      System.out.println("lat " + lat + " lon " + lon);
      List<Map<String, Object>> partyMapsToReturn = new ArrayList<>();
      if (lat != null && lon != null) {
        Coordinate coord = new Coordinate(Double.valueOf(lat),
            Double.valueOf(lon));
        List<Party> parties = Party.getActivePartiesWithinDistance(coord,
            Constants.PARTY_JOIN_RADIUS);
        for (Party party : parties) {
          Map<String, Object> partyMap = new HashMap<>();
          partyMap.put("name", party.getName());
          partyMap.put("partyId", party.getPartyId());
          partyMap.put("accessType", party.getAccessType());
          partyMapsToReturn.add(partyMap);
        }
      }
      Map<String, Object> variables = ImmutableMap.of("parties",
          partyMapsToReturn);
      return GSON.toJson(variables);
    }
  }

  /**
   * <<<<<<< HEAD Handles request to join a sesh page. ======= Handles request
   * to join a sesh page. <<<<<<< HEAD ======= >>>>>>>
   * 3c9453bc42a1b742af1d7554f4cb30503c82ca95 >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * 
=======
   *
>>>>>>> finished requests highlighting
   * @author HE23
   */
  private static class JoinHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String ftlPage = "join.ftl";
      try {
        QueryParamsMap qm = req.queryMap();
        String userId = qm.value("joinUserId");

        Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh");

        if (userId != null) {
          variables = ImmutableMap.of("title", "Join a Sesh", "userId", userId);
        }

        return new ModelAndView(variables, "join.ftl");
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  /**
   * Handles joining a party.
   */
  private class GuestViewHandler implements TemplateViewRoute {
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
        Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh",
            "userId", userId);
        return new ModelAndView(variables, "join.ftl");

      }

      // should probably get party name from previous page to display on
      // guest's
      // party view

      Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh",
          "userId", userId, "partyId", partyId);
      return new ModelAndView(variables, "joinParty.ftl");
    }
  }

  private static class JoinPartyHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      Map<String, Object> variables;

      try {
        QueryParamsMap qm = req.queryMap();
        // success
        String userId = qm.value("userId");
        String partyId = qm.value("partyId");
        String accessCode = qm.value("accessCode");
        System.out.println("partyId: " + partyId);
        System.out.println("userId: " + userId);
        System.out.println("accessCode: " + accessCode);

        User user = User.of(userId);
        Party party = Party.of(Integer.valueOf(partyId));
        if (!party.getGuests().contains(user)) {
          boolean success = party.addGuest(user, accessCode);
          if (success) {
            variables = ImmutableMap.of("success", true, "userId", userId,
                "partyId", partyId);
            return GSON.toJson(variables);
          }
        } else {
          variables = ImmutableMap.of("success", true, "userId", userId,
              "partyId", partyId);
          return GSON.toJson(variables);

        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      variables = ImmutableMap.of("success", false);
      return GSON.toJson(variables);

    }
  }

  /**
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * <<<<<<< f999ce85843e395275513f91f16f22a8d479f57f <<<<<<< HEAD Handles
   * request to create a sesh page. ======= Handles request to create a sesh
   * page. <<<<<<< HEAD ======= >>>>>>> 3c9453bc42a1b742af1d7554f4cb30503c82ca95
   * >>>>>>> 4cd40d6a734985201d6a104cccf4a71b9b2281c3 ======= Handles request to
   * create a sesh page.
   *
   * >>>>>>> added access code functionality
   * 
=======
   * <<<<<<< HEAD Handles request to create a sesh page. ======= Handles request
   * to create a sesh page. <<<<<<< HEAD ======= >>>>>>>
   * 3c9453bc42a1b742af1d7554f4cb30503c82ca95 >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
   *
>>>>>>> finished requests highlighting
   * @author HE23
   */
  private static class PartySettingsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String ftlPage;
      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("createUserId");
      Map<String, Object> variables = ImmutableMap.of("title", "Create a Sesh",
          "userId", userId);

      return new ModelAndView(variables, "partySettings.ftl");
    }
  }

  /**
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * <<<<<<< f999ce85843e395275513f91f16f22a8d479f57f <<<<<<< HEAD Creates party
   * in the backend. ======= Creates party in the backend. <<<<<<< HEAD =======
   * >>>>>>> 3c9453bc42a1b742af1d7554f4cb30503c82ca95 >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3 ======= Creates party in the
   * backend.
   *
   * >>>>>>> added access code functionality
   * 
=======
   * <<<<<<< HEAD Creates party in the backend. ======= Creates party in the
   * backend. <<<<<<< HEAD ======= >>>>>>>
   * 3c9453bc42a1b742af1d7554f4cb30503c82ca95 >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
   *
>>>>>>> finished requests highlighting
   * @author HE23
   */
  private class GetPartyHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String accessTypeStr = qm.value("accessType");
      AccessType accessType = AccessType.valueOf(accessTypeStr);
      String accessCode = qm.value("accessCode");

      System.out.println("access type: " + accessType);
      System.out.println("access code: " + accessCode);

      String userId = qm.value("userId");
      String partyName = qm.value("sesh_name"); // required
      // String hostName = qm.value("host_name"); WE DONT NEED HOST NAME
      String lat = qm.value("lat");
      String lon = qm.value("lon");
      String deviceId = qm.value("deviceId");

      Party party = null;
      int partyId = -1;
      // Coordinate coord = new Coordinate(Double.valueOf(lat),
      // Double.valueOf(lon));
      Coordinate coord = new Coordinate(41.8240, 71.4128);
      Map<String, Object> variables = ImmutableMap.of("partyId", "",
          "partyName", partyName, "userId", userId);
      System.out.println("going into try");
      try {
        User host = User.of(userId);
        System.out.println("got the user");
        party = Party.create(partyName, host, coord, LocalDateTime.now(),
            deviceId, partyName, accessType, accessCode);
        System.out.println("created the party");
        partyId = party.getPartyId();
        variables = ImmutableMap.of("partyId", partyId, "partyName", partyName,
            "userId", userId);
        System.out.println("successfully created party in backend");
      } catch (SQLException e) {
        System.out.println("Failed to add party to database");
      } catch (SpotifyUserApiException e) {
        // TODO SEND USER TO THE LOGIN PAGE
        variables = ImmutableMap.of("Message",
            "Your have been logged out! Please log back in again.");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        // If the user
        variables = ImmutableMap.of("Message",
            "You must have a premium spotify account to host a party");
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
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * Handles when a guest leaves a party.
   * 
=======
   * Handles when a guest leaves a party. <<<<<<< HEAD ======= >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
   *
>>>>>>> finished requests highlighting
   * @author Matt
   */
  private class LeavePartyHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("userId");
      String partyId = qm.value("partyId");
      Boolean deleteBool = Boolean.valueOf(qm.value("deleteBool"));
      Boolean partyEndedBool = Boolean.valueOf(qm.value("partyEndedBool"));
      User user = User.of(userId);
      Party party = Party.of(Integer.valueOf(partyId));
      if (!deleteBool) {
        try {
          party.followPlaylist(userId);
        } catch (SpotifyUserApiException e) {
          // TODO: Redirect to login page
          e.printStackTrace();
        }
      }

      if (!partyEndedBool) {
        party.removeGuest(user);
      }

      Map<String, Object> variables = ImmutableMap.of("title", "Sesh", "userId",
          userId);

      return new ModelAndView(variables, "createJoin.ftl");
    }

  }

  /**
   * Handles displaying search results. <<<<<<< HEAD ======= >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * 
=======
   *
>>>>>>> finished requests highlighting
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

  /**
   * Handles displaying search results.
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * 
=======
   *
>>>>>>> finished requests highlighting
   * @author HE23
   */
  private static class AddFavoriteHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      try {
        QueryParamsMap qm = req.queryMap();
        String userId = qm.value("userId");
        String songId = qm.value("songId");
        int partyId = Integer.parseInt(qm.value("partyId"));

        boolean add = Boolean.parseBoolean(qm.value("add"));
        if (add) {
          DbHandler.AddSongToFavorites(userId, songId);
        } else {
          DbHandler.removeSongFromFavorites(userId, songId);
        }
        List<Song> favorites = DbHandler.GetUserFavoritedSongs(userId);
        Map<String, Map<String, Object>> favoriteRequestIdToSongMaps = new HashMap<>();
        for (Song song : favorites) {
          String requestId = edu.brown.cs.am209hhe2lbenzonmsicat.models.Request
              .getId(partyId, song.getSpotifyId());
          favoriteRequestIdToSongMaps.put(requestId, song.toMap());
        }
        System.out.println(favoriteRequestIdToSongMaps);
        Map<String, Object> variables = ImmutableMap.of("favorites",
            favoriteRequestIdToSongMaps);
        return GSON.toJson(variables);
      } catch (Exception c) {
        c.printStackTrace();
      }
      Map<String, Object> variables = ImmutableMap.of("favorites",
          new ArrayList<String>());
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles devices.
   */
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

  /**
   * <<<<<<< HEAD Handles redirecting if user is already seshing. =======
   * Handles redirecting if user is already seshing. <<<<<<< HEAD =======
   * >>>>>>> 3c9453bc42a1b742af1d7554f4cb30503c82ca95 >>>>>>>
   * 4cd40d6a734985201d6a104cccf4a71b9b2281c3
<<<<<<< 9b462668b2a36c12e82182293553b9f868945626
   * 
=======
   *
>>>>>>> finished requests highlighting
   * @author Matt
   */
  private static class ActivePartyHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      String ftlPage;
      QueryParamsMap qm = req.queryMap();
      String userId = qm.value("userId");
      User user = User.of(userId);
      Party p = Party.getActivePartyOfUser(user);
      if (p != null) {
        if (user.equals(p.getHost())) {
          ftlPage = "/create/party";
        } else {
          ftlPage = "/join/party";
        }
      } else {
        ftlPage = null;
      }
      Map<String, Object> variables = ImmutableMap.of("userId", userId,
          "partyId", p.getPartyId(), "partyName", p.getName(), "redirectPage",
          ftlPage);
      return GSON.toJson(variables);
    }
  }

  // /**
  // * Handles returning the current song being played at the party. Will
  // return
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

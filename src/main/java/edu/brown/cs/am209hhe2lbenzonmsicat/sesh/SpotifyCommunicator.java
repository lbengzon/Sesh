package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.PlaylistTrackPosition;
import com.wrapper.spotify.models.Track;

/**
 * Class that integrates Spotify API for Sesh.
 *
 * @author HE23
 */
public class SpotifyCommunicator {

  // private final Api api = Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
  // .clientSecret(Consta
  // nts.LEANDRO_CLIENT_SECRET).redirectURI(Constants.REDIRECT_URL)
  // .build();
  private static Api testApi;
  private List<String> results;
  private static ConcurrentHashMap<String, Api> userToApi = new ConcurrentHashMap<String, Api>();
  private static ApiPool apiPool;

  private static Api publicApi = Api.builder()
      .clientId(Constants.LEANDRO_CLIENT_ID)
      .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
      .redirectURI(Constants.REDIRECT_URL).build();

  /**
   * This is the constructor which creates our map.
   */
  public SpotifyCommunicator() {
    apiPool = new ApiPool();
  }

  // TODO: Fix this so it works with the Api Pool!
  public static void setUpPublicApi() {
    publicApi.setRefreshToken(Constants.SESH_REFRESH);
    String aT;
    try {
      aT = publicApi.refreshAccessToken().build().get().getAccessToken();
      publicApi.setAccessToken(aT);
    } catch (IOException | WebApiException e) {
      // ERROR
    }
  }

  public static void setUpTestApi() {
    testApi = Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
        .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
        .redirectURI(Constants.REDIRECT_URL).build();
    testApi.setRefreshToken(Constants.SESH_REFRESH);
    String aT2;
    try {
      aT2 = testApi.refreshAccessToken().build().get().getAccessToken();
      testApi.setAccessToken(aT2);
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }
    userToApi.put("s3shteam32", testApi);
    Api hannahApi = Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
        .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
        .redirectURI(Constants.REDIRECT_URL).build();
    hannahApi.setRefreshToken(Constants.HANNAH_REFRESH);
    String aT;
    try {
      aT = hannahApi.refreshAccessToken().build().get().getAccessToken();
      hannahApi.setAccessToken(aT);
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }
    userToApi.put("1185743437", hannahApi);
    userToApi.put("hhe", testApi);
    Api aliApi = Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
        .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
        .redirectURI(Constants.REDIRECT_URL).build();
    aliApi.setRefreshToken(Constants.ALI_REFRESH);
    String aT3;
    try {
      aT3 = aliApi.refreshAccessToken().build().get().getAccessToken();
      aliApi.setAccessToken(aT3);
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }
    userToApi.put("alimiraculous", aliApi);

  }

  /**
   * Create authorize URL.
   */
  public String createAuthorizeURL() {

    /* Set the necessary scopes that the application will need from the user */
    final List<String> scopes = Arrays.asList("user-read-private",
        "user-read-email", "playlist-modify-private", "playlist-modify-public",
        "playlist-read-private", "playlist-read-collaborative",
        "user-read-playback-state", "user-read-currently-playing");

    /* Set a state. This is used to prevent cross site request forgeries. */
    final String state = "someExpectedStateString";
    Api newApi = Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
        .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
        .redirectURI(Constants.REDIRECT_URL).build();
    String authorizeURL = newApi.createAuthorizeURL(scopes, state);

    /*
     * Continue by sending the user to the authorizeURL, which will look
     * something like https://accounts.spotify.com:443/authorize?client_id=
     * 5fe01282e44241328a84e7c5cc169165&response_type=code&redirect_uri=https://
     * example.com/callback&scope=user-read-private%20user-read-email&state=some
     * -state-of-my-choice
     */
    return authorizeURL;
  }

  /**
   * Get access token.
   *
   * @param code
   *          - code
   * @return a list of the user's info
   */
  public List<String> getAccessToken(String code) {
    /*
     * Make a token request. Asynchronous requests are made with the .getAsync
     * method and synchronous requests are made with the .get method. This holds
     * for all type of requests.
     */
    Api api = Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
        .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
        .redirectURI(Constants.REDIRECT_URL).build();
    final SettableFuture<AuthorizationCodeCredentials> authCodeCredFuture = api
        .authorizationCodeGrant(code).build().getAsync();

    /* Add callbacks to handle success and failure */
    Futures.addCallback(authCodeCredFuture,
        new FutureCallback<AuthorizationCodeCredentials>() {
          @Override
          public void onSuccess(
              AuthorizationCodeCredentials authorizationCodeCredentials) {
            /* The tokens were retrieved successfully! */
            System.out.println("Successfully retrieved an access token! "
                + authorizationCodeCredentials.getAccessToken());
            System.out.println("The access token expires in "
                + authorizationCodeCredentials.getExpiresIn() + " seconds");
            System.out
                .println("Luckily, I can refresh it using this refresh token! "
                    + authorizationCodeCredentials.getRefreshToken());

            /*
             * Set the access token and refresh token so that they are used
             * whenever needed
             */
            api.setAccessToken(authorizationCodeCredentials.getAccessToken());
            api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            results = new ArrayList<String>();
            try {
              com.wrapper.spotify.models.User u = api.getMe().build().get();
              results.add(u.getId());
              results.add(u.getEmail());
              results.add(u.getDisplayName());
            } catch (IOException | WebApiException e) {
              throw new RuntimeException(e.getMessage());
            }

            userToApi.put(results.get(0), api);

          }

          @Override
          public void onFailure(Throwable throwable) {
            /*
             * Let's say that the client id is invalid, or the code has been
             * used more than once, the request will fail. Why it fails is
             * written in the throwable's message.
             */
            System.out.println(throwable.getMessage());

          }

        });
    return results;
  }

  /**
   * This method gets the playlist tracks.
   *
   * @param userId
   *          user id
   * @param playlistId
   *          playlist id
   * @return list of all the playlist songs
   */
  public static List<Song> getPlaylistTracks(String userId, String playlistId) {
    Api api = userToApi.get(userId);
    List<Song> res = new ArrayList<Song>();
    try {
      List<PlaylistTrack> plist = api.getPlaylistTracks(userId, playlistId)
          .build().get().getItems();
      for (PlaylistTrack pt : plist) {
        Track t = pt.getTrack();
        Song s = Song.of(t.getId(), t.getName(), t.getAlbum().getName(),
            t.getArtists().get(0).getName(), t.getDuration());
        res.add(s);
      }
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }
    return res;
  }

  public static List<Track> searchTracks(String query) {
    Api api = (Api) apiPool.checkOut();
    List<Track> tracks = new ArrayList<Track>();
    try {
      tracks = api.searchTracks(query).build().get().getItems();
      return tracks;
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }

  }

  public static Track getTrack(String id) {
    Api api = (Api) apiPool.checkOut();
    try {
      Track t = api.getTrack(id).build().get();
      return t;
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static void removeTrack(String userId, String playlistId,
      List<PlaylistTrackPosition> trackUris) {
    try {
      Api api = userToApi.get(userId);
      api.removeTrackFromPlaylist(userId, playlistId, trackUris).build().get();

    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static void addTrack(String userId, String playlistId,
      List<String> uris) {
    Api api = userToApi.get(userId);
    try {
      api.addTracksToPlaylist(userId, playlistId, uris).build().get();

    } catch (IOException | WebApiException e) {
      System.out.println("message" + e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  public static String createPlaylist(String userId, String title) {
    Api api = userToApi.get(userId);
    try {
      String id = api.createPlaylist(userId, title).build().get().getId();
      return id;
    } catch (IOException | WebApiException e) {
      // ERROR
      throw new RuntimeException(e.getMessage());
    }
  }

  public static void removePlaylist() {

  }

  /**
   * This method reorders tracks in the playlist.
   *
   * @param userId
   *          the user id
   * @param playlistId
   *          playlist id
   * @param rangeStart
   *          the current position of the track (or if there are many tracks,
   *          the first track to be reordered)
   * @param insertBefore
   *          the new position of the track
   */
  public static void reorderPlaylist(String userId, String playlistId,
      int rangeStart, int insertBefore) {
    Api api = userToApi.get(userId);
    api.reorderTracksInPlaylist(userId, playlistId, rangeStart, insertBefore);

  }

  public static void addTrackInPosition(String userId, String playlistId,
      List<String> uris, int pos) {
    Api api = userToApi.get(userId);
    api.addTracksToPlaylist(userId, playlistId, uris).position(pos).build();

  }

  public static Song getCurrentSong(String userId, String playlistId) {
    Api api = userToApi.get(userId);
    Song s = null;
    try {
      String accessToken = api.refreshAccessToken().build().get()
          .getAccessToken();
      api.setAccessToken(accessToken);
      StringBuilder sb = new StringBuilder();
      sb.append("https://api.spotify.com/v1/me/player/currently-playing");
      URL url = new URL(sb.toString());
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      StringBuilder sb2 = new StringBuilder();
      sb2.append("Bearer ");
      sb2.append(accessToken);
      conn.setRequestProperty("Authorization", sb2.toString());
      BufferedReader in = new BufferedReader(
          new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      JsonParser p = new JsonParser();
      JsonObject jsonObject = p.parse(response.toString()).getAsJsonObject();

      JsonElement context = p.parse(jsonObject.get("context").toString());
      JsonArray item = jsonObject.getAsJsonArray();

      // String type = context.get("type").toString();
      // if (!type.equals("playlist")) {
      // return null;
      // }
      // String uri = context.get("uri").toString();
      // sb = new StringBuilder();
      // sb.append("spotify:user:spotify:playlist:");
      // sb.append(playlistId);
      // if (!uri.equals(sb.toString())) {
      // return null;
      // }
      String spotifyId = item.get(2).getAsString();
      s = Song.of(spotifyId);
      return s;
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }

  }

}

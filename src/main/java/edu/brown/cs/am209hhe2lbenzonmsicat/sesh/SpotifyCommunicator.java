package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Arrays;
import java.util.List;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.RemoveTrackFromPlaylistRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.PlaylistTrackPosition;

public class SpotifyCommunicator {



  final Api api = Api.builder().clientId(Constants.clientId).clientSecret(Constants.clientSecret)
      .redirectURI(Constants.redirectURI).build();

  public void createAuthorizeURL() {

    /* Set the necessary scopes that the application will need from the user */
    final List<String> scopes = Arrays.asList("user-read-private",
        "user-read-email", "playlist-modify-private", "playlist-modify-public",
        "playlist-read-private", "playlist-read-collaborative");

    /* Set a state. This is used to prevent cross site request forgeries. */
    final String state = "someExpectedStateString";

    String authorizeURL = api.createAuthorizeURL(scopes, state);

    /*
     * Continue by sending the user to the authorizeURL, which will look
     * something like https://accounts.spotify.com:443/authorize?client_id=
     * 5fe01282e44241328a84e7c5cc169165&response_type=code&redirect_uri=https://
     * example.com/callback&scope=user-read-private%20user-read-email&state=some
     * -state-of-my-choice
     */
    System.out.println(authorizeURL);
  }

  public void getAccessToken(String code) {
    /*
     * Make a token request. Asynchronous requests are made with the .getAsync
     * method and synchronous requests are made with the .get method. This holds
     * for all type of requests.
     */
    final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = api
        .authorizationCodeGrant(code).build().getAsync();

    /* Add callbacks to handle success and failure */
    Futures.addCallback(authorizationCodeCredentialsFuture,
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
            addSongs();
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

  }

  public void addSongs() {
    final List<String> tracksToAdd = Arrays
        .asList("spotify:track:4BYGxv4rxSNcTgT3DsFB9o");

    // Index starts at 0
    final int insertIndex = 1;

    // final AddTrackToPlaylistRequest request = api
    // .addTracksToPlaylist("22f3kk24xtzmkkuw477v3dntq",
    // "7JB7rWkoXeO2Ov5DGJkFic", tracksToAdd)
    // .position(insertIndex).build();

    PlaylistTrackPosition playlistTrackPosition1 = new PlaylistTrackPosition(
        "spotify:track:4BYGxv4rxSNcTgT3DsFB9o");

    final List<PlaylistTrackPosition> tracksToRemove = Arrays
        .asList(playlistTrackPosition1);

    final RemoveTrackFromPlaylistRequest request = api
        .removeTrackFromPlaylist("22f3kk24xtzmkkuw477v3dntq",
            "7JB7rWkoXeO2Ov5DGJkFic", tracksToRemove)
        .build();

    try {
      request.get();
    } catch (Exception e) {
      System.out.println("Something went wrong!" + e.getMessage());
    }
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import org.junit.Test;

import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator;

public class SpotifyCommunicatorTest {

  @Test
  public void testPlay() {
    // SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.play("22f3kk24xtzmkkuw477v3dntq",
    // "7JB7rWkoXeO2Ov5DGJkFic", 2, true,
    // "cd319d9b74e13b54f820921bfd125d6733c8efde");
  }

  @Test
  public void testGetCurrentSong() {
    // SpotifyCommunicator.setUpTestApi();
    // System.out.println(SpotifyCommunicator.getCurrentSong(
    // "22f3kk24xtzmkkuw477v3dntq", "7JB7rWkoXeO2Ov5DGJkFic", true));
  }

  // @Test
  // public void testNext() {
  // SpotifyCommunicator.setUpTestApi();
  // SpotifyCommunicator.nextSong("alimiraculous", true);
  // }

  @Test
  public void testPause() {
    // SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.pause("22f3kk24xtzmkkuw477v3dntq", true,
    // "cd319d9b74e13b54f820921bfd125d6733c8efde");
  }

  @Test
  public void testNext() {
    // SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.nextSong("22f3kk24xtzmkkuw477v3dntq", true);
  }

  @Test
  public void testPrev() {
    // SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.prevSong("22f3kk24xtzmkkuw477v3dntq", true);
  }

  @Test
  public void testDevices() {
    SpotifyCommunicator.setUpTestApi();
    try {
      SpotifyCommunicator.getDevices("alimiraculous", true);
    } catch (SpotifyUserApiException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testSeek() {
    // SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.seek("22f3kk24xtzmkkuw477v3dntq", 10000,
    // "e38a34f0fb9fc1b3c406a6fba28fa7b61e255fbe", true);
  }

  @Test
  public void testUnfollow() {
    SpotifyCommunicator.setUpTestApi();
    // spotify:user:spotify:playlist:37i9dQZF1DWTJNOeepZTGy
    // spotify:user:spotify:playlist:37i9dQZF1DWTJNOeepZTGy
    try {
      SpotifyCommunicator.unfollowPlaylist("22f3kk24xtzmkkuw477v3dntq",
          "spotify", "37i9dQZF1DWTJNOeepZTGy", true);
    } catch (SpotifyUserApiException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testFollow() {
    try {
      SpotifyCommunicator.setUpTestApi();
      // spotify:user:11127253053:playlist:6Z2soNlTlRvVPeLmdnlQ95
      // spotify:user:2237gcc5lt5k3vmvfjr6756qi:playlist:6dp1oOzZMx1cvJQYAq4uU8
      SpotifyCommunicator.followPlaylist("alimiraculous",
          "2237gcc5lt5k3vmvfjr6756qi", "6dp1oOzZMx1cvJQYAq4uU8", true);
    } catch (SpotifyUserApiException e) {

    }
  }

}

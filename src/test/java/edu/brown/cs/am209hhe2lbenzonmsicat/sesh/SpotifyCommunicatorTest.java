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
    // SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.getDevices("22f3kk24xtzmkkuw477v3dntq", true);
  }

  @Test
  public void testSeek() {
    SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.seek("22f3kk24xtzmkkuw477v3dntq", 10000,
    // "e38a34f0fb9fc1b3c406a6fba28fa7b61e255fbe", true);
  }

  @Test
  public void testUnfollow() {
    // SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.unfollowPlaylist("22f3kk24xtzmkkuw477v3dntq",
    // "5DnDsEbL6Gppqg9wSFTX8I", true);
  }

}

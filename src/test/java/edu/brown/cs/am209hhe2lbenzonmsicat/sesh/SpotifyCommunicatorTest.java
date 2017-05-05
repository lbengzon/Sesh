package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import org.junit.Test;

public class SpotifyCommunicatorTest {

  @Test
  public void testPlay() {
    SpotifyCommunicator.setUpTestApi();
    // SpotifyCommunicator.play("alimiraculous", "6SnHyWgrI3Jo2cmququpbN", 2,
    // true);
    SpotifyCommunicator.play("22f3kk24xtzmkkuw477v3dntq",
        "7JB7rWkoXeO2Ov5DGJkFic", 2, true);
  }

  @Test
  public void testGetCurrentSong() {
    SpotifyCommunicator.setUpTestApi();
    // spotify:user:alimiraculous:playlist:6SnHyWgrI3Jo2cmququpbN
    // System.out.println(SpotifyCommunicator
    // .getCurrentSong("alimiraculous", "6SnHyWgrI3Jo2cmququpbN", true)
    // .getTitle());
    System.out.println(SpotifyCommunicator.getCurrentSong(
        "22f3kk24xtzmkkuw477v3dntq", "7JB7rWkoXeO2Ov5DGJkFic", true));
  }

  // @Test
  // public void testNext() {
  // SpotifyCommunicator.setUpTestApi();
  // SpotifyCommunicator.nextSong("alimiraculous", true);
  // }

  @Test
  public void testPause() {
    SpotifyCommunicator.setUpTestApi();
    SpotifyCommunicator.pause("22f3kk24xtzmkkuw477v3dntq", true);
  }

  @Test
  public void testNext() {
    SpotifyCommunicator.setUpTestApi();
    SpotifyCommunicator.nextSong("22f3kk24xtzmkkuw477v3dntq", true);
  }

  @Test
  public void testPrev() {
    SpotifyCommunicator.setUpTestApi();
    SpotifyCommunicator.prevSong("22f3kk24xtzmkkuw477v3dntq", true);
  }

  @Test
  public void testDevices() {
    SpotifyCommunicator.setUpTestApi();
    SpotifyCommunicator.getDevices("22f3kk24xtzmkkuw477v3dntq", true);
  }

}

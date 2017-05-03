package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import org.junit.Test;

public class SpotifyCommunicatorTest {

  @Test
  public void testPlay() {
    SpotifyCommunicator.setUpTestApi();
    // spotify:user:alimiraculous:playlist:6SnHyWgrI3Jo2cmququpbN
    System.out.println(SpotifyCommunicator
        .getCurrentSong("alimiraculous", "6SnHyWgrI3Jo2cmququpbN", true)
        .getTitle());
    SpotifyCommunicator.play("alimiraculous", "6SnHyWgrI3Jo2cmququpbN", true);
  }

  @Test
  public void testNext() {
    SpotifyCommunicator.setUpTestApi();
    SpotifyCommunicator.nextSong("alimiraculous", true);
  }

}

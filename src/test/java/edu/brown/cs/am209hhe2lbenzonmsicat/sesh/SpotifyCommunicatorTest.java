package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import org.junit.Test;

public class SpotifyCommunicatorTest {

  @Test
  public void testPlay() {
    SpotifyCommunicator.setUpTestApi();
    SpotifyCommunicator.play("alimiraculous");
  }

  @Test
  public void testNext() {
    SpotifyCommunicator.setUpTestApi();
    SpotifyCommunicator.nextSong("alimiraculous");
  }

}

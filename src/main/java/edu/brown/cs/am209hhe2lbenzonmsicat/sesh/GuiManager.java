package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import spark.template.freemarker.FreeMarkerEngine;

/**
 * Gui Manager class.
 *
 * @author HE23
 *
 */
public class GuiManager {

  /**
   * Default constructor.
   *
   * @param freeMarkerEngine
   *          - freemarker engine
   */
  public GuiManager(FreeMarkerEngine freeMarkerEngine) {
    installRoutes(freeMarkerEngine);
  }

  private void installRoutes(FreeMarkerEngine fme) {
    // install spark routes here
  }

}

package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Gui Manager class.
 * @author HE23
 */
public class GuiManager {
  SpotifyCommunicator comm = new SpotifyCommunicator();

  /**
   * Default constructor.
   * @param freeMarkerEngine
   *          - freemarker engine
   */
  public GuiManager(FreeMarkerEngine freeMarkerEngine) {
    installRoutes(freeMarkerEngine);
    comm.createAuthorizeURL();

  }

  private void installRoutes(FreeMarkerEngine fme) {
    // install spark routes here
    Spark.get("/spotifycallback", new MapsHandler(), fme);

  }

  private class MapsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      String code = qm.value("code");
      System.out.println(code);
      comm.getAccessToken(code);

      Map<String, Object> variables = ImmutableMap.of("title", "Maps");
      return new ModelAndView(variables, "home.ftl");
    }
  }

}

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
  private SpotifyCommunicator comm = new SpotifyCommunicator();

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
    Spark.get("/spotifycallback", new MapsHandler(), fme);
    Spark.get("/sesh", new FrontHandler(), fme);
    Spark.get("/create", new CreateHandler(), fme);
    Spark.get("/join", new JoinHandler(), fme);
  }

  /**
   * Spotify end point.
   */
  private class MapsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      String code = qm.value("code");
      System.out.println(code);
      comm.getAccessToken(code);
      String userId;
      Map<String, Object> variables = ImmutableMap.of("title", "Maps");
      return new ModelAndView(variables, "home.ftl");
    }
  }

  /**
   * Handles request to front page (join or create a sesh).
   * @author HE23
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Sesh");
      return new ModelAndView(variables, "createJoin.ftl");
    }
  }

  /**
   * Handles request to create a sesh page.
   * @author HE23
   */
  private static class CreateHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Create a Sesh");
      return new ModelAndView(variables, "create.ftl");
    }
  }

  /**
   * Handles request to join a sesh page.
   * @author HE23
   */
  private static class JoinHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh");
      return new ModelAndView(variables, "join.ftl");
    }
  }

}

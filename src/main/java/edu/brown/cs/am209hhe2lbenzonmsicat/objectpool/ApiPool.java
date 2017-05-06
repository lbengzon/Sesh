package edu.brown.cs.am209hhe2lbenzonmsicat.objectpool;

import java.io.IOException;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Constants;

/**
 * Models a pool of API objects.
 *
 * @author Matt
 *
 */
public class ApiPool extends ObjectPool<Api> {
  /**
   * Constructor.
   */
  public ApiPool() {
    super();
    this.expirationTime = 60000; // 60 seconds
  }

  protected Api create() {
    Api api = Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
        .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
        .redirectURI(Constants.REDIRECT_URL).build();
    api.setRefreshToken(Constants.SESH_REFRESH);
    String aT;
    try {
      aT = api.refreshAccessToken().build().get().getAccessToken();
      api.setAccessToken(aT);
    } catch (IOException | WebApiException e) {
      throw new RuntimeException(e.getMessage());
    }
    return api;
  }

  protected boolean validate(Api o) {
    if (o instanceof Api) {
      return true;
    }
    assert false : "Only APIs should be stored here!";
    return false;
  }

  protected void expire(Api o) {
    o = null;
  }

}

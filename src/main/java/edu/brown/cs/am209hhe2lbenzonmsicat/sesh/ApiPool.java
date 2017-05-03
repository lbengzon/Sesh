package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import com.wrapper.spotify.Api;

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
    return Api.builder().clientId(Constants.LEANDRO_CLIENT_ID)
        .clientSecret(Constants.LEANDRO_CLIENT_SECRET)
        .redirectURI(Constants.REDIRECT_URL).build();
  }

  protected boolean validate(Object o) {
    if (o instanceof Api) {
      return true;
    }
    assert false : "Only APIs should be stored here!";
    return false;
  }

  protected void expire(Object o) {
    o = null;
  }

}

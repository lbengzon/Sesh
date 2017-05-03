package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import com.wrapper.spotify.Api;

public class ApiPool extends ObjectPool<Api> {

  public ApiPool() {
    super();
    this.expirationTime = 30000;
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
    return false;
  }

  protected void expire(Object o) {
    o = null;
  }

}

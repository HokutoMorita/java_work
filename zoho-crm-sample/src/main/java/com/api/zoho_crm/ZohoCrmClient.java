package com.api.zoho_crm;

import java.io.IOException;

public class ZohoCrmClient {
  private OAuth oauth;
  private String accessToken;

  public ZohoCrmClient(PluginTask task) throws IOException {
    this.oauth = new OAuth(task);
  }

  public void refreshAccessToken(PluginTask task) throws IOException {
    this.accessToken = this.oauth.getAccessToken(task);
    //    logger.info("Refreshed!! new AccessToken is : {}", this.accessToken);
    System.out.println(String.format("Refreshed!! new AccessToken is : %s", this.accessToken));
  }
}

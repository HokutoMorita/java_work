package com.api.zoho_crm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class OAuth {
  private static final String UTF8_CHARSET = "UTF-8";

  private String oauthUrl;
  private CloseableHttpClient httpClient;

  private final String OAUTH_ACCESS_TOKEN = "access_token";

  public OAuth(PluginTask task) {
    this.oauthUrl = String.format("%s/oauth/v2/token", task.getAccountsUrl());
    this.httpClient =
        HttpClientBuilder.create()
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setConnectTimeout(task.getConnectTimeout())
                    .setConnectionRequestTimeout(task.getConnectRequestTimeout())
                    .setSocketTimeout(task.getSocketTimeout())
                    .setCookieSpec(CookieSpecs.STANDARD)
                    .build())
            .build();
  }

  /** リフレッシュトークンを使用したOAuthでアクセストークンを取得する */
  public String getAccessToken(PluginTask task) throws IOException {
    HttpPost request = new HttpPost(this.oauthUrl);

    List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
    parameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
    parameters.add(new BasicNameValuePair("client_id", task.getClientId()));
    parameters.add(new BasicNameValuePair("client_secret", task.getClientSecret()));
    parameters.add(new BasicNameValuePair("refresh_token", task.getRefreshToken()));

    HttpEntity entity = null;
    try {
      entity = new UrlEncodedFormEntity(parameters);
    } catch (UnsupportedEncodingException e) {
      System.out.println(e.toString());
    }
    request.setEntity(entity);
    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
    System.out.println(
        String.format("OAuth request to get AccessToken at this URL %s", this.oauthUrl));

    CloseableHttpResponse response = this.httpClient.execute(request);
    int stateCode = response.getStatusLine().getStatusCode();
    String responseEntity = EntityUtils.toString(response.getEntity(), UTF8_CHARSET);
    if (!(stateCode == HttpStatus.SC_OK || stateCode == HttpStatus.SC_CREATED)) {
      //      logger.error("HTTP Status: {}", stateCode);
      //      logger.error(responseEntity);
      System.out.println(String.format("HTTP Status: %s", stateCode));
      System.out.println(responseEntity);
      return "";
    } else {
      //      logger.info("HTTP Status: {}", stateCode);
      //      logger.info(responseEntity);
      System.out.println(String.format("HTTP Status: %s", stateCode));
      System.out.println(responseEntity);
      JSONObject result = new JSONObject(responseEntity);
      return result.getString(OAUTH_ACCESS_TOKEN);
    }
  }
}

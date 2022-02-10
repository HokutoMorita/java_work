package com.api.zoho_crm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ZohoCrmClient {
  private static final String UTF8_CHARSET = "UTF-8";
  private CloseableHttpClient httpClient;
  private OAuth oauth;
  private String accessToken;

  public ZohoCrmClient(PluginTask task) throws IOException {
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
    this.oauth = new OAuth(httpClient, task);
  }

  public void refreshAccessToken(PluginTask task) throws IOException {
    this.accessToken = this.oauth.getAccessToken(task);
    //    logger.info("Refreshed!! new AccessToken is : {}", this.accessToken);
    System.out.printf("Refreshed!! new AccessToken is : %s%n", this.accessToken);
  }

  public JSONObject getSampleData(PluginTask task) throws URISyntaxException, IOException {
    // リードデータを取得する
    String baseUrl = "https://www.zohoapis.com/crm/v2/Leads";
    URI requestUrl = buildGetRequestUrl(baseUrl, task);

    HttpGet request = new HttpGet(requestUrl);
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    request.setHeader(HttpHeaders.AUTHORIZATION, "Zoho-oauthtoken " + this.accessToken);

    System.out.printf("Get request at this URL %s%n", requestUrl);
    CloseableHttpResponse response = this.httpClient.execute(request);
    int stateCode = response.getStatusLine().getStatusCode();
    String responseEntity = EntityUtils.toString(response.getEntity(), UTF8_CHARSET);
    if (!(stateCode == HttpStatus.SC_OK || stateCode == HttpStatus.SC_CREATED)) {
      //      logger.error("HTTP Status: {}", stateCode);
      //      logger.error(responseEntity);
      System.out.printf("HTTP Status: %s%n", stateCode);
      System.out.println(responseEntity);
      return new JSONObject(responseEntity);
    } else {
      //      logger.info("HTTP Status: {}", stateCode);
      //      logger.info(responseEntity);
      System.out.printf("HTTP Status: %s%n", stateCode);
      System.out.println(responseEntity);
      return new JSONObject(responseEntity);
    }
  }

  private URI buildGetRequestUrl(String baseUrl, PluginTask task) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder(baseUrl);
    if (task.getFields().isPresent()) {
      uriBuilder.addParameter("fields", task.getFields().get());
    }
    if (task.getIds().isPresent()) {
      uriBuilder.addParameter("ids", task.getIds().get());
    }
    if (task.getSortOrder().isPresent()) {
      uriBuilder.addParameter("sort_order", task.getSortOrder().get());
    }
    if (task.getSortBy().isPresent()) {
      uriBuilder.addParameter("sort_by", task.getSortBy().get());
    }
    if (task.getConverted().isPresent()) {
      uriBuilder.addParameter("converted", task.getConverted().get());
    }
    if (task.getApproved().isPresent()) {
      uriBuilder.addParameter("approved", task.getApproved().get());
    }
    if (task.getPage().isPresent()) {
      uriBuilder.addParameter("page", task.getPage().get().toString());
    }
    if (task.getPerPage().isPresent()) {
      uriBuilder.addParameter("per_page", task.getPerPage().get().toString());
    }
    if (task.getCvid().isPresent()) {
      uriBuilder.addParameter("cvid", task.getCvid().get());
    }
    if (task.getTerritoryId().isPresent()) {
      uriBuilder.addParameter("territory_id", task.getTerritoryId().get());
    }
    if (task.getIncludeChild().isPresent()) {
      uriBuilder.addParameter("include_child", task.getIncludeChild().get().toString());
    }
    return uriBuilder.build();
  }
}

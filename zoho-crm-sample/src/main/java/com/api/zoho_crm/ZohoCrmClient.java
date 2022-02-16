package com.api.zoho_crm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ZohoCrmClient {
  private static final String UTF8_CHARSET = "UTF-8";
  private final String moduleName = "Leads";
  private final List<String> selectColumns =
      Arrays.asList(
          "Fax",
          "Skype_ID",
          "Twitter",
          "Website",
          "Secondary_Email",
          "Email",
          "Email_Opt_Out",
          "Company",
          "Created_By",
          "First_Name",
          "Country",
          "Last_Name",
          "City",
          "Annual_Revenue",
          "No_of_Employees",
          "Mobile",
          "Modified_By",
          "Industry",
          "Street",
          "Designation",
          "Lead_Source",
          "Owner",
          "Record_Image",
          "Lead_Status",
          "Rating",
          "Description",
          "Zip_Code",
          "State",
          "Phone",
          "Created_Time",
          "Modified_Time" // Created_Time, Modified_Timeは全てのモジュールで必須で含めること
          );
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

  public JSONObject getSampleDataByQuery(PluginTask task) throws IOException {
    // リードデータを取得する
    String url = "https://www.zohoapis.com/crm/v2/coql";
    HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase) new HttpPost(url);

    // クエリ設定用jsonデータを生成
    JSONObject requestBody = new JSONObject();
    //    String selectQuery = this.buildSelectQuery(task);
    String selectQuery = task.getQuery();
    requestBody.put("select_query", selectQuery);
    request.setEntity(new StringEntity(requestBody.toString(), UTF8_CHARSET));
    request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Zoho-oauthtoken " + this.accessToken);

    System.out.printf("Get request at this URL %s%n", url);
    System.out.printf("Using this query %s%n", selectQuery);
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

  public Map<String, String> createSchemaColumns(JSONObject responseJson) {
    Map<String, String> schemaColumnMaps = new HashMap<>();
    JSONObject responseDataJson = responseJson.getJSONArray("data").getJSONObject(0);
    for (String dataJsonKey : responseDataJson.keySet()) {
      System.out.println("キー名: " + dataJsonKey + "のレスポンスの各型の確認");
      System.out.println(responseDataJson.get(dataJsonKey));
      System.out.println(responseDataJson.get(dataJsonKey).getClass().getSimpleName());
    }
    return schemaColumnMaps;
  }

  private String buildSelectQuery(PluginTask task) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
    sb.append(String.join(",", this.selectColumns)); // TODO カスタムカラムをコンフィグから取得して追加する必要あること
    sb.append(" FROM ");
    sb.append(this.moduleName);

    Optional<String> createdTimeFrom = task.getCreatedTimeFrom();
    Optional<String> modifiedTimeFrom = task.getModifiedTimeFrom();
    if (createdTimeFrom.isPresent() && modifiedTimeFrom.isPresent()) {
      sb.append(" WHERE ");
      sb.append(
          String.format(
              "Created_Time >= '%s' AND Modified_Time >= '%s' ",
              createdTimeFrom.get(), modifiedTimeFrom.get()));
    } else if (createdTimeFrom.isPresent()) {
      sb.append(" WHERE ");
      sb.append(String.format("Created_Time >= '%s' ", createdTimeFrom.get()));
    } else if (modifiedTimeFrom.isPresent()) {
      sb.append(" WHERE ");
      sb.append(String.format("Modified_Time >= '%s' ", modifiedTimeFrom.get()));
    } else {
      sb.append(" WHERE "); // COQLは必ずWHERE句を指定する必要があるため絞り込み条件がない場合は、UNIXエポック以上のデータを取得するようにする
      sb.append(String.format("Created_Time >= '1970-01-01T00:00:00+09:00' "));
    }
    return sb.toString();
  }
}

package com.api.zoho_crm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZohoCrmClient {
  private final String UTF8_CHARSET = "UTF-8";
  private final int QUERY_API_LIMIT = 2;
  private final String INVALID_TOKEN = "INVALID_TOKEN";
  private final String DUPLICATE_DATA = "DUPLICATE_DATA";
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

  public JSONObject getSampleDataByQuery(String selectQuery, PluginTask task) throws IOException {
    // リードデータを取得する
    String url = "https://www.zohoapis.com/crm/v2/coql";
    HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase) new HttpPost(url);

    // クエリ設定用jsonデータを生成
    JSONObject requestBody = new JSONObject();
    //    String selectQuery = this.buildSelectQuery(task);
    requestBody.put("select_query", selectQuery);
    request.setEntity(new StringEntity(requestBody.toString(), UTF8_CHARSET));
    request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Zoho-oauthtoken " + this.accessToken);

    System.out.printf("Get request at this URL %s%n", url);
    System.out.printf("Using this query %s%n", selectQuery);
    CloseableHttpResponse response = this.httpClient.execute(request);
    int stateCode = response.getStatusLine().getStatusCode();
    if (response.getEntity() == null) {
      return null;
    }
    String responseEntity = EntityUtils.toString(response.getEntity(), UTF8_CHARSET);
    if (!(stateCode == HttpStatus.SC_OK || stateCode == HttpStatus.SC_CREATED)) {
      //      logger.error("HTTP Status: {}", stateCode);
      //      logger.error(responseEntity);
      String errorCode = new JSONObject(responseEntity).getString("code");
      if (errorCode.equals(INVALID_TOKEN)) {
        System.out.println("AccessToken is expired and need to refresh access token.");
        // アクセストークンが期限切れの場合は、アクセストークンを更新して再度データ取得をする
        this.refreshAccessToken(task);
        return getSampleDataByQuery(selectQuery, task);
      }
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

  public Map<String, String> createSchemaColumns(JSONObject responseJson) {
    Map<String, String> schemaColumnMaps = new HashMap<>();
    JSONObject responseDataJson = responseJson.getJSONArray("data").getJSONObject(0);
    for (String dataJsonKey : responseDataJson.keySet()) {
      schemaColumnMaps.put(dataJsonKey, "StringType");
    }
    return schemaColumnMaps;
  }

  public void search(PluginTask task) throws IOException {
    // TODO 本番では、引数にtaskに加えてPageBuilder pageBuilderを含めてスキーマ情報の書き込みをおこなうこと
    String baseSelectQuery = task.getQuery();
    int offset = 0;
    int totalDataCount = 0;
    this.accessToken = "hoge";
    while (true) {
      String selectQuery =
          baseSelectQuery + String.format(" LIMIT %s OFFSET %s", QUERY_API_LIMIT, offset);
      JSONObject response = this.getSampleDataByQuery(selectQuery, task);
      if (response == null) {
        System.out.println("This iteration's API response is null");
        break;
      }
      JSONArray responseDataJsonArray = response.getJSONArray("data");

      // TODO ここでスキーマ情報にデータを書き込む処理をおこなう
      List<Map<String, String>> transformedColumnValues =
          this.transformColumnValues(responseDataJsonArray);
      System.out.println("整形後のデータの中身確認");
      System.out.println(transformedColumnValues.toString());

      int responseDataLength = responseDataJsonArray.length();
      totalDataCount += responseDataLength;
      if (responseDataLength < QUERY_API_LIMIT) {
        break; // 取得したデータ件数がQueryAPIの一度のリクエストで取得できる最大件数である200件未満の場合breakする
      } else {
        offset += QUERY_API_LIMIT;
      }
    }
    System.out.println("total data count is: " + totalDataCount);
  }

  public List<Map<String, String>> transformColumnValues(JSONArray responseDataJsonArray) {
    List<Map<String, String>> transformedColumnValues = new ArrayList<>();
    for (int i = 0; i < responseDataJsonArray.length(); i++) {
      Map<String, String> transformedColumnMaps = new HashMap<>();
      JSONObject responseDataJson = responseDataJsonArray.getJSONObject(i);
      for (String responseDataJsonKey : responseDataJson.keySet()) {
        try {
          JSONArray responseDataArray = responseDataJson.getJSONArray(responseDataJsonKey);
          transformedColumnMaps.put(
              responseDataJsonKey, convertJsonArrayToJson(responseDataJsonKey, responseDataArray));
        } catch (JSONException e) {
          // カラムに紐づく値がリスト形式以外の場合、例外が発生するためcatch文内でリスト形式以外の値を設定する
          // カラムに紐づく値がリスト形式以外の場合、整形せずに値を設定する
          transformedColumnMaps.put(
              responseDataJsonKey, responseDataJson.get(responseDataJsonKey).toString());
        }
      }
      transformedColumnValues.add(transformedColumnMaps);
    }
    return transformedColumnValues;
  }

  /**
   * hoge = ["item1", "item2", "item3"]のような形式のデータを hoge = {"hoge_items": ["item1", "item2",
   * "item3"]}のような形式に変換する
   */
  private String convertJsonArrayToJson(String keyName, JSONArray responseDataArray) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(keyName + "_items", responseDataArray);
    return jsonObject.toString();
  }

  /** 戻り値として、insert対象データの内、既にZoho CRMで登録されている重複データの件数を返す */
  public int insertData(JSONArray insertDataList, PluginTask task) throws IOException {
    String url = String.format("https://www.zohoapis.com/crm/v2/%s", task.getModuleType());
    HttpEntityEnclosingRequestBase request = new HttpPost(url);

    String responseEntity = this.executeApi(url, request, insertDataList);
    int duplicateCount = 0;
    try {
      JSONArray responseDataArray = new JSONObject(responseEntity).getJSONArray("data");
      // TODO 重複データはinsertされないので、重複したデータは、
      //  Warningログとしてembulk上で出力する、どのIDが重複しているかがわかるようにIDも出力する、
      //  trocco上では、embulkの実行ログの中からスキップした行数をカウントするようになっている
      //  戻り値として、重複したデータの件数を返すので、insertDataメソッドの呼び出し先でトータルの重複件数を算出すること
      //  重複件数の出力フォーマットはこちらのプルリクを参考にすること: https://github.com/primenumber-dev/n-transfer-ui/pull/2431/files
      for (int i = 0; i < responseDataArray.length(); i++) {
        JSONObject record = responseDataArray.getJSONObject(i);
        String statusCode = record.getString("code");
        if (statusCode.equals(DUPLICATE_DATA)) {
          String id = record.getJSONObject("details").getString("id");
          // logger.warn("This record has already been registered, so it was duplicated.");
          // logger.warn("Duplicated record id is {}", id);
          System.out.println("This record has already been registered, so it was duplicated.");
          System.out.printf("Duplicated record id is %s%n", id);
          duplicateCount++;
        }
      }
    } catch (JSONException e) {
      String errorCode = new JSONObject(responseEntity).getString("code");
      if (errorCode.equals(INVALID_TOKEN)) {
        // logger.error("AccessToken is expired and need to refresh access token.");
        System.out.println("AccessToken is expired and need to refresh access token.");
        // アクセストークンが期限切れの場合は、アクセストークンを更新して再度データ取得をする
        this.refreshAccessToken(task);
        insertData(insertDataList, task);
      }
    }
    return duplicateCount;
  }

  public void updateData(JSONArray updateDataList, PluginTask task) throws IOException {
    String url = String.format("https://www.zohoapis.com/crm/v2/%s", task.getModuleType());
    HttpEntityEnclosingRequestBase request = new HttpPut(url);

    // TODO ここに実装する
  }

  private String executeApi(String url, HttpEntityEnclosingRequestBase request, JSONArray dataList)
      throws IOException {
    // リクエストボディ生成
    JSONObject requestBody = new JSONObject();
    requestBody.put("data", dataList);
    request.setEntity(new StringEntity(requestBody.toString(), UTF8_CHARSET));

    // リクエストヘッダー生成
    request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Zoho-oauthtoken " + this.accessToken);

    // API呼び出し
    System.out.printf("Get request at this URL %s%n", url);
    CloseableHttpResponse response = this.httpClient.execute(request);
    int stateCode = response.getStatusLine().getStatusCode();
    String responseEntity = EntityUtils.toString(response.getEntity(), UTF8_CHARSET);
    //      logger.info("HTTP Status: {}", stateCode);
    //      logger.info(responseEntity);
    System.out.printf("HTTP Status: %s%n", stateCode);
    System.out.println(responseEntity);
    return responseEntity;
  }
}

package com.api.zoho_crm;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import org.json.JSONObject;

public class Main {
  public static void main(String[] args) throws IOException, URISyntaxException {
    System.out.println("Hello World");
    // 環境変数は、intellijの実行構成の編集上で追加すること(https://reasonable-code.com/intellij-environment-variable/)
    String ZOHO_CLIENT_ID = System.getenv("ZOHO_CLIENT_ID");
    String ZOHO_CLIENT_SECRET = System.getenv("ZOHO_CLIENT_SECRET");
    String ZOHO_REFRESH_TOKEN = System.getenv("ZOHO_REFRESH_TOKEN");
    System.out.println("CLIENT_ID: " + ZOHO_CLIENT_ID);
    System.out.println("CLIENT_SECRET: " + ZOHO_CLIENT_SECRET);
    System.out.println("REFRESH_TOKEN: " + ZOHO_REFRESH_TOKEN);

    // PluginTaskのインスタンス生成
    String zohoAccountsUrl = "https://accounts.zoho.com";
    PluginTask task =
        new PluginTask(
            zohoAccountsUrl,
            ZOHO_CLIENT_ID,
            ZOHO_CLIENT_SECRET,
            ZOHO_REFRESH_TOKEN,
            300000,
            300000,
            300000);

    ZohoCrmClient zohoCrmClient = new ZohoCrmClient(task);

    // OAuth
    // logger.info("[1] Using OAuth2.0 get latest AccessToken");
    zohoCrmClient.refreshAccessToken(task);

    // サンプルデータをリクエスト
    // リクエストパラメータを設定
    //    task.setPerPage(Optional.of(3));
    //
    //    JSONObject response = zohoCrmClient.getSampleData(task);
    //    System.out.println("レスポンス内容確認");
    //    System.out.println(response);

    // クエリを使用してデータをリクエスト
    String query =
        "SELECT custom_user1, custom1, Company, Email, Last_Name, First_Name, Created_Time, Modified_Time FROM Leads WHERE Created_Time >= '2022-02-15T00:00:00+09:00' LIMIT 1";
    task.setQuery(query);
    JSONObject response = zohoCrmClient.getSampleDataByQuery(task);
    System.out.println("レスポンス内容確認");
    System.out.println(response);
  }
}

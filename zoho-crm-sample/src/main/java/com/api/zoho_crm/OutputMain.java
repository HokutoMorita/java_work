package com.api.zoho_crm;

import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONArray;
import org.json.JSONObject;

public class OutputMain {
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

    // TODO createOnlyの実行
    task.setAction("createOnly");
    // Leadsモジュールのinsert
    task.setModuleType("Leads");
    JSONArray insertDataList = createOnlyJson();
    int duplicateCount = zohoCrmClient.insertData(insertDataList, task);
    System.out.println("重複データの件数: " + duplicateCount);

    // TODO updateOnlyの実行

    // TODO createOrUpdateの実行
  }

  private static JSONArray createOnlyJson() {
    JSONArray jsonArray = new JSONArray();

    JSONObject record1 = new JSONObject();
    record1.put("Company", "株式会社埼玉2");
    record1.put("Last_Name", "佐藤2");
    record1.put("First_Name", "hoge太郎2");
    record1.put("Email", "hoge-hoge@hoge.com");
    record1.put("State", "埼玉県2");

    JSONObject record2 = new JSONObject();
    record2.put("Company", "株式会社埼玉6");
    record2.put("Last_Name", "佐藤6");
    record2.put("First_Name", "hoge太郎6");
    record2.put("Email", "hoge-hoge@hoge6.com");
    record2.put("State", "埼玉県6");

    jsonArray.put(record1);
    jsonArray.put(record2);
    return jsonArray;
  }
}

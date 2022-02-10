package com.api.zoho_crm;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
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
  }
}

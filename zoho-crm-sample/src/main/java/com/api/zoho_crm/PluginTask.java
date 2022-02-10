package com.api.zoho_crm;

public class PluginTask {
  private String clientId;
  private String accountsUrl;
  private String clientSecret;
  private String refreshToken;
  private int connectTimeout;
  private int connectRequestTimeout;
  private int socketTimeout;

  public PluginTask(
      String accountsUrl,
      String clientId,
      String clientSecret,
      String refreshToken,
      int connectTimeout,
      int connectRequestTimeout,
      int socketTimeout) {
    this.accountsUrl = accountsUrl;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.refreshToken = refreshToken;
    this.connectTimeout = connectTimeout;
    this.connectRequestTimeout = connectRequestTimeout;
    this.socketTimeout = socketTimeout;
  }

  public String getAccountsUrl() {
    return this.accountsUrl;
  }

  public String getClientId() {
    return this.clientId;
  }

  public String getClientSecret() {
    return this.clientSecret;
  }

  public String getRefreshToken() {
    return this.refreshToken;
  }

  // デフォルト値が300000になる @ConfigDefault("300000")で設定すること
  public int getConnectTimeout() {
    return this.connectTimeout;
  }

  // デフォルト値が300000になる @ConfigDefault("300000")で設定すること
  public int getConnectRequestTimeout() {
    return this.connectRequestTimeout;
  }

  // デフォルト値が300000になる @ConfigDefault("300000")で設定すること
  public int getSocketTimeout() {
    return this.socketTimeout;
  }
}

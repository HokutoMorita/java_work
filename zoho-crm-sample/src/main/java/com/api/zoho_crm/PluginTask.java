package com.api.zoho_crm;

import java.util.Optional;

public class PluginTask {
  private String clientId;
  private String accountsUrl;
  private String clientSecret;
  private String refreshToken;
  private int connectTimeout;
  private int connectRequestTimeout;
  private int socketTimeout;
  private Optional<String> fields = Optional.empty();
  private Optional<String> ids = Optional.empty();
  private Optional<String> sortOrder = Optional.empty();
  private Optional<String> sortBy = Optional.empty();
  private Optional<String> converted = Optional.empty();
  private Optional<String> approved = Optional.empty();
  private Optional<Integer> page = Optional.empty();
  private Optional<Integer> perPage = Optional.empty();
  private Optional<String> cvid = Optional.empty();
  private Optional<String> territoryId = Optional.empty();
  private Optional<Boolean> includeChild = Optional.empty();

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

  // @ConfigDefault("null")で設定すること
  public Optional<String> getFields() {
    return fields;
  }

  public void setFields(Optional<String> fields) {
    this.fields = fields;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<String> getIds() {
    return ids;
  }

  public void setIds(Optional<String> ids) {
    this.ids = ids;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<String> getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Optional<String> sortOrder) {
    this.sortOrder = sortOrder;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<String> getSortBy() {
    return sortBy;
  }

  public void setSortBy(Optional<String> sortBy) {
    this.sortBy = sortBy;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<String> getConverted() {
    return converted;
  }

  public void setConverted(Optional<String> converted) {
    this.converted = converted;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<String> getApproved() {
    return approved;
  }

  public void setApproved(Optional<String> approved) {
    this.approved = approved;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<Integer> getPage() {
    return page;
  }

  public void setPage(Optional<Integer> page) {
    this.page = page;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<Integer> getPerPage() {
    return perPage;
  }

  public void setPerPage(Optional<Integer> perPage) {
    this.perPage = perPage;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<String> getCvid() {
    return cvid;
  }

  public void setCvid(Optional<String> cvid) {
    this.cvid = cvid;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<String> getTerritoryId() {
    return territoryId;
  }

  public void setTerritoryId(Optional<String> territoryId) {
    this.territoryId = territoryId;
  }

  // @ConfigDefault("null")で設定すること
  public Optional<Boolean> getIncludeChild() {
    return includeChild;
  }

  public void setIncludeChild(Optional<Boolean> includeChild) {
    this.includeChild = includeChild;
  }
}

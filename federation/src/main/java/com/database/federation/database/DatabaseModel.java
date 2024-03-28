package com.database.federation.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.database.federation.utils.DatabaseForm;

@Document(collection = "databases")
public class DatabaseModel {

  @Id
  private String id;
  private String url;
  private int port;
  private String dbName;
  private String dbType;
  private String companyName;

  public DatabaseModel() {
  }

  public DatabaseModel(DatabaseForm form) {
    this.url = form.getUrl();
    this.port = form.getPort();
    this.dbName = form.getDbName();
    this.dbType = form.getDbType();
    this.companyName = form.getCompanyName();
  }


  //generate getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public String getDbType() {
    return dbType;
  }

  public void setDbType(String dbType) {
    this.dbType = dbType;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

}

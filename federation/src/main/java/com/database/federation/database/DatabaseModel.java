package com.database.federation.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.database.federation.utils.DatabaseForm;

@Document(collection = "databases")
public class DatabaseModel {

  @Id
  private String id;
  private String url;
  private String dbName;
  private String dbType;
  private String companyName;
  private String username;
  private String password;

  public DatabaseModel() {
  }

  public DatabaseModel(DatabaseForm form) {
    this.url = form.getUrl();
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}

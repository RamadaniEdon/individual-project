package com.server.backend.components;

import java.util.ArrayList;

public class DatabaseComponent {

  private String url;
  private String name;
  private String companyName;
  private String port;
  private String host;
  private String username;
  private String password;
  private ArrayList<Table> tables;

  public DatabaseComponent(String url, String name, String companyName, String port, String host, String username, String password,
      ArrayList<Table> tables) {
    this.url = url;
    this.name = name;
    this.companyName = companyName;
    this.port = port;
    this.host = host;
    this.username = username;
    this.password = password;
    this.tables = tables;
  }
  public DatabaseComponent(){

  }
  // create getters and setters for all the fields
  
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
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

  public ArrayList<Table> getTables() {
    return tables;
  }

  public void setTables(ArrayList<Table> tables) {
    this.tables = tables;
  }

  

  public static class Table {

    private String name;
    private String meaning;
    private ArrayList<Column> columns;
    
    public Table(String name, String meaning, ArrayList<Column> columns) {
      this.name = name;
      this.meaning = meaning;
      this.columns = columns;
    }
    public Table(){

    }

    // create getters and setters for all the fields
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getMeaning() {
      return meaning;
    }

    public void setMeaning(String meaning) {
      this.meaning = meaning;
    }

    public ArrayList<Column> getColumns() {
      return columns;
    }

    public void setColumns(ArrayList<Column> columns) {
      this.columns = columns;
    }


  }

  public static class Column {

    private String name;
    private String meaning;

    public Column(String name, String meaning) {
      this.name = name;
      this.meaning = meaning;
    }
    public Column(){

    }

    // create getters and setters for all the fields

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getMeaning() {
      return meaning;
    }

    public void setMeaning(String meaning) {
      this.meaning = meaning;
    }
  }

}

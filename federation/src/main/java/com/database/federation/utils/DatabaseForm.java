package com.database.federation.utils;

import java.util.List;

public class DatabaseForm {

  private String url;
  private int port;
  private String dbName;
  private String dbType;
  private String companyName;
  private List<Collection> collections;

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

  public List<Collection> getCollections() {
    return collections;
  }

  public void setCollections(List<Collection> collections) {
    this.collections = collections;
  }

  
}

class Collection{
  private String collectionName;
  private String meaning;
  private List<Field> fields;

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }
}

class Field {
  private String name;
  private String meaning;
  private boolean foreignKey;
  private boolean datatype;
  private List<Field> fields;

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

  public boolean isForeignKey() {
    return foreignKey;
  }

  public void setForeignKey(boolean foreignKey) {
    this.foreignKey = foreignKey;
  }

  public boolean isDatatype() {
    return datatype;
  }

  public void setDatatype(boolean datatype) {
    this.datatype = datatype;
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }


}
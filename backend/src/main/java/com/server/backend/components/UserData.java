package com.server.backend.components;

public class UserData {
  private String name;
  private String type;
  private String value;

  public UserData(String name, String type, String value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public UserData(String name, String value){
    this.name = name;
    this.value = value;
  }

  public UserData() {
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setValue(String value) {
    this.value = value;
  }

}

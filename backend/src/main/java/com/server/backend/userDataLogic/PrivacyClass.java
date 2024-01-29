package com.server.backend.userDataLogic;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;

@Document(collection = "PrivacyClasses")
public class PrivacyClass {
  
  @Id
  private String id;
  private String name;
  private String description;
  private String userAfm;
  private int price;

  public PrivacyClass(String name, String description, String userAfm, int price) {
    this.name = name;
    this.description = description;
    this.userAfm = userAfm;
    this.price = price;
  }

  public PrivacyClass() {

  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription(){
    return description;
  } 

  public String getUserAfm(){
    return userAfm;
  }

  public int getPrice(){
    return price;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name){
    this.name = name;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public void setUserAfm(String userAfm){
    this.userAfm = userAfm;
  }

  public void setPrice(int price){
    this.price = price;
  } 


}

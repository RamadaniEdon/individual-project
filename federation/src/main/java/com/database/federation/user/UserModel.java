package com.database.federation.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserModel {

  @Id
  private String id;
  private String afm;
  private String name;
  private String surname;
  private String password;

  //generate getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAfm() {
    return afm;
  }

  public void setAfm(String field1) {
    this.afm = field1;
  }

  public String getName() {
    return name;
  }

  public void setName(String field2) {
    this.name = field2;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String field3) {
    this.surname = field3;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

}

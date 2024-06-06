package com.database.federation.utils;

public class JwtObject {
  
  private String afm;

  public JwtObject() {
  }
  
  public JwtObject(String afm) {
    this.afm = afm;
  }

  public String getAfm() {
    return afm;
  }

  public void setAfm(String afm) {
    this.afm = afm;
  }

}

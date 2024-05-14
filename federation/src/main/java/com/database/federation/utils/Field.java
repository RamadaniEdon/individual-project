package com.database.federation.utils;

import java.util.List;

public class Field {
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

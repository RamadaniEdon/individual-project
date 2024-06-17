package com.database.federation.utils;

import java.util.List;

public class Field {
  private String name;
  private FieldType fieldType;
  private String meaning;
  private String rangeClass;
  private List<Field> fields;
  private String foreignKey;


  public String getForeignKey() {
    return foreignKey;
  }

  public void setForeignKey(String foreignKey) {
    this.foreignKey = foreignKey;
  }

  public String getRangeClass() {
    return rangeClass;
  }

  public void setRangeClass(String rangeClass) {
    this.rangeClass = rangeClass;
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFieldType(FieldType fieldType) {
    this.fieldType = fieldType;
  }

  public FieldType getFieldType() {
    return fieldType;
  }

}

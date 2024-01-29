package com.server.backend.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDataTable {
  private boolean privateData;
  private String tableName;
  private List<String> columnNames;
  private List<List<UserData>> rows;
  
  public UserDataTable(boolean privateData, String tableName, List<String> columnNames, List<List<UserData>> rows) {
    this.privateData = privateData;
    this.tableName = tableName;
    this.columnNames = columnNames;
    this.rows = rows;
  }

  public UserDataTable() {
    this.columnNames = new ArrayList<String>();
    this.rows = new ArrayList<List<UserData>>();
  }

  public UserDataTable(boolean privateData, String tableName, List<String> columnNames) {
    this.privateData = privateData;
    this.tableName = tableName;
    this.columnNames = columnNames;
    this.rows = new ArrayList<List<UserData>>();
  }

  public UserDataTable(boolean privateData, String tableName) {
    this.privateData = privateData;
    this.tableName = tableName;
    this.columnNames = new ArrayList<String>();
    this.rows = new ArrayList<List<UserData>>();
  }

  public boolean isPrivateData() {
    return privateData;
  }

  public void setPrivateData(boolean privateData) {
    this.privateData = privateData;
  }

  public List<String> getColumnNames() {
    return columnNames;
  }

  public void setColumnNames(List<String> columnNames) {
    this.columnNames = columnNames;
  }

  public List<List<UserData>> getRows() {
    return rows;
  } 

  public void setRows(List<List<UserData>> rows) {
    this.rows = rows;
  }

  public void addRow(List<UserData> row) {
    this.rows.add(row);
  }

  public static List<List<UserData>> getMappedUserDataList(List<Map<String, Object>> rows) {
    List<List<UserData>> mappedRows = new ArrayList<List<UserData>>();
    for (Map<String, Object> row : rows) {
      List<UserData> mappedRow = new ArrayList<UserData>();

      for (Map.Entry<String, Object> entry : row.entrySet()) {
        mappedRow.add(new UserData(entry.getKey(), entry.getValue().toString()));
      }
    }
    return mappedRows;
  }
  
}

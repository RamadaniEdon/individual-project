package com.server.backend.utils;

import java.util.Map;

public class QueryHelpers {
  public static String getQuery(String tableName, String columnName, String value) {
    return "SELECT * FROM " + tableName + " WHERE " + columnName + " = " + "'" + value + "';";
  }

  public static String getMyReferencesQuery(String tableName) {
    return String.format("SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
        "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
        "WHERE TABLE_NAME = '%s' " +
        "AND REFERENCED_TABLE_NAME IS NOT NULL " +
        "AND REFERENCED_COLUMN_NAME IS NOT NULL;",
        tableName);
  }

  public static String getOtherReferencesQuery(String tableName) {
    return String.format("SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
        "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
        "WHERE REFERENCED_TABLE_NAME = '%s';",
        tableName);
  }

  public static String getTableNameReferencing(Map<String, Object> row){
    return (String) row.get("TABLE_NAME");
  }

  public static String getColumnNameReferencing(Map<String, Object> row){
    return (String) row.get("COLUMN_NAME");
  }

  public static String getReferencedTable(Map<String, Object> row){
    return (String) row.get("REFERENCED_TABLE_NAME");
  }

  public static String getReferencedColumn(Map<String, Object> row){
    return (String) row.get("REFERENCED_COLUMN_NAME");
  }
}

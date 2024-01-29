package com.server.backend.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mysql.cj.jdbc.DatabaseMetaData;
import com.server.backend.components.UserDataTable;
import com.server.backend.databaseLogic.Database;
import com.server.backend.utils.QueryHelpers;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DynamicDatabaseService {

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public List<Map<String, Object>> retrieveTable(String tableName){
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
    }

    public String getPrimaryKeyColumnName(String tableName) {
        String sql = "SELECT COLUMN_NAME " +
                     "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                     "WHERE TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, tableName);

        if (!result.isEmpty()) {
            return (String) result.get(0).get("COLUMN_NAME");
        }

        return null; // Return null if no primary key found
    }


    public void setDynamicDataSource(Database db) {
        DataSource dynamicDataSource = createDynamicDataSource(db.getUrl(), db.getName(), db.getUsername(), db.getPassword());
        jdbcTemplate.setDataSource(dynamicDataSource);
    }


    public void setDynamicDataSource(String url, String dbName, String username, String password) {
        DataSource dynamicDataSource = createDynamicDataSource(url, dbName, username, password);
        jdbcTemplate.setDataSource(dynamicDataSource);
    }

    private DataSource createDynamicDataSource(String url, String dbName, String username, String password) {
        // Create a new DataSource for the dynamic database
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + url + "/" + dbName);
        config.setUsername(username);
        config.setPassword(password);

        return new HikariDataSource(config);
    }

    public void resetDataSource() {
        jdbcTemplate.setDataSource(null);
    }

    public List<Map<String, Object>> executeQuery2() {
        return null;
    }

    private List<UserDataTable> result;

    public List<UserDataTable> getUserData(String usersTable, String usersAfmColumn, String afmValue) {
        result = new ArrayList<UserDataTable>();
        List<Map<String, Object>> userData = jdbcTemplate
                .queryForList(QueryHelpers.getQuery(usersTable, usersAfmColumn, afmValue));

        result.add(new UserDataTable(true, usersTable, new ArrayList(userData.get(0).keySet()), UserDataTable.getMappedUserDataList(userData)));
        

        List<Map<String, Object>> result = jdbcTemplate.queryForList("query");

        return null;
    }

    private void recursiveQuery(String tableName, Map<String, Object> row) {

    }

    public static int factorial(int a) {
        if (a == 1) {
            return 1;
        }
        return a * factorial(a - 1);
    }

    public List<Map<String, Object>> executeQuery() {
        // Create a new DataSource for the dynamic database
        String url = "localhost:10001";
        String dbName = "sql_test_orders";
        String username = "db_user";
        String password = "db_user_pass";

        String getUserQuery = "SELECT * FROM Users WHERE afm = 987654321";
        String getRowsQuery = "SELECT * FROM ";
        String query = "SELECT\n" + //
                "  TABLE_NAME,\n" + //
                "  COLUMN_NAME,\n" + //
                "  REFERENCED_TABLE_NAME,\n" + //
                "  REFERENCED_COLUMN_NAME\n" + //
                "FROM\n" + //
                "  INFORMATION_SCHEMA.KEY_COLUMN_USAGE\n" + //
                "WHERE\n" + //
                "  REFERENCED_TABLE_NAME = 'Users';";

        DataSource dynamicDataSource = createDynamicDataSource(url, dbName, username, password);

        // Set the dynamic DataSource as the current DataSource
        jdbcTemplate.setDataSource(dynamicDataSource);

        // Execute the query using the dynamic DataSource

        Map<String, Object> userData = jdbcTemplate.queryForList(getUserQuery).get(0);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        for (Map<String, Object> row : result) {
            String tableName = (String) row.get("TABLE_NAME");
            String columnName = (String) row.get("COLUMN_NAME");
            String getRows = getRowsQuery + tableName + " WHERE " + columnName + " = " + "Users."
                    + userData.get(columnName);

        }

        // Reset back to the default DataSource
        jdbcTemplate.setDataSource(null);

        return result;
    }

}

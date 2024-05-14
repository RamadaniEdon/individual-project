package com.server.backend.sen_svyn;

import org.springframework.web.bind.annotation.RestController;

import com.server.backend.components.DatabaseComponent;
import com.server.backend.components.UserDataTable;
// import com.server.backend.configurations.DynamicDatabaseService;
import com.server.backend.databaseLogic.Database;
import com.server.backend.databaseLogic.DatabaseService;
import com.server.backend.ontologyLogic.OntologyRepository;
import com.server.backend.userDataLogic.PrivacyClass;
import com.server.backend.userDataLogic.PrivateData;
import com.server.backend.userDataLogic.PrivateDataService;
import com.server.backend.userDataLogic.User;
import com.server.backend.userDataLogic.UserService;
import com.server.backend.utils.Helpers;
import com.server.backend.utils.OntologyHelpers;

import jakarta.websocket.server.PathParam;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Tests {

  private final DatabaseService databaseService;
  private OntologyRepository ontologyRepository = new OntologyRepository();

  @Autowired
  public Tests(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @GetMapping("/contents")
  public String getMethodName() {
    String directoryPath = "./src/uploads";

    // Create a File object for the specified directory
    File directory = new File(directoryPath);
    String result = "";
    // Check if the directory exists
    if (directory.exists() && directory.isDirectory()) {
      // Get the list of files and directories in the specified directory
      File[] files = directory.listFiles();

      // Display the contents of the directory
      System.out.println("Contents of " + directoryPath + ":");
      if (files != null) {
        for (File file : files) {
          result += file.getName() + "\n";
          System.out.println(file.getName());
        }
      } else {
        System.out.println("Unable to list directory contents.");
      }
    } else {
      System.out.println("Specified directory does not exist or is not a directory.");
    }
    return result;
  }

  @PostMapping("/databases")
  public ResponseEntity<String> postMethodName(@RequestBody DatabaseComponent database) {
    System.out.println("Received Database:");
    System.out.println("URL: " + database.getUrl());
    System.out.println("Name: " + database.getName());
    System.out.println("Port: " + database.getPort());
    System.out.println("Host: " + database.getHost());
    System.out.println("Username: " + database.getUsername());
    System.out.println("Password: " + database.getPassword());

    for (DatabaseComponent.Table table : database.getTables()) {
      System.out.println("Table Name: " + table.getName());
      System.out.println("Table Meaning: " + table.getMeaning());

      for (DatabaseComponent.Column column : table.getColumns()) {
        System.out.println("Column Name: " + column.getName());
        System.out.println("Column Meaning: " + column.getMeaning());
      }
    }

    int dbId = databaseService.saveDatabase(Helpers.databaseFromComponent(database));

    ontologyRepository.mapDatabase(database, dbId);

    return new ResponseEntity<>("Database created successfully", HttpStatus.OK);
  }

  // @Autowired
  // DynamicDatabaseService dynamicDatabaseService;

  // @GetMapping("/databases/tables")
  // public ResponseEntity<List<Map<String, Object>>> getTables() {
  // List<Map<String, Object>> rtr = dynamicDatabaseService.executeQuery();
  // return new ResponseEntity<>(rtr, HttpStatus.OK);
  // }

  // @GetMapping("/user/data")
  // public String getMethodName(@RequestParam long afm, @RequestParam int dbId) {
  // String afmTable = "Users";
  // String afmColumn = "afm";
  // Database database = databaseService.getDatabaseById(dbId);
  // dynamicDatabaseService.setDynamicDataSource(database);
  // List<UserDataTable> userData = dynamicDatabaseService.getUserData(afmTable,
  // afmColumn, afm);

  // return OntologyHelpers.getEquivalentPropertyIRI("" + dbId,
  // "https://schema.org/taxID").toString();

  // }

  @Autowired
  private UserService userService;
  @Autowired
  private PrivateDataService privateDataService;

  // post mapping for adding a new user
  @PostMapping("/user")
  public ResponseEntity<String> postMethodName(@RequestBody User user) {
    userService.addNewUser(user);

    return ResponseEntity.ok("User added successfully");
  }

  // post mapping for adding a new privacy class
  @PostMapping("/user/privacyClass")
  public ResponseEntity<String> postMethodName(@RequestBody PrivacyClass privacyClass) {
    privateDataService.addNewPrivacyClass(privacyClass);

    return ResponseEntity.ok("Privacy Class added successfully");
  }

  // post mapping for adding a new private data
  @PostMapping("/user/privateData")
  public ResponseEntity<String> postMethodName(@RequestBody PrivateData privateData) {
    privateDataService.addNewPrivateData(privateData);

    return ResponseEntity.ok("Private Data added successfully");
  }

  // get mapping for getting all users
  @GetMapping("/user")
  public List<User> getUsers() {
    return userService.getUsers();
  }

  // get mapping for getting all privacy classes
  @GetMapping("/user/privacyClass")
  public List<PrivacyClass> getPrivacyClasses() {
    return privateDataService.getPrivacyClasses();
  }

  // get mapping for getting all private data

  @GetMapping("/user/privateData")
  public List<PrivateData> getPrivateData() {
    return privateDataService.getPrivateData();
  }

  @GetMapping("/databases")
  public List<Database> getMethodName1() {
    return databaseService.getAllDatabases();
  }

  // @GetMapping("/database/table")
  // public List<Map<String, Object>> getMethodName(@RequestParam int dbId,
  // @RequestParam String tableName) {
  // Database database = databaseService.getDatabaseById(dbId);
  // dynamicDatabaseService.setDynamicDataSource(database);
  // String primaryKey =
  // dynamicDatabaseService.getPrimaryKeyColumnName(tableName);
  // List<Map<String, Object>> tableResult =
  // dynamicDatabaseService.retrieveTable(tableName);
  // dynamicDatabaseService.resetDataSource();
  // privateDataService.filterTable(tableResult, dbId, tableName, primaryKey);

  // return tableResult;
  // }

  @GetMapping("/database/{dbId}")
  public Database getMethodName(@PathParam(value = "dbId") int dbId) {
    return databaseService.getDatabaseById(dbId);
  }

  @PutMapping("user/privateData/{id}")
  public ResponseEntity<String> putMethodName(@PathVariable String id, @RequestBody PrivateData privateData) {
    privateDataService.updateUser(privateData, id);

    return ResponseEntity.ok("Private Data added successfully");
  }

  @GetMapping("testitestitesti")
  public Map<String, Object> getMethodNamesdfsd() {
    Map<String, Object> response = new HashMap<>();
    String url = "jdbc:mysql://localhost:10001/sql_test_orders";
    String username = "db_user";
    String password = "db_user_pass";
    List<Map<String, Object>> orders = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      response.put("message", "Connected successfully to the database.");
      String selectQuery = "SELECT * FROM Orders";
      try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
          ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          Map<String, Object> order = new HashMap<>();
          order.put("orderId", resultSet.getInt("id"));
          order.put("quantity", resultSet.getInt("userId"));
          // Add more columns as needed
          orders.add(order);
        }
      }
      response.put("orders", orders);

    } catch (SQLException e) {
      response.put("error", "Failed to connect to the database: " + e.getMessage());
    }
    return response;
  }

}

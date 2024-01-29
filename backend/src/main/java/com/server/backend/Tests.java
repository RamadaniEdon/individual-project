package com.server.backend;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Dynamic;
import com.server.backend.components.DatabaseComponent;
import com.server.backend.configurations.DynamicDatabaseService;
import com.server.backend.databaseLogic.Database;
import com.server.backend.databaseLogic.DatabaseRepository;
import com.server.backend.databaseLogic.DatabaseService;
import com.server.backend.ontologyLogic.OntologyRepository;
import com.server.backend.userDataLogic.PrivacyClass;
import com.server.backend.userDataLogic.PrivateData;
import com.server.backend.userDataLogic.PrivateDataService;
import com.server.backend.userDataLogic.User;
import com.server.backend.userDataLogic.UserService;
import com.server.backend.utils.Helpers;

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

@RestController
public class Tests {

  private final DatabaseService databaseService;
  private OntologyRepository ontologyRepository = new OntologyRepository();

  @Autowired
  public Tests(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @GetMapping("/databases")
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

  @Autowired
  DynamicDatabaseService dynamicDatabaseService;

  @GetMapping("/databases/tables")
  public ResponseEntity<List<Map<String, Object>>> getTables() {
    List<Map<String, Object>> rtr = dynamicDatabaseService.executeQuery();
    return new ResponseEntity<>(rtr, HttpStatus.OK);
  }

  @GetMapping("/user/data")
  public String getMethodName(@RequestParam long afm, @RequestParam String url) {

    return "EDONI";
  }


  @Autowired
  private UserService userService;
  @Autowired
  private PrivateDataService  privateDataService;

  //post mapping for adding a new user
  @PostMapping("/user")
  public ResponseEntity<String> postMethodName(@RequestBody User user) {
    userService.addNewUser(user);

    return ResponseEntity.ok("User added successfully");
  }
  //post mapping for adding a new privacy class
  @PostMapping("/user/privacyClass")
  public ResponseEntity<String> postMethodName(@RequestBody PrivacyClass privacyClass) {
    privateDataService.addNewPrivacyClass(privacyClass);

    return ResponseEntity.ok("Privacy Class added successfully");
  }
  //post mapping for adding a new private data
  @PostMapping("/user/privateData")
  public ResponseEntity<String> postMethodName(@RequestBody PrivateData privateData) {
    privateDataService.addNewPrivateData(privateData);

    return ResponseEntity.ok("Private Data added successfully");
  }

  //get mapping for getting all users
  @GetMapping("/user")
  public List<User> getUsers() {
    return userService.getUsers();
  }

  //get mapping for getting all privacy classes
  @GetMapping("/user/privacyClass")
  public List<PrivacyClass> getPrivacyClasses() {
    return privateDataService.getPrivacyClasses();
  }

  //get mapping for getting all private data

  @GetMapping("/user/privateData")
  public List<PrivateData> getPrivateData() {
    return privateDataService.getPrivateData();
  }

  @GetMapping("/merridatabazat")
  public List<Database> getMethodName1() {
      return databaseService.getAllDatabases();
  }
  

}

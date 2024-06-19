package com.database.federation.userData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.federation.configurations.Protected;
import com.database.federation.database.DatabaseModel;
import com.database.federation.database.DatabaseService;
import com.database.federation.dbConnector.MySQLService;
import com.database.federation.ontology.Ontology;
import com.database.federation.ontology.OntologyService;
import com.database.federation.user.UserModel;

@RestController
@RequestMapping("/userData")
public class UserDataController {

    
    private final DatabaseService databaseService;
    private final UserDataService userDataService;

    @Autowired
    public UserDataController(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.userDataService = new UserDataService();
    }


    @PostMapping("/users")
    public ResponseEntity<String> addDocument(@RequestBody UserModel user) {
        try {
            return new ResponseEntity<>("Document added successfully with id: ",
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Protected
    @GetMapping("/users/{id}")
    public ResponseEntity<String> getUserById(@PathVariable String id) {
        try {

            return new ResponseEntity<>("user", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Protected
    @GetMapping
    public ResponseEntity<UserDataGlobalFormat> getUserData(@RequestAttribute("userAfm") String userAfm) {
        try {
            // DatabaseModel database = databaseService.findDatabaseById(null);
            DatabaseModel database = new DatabaseModel();
            database.setId(null);
            UserDataGlobalFormat userData = userDataService.getGlobalFormat(database);
            String dbId = "6672f70698abc9024e7d19d3";
            Ontology ontology = new Ontology(OntologyService.getNewOntologyPrefix(dbId), "./" +dbId+ ".owl", false);
            UserDataGlobalFormat result = ontology.getGlobalFormat();
            MySQLService dbService = new MySQLService("localhost:10010", "orders", "root", "root_pass");
            
            dbService.mapDatabaseToGlobalFormat(result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

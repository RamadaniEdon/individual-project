package com.database.federation.userData;

import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
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
import com.database.federation.dbConnector.MongoService;
import com.database.federation.dbConnector.MySQLService;
import com.database.federation.ontology.Ontology;
import com.database.federation.ontology.OntologyService;
import com.database.federation.user.UserModel;
import com.database.federation.utils.ClassWithProperties;

@RestController
@RequestMapping("/userData")
public class UserDataController {

    private final DatabaseService databaseService;
    private final UserDataService userDataService;
    private final OntologyService ontologyService;

    @Autowired
    public UserDataController(DatabaseService databaseService) throws Exception {
        this.databaseService = databaseService;
        this.userDataService = new UserDataService();
        this.ontologyService = new OntologyService();
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
    public ResponseEntity<List<UserDataGlobalFormat>> getUserData(@RequestAttribute("userAfm") String userAfm) {

        try {
            // DatabaseModel database = databaseService.findDatabaseById(null);
            List<UserDataGlobalFormat> results = new ArrayList<>();
            List<DatabaseModel> dbs = databaseService.getAllDatabases();
            for (DatabaseModel db : dbs) {
                Ontology ontology = new Ontology(OntologyService.getNewOntologyPrefix(db.getId()),
                        "./" + db.getId() + ".owl", false);
                UserDataGlobalFormat result = ontology.getGlobalFormat();
                result.setCompanyName(db.getCompanyName());
                result.setDatabaseId(db.getId());
                if (db.getDbType().equals("sql")) {
                    MySQLService dbService = new MySQLService(db.getUrl(), db.getDbName(), "root", "root_pass");
                    dbService.mapDatabaseToGlobalFormat(result, userAfm);
                } else if (db.getDbType().equals("nosql")) {
                    MongoService dbService = new MongoService(db.getUrl(), db.getDbName());
                    dbService.mapDatabaseToGlobalFormat(result, userAfm);
                }
                ontologyService.respectAccessControl(result, db.getId());
                results.add(result);
            }

            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Protected
    @PostMapping("/database/{dbId}/accessControl")
    public ResponseEntity<String> changeAccessControl(@RequestAttribute("userAfm") String userAfm,
            @RequestBody CategoryForData dataCategory, @PathVariable String dbId) {
        try {

            ontologyService.changeAccessControlOfData(dataCategory, dbId, userAfm);

            return new ResponseEntity<>("Category updated successfully",
                    HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Protected
    @PostMapping("/filter")
    public ResponseEntity<List<UserDataGlobalFormat>> getFilteredUserData(@RequestAttribute("userAfm") String userAfm,
            @RequestBody UserDataFilter filter) {

        try {
            String resString = "";
            List<UserDataGlobalFormat> results = new ArrayList<>();

            List<DatabaseModel> databases = new ArrayList<>();

            if (filter.getDatabaseId() != null) {
                DatabaseModel db = databaseService.findDatabaseById(filter.getDatabaseId());
                Ontology ontology = new Ontology(OntologyService.getNewOntologyPrefix(db.getId()),
                        "./" + db.getId() + ".owl", false);
                System.out.println("Qetu erdha");
                if (ontologyService.checkFilters(filter, ontology)) {
                    databases.add(db);
                }
            } else {
                List<DatabaseModel> dbs = databaseService.getAllDatabases();
                for (DatabaseModel db : dbs) {
                    Ontology ontology = new Ontology(OntologyService.getNewOntologyPrefix(db.getId()),
                            "./" + db.getId() + ".owl", false);
                    System.out.println("Qetu erdha");
                    if (ontologyService.checkFilters(filter, ontology)) {
                        databases.add(db);
                    }
                }
            }

            for (DatabaseModel db : databases) {
                Ontology ontology = new Ontology(OntologyService.getNewOntologyPrefix(db.getId()),
                        "./" + db.getId() + ".owl", false);
                UserDataGlobalFormat result = ontology.getGlobalFormat();
                result.setDatabaseId(db.getId());
                result.setCompanyName(db.getCompanyName());
                if (db.getDbType().equals("sql")) {
                    MySQLService dbService = new MySQLService(db.getUrl(), db.getDbName(), "root", "root_pass");
                    dbService.mapDatabaseToGlobalFormat(result, userAfm);
                } else if (db.getDbType().equals("nosql")) {
                    MongoService dbService = new MongoService(db.getUrl(), db.getDbName());
                    dbService.mapDatabaseToGlobalFormat(result, userAfm);
                }
                ontologyService.respectAccessControl(result, db.getId());
                results.add(result);
            }

            List<UserDataGlobalFormat> toRemove = new ArrayList<>();
            for (UserDataGlobalFormat res : results) {
                res.applyFilters(filter);
                if (res.getCollections() == null || res.getCollections().isEmpty())
                    toRemove.add(res);
            }

            for (UserDataGlobalFormat res : toRemove) {
                results.remove(res);
            }

            // DatabaseModel database = databaseService.findDatabaseById(null);
            // List<UserDataGlobalFormat> results = new ArrayList<>();
            // List<DatabaseModel> dbs = databaseService.getAllDatabases();
            // for(DatabaseModel db : dbs){
            // Ontology ontology = new
            // Ontology(OntologyService.getNewOntologyPrefix(db.getId()), "./" + db.getId()
            // + ".owl", false);
            // UserDataGlobalFormat result = ontology.getGlobalFormat();
            // result.setCompanyName(db.getCompanyName());
            // if(db.getDbType().equals("sql")){
            // MySQLService dbService = new MySQLService(db.getUrl(), db.getDbName(),
            // "root", "root_pass");
            // dbService.mapDatabaseToGlobalFormat(result, userAfm);
            // }
            // else if(db.getDbType().equals("nosql")){
            // MongoService dbService = new MongoService(db.getUrl(), db.getDbName());
            // dbService.mapDatabaseToGlobalFormat(result, userAfm);
            // }
            // ontologyService.respectAccessControl(result, db.getId());
            // results.add(result);
            // }

            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

}

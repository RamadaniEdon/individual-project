package com.database.federation.database;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.database.federation.ontology.OntologyService;
import com.database.federation.utils.DatabaseForm;


@RestController
public class DatabaseController {

  @PostMapping("/databases")
  public ResponseEntity<String> addDocument(@RequestBody DatabaseForm databaseForm) {
    try {
      databaseForm.setId("1");
      System.out.println(databaseForm);
      OntologyService.mapDatabaseToOntology(databaseForm);
      return new ResponseEntity<>("Database created successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to add document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}

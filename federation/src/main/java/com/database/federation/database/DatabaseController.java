package com.database.federation.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.database.federation.ontology.OntologyService;
import com.database.federation.user.UserModel;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class DatabaseController {

  @PostMapping("/databases")
  public ResponseEntity<String> addDocument() {
    try {
      OntologyService.mapDatabaseToOntology(null);
      return new ResponseEntity<>("Database created successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to add document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}

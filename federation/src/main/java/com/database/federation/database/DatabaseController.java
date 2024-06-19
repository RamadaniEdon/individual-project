package com.database.federation.database;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import com.database.federation.ontology.Ontology;
import com.database.federation.ontology.OntologyService;
import com.database.federation.utils.DatabaseForm;
import com.database.federation.utils.FieldType;

@RestController
@RequestMapping("/databases")
public class DatabaseController {

  OntologyService ontologyService;
  DatabaseService databaseService;

  @Autowired
  public DatabaseController(DatabaseService databaseService) {
    this.databaseService = databaseService;
    try {
      ontologyService = new OntologyService();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @PostMapping
  public ResponseEntity<String> addDocument(@RequestBody DatabaseForm databaseForm) {
    try {
      databaseForm.setUrl(databaseForm.getUrl()+"/");
      DatabaseModel database = new DatabaseModel(databaseForm);
      database = databaseService.addDatabase(database);

      databaseForm.setId(database.getId());
      ontologyService.mapDatabaseToOntology(databaseForm);
      return new ResponseEntity<>("Database created successfully " + database.getId(), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to add document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{id}/ontology")
  public ResponseEntity<Object> getOntology( @PathVariable String id) {
    try {
      Path filePath = Paths.get("./ontologies/"+id+".owl").toAbsolutePath().normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists()) {
        return new ResponseEntity<>("Ontology doesnt exist", HttpStatus.NOT_FOUND);
      }
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_XML)
          .body(resource);
    } catch (Exception ex) {
      return new ResponseEntity<>("Failed to load ontology: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/data/test")
  public ResponseEntity<String> saveTest() {
    try {
      Ontology ont = new Ontology("123", "123.owl", true);
      ont.createOntologyFile();
      return new ResponseEntity<>("Database created successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to add document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}

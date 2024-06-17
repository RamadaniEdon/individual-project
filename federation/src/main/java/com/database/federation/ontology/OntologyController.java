package com.database.federation.ontology;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
@RestController
@RequestMapping("/ontologies")
public class OntologyController {

  OntologyService ontologyService;

  @Autowired
  public OntologyController() {
    try {
      ontologyService = new OntologyService();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GetMapping("/categories")
  public ResponseEntity<Object> getCategoriesOntology() {
    try {
      Path filePath = Paths.get(OntologyService.CATEGORY_ONTOLOGY_PATH).toAbsolutePath().normalize();
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
  

}

package com.database.federation.ontology;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.federation.configurations.Protected;
import com.database.federation.user.UserModel;
import com.database.federation.utils.CategoryJson;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
@RestController
@RequestMapping("/ontologies")
public class OntologyController {

  OntologyService ontologyService;

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

  @Protected
  @PostMapping("/categories")
  public ResponseEntity<String> createNewCategory(@RequestAttribute("userAfm") String userAfm, @RequestBody CategoryJson categoryInfo) {
    try {

      ontologyService.addCategory(categoryInfo, userAfm);

      return new ResponseEntity<>("Category created successfully", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to create category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Protected
  @GetMapping("/categories/user")
  public ResponseEntity<List<CategoryJson>> getCategories(@RequestAttribute("userAfm") String userAfm) {
    try {

      List<CategoryJson> categories = ontologyService.getCategories(userAfm);

      return new ResponseEntity<>(categories, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR );
    }
  }
  

}

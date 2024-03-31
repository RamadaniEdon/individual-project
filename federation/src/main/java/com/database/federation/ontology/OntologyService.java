package com.database.federation.ontology;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Path;
import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import com.database.federation.utils.DatabaseForm;

public class OntologyService {

  private static OWLOntologyManager manager;
  private static String schemaOrgURI = "https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/26.0/schemaorg.owl";

  public static void mapDatabaseToOntology(DatabaseForm form) throws Exception {
    // This method will map the database form to the ontology
    // Create OWLOntologyManager
    manager = OWLManager.createOWLOntologyManager();

    // OWLOntology schemaOrgOntology =
    // manager.loadOntologyFromOntologyDocument(IRI.create(schemaOrgURI));

    // Create an empty ontology
    OWLOntology ontology = manager.createOntology();

    // Add import declaration to the ontology
    OWLImportsDeclaration importDeclaration = manager.getOWLDataFactory()
        .getOWLImportsDeclaration(IRI.create(schemaOrgURI));
    manager.applyChange(new AddImport(ontology, importDeclaration));

    addEquivalentClass(ontology);

    createOntologyFile(ontology, "ontology.owl");
  }

  public static boolean isFormSetCorrectly(DatabaseForm form) {
    // check if the database comply with the schema.org ontology standards
    return true;
  }


  //Save Ontology to Directory
  public static void createOntologyFile(OWLOntology ontology, String fileName) throws Exception {

    // Get current directory
    Path currentDirectory = Paths.get("./ontologies").toAbsolutePath();

    // Define file path
    Path filePath = currentDirectory.resolve(fileName);

    // Create file
    File file = filePath.toFile();

    // Save the ontology to file
    manager.saveOntology(ontology, new FileOutputStream(file));
  }

  //Add equivalent class axiom to ontology
  public static void addEquivalentClass(OWLOntology ontology) {
    IRI myClassIRI = IRI.create("http://example.org#myClass");
    OWLClass myClass = manager.getOWLDataFactory().getOWLClass(myClassIRI);

    // Define the class equivalent to schema.org:ActivateAction
    IRI activateActionIRI = IRI.create("https://schema.org/ActivateAction");
    OWLClass activateAction = manager.getOWLDataFactory().getOWLClass(activateActionIRI);

    // Define the equivalence axiom
    OWLEquivalentClassesAxiom equivalentClassesAxiom = manager.getOWLDataFactory()
        .getOWLEquivalentClassesAxiom(myClass, activateAction);

    // Add the equivalence axiom to the ontology
    manager.addAxiom(ontology, equivalentClassesAxiom);
  }

  //NEED TO BE IMPLEMENTED
  public static boolean isPropertyOfClass(String propertyStr, String classStr) {
    // Check if the property is a property of the class
    return true;
  }

  //NEED TO BE IMPLEMENTED
  public static boolean isObjectProperty(String propertyStr){
    // Check if the property is an object property
    return true;
  }

  //NEED TO BE IMPLEMENTED
  public static boolean isDataTypeProperty(String propertyStr){
    // Check if the property is a datatype property
    return true;
  }

  //NEED TO BE IMPLEMENTED
  public static boolean acceptsDataTypes(String propertyStr){
    // Check if the property accepts data types in range
    return true;
  }
  
  //NEED TO BE IMPLEMENTED
  public static List<String> getSuperClasses(String classStr){
    // Get the super classes of the class
    return null;
  }
  
  //FSHIJE SE PO BOHET MUT
  public static void main(String[] args) {
    try {
      
    } catch (Exception e) {
      System.out.println("Failed to map database to ontology: " + e.getMessage());
    }
  }
}

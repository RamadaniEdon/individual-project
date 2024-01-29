package com.server.backend;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Reasoner1 {

  String directoryPath = "./src/uploads/ontologies/";

  


  public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
    String schemaOrgFilePath = "./customontology.owl";
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLOntology schemaOrgOntology = manager.loadOntologyFromOntologyDocument(new File(schemaOrgFilePath));

    // Create a new ontology

    // Import Schema.org ontology

    // Create 'Users' class as a subclass of 'Person'
    OWLClass personClass = manager.getOWLDataFactory().getOWLClass(IRI.create("http://schema.org/Persondd"));
    OWLClass usersClass = manager.getOWLDataFactory().getOWLClass(IRI.create("#Usersa"));
    OWLAxiom subclassAxiom = manager.getOWLDataFactory().getOWLSubClassOfAxiom(usersClass, personClass);
    manager.addAxiom(schemaOrgOntology, subclassAxiom);

    // // Create 'name' property as equivalent to 'givenName'
    // OWLDataProperty givenNameProperty = manager.getOWLDataFactory()
    // .getOWLDataProperty(IRI.create("http://schema.org/givenName"));
    // OWLDataProperty nameProperty =
    // manager.getOWLDataFactory().getOWLDataProperty(IRI.create("#name"));
    // OWLAxiom equivalentPropertyAxiom =
    // manager.getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(nameProperty,
    // givenNameProperty);
    // manager.addAxiom(customOntology, equivalentPropertyAxiom);

    // // Save the custom ontology to a new file with explicit owl prefixes
    // File outputFile = new File("customontology.owl");
    try (OutputStream outputStream = new FileOutputStream("customontology1.owl")) {
      manager.saveOntology(schemaOrgOntology, new RDFXMLDocumentFormat(), outputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
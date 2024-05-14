package com.server.backend;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.IOException;
import java.net.URL;

public class Main {
  public static void main(String[] args) {
    try {
      // Load schema.org ontology from URL
      OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
      // URL schemaOrgUrl = new
      // URL("https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/26.0/schemaorg.owl");
      // OWLOntology schemaOrg =
      // manager.loadOntologyFromOntologyDocument(schemaOrgUrl.openStream());

      OWLOntology schemaOrg = manager.loadOntologyFromOntologyDocument(
          IRI.create("https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/26.0/schemaorg.owl"));

      // Get all classes defined in the ontology
      for (OWLClass cls : schemaOrg.getClassesInSignature()) {
        System.out.println("Class: " + cls.getIRI());
      }
    } catch (OWLOntologyCreationException e) {
      e.printStackTrace();
    }
  }
}
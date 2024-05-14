package com.server.backend.sen_svyn;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Reasoner {
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
        // Load the Schema.org ontology from a local file
        String schemaOrgFilePath = "./backend/src/main/resources/ontologies/schemaorg.owl";
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology schemaOrgOntology = manager.loadOntologyFromOntologyDocument(new File(schemaOrgFilePath));

        // Create a new ontology
        OWLOntology customOntology = manager.createOntology();

        // Import Schema.org ontology
        OWLImportsDeclaration schemaOrgImport = manager.getOWLDataFactory().getOWLImportsDeclaration(schemaOrgOntology.getOntologyID().getOntologyIRI().get());
        manager.applyChange(new AddImport(customOntology, schemaOrgImport));

        // Create 'Users' class as a subclass of 'Person'
        OWLClass personClass = manager.getOWLDataFactory().getOWLClass(IRI.create("http://schema.org/Person"));
        OWLClass usersClass = manager.getOWLDataFactory().getOWLClass(IRI.create("#Users"));
        OWLAxiom subclassAxiom = manager.getOWLDataFactory().getOWLSubClassOfAxiom(usersClass, personClass);
        manager.addAxiom(customOntology, subclassAxiom);

        // Create 'name' property as equivalent to 'givenName'
        OWLDataProperty givenNameProperty = manager.getOWLDataFactory().getOWLDataProperty(IRI.create("http://schema.org/givenName"));
        OWLDataProperty nameProperty = manager.getOWLDataFactory().getOWLDataProperty(IRI.create("#name"));
        OWLAxiom equivalentPropertyAxiom = manager.getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(nameProperty, givenNameProperty);
        manager.addAxiom(customOntology, equivalentPropertyAxiom);

        // Save the custom ontology to a new file with explicit owl prefixes
        File outputFile = new File("./backend/customontology.owl");
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            manager.saveOntology(customOntology, new RDFXMLDocumentFormat(), outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

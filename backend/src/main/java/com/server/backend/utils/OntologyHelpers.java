package com.server.backend.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class OntologyHelpers {

  // private static final String schemaOrgFilePath =
  // "./backend/src/main/resources/ontologies/schemaorg.owl";
  private static final String schemaOrgFilePath = "./target/classes/ontologies/schemaorg.owl";
  private static OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
  private static final File ontologyFile = new File(schemaOrgFilePath);
  private static OWLOntology ontology;
  private static OWLOntology newOntology;
  private static final String schemaOrgIRI = "https://schema.org/";
  static String directoryPath = "./src/uploads/ontologies/";

  public static void reloadOntologyManager() throws OWLOntologyCreationException {
    ontologyManager = OWLManager.createOWLOntologyManager();
  }

  public static void reloadOntology() throws OWLOntologyCreationException {
    ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);
  }

  public static void reloadNewOntology() throws OWLOntologyCreationException {
    newOntology = ontologyManager.createOntology();
    OWLImportsDeclaration schemaOrgImport = ontologyManager.getOWLDataFactory()
        .getOWLImportsDeclaration(ontology.getOntologyID().getOntologyIRI().get());
    ontologyManager.applyChange(new AddImport(newOntology, schemaOrgImport));
  }

  public static void saveNewOntology(int pathAdjuctor) throws OWLOntologyStorageException {
    File outputFile = new File(directoryPath + "ontology"+pathAdjuctor+".owl");
    try (OutputStream outputStream = new FileOutputStream(outputFile)) {
      ontologyManager.saveOntology(newOntology, new RDFXMLDocumentFormat(), outputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void addNewEquivalentProperty(String type, String newProperty, String equivalentProperty)
      throws OWLOntologyCreationException {

    OWLClass typeClass = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create(schemaOrgIRI + type));
    OWLObjectProperty eqObjectProperty = ontologyManager.getOWLDataFactory()
        .getOWLObjectProperty(IRI.create(schemaOrgIRI + equivalentProperty));
    if (isDataProperty(equivalentProperty)) {
      OWLDataProperty eqDataProperty = ontologyManager.getOWLDataFactory()
          .getOWLDataProperty(IRI.create(schemaOrgIRI + equivalentProperty));
      if (isClassOfPropertyDomain(typeClass, eqObjectProperty)) {
        addEquivalentDataProperty(newProperty, equivalentProperty);
      } else if (PropertyMapper.getLinkingClass(equivalentProperty, type) != null) {
        addEquivalentDataProperty(newProperty, equivalentProperty);
      } else {
        System.out.println("Property '" + equivalentProperty + "' is not of type '" + type + "'");
      }
    } else if (isObjectProperty(equivalentProperty)) {
      if (isClassOfPropertyDomain(typeClass, eqObjectProperty)) {
        addEquivalentObjectProperty(newProperty, equivalentProperty);
      } else if (PropertyMapper.getLinkingClass(equivalentProperty, type) != null) {
        addEquivalentObjectProperty(newProperty, equivalentProperty);
      } else {
        System.out.println("Property '" + equivalentProperty + "' is not of type '" + type + "'");
      }
    }

    // if (isClassOfPropertyDomain(typeClass, eqProperty)) {
    // addEquivalentProperty(newProperty, equivalentProperty);
    // } else if (PropertyMapper.getLinkingClass(equivalentProperty, type) != null)
    // {
    // addEquivalentProperty(newProperty, equivalentProperty);
    // } else {
    // System.out.println("Property '" + equivalentProperty + "' is not of type '" +
    // type + "'");
    // }
  }

  // public static void isObjectProperty(String property)
  public static void addEquivalentObjectProperty(String newProperty, String equivalentProperty) {
    OWLObjectProperty givenNameProperty = ontologyManager.getOWLDataFactory()
        .getOWLObjectProperty(IRI.create(schemaOrgIRI + equivalentProperty));
    OWLObjectProperty nameProperty = ontologyManager.getOWLDataFactory()
        .getOWLObjectProperty(IRI.create(newProperty));
    OWLAxiom equivalentPropertyAxiom = ontologyManager.getOWLDataFactory()
        .getOWLEquivalentObjectPropertiesAxiom(givenNameProperty, nameProperty);
    ontologyManager.addAxiom(newOntology, equivalentPropertyAxiom);
  }

  public static void addEquivalentDataProperty(String newProperty, String equivalentProperty) {
    OWLDataProperty givenNameProperty = ontologyManager.getOWLDataFactory()
        .getOWLDataProperty(IRI.create(schemaOrgIRI + equivalentProperty));
    OWLDataProperty nameProperty = ontologyManager.getOWLDataFactory().getOWLDataProperty(IRI.create(newProperty));
    OWLAxiom equivalentPropertyAxiom = ontologyManager.getOWLDataFactory()
        .getOWLEquivalentDataPropertiesAxiom(givenNameProperty, nameProperty);
    ontologyManager.addAxiom(newOntology, equivalentPropertyAxiom);
    System.out.println("Property '" + equivalentProperty + "' has the type '" + newProperty + "' for domain: ");
  }

  public static void addNewSubclassType(String newType, String superClassStr) {

    OWLClass superClass = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create(schemaOrgIRI + superClassStr));
    OWLClass newClass = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create(newType));
    OWLAxiom newClassAxiom = ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(newClass, superClass);
    ontologyManager.addAxiom(newOntology, newClassAxiom);

  }

  private static boolean isClassOfPropertyDomain(OWLClass cls, OWLObjectProperty property) {

    Set<OWLClass> classes = getPropertyDomainTypes(property);

    for (OWLClass cl : classes) {
      if (isSubclassOf(cls, cl)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isClassOfDataPropertyDomain(OWLClass cls, OWLDataProperty property) {

    Set<OWLClass> classes = getDataPropertyDomainClasses(property);

    for (OWLClass cl : classes) {
      if (isSubclassOf(cls, cl)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isSubclassOf(OWLClass subclass, OWLClass superclass) {
    // Create a structural reasoner (you can use HermiT if it's available)
    if (subclass.getIRI().toString().equals(superclass.getIRI().toString())) {
      return true;
    }

    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

    // Check if the subclass is a subclass of the superclass
    NodeSet<OWLClass> subClasses = reasoner.getSubClasses(superclass, false);

    for (Node<OWLClass> node : subClasses) {
      if (node.contains(subclass)) {
        return true;
      }
    }

    return false;
  }

  public static Set<OWLClass> getPropertyDomainTypes(OWLObjectProperty property) {
    Set<OWLClass> domainTypes = new HashSet<>();

    // Get the axioms for the property
    for (OWLObjectPropertyDomainAxiom domainAxiom : ontology.getObjectPropertyDomainAxioms(property)) {
      OWLClassExpression domain = domainAxiom.getDomain();

      // Add the domain class or classes to the set
      if (domain.isClassExpressionLiteral()) {
        domainTypes.add(domain.asOWLClass());
      } else if (domain instanceof OWLObjectUnionOf) {
        OWLObjectUnionOf unionOf = (OWLObjectUnionOf) domain;
        for (OWLClassExpression classExpression : unionOf.getOperands()) {
          if (classExpression.isClassExpressionLiteral()) {
            domainTypes.add(classExpression.asOWLClass());
          }
        }
      }
    }

    return domainTypes;
  }

  public static Set<OWLClass> getDataPropertyDomainClasses(OWLDataProperty dataProperty) {
    Set<OWLClass> domainClasses = new HashSet<>();

    // Get the domain axioms for the data property
    for (OWLDataPropertyDomainAxiom domainAxiom : ontology.getDataPropertyDomainAxioms(dataProperty)) {
      OWLClassExpression domain = domainAxiom.getDomain();

      if (domain.isClassExpressionLiteral()) {
        domainClasses.add(domain.asOWLClass());
      } else if (domain instanceof OWLObjectUnionOf) {
        OWLObjectUnionOf unionOf = (OWLObjectUnionOf) domain;
        for (OWLClassExpression classExpression : unionOf.getOperands()) {
          if (classExpression.isClassExpressionLiteral()) {
            domainClasses.add(classExpression.asOWLClass());
          }
        }
      }
      // You can handle other types of expressions as needed
    }

    return domainClasses;
  }

  public static boolean isObjectProperty(String property) {

    IRI propertyIRIObject = IRI.create(schemaOrgIRI + property);
    OWLObjectProperty objectProperty = ontology.getOWLOntologyManager().getOWLDataFactory()
        .getOWLObjectProperty(propertyIRIObject);
    return ontology.getObjectPropertiesInSignature().contains(objectProperty);
  }

  public static boolean isDataProperty(String property) {

    IRI propertyIRIObject = IRI.create(schemaOrgIRI + property);
    OWLDataProperty dataProperty = ontology.getOWLOntologyManager().getOWLDataFactory()
        .getOWLDataProperty(propertyIRIObject);
    return ontology.getDataPropertiesInSignature().contains(dataProperty);
  }

}

package com.database.federation.ontology;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Path;
import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.database.federation.utils.DatabaseForm;

public class OntologyService {

  private static final String SCHEMA_ORG_URI = "https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/26.0/schemaorg.owl";
  private static final String SCHEMA_ORG_PREFIX = "https://schema.org/";
  private static OWLOntologyManager manager;
  private static OWLOntology schemaOrgOntology;
  private static String newOntologyPrefix;

  static {
    manager = OWLManager.createOWLOntologyManager();
    try {
      loadSchemaOntology();
    } catch (Exception e) {
      System.out.println("Failed to load schema.org ontology: " + e.getMessage());
    }

    newOntologyPrefix = "http://example.org#"; //ma vone e hekiiiii
  }

  public static void mapDatabaseToOntology(DatabaseForm form) throws Exception {
    // This method will map the database form to the ontology
    // Create OWLOntologyManager

    // OWLOntology schemaOrgOntology =
    // manager.loadOntologyFromOntologyDocument(IRI.create(SCHEMA_ORG_URI));

    // Create an empty ontology
    OWLOntology ontology = manager.createOntology();

    // Add import declaration to the ontology
    OWLImportsDeclaration importDeclaration = manager.getOWLDataFactory()
        .getOWLImportsDeclaration(IRI.create(SCHEMA_ORG_URI));
    manager.applyChange(new AddImport(ontology, importDeclaration));

    addEquivalentClass(ontology, "myClassStr", "schemaClassStr");
    addSubClass(ontology, "myClassStr", "schemaClassStr");
    addSubObjectProperty(ontology, "myObjectPropertyStr", "schemaObjectPropertyStr");
    addEquivalentObjectProperty(ontology, "myObjectPropertyStr", "schemaObjectPropertyStr");
    addSubDatatypeProperty(ontology, "myDatatypePropertyStr", "schemaDatatypePropertyStr");
    addEquivalentDatatype(ontology, "myDatatypeStr", "schemaDatatypeStr");

    createOntologyFile(ontology, "ontology.owl");
  }

  // NEED TO BE IMPLEMENTED
  public static boolean isFormSetCorrectly(DatabaseForm form) {
    // check if the database comply with the schema.org ontology standards
    return true;
  }

  // Save Ontology to Directory
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

  // Add equivalent class axiom to ontology - DONE
  public static void addEquivalentClass(OWLOntology ontology, String myClassStr, String schemaClassStr) {
    IRI myClassIRI = IRI.create(newOntologyPrefix + myClassStr);
    OWLClass myClass = manager.getOWLDataFactory().getOWLClass(myClassIRI);

    // Define the class equivalent to schema.org:ActivateAction
    IRI schemaClassIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaClassStr);
    OWLClass schemaClass = manager.getOWLDataFactory().getOWLClass(schemaClassIRI);

    // Define the equivalence axiom
    OWLEquivalentClassesAxiom equivalentClassesAxiom = manager.getOWLDataFactory()
        .getOWLEquivalentClassesAxiom(myClass, schemaClass);

    // Add the equivalence axiom to the ontology
    manager.addAxiom(ontology, equivalentClassesAxiom);
  }

  // DONE
  public static void addSubClass(OWLOntology ontology, String myClassStr, String schemaClassStr) {
    // Define the IRIs for myClass and ActivateAction
    IRI myClassIRI = IRI.create(newOntologyPrefix + myClassStr);
    IRI schemaClassIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaClassStr);

    // Create OWLClass objects for myClass and ActivateAction
    OWLClass myClass = manager.getOWLDataFactory().getOWLClass(myClassIRI);
    OWLClass schemaClass = manager.getOWLDataFactory().getOWLClass(schemaClassIRI);

    // Define the subclass axiom stating that myClass is a subclass of
    // schemaClass
    OWLSubClassOfAxiom subclassAxiom = manager.getOWLDataFactory().getOWLSubClassOfAxiom(myClass, schemaClass);

    // Add the subclass axiom to the ontology
    manager.addAxiom(ontology, subclassAxiom);
  }

  // DONE
  public static void addSubObjectProperty(OWLOntology ontology, String myPropertyStr, String schemaPropertyStr) {
    // Define the IRIs for banimi and address
    IRI myPropertyIRI = IRI.create(newOntologyPrefix + myPropertyStr);
    IRI schemaPropertyIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaPropertyStr);

    // Create OWLObjectProperty objects for banimi and address
    OWLObjectProperty myProperty = manager.getOWLDataFactory().getOWLObjectProperty(myPropertyIRI);
    OWLObjectProperty schemaProperty = manager.getOWLDataFactory().getOWLObjectProperty(schemaPropertyIRI);

    // Define the subclass axiom stating that myProperty is a subproperty of schemaProperty
    OWLSubObjectPropertyOfAxiom subPropertyAxiom = manager.getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(myProperty,
        schemaProperty);

    // Add the subproperty axiom to the ontology
    manager.addAxiom(ontology, subPropertyAxiom);
  }

  // DONE
  public static void addEquivalentObjectProperty(OWLOntology ontology, String myPropertyStr, String schemaPropertyStr) {
    // Define the IRIs for banimi and address
    IRI myPropertyIRI = IRI.create(newOntologyPrefix + myPropertyStr);
    IRI schemaPropertyIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaPropertyStr);

    // Create OWLObjectProperty objects for banimi and address
    OWLObjectProperty myProperty = manager.getOWLDataFactory().getOWLObjectProperty(myPropertyIRI);
    OWLObjectProperty schemaProperty = manager.getOWLDataFactory().getOWLObjectProperty(schemaPropertyIRI);

    // Define the subclass axiom stating that mProperty is a subproperty of schemaProperty
    OWLEquivalentObjectPropertiesAxiom equivalentPropertiesAxiom = manager.getOWLDataFactory()
        .getOWLEquivalentObjectPropertiesAxiom(myProperty, schemaProperty);

    // Add the subproperty axiom to the ontology
    manager.addAxiom(ontology, equivalentPropertiesAxiom);
  }

  // DONE
  public static void addSubDatatypeProperty(OWLOntology ontology, String myPropertyStr, String schemaPropertyStr) {
    // Define the IRIs for closeTime and closes
    IRI myPropertyIRI = IRI.create(newOntologyPrefix + myPropertyStr);
    IRI schemaPropertyIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaPropertyStr);

    // Create OWLDatatypeProperty objects for closeTime and closes
    OWLDataProperty myProperty = manager.getOWLDataFactory().getOWLDataProperty(myPropertyIRI);
    OWLDataProperty schemaProperty = manager.getOWLDataFactory().getOWLDataProperty(schemaPropertyIRI);

    // Define the subclass axiom stating that myProperty is a subproperty of closes
    OWLSubDataPropertyOfAxiom subPropertyAxiom = manager.getOWLDataFactory().getOWLSubDataPropertyOfAxiom(myProperty,
        schemaProperty);

    // Add the subproperty axiom to the ontology
    manager.addAxiom(ontology, subPropertyAxiom);
  }

  // DONE
  public static void addEquivalentDatatype(OWLOntology ontology, String myPropertyStr, String schemaPropertyStr) {
    // Define the IRIs for closeTime and closes
    IRI myPropertyIRI = IRI.create(newOntologyPrefix + myPropertyStr);
    IRI schemaPropertyIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaPropertyStr);

    // Create OWLDatatypeProperty objects for closeTime and closes
    OWLDataProperty myProperty = manager.getOWLDataFactory().getOWLDataProperty(myPropertyIRI);
    OWLDataProperty schemaProperty = manager.getOWLDataFactory().getOWLDataProperty(schemaPropertyIRI);

    // Define the equivalent property axiom stating that myProperty is equivalent to
    // schemaProperty
    OWLEquivalentDataPropertiesAxiom equivalentPropertiesAxiom = manager.getOWLDataFactory()
        .getOWLEquivalentDataPropertiesAxiom(myProperty, schemaProperty);

    // Add the equivalent property axiom to the ontology
    manager.addAxiom(ontology, equivalentPropertiesAxiom);
  }

  // DONE
  public static boolean isPropertyOfClass(String propertyStr, String classStr) throws Exception {
    // Get Domain Classes of the property
    List<String> domainClasses = getDomainClasses(propertyStr);
    Set<String> superClasses = getSuperClasses(classStr);
    // add the class itself to the superclasses to check for it as well
    superClasses.add(classStr);

    for (String domainClass : domainClasses) {
      if (superClasses.contains(domainClass)) {
        return true;
      }
    }

    return false;
  }

  // DONE
  public static boolean isObjectProperty(String propertyName) throws Exception {
    // Check if the property is an object property

    OWLDataProperty dataProperty = schemaOrgOntology.getOWLOntologyManager().getOWLDataFactory()
        .getOWLDataProperty(IRI.create(SCHEMA_ORG_PREFIX + propertyName));
    OWLObjectProperty objectProperty = schemaOrgOntology.getOWLOntologyManager().getOWLDataFactory()
        .getOWLObjectProperty(IRI.create(SCHEMA_ORG_PREFIX + propertyName));

    if (schemaOrgOntology.containsDataPropertyInSignature(dataProperty.getIRI())) {
      return false;
    } else if (schemaOrgOntology.containsObjectPropertyInSignature(objectProperty.getIRI())) {
      return true;
    } else {
      throw new IllegalArgumentException(propertyName + " is neither a datatype property nor an object property.");
    }
  }

  // DONE
  // http://www.w3.org/1999/02/22-rdf-syntax-ns#type /////
  // This is also and object property but not in the schema.org ontology but used
  // there
  public static boolean acceptsDataTypes(String propertyStr) throws Exception {
    if (!isObjectProperty(propertyStr))
      return true;

    List<String> rangeClasses = getRangeClasses(propertyStr);

    for (String rangeClass : rangeClasses) {
      Set<String> superClasses = getSuperClasses(rangeClass);
      if (superClasses.contains("DataType")) {
        return true;
      }
    }

    return false;
  }

  // DONE
  public static Set<String> getSuperClasses(String className) throws Exception {
    // Get the class object for the given class name
    OWLDataFactory factory = manager.getOWLDataFactory();
    OWLClass clazz = factory.getOWLClass(IRI.create(SCHEMA_ORG_PREFIX + className));

    // Create a reasoner to infer superclasses
    OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createReasoner(schemaOrgOntology);

    // Retrieve all superclasses of the given class
    Set<OWLClass> superClasses = reasoner.getSuperClasses(clazz, false).getFlattened();

    // Extract class names from IRIs
    Set<String> superClassNames = new HashSet<>();
    for (OWLClass superClass : superClasses) {
      superClassNames.add(superClass.getIRI().getFragment());
    }

    return superClassNames;
  }

  // DONE
  public static List<String> getDomainClasses(String propertyName) throws Exception {
    // Load the Schema.org ontology from the URL

    // Get the property object for the given property name
    OWLDataFactory factory = manager.getOWLDataFactory();
    OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(SCHEMA_ORG_PREFIX + propertyName));

    return schemaOrgOntology.objectPropertyDomainAxioms(property)
        .flatMap(axiom -> getClassNamesDomainAxiom(axiom, schemaOrgOntology))
        .distinct() // Remove duplicates
        .collect(Collectors.toList());
  }

  // Below is private for above depended
  private static Stream<String> getClassNamesDomainAxiom(OWLObjectPropertyDomainAxiom axiom, OWLOntology ontology) {
    OWLClassExpression domainExpression = axiom.getDomain();
    if (domainExpression instanceof OWLObjectUnionOf) {
      OWLObjectUnionOf union = (OWLObjectUnionOf) domainExpression;
      return union.getOperands().stream()
          .map(operand -> ((OWLClass) operand).getIRI().getFragment());
    } else {
      return Stream.empty();
    }
  }

  // DONE
  public static List<String> getRangeClasses(String propertyName) throws Exception {
    // Get the property object for the given property name
    OWLDataFactory factory = manager.getOWLDataFactory();
    OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(SCHEMA_ORG_PREFIX + propertyName));

    return schemaOrgOntology.objectPropertyRangeAxioms(property)
        .flatMap(axiom -> getClassNamesRangeAxiom(axiom, schemaOrgOntology))
        .distinct() // Remove duplicates
        .collect(Collectors.toList());
  }

  // Below is private for above depended
  private static Stream<String> getClassNamesRangeAxiom(OWLObjectPropertyRangeAxiom axiom, OWLOntology ontology) {
    OWLClassExpression domainExpression = axiom.getRange();
    if (domainExpression instanceof OWLObjectUnionOf) {
      OWLObjectUnionOf union = (OWLObjectUnionOf) domainExpression;
      return union.getOperands().stream()
          .map(operand -> ((OWLClass) operand).getIRI().getFragment());
    } else {
      return Stream.empty();
    }
  }

  // DONE - Load Schema.org ontology
  public static void loadSchemaOntology() throws Exception {
    InputStream inputStream = new URL(SCHEMA_ORG_URI).openStream();
    schemaOrgOntology = manager.loadOntologyFromOntologyDocument(inputStream);
  }

  // FSHIJE SE PO BOHET MUT
  public static void main(String[] args) {
    try {

      int i = 0;
      int lower = 400;
      int upper = 600;
      for (OWLObjectProperty objectProperty : schemaOrgOntology.getObjectPropertiesInSignature()) {
        i++;
        if (i < lower)
          continue;
        if (i > upper)
          break;
        if (objectProperty.getIRI().getFragment().equals("type")) {
          System.out.println("Object property: " + objectProperty.getIRI());
        } else if (!acceptsDataTypes(objectProperty.getIRI().getFragment())) {
          System.out.println("Object property: " + objectProperty.getIRI().getFragment());
        } else {
          System.out.println("Object property: " + objectProperty.getIRI().getFragment());
          System.out.print("YAY - ");
        }
      }

      List<String> rangeClasses = getRangeClasses("closes");
      System.out.println("Range classes: " + rangeClasses);

      System.out.println("Is it??:: " + isPropertyOfClass("identifier", "OrderItem"));

      List<String> domainClasses = getDomainClasses("address");
      System.out.println("Domain classes: " + domainClasses);

      Set<String> superClasses = getSuperClasses("Person");
      System.out.println("Super classes: " + superClasses);

      System.out.println("Object property: " + isObjectProperty("address"));
    } catch (Exception e) {
      System.out.println("Failed to map database to ontology: " + e.getMessage());
    }
  }
}

package com.database.federation.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import com.database.federation.utils.Collection;
import com.database.federation.utils.DatabaseForm;
import com.database.federation.utils.Field;
import com.database.federation.utils.FieldType;
public class OntologyService {

  public static final String CATEGORY_ONTOLOGY_PATH = "./ontologies/categories.owl";
  public static final String CATEGORY_ONTOLOGY_URL = "http://localhost:8081/ontologies/categories";

  private static final String SCHEMA_ORG_URI = "https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/26.0/schemaorg.owl";
  private static final String SCHEMA_ORG_PREFIX = "https://schema.org/";
  private OWLOntologyManager manager;
  private Ontology schemaOrgOntology;
  private Ontology categoriesOntology;
  private static String newOntologyPrefix;
  private static int indirections;

  public OntologyService() throws Exception {
    schemaOrgOntology = new Ontology(SCHEMA_ORG_PREFIX, SCHEMA_ORG_URI);
    categoriesOntology = new Ontology(CATEGORY_ONTOLOGY_URL, CATEGORY_ONTOLOGY_PATH, false);
    manager = OWLManager.createOWLOntologyManager();
  }

  private static String getNewOntologyPrefix(DatabaseForm dbForm) {
    return "http://localhost:8081/databases/"+dbForm.getId()+"/ontology";
  }

  private static void setNewOntologyPrefix(DatabaseForm form) {
    newOntologyPrefix = "http://localhost:8081/ontology/" + form.getId() + "/";
  }

  public void mapDatabaseToOntology(DatabaseForm form) throws Exception {
    // This method will map the database form to the ontology
    // Create OWLOntologyManager

    // OWLOntology schemaOrgOntology =
    // manager.loadOntologyFromOntologyDocument(IRI.create(SCHEMA_ORG_URI));

    // Create an empty ontology
    Ontology ontology = new Ontology(getNewOntologyPrefix(form), form.getId() + ".owl", true);
    indirections = 0;
    setNewOntologyPrefix(form);
    // Add import declaration to the ontology
    OWLImportsDeclaration importSchemaDeclaration = manager.getOWLDataFactory()
        .getOWLImportsDeclaration(IRI.create(SCHEMA_ORG_URI));

    OWLImportsDeclaration importCategoriesDeclaration = manager.getOWLDataFactory()
        .getOWLImportsDeclaration(IRI.create(CATEGORY_ONTOLOGY_URL));

    ontology.addImportDeclaration(importSchemaDeclaration);
    ontology.addImportDeclaration(importCategoriesDeclaration);

    for (Collection c : form.getCollections()) {
      ontology.addSubClass("#"+c.getCollectionName(), c.getMeaning(), schemaOrgOntology);

      for (Field f : c.getFields()) {
        mapFieldAgain(
          ontology, 
          f, 
          ontology.prefix + "#"+c.getCollectionName(), 
          c.getCollectionName()
        );
      }
    }


    ontology.createOntologyFile();
  }

  private void mapFieldAgain(Ontology ont, Field f, String parent, String collectionName) throws Exception {
    indirections++;
    if (f.getFieldType() == FieldType.FOREIGN) {

      String propertyName = "#" + collectionName + "-" + f.getName();
      String foreignFieldName = "#" + f.getForeignKey();
      String rangeClass = ont.prefix + "#" + f.getRangeClass();

      ont.addRelatedObjectProperties(
        propertyName,
        foreignFieldName,
        new ArrayList<>(Arrays.asList(parent)),
        new ArrayList<>(Arrays.asList(rangeClass))
      );
    }
    else if(f.getFieldType() == FieldType.OBJECT_PROPERTY){

      String tempClassName = "#"+f.getRangeClass()+"-"+ indirections;
      String tempPropertyName = "#"+f.getName()+"-"+indirections;
      String rangeClass = ont.prefix+"#"+f.getRangeClass()+"-"+ indirections;


      ont.addSubClass(
        tempClassName,
        f.getRangeClass(),
        schemaOrgOntology
      );
      ont.addSubObjectProperty(
        tempPropertyName,
        f.getName(),
        schemaOrgOntology,
        new ArrayList<>(Arrays.asList(parent)),
        new ArrayList<>(Arrays.asList(rangeClass))
      );

      for(Field field : f.getFields()){
        mapFieldAgain(ont, field, rangeClass, collectionName);
      }
    }
    else {

      String propertyName = "#" + f.getName();
      
      if(schemaOrgOntology.isObjectProperty(f.getMeaning())){

        String rangeClass = categoriesOntology.prefix + "#UserDataReference";
        
        ont.addSubObjectProperty(
          propertyName,
          f.getMeaning(),
          schemaOrgOntology,
          new ArrayList<>(Arrays.asList(parent)),
          new ArrayList<>(Arrays.asList(rangeClass))
        );
      } else {
        ont.addSubDataProperty(
          propertyName,
          f.getMeaning(),
          schemaOrgOntology,
          new ArrayList<>(Arrays.asList(parent))
        );
      }
    }
  }

  // NEED TO BE IMPLEMENTED
  public static boolean isFormSetCorrectly(DatabaseForm form) {
    // check if the database comply with the schema.org ontology standards
    return true;
  }

  // // DONE
  // public static void addEquivalentObjectProperty(OWLOntology ontology, String
  // myPropertyStr, String schemaPropertyStr) {
  // // Define the IRIs for banimi and address
  // IRI myPropertyIRI = IRI.create(newOntologyPrefix + myPropertyStr);
  // IRI schemaPropertyIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaPropertyStr);

  // // Create OWLObjectProperty objects for banimi and address
  // OWLObjectProperty myProperty =
  // manager.getOWLDataFactory().getOWLObjectProperty(myPropertyIRI);
  // OWLObjectProperty schemaProperty =
  // manager.getOWLDataFactory().getOWLObjectProperty(schemaPropertyIRI);

  // // Define the subclass axiom stating that mProperty is a subproperty of
  // // schemaProperty
  // OWLEquivalentObjectPropertiesAxiom equivalentPropertiesAxiom =
  // manager.getOWLDataFactory()
  // .getOWLEquivalentObjectPropertiesAxiom(myProperty, schemaProperty);

  // // Add the subproperty axiom to the ontology
  // manager.addAxiom(ontology, equivalentPropertiesAxiom);
  // }

  // // DONE
  // public static void addEquivalentDatatype(OWLOntology ontology, String
  // myPropertyStr, String schemaPropertyStr) {
  // // Define the IRIs for closeTime and closes
  // IRI myPropertyIRI = IRI.create(newOntologyPrefix + myPropertyStr);
  // IRI schemaPropertyIRI = IRI.create(SCHEMA_ORG_PREFIX + schemaPropertyStr);

  // // Create OWLDatatypeProperty objects for closeTime and closes
  // OWLDataProperty myProperty =
  // manager.getOWLDataFactory().getOWLDataProperty(myPropertyIRI);
  // OWLDataProperty schemaProperty =
  // manager.getOWLDataFactory().getOWLDataProperty(schemaPropertyIRI);

  // // Define the equivalent property axiom stating that myProperty is equivalent
  // to
  // // schemaProperty
  // OWLEquivalentDataPropertiesAxiom equivalentPropertiesAxiom =
  // manager.getOWLDataFactory()
  // .getOWLEquivalentDataPropertiesAxiom(myProperty, schemaProperty);

  // // Add the equivalent property axiom to the ontology
  // manager.addAxiom(ontology, equivalentPropertiesAxiom);
  // }

  // FSHIJE SE PO BOHET MUT
  public static void main(String[] args) {
    try {

      // int i = 0;
      // int lower = 400;
      // int upper = 600;
      // for (OWLObjectProperty objectProperty :
      // schemaOrgOntology.getObjectPropertiesInSignature()) {
      // i++;
      // if (i < lower)
      // continue;
      // if (i > upper)
      // break;
      // if (objectProperty.getIRI().getFragment().equals("type")) {
      // System.out.println("Object property: " + objectProperty.getIRI());
      // } else if (!acceptsDataTypes(objectProperty.getIRI().getFragment())) {
      // System.out.println("Object property: " +
      // objectProperty.getIRI().getFragment());
      // } else {
      // System.out.println("Object property: " +
      // objectProperty.getIRI().getFragment());
      // System.out.print("YAY - ");
      // }
      // }

      //////////////////////////// UPDATE THESE TESTS?/////////////////////////
      // List<String> rangeClasses = getRangeClasses("closes");
      // System.out.println("Range classes: " + rangeClasses);

      // System.out.println("Is it??:: " + isPropertyOfClass("identifier",
      // "OrderItem"));

      // List<String> domainClasses = getDomainClasses("address");
      // System.out.println("Domain classes: " + domainClasses);

      // Set<String> superClasses = getSuperClasses("Person");
      // System.out.println("Super classes: " + superClasses);

      // System.out.println("Object property: " + isObjectProperty("address"));
      // System.out.println(acceptsDataTypes("address"));
    } catch (Exception e) {
      System.out.println("Failed to map database to ontology: " + e.getMessage());
    }

    try {
      // OWLOntology ontology = manager.createOntology();
      // addSubObjectProperty(ontology, "adresa", "address");
      // // createOntologyFile(ontology, "ontology123.owl");
      // System.out.println("sadfasdfas");
      // List<String> domainClasses = getDomainClasses1(ontology,
      // "https://asdfasdf.com/nulladresa");
      // System.out.println("Domain sadfdsfclasses: " + domainClasses);
      // OntologyService service = new OntologyService();
      // service.printClassesInDomain("./ontologies/ontology123.owl", "#nulladresa");

      ///////////////////// UPDATE TESTS////////////////////////////

      // OWLOntology ontology = loadOntologyFromFile("./ontologies/ontology1234.owl");
      // OWLOntology ontology = manager.createOntology();
      // addSubObjectProperty(ontology, "edon", "sedon");
      // createOntologyFile(ontology, "./ontology1234.owl");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

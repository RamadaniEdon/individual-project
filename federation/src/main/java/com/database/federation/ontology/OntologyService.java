package com.database.federation.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import com.database.federation.database.DatabaseModel;
import com.database.federation.userData.CategoryForData;
import com.database.federation.userData.Entity;
import com.database.federation.userData.Instance;
import com.database.federation.userData.UserDataGlobalFormat;
import com.database.federation.utils.CategoryJson;
import com.database.federation.utils.Collection;
import com.database.federation.utils.DatabaseForm;
import com.database.federation.utils.Field;
import com.database.federation.utils.FieldType;

import ch.qos.logback.core.encoder.EchoEncoder;

public class OntologyService {

  public static final String CATEGORY_ONTOLOGY_PATH = "./categories.owl";
  public static final String CATEGORY_ONTOLOGY_URL = "http://localhost:8081/ontologies/categories";
  public static final String CATEGORY_ONTOLOGY_PREFIX = "http://localhost:8081/ontologies/categories#";

  private static final String SCHEMA_ORG_URI = "https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/26.0/schemaorg.owl";
  private static final String SCHEMA_ORG_PREFIX = "https://schema.org/";
  private OWLOntologyManager manager;
  private Ontology schemaOrgOntology;
  private Ontology categoriesOntology;
  private static int indirections;

  public OntologyService() throws Exception {
    schemaOrgOntology = new Ontology(SCHEMA_ORG_PREFIX, SCHEMA_ORG_URI);
    categoriesOntology = new Ontology(CATEGORY_ONTOLOGY_PREFIX, CATEGORY_ONTOLOGY_PATH, false);
    manager = OWLManager.createOWLOntologyManager();
  }

  public static String getNewOntologyPrefix(String dbId) {
    return "http://localhost:8081/databases/" + dbId + "/ontology#";
  }

  public void mapDatabaseToOntology(DatabaseForm form) throws Exception {
    Ontology ontology = new Ontology(getNewOntologyPrefix(form.getId()), form.getId() + ".owl", true);
    indirections = 0;

    // Add import declaration to the ontology
    // OWLImportsDeclaration importSchemaDeclaration = manager.getOWLDataFactory()
    // .getOWLImportsDeclaration(IRI.create(SCHEMA_ORG_URI));

    // OWLImportsDeclaration importCategoriesDeclaration =
    // manager.getOWLDataFactory()
    // .getOWLImportsDeclaration(IRI.create(CATEGORY_ONTOLOGY_URL));

    // ontology.addImportDeclaration(importSchemaDeclaration);
    // ontology.addImportDeclaration(importCategoriesDeclaration);

    for (Collection c : form.getCollections()) {
      ontology.addSubClass(c.getCollectionName(), c.getMeaning(), schemaOrgOntology);

      for (Field f : c.getFields()) {
        mapFieldAgain(
            ontology,
            f,
            ontology.prefix + c.getCollectionName(),
            c.getCollectionName());
      }
    }

    ontology.createOntologyFile();
  }

  private void mapFieldAgain(Ontology ont, Field f, String parent, String prefix) throws Exception {
    indirections++;
    if (f.getFieldType() == FieldType.FOREIGN) {

      String propertyName = prefix + "." + f.getName();
      String foreignFieldName = f.getRangeClass() + "." + f.getForeignKey();

      ont.addRelatedObjectProperties(
          propertyName,
          foreignFieldName,
          new ArrayList<>(Arrays.asList(parent)),
          null);
    } else if (f.getFieldType() == FieldType.OBJECT_PROPERTY) {

      if (f.getMeaning() != null && !f.getMeaning().isEmpty()) {
        String propertyName = prefix + "." + f.getName();
        String tempClassName = f.getRangeClass() + "-" + indirections;
        String rangeClass = ont.prefix + f.getRangeClass() + "-" + indirections;

        schemaOrgOntology.isObjectProperty(f.getMeaning());

        String userDataReference = categoriesOntology.prefix + "UserDataReference";

        ont.addSubClass(
            tempClassName,
            f.getRangeClass(),
            schemaOrgOntology);

        ont.addSubObjectProperty(
            propertyName,
            f.getMeaning(),
            schemaOrgOntology,
            new ArrayList<>(Arrays.asList(parent)),
            new ArrayList<>(Arrays.asList(rangeClass, userDataReference)));

        for (Field field : f.getFields()) {
          mapFieldAgain(ont, field, rangeClass, prefix+"."+f.getName());
        }

      } else {

        String tempClassName = f.getRangeClass() + "-" + indirections;
        String tempPropertyName = f.getName() + "-" + indirections;
        String rangeClass = ont.prefix + f.getRangeClass() + "-" + indirections;

        ont.addSubClass(
            tempClassName,
            f.getRangeClass(),
            schemaOrgOntology);

        ont.addSubObjectProperty(
            tempPropertyName,
            f.getName(),
            schemaOrgOntology,
            new ArrayList<>(Arrays.asList(parent)),
            new ArrayList<>(Arrays.asList(rangeClass)));

        for (Field field : f.getFields()) {
          mapFieldAgain(ont, field, rangeClass, prefix);
        }
      }

    } else {

      String propertyName = prefix + "." + f.getName();

      schemaOrgOntology.isObjectProperty(f.getMeaning());

      String rangeClass = categoriesOntology.prefix + "UserDataReference";

      ont.addSubObjectProperty(
          propertyName,
          f.getMeaning(),
          schemaOrgOntology,
          new ArrayList<>(Arrays.asList(parent)),
          new ArrayList<>(Arrays.asList(rangeClass)));
      //
      // ont.addSubDataProperty(
      // propertyName,
      // f.getMeaning(),
      // schemaOrgOntology,
      // new ArrayList<>(Arrays.asList(parent)));
    }
  }

  // NEED TO BE IMPLEMENTED
  public static boolean isFormSetCorrectly(DatabaseForm form) {
    // check if the database comply with the schema.org ontology standards
    return true;
  }

  public void addCategory(CategoryJson categoryInfo, String userAfm) throws Exception {
    OWLDataFactory dataFactory = manager.getOWLDataFactory();

    // Create an instance of the Person class
    OWLNamedIndividual existingPersonIndividual = dataFactory
        .getOWLNamedIndividual(IRI.create(CATEGORY_ONTOLOGY_PREFIX + userAfm));
    boolean personExists = categoriesOntology.getOntology()
        .containsIndividualInSignature(existingPersonIndividual.getIRI());

    OWLNamedIndividual personIndividual;
    if (personExists) {
      // Use the existing Person instance
      personIndividual = existingPersonIndividual;
    } else {
      // Create a new Person instance
      OWLClass personClass = dataFactory.getOWLClass(IRI.create(SCHEMA_ORG_PREFIX + "Person"));
      personIndividual = dataFactory
          .getOWLNamedIndividual(IRI.create(CATEGORY_ONTOLOGY_PREFIX + userAfm));
      OWLClassAssertionAxiom personClassAssertion = dataFactory.getOWLClassAssertionAxiom(personClass,
          personIndividual);
      manager.addAxiom(categoriesOntology.getOntology(), personClassAssertion);

      // Add an identifier property to the Person instance
      OWLDataProperty identifierProperty = dataFactory.getOWLDataProperty(IRI.create(SCHEMA_ORG_PREFIX + "identifier"));
      OWLDataPropertyAssertionAxiom identifierPropertyAssertion = dataFactory
          .getOWLDataPropertyAssertionAxiom(identifierProperty, personIndividual, userAfm);
      manager.addAxiom(categoriesOntology.getOntology(), identifierPropertyAssertion);
    }

    String accessControlCategoryIri = CATEGORY_ONTOLOGY_PREFIX + userAfm + "."
        + categoryInfo.getCategoryName();
    OWLNamedIndividual accessControlCategoryIndividual = dataFactory
        .getOWLNamedIndividual(IRI.create(accessControlCategoryIri));
    boolean accessControlCategoryExists = categoriesOntology.getOntology()
        .containsIndividualInSignature(accessControlCategoryIndividual.getIRI());

    System.out.println(accessControlCategoryExists + " pasha ty po ");
    if (!accessControlCategoryExists) {

      // Create an instance of the AccessControlCategory class
      OWLClass accessControlCategoryClass = dataFactory
          .getOWLClass(IRI.create(CATEGORY_ONTOLOGY_PREFIX + "AccessControlCategory"));
      OWLClassAssertionAxiom accessControlCategoryClassAssertion = dataFactory
          .getOWLClassAssertionAxiom(accessControlCategoryClass, accessControlCategoryIndividual);
      manager.addAxiom(categoriesOntology.getOntology(), accessControlCategoryClassAssertion);

      // Add properties to the AccessControlCategory instance
      addDataProperty(CATEGORY_ONTOLOGY_PREFIX + "categoryName", categoryInfo.getCategoryName(),
          accessControlCategoryIndividual);
      addDataProperty(CATEGORY_ONTOLOGY_PREFIX + "categoryPrice", "" + categoryInfo.getCategoryPrice(),
          accessControlCategoryIndividual);
      addDataProperty(CATEGORY_ONTOLOGY_PREFIX + "categoryAccessControl",
          categoryInfo.getCategoryAccessControl(), accessControlCategoryIndividual);

      // Create an object property instance and link the AccessControlCategory
      // instance with the Person instance
      OWLObjectProperty creatorOfCategoryProperty = dataFactory
          .getOWLObjectProperty(IRI.create(CATEGORY_ONTOLOGY_PREFIX + "creatorOfCategory"));
      OWLObjectPropertyAssertionAxiom creatorOfCategoryPropertyAssertion = dataFactory
          .getOWLObjectPropertyAssertionAxiom(
              creatorOfCategoryProperty, personIndividual, accessControlCategoryIndividual);
      manager.addAxiom(categoriesOntology.getOntology(), creatorOfCategoryPropertyAssertion);

      System.out.println("Category added successfully");
    } else {
      // Update properties of the AccessControlCategory instance
      updateDataProperty(CATEGORY_ONTOLOGY_PREFIX + "categoryName", categoryInfo.getCategoryName(),
          accessControlCategoryIndividual);
      updateDataProperty(CATEGORY_ONTOLOGY_PREFIX + "categoryPrice",
          "" + categoryInfo.getCategoryPrice(), accessControlCategoryIndividual);
      updateDataProperty(CATEGORY_ONTOLOGY_PREFIX + "categoryAccessControl",
          categoryInfo.getCategoryAccessControl(), accessControlCategoryIndividual);

      System.out.println("Category properties updated successfully");
    }
    categoriesOntology.createOntologyFile();
  }

  private void addDataProperty(String propertyIri, String value, OWLNamedIndividual individual) {
    OWLDataFactory dataFactory = manager.getOWLDataFactory();
    OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(IRI.create(propertyIri));
    OWLDataPropertyAssertionAxiom dataPropertyAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(dataProperty,
        individual, value);
    manager.addAxiom(categoriesOntology.getOntology(), dataPropertyAssertion);
  }

  private void updateDataProperty(String propertyIri, String newValue, OWLNamedIndividual individual) {
    OWLDataFactory dataFactory = manager.getOWLDataFactory();
    OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(IRI.create(propertyIri));

    // Remove all existing properties with the specified IRI
    Set<OWLDataPropertyAssertionAxiom> existingProperties = categoriesOntology.getOntology()
        .getDataPropertyAssertionAxioms(individual);
    for (OWLDataPropertyAssertionAxiom axiom : existingProperties) {
      if (axiom.getProperty().asOWLDataProperty().getIRI().toString().equals(dataProperty.getIRI().toString())) {
        System.out.println("Gang in this bitch" + axiom.getProperty().asOWLDataProperty().getIRI());
        categoriesOntology.getOntology().removeAxiom((axiom));
      }
    }

    // Create a new axiom with the updated value
    OWLDataPropertyAssertionAxiom dataPropertyAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(dataProperty,
        individual, newValue);
    manager.addAxiom(categoriesOntology.getOntology(), dataPropertyAssertion);
  }

  public List<CategoryJson> getCategories(String userAfm) {
    OWLDataFactory dataFactory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

    // Get the Person individual
    String personIri = CATEGORY_ONTOLOGY_PREFIX + userAfm;
    OWLNamedIndividual personIndividual = dataFactory.getOWLNamedIndividual(IRI.create(personIri));

    // Get the creatorOfCategory object property
    String propertyIri = CATEGORY_ONTOLOGY_PREFIX + "creatorOfCategory";
    OWLObjectProperty objectProperty = dataFactory.getOWLObjectProperty(IRI.create(propertyIri));

    // Get all categories linked to the person via the creatorOfCategory object
    // property
    Set<OWLNamedIndividual> categories = categoriesOntology.getOntology()
        .getObjectPropertyAssertionAxioms(personIndividual)
        .stream()
        .filter(axiom -> axiom.getProperty().equals(objectProperty))
        .map(OWLObjectPropertyAssertionAxiom::getObject)
        .map(individual -> (OWLNamedIndividual) individual)
        .collect(Collectors.toSet());

    for (OWLNamedIndividual category : categories) {
      System.out.println(category.getIRI());
    }

    // Convert the categories to CategoryJson objects
    List<CategoryJson> categoryJsons = categories.stream()
        .map(category -> {
          // Get the properties of the category
          Set<OWLDataPropertyAssertionAxiom> properties = categoriesOntology.getOntology()
              .getDataPropertyAssertionAxioms(category);

          // Extract the values of the properties
          String categoryName = properties.stream()
              .filter(axiom -> axiom.getProperty().asOWLDataProperty().getIRI().toString()
                  .equals(CATEGORY_ONTOLOGY_PREFIX + "categoryName"))
              .findFirst()
              .map(OWLDataPropertyAssertionAxiom::getObject)
              .map(OWLLiteral::getLiteral)
              .orElse(null);

          double categoryPrice = properties.stream()
              .filter(axiom -> axiom.getProperty().asOWLDataProperty().getIRI().toString()
                  .equals(CATEGORY_ONTOLOGY_PREFIX + "categoryPrice"))
              .findFirst()
              .map(OWLDataPropertyAssertionAxiom::getObject)
              .map(OWLLiteral::getLiteral)
              .map(Double::parseDouble)
              .orElse(0.0);

          String categoryAccessControl = properties.stream()
              .filter(axiom -> axiom.getProperty().asOWLDataProperty().getIRI().toString()
                  .equals(CATEGORY_ONTOLOGY_PREFIX + "categoryAccessControl"))
              .findFirst()
              .map(OWLDataPropertyAssertionAxiom::getObject)
              .map(OWLLiteral::getLiteral)
              .orElse(null);

          // Create a new CategoryJson object with the extracted values
          return new CategoryJson(categoryName, categoryPrice, categoryAccessControl);
        })
        .collect(Collectors.toList());

    return categoryJsons;
  }

  public UserDataGlobalFormat getGlobalFormat(String dbId) throws Exception {
    Ontology dbOnt = new Ontology(getNewOntologyPrefix(dbId), dbId + ".owl", false);

    UserDataGlobalFormat result = new UserDataGlobalFormat();

    return null;

  }

  public void changeAccessControlOfData(CategoryForData dataCategory, String dbId, String userAfm) throws Exception {

    Ontology dbOnt = new Ontology(getNewOntologyPrefix(dbId), dbId + ".owl", false);

    List<String> propertyDomains = dbOnt
        .getDomainClasses(dataCategory.getCollection() + "." + dataCategory.getProperty());
    String propertyDomain = propertyDomains.get(0);
    System.out.println(propertyDomain + " :: Edon Ramadani");

    List<IRI> dataReferencesInRange = dbOnt
        .getIndividualsInRangeOfObjectProperty(dataCategory.getCollection() + "." + dataCategory.getProperty());

    for (IRI iri : dataReferencesInRange) {
      System.out.println(iri + " :: Edon Ramadani nga gjilani");
    }

    List<IRI> dataReferencesWithTheGivenId = dbOnt.getInstancesWithPropertyValueFromListWithoutPrefix(
        dataReferencesInRange, categoriesOntology.prefix + "userDataIdentifier", dataCategory.getIdentifier());
    System.out.println("ASDFAASDFSADF");
    for (IRI iri : dataReferencesWithTheGivenId) {
      System.out.println(iri + " :: Edon Ramadani");
    }

    if (dataReferencesWithTheGivenId.size() == 0) {
      System.out.println("Data reference with the given identifier does not exist");
      // return;
    }

    IRI dataReference;

    if (dataReferencesWithTheGivenId.size() == 0) {
      dataReference = dbOnt.createInstanceOfClassWithoutPrefix(
          this.categoriesOntology.prefix + "UserDataReference");

      dbOnt.addPropertyValueToInstanceWithoutPrefix(
          dataReference,
          categoriesOntology.prefix + "userDataEntityOrigin",
          dataCategory.getCollection());

      dbOnt.addPropertyValueToInstanceWithoutPrefix(
          dataReference,
          categoriesOntology.prefix + "userDataIdentifier",
          dataCategory.getIdentifier());

      IRI newDomainClass = dbOnt.createInstanceOfClass(propertyDomain);

      IRI domainDataProperty = dbOnt.createObjectPropertyAndLink(
          dataCategory.getCollection() + "." + dataCategory.getProperty(), newDomainClass, dataReference);
    } else {
      dataReference = dataReferencesWithTheGivenId.get(0);
      dbOnt.removeAxiomsWithIndividualInDomainWithoutPrefix(dataReference, categoriesOntology.prefix + "accessControl");
    }

    IRI accessControlCategory = categoriesOntology.getInstanceOfClassWithPropertyValue(
        "AccessControlCategory",
        "categoryName",
        dataCategory.getCategoryName());

    IRI dataAccessProperty = dbOnt.createObjectPropertyAndLinkWithoutPrefix(
        categoriesOntology.prefix + "accessControl",
        dataReference,
        accessControlCategory);

    dbOnt.createOntologyFile();
  }

  public void respectAccessControl(UserDataGlobalFormat userData, String dbId) throws Exception {
    Ontology dbOnt = new Ontology(getNewOntologyPrefix(dbId), dbId + ".owl", false);
    List<IRI> dataReferences = dbOnt
        .getIndividualsOfClassWithoutPrefix(categoriesOntology.prefix + "UserDataReference");

    for (IRI dataReference : dataReferences) {
      String idValue = dbOnt.getPropertyValueForIndividualWithoutPrefix(dataReference,
          categoriesOntology.prefix + "userDataIdentifier");
      String dbReference = dbOnt.getObjectPropertyWithIndividualInRange(dataReference);
      String dbCollection = dbReference.split("\\.")[0];
      IRI accessControlCategory = dbOnt.getRangeIndividualOfObjectPropertyWithoutPrefix(dataReference,
          categoriesOntology.prefix + "accessControl");
      String categoryName = categoriesOntology.getPropertyValueForIndividual(accessControlCategory, "categoryName");

      Entity entity = null;
      for (Entity e : userData.getCollections()) {
        if (e.getNameInDb().equals(dbCollection)) {
          entity = e;
          break;
        }
      }
      if (entity == null) {
        return;
      }

      for (List<Instance> documents : entity.getDocuments()) {
        Instance idInstance = getInstanceRepresentingProperty(documents, "identifier");
        if (idInstance.getValue() != null && idInstance.getValue().equals(idValue)) {
          Instance accessControlInstance = getInstanceRepresentingPropertyInDb(documents, dbReference);
          System.out.println("Se di pse: " + dbReference);
          System.out.println("Se di pse: " + categoryName);
          System.out.println("Se di pse: " + categoryName);
          System.out.println("Se di pse: " + categoryName);
          // if(accessControlInstance == null){
          // continue;
          // }
          accessControlInstance.setCategoryName(categoryName);
        }
      }

    }
  }

  private Instance getInstanceRepresentingProperty(List<Instance> documents, String property) {
    for (Instance instance : documents) {
      if (instance.getField() != null && instance.getField().equals(property)) {
        return instance;
      }
      if (instance.getFields() != null) {
        Instance result = getInstanceRepresentingProperty(instance.getFields(), property);

        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  private Instance getInstanceRepresentingPropertyInDb(List<Instance> documents, String property) {
    for (Instance instance : documents) {
      if (instance.getDbField() != null && instance.getDbField().equals(property)) {
        return instance;
      }
      if (instance.getFields() != null) {
        Instance result = getInstanceRepresentingPropertyInDb(instance.getFields(), property);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }
}

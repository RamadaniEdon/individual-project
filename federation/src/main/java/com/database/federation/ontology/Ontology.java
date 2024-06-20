package com.database.federation.ontology;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.database.federation.userData.Entity;
import com.database.federation.userData.Instance;
import com.database.federation.userData.UserDataGlobalFormat;

public class Ontology {
    public String prefix;
    public String filename;
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    OWLDataFactory factory;

    public Ontology(String prefix, String filename, boolean newOntology) throws Exception {
        this.prefix = prefix;
        this.filename = filename;
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();

        if (newOntology) {
            ontology = manager.createOntology();
        } else {
            loadOntologyFromFile(filename);
        }
    }

    public Ontology(String prefix, String url) throws Exception {
        this.prefix = prefix;
        manager = OWLManager.createOWLOntologyManager();
        loadOntologyFromURL(url);
    }

    public Ontology(String prefix, String filename, boolean newOntology, boolean development) throws Exception {
        this.prefix = prefix;
        this.filename = filename;
        manager = OWLManager.createOWLOntologyManager();

        File file = new File(filename);
        ontology = manager.loadOntologyFromOntologyDocument(file);
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void addImportDeclaration(OWLImportsDeclaration imports) {
        manager.applyChange(new AddImport(ontology, imports));
    }

    public void loadOntologyFromURL(String url) throws Exception {
        InputStream inputStream = new URL(url).openStream();
        ontology = manager.loadOntologyFromOntologyDocument(inputStream);
    }

    public void loadOntologyFromFile(String filePath) throws OWLOntologyCreationException {
        File file = new File("./ontologies/" + filePath);
        ontology = manager.loadOntologyFromOntologyDocument(file);
    }

    public void createOntologyFile() throws Exception {
        // Get current directory
        Path currentDirectory = Paths.get("./ontologies").toAbsolutePath();

        // Define file path
        Path filePath = currentDirectory.resolve(filename);

        // Create file
        File file = filePath.toFile();

        RDFXMLDocumentFormat format = new RDFXMLDocumentFormat();
        manager.setOntologyFormat(ontology, format);

        // Save the ontology to file
        manager.saveOntology(ontology, new FileOutputStream(file));
    }

    public void addEquivalentClass(String thisClassStr, String otherClassStr, Ontology otherOnt) {
        IRI thisClassIRI = IRI.create(this.prefix + thisClassStr);
        OWLClass thisClass = manager.getOWLDataFactory().getOWLClass(thisClassIRI);

        // Define the class equivalent to schema.org:ActivateAction
        IRI otherClassIRI = IRI.create(otherOnt.prefix + otherClassStr);
        OWLClass otherClass = manager.getOWLDataFactory().getOWLClass(otherClassIRI);

        // Define the equivalence axiom
        OWLEquivalentClassesAxiom equivalentClassesAxiom = manager.getOWLDataFactory()
                .getOWLEquivalentClassesAxiom(thisClass, otherClass);

        // Add the equivalence axiom to the ontology
        manager.addAxiom(ontology, equivalentClassesAxiom);
    }

    public void addSubClass(String thisClassStr, String otherClassStr, Ontology otherOnt) {
        // Define the IRIs for myClass and ActivateAction
        IRI thisClassIRI = IRI.create(this.prefix + thisClassStr);
        IRI otherClassIRI = IRI.create(otherOnt.prefix + otherClassStr);

        // Create OWLClass objects for myClass and ActivateAction
        OWLClass thisClass = manager.getOWLDataFactory().getOWLClass(thisClassIRI);
        OWLClass otherClass = manager.getOWLDataFactory().getOWLClass(otherClassIRI);

        // Define the subclass axiom stating that myClass is a subclass of
        // schemaClass
        OWLSubClassOfAxiom subclassAxiom = manager.getOWLDataFactory().getOWLSubClassOfAxiom(thisClass, otherClass);

        // Add the subclass axiom to the ontology
        manager.addAxiom(ontology, subclassAxiom);
    }

    public void addRelatedObjectProperties(String thisPropertyStr, String otherPropertyStr,
            List<String> domainClassStrs, List<String> rangeClassStrs) {
        // Define the IRIs for banimi and address
        IRI thisPropertyIRI = IRI.create(this.prefix + thisPropertyStr);
        IRI otherPropertyIRI = IRI.create(this.prefix + otherPropertyStr);

        // Create OWLObjectProperty objects for banimi and address
        OWLObjectProperty thisProperty = manager.getOWLDataFactory().getOWLObjectProperty(thisPropertyIRI);
        OWLObjectProperty otherProperty = manager.getOWLDataFactory().getOWLObjectProperty(otherPropertyIRI);

        addPropertyChain(thisProperty, otherProperty);

        if (domainClassStrs != null) {
            Set<OWLClassExpression> domainClasses = domainClassStrs.stream()
                    .map(classStr -> manager.getOWLDataFactory().getOWLClass(IRI.create(classStr)))
                    .collect(Collectors.toSet());

            // Create an OWLObjectUnionOf object to represent the union of the domain
            // classes
            OWLObjectUnionOf domainUnion = manager.getOWLDataFactory().getOWLObjectUnionOf(domainClasses);

            // Define the domain axiom stating that the union of the domain classes is the
            // domain of myProperty
            OWLObjectPropertyDomainAxiom domainAxiom = manager.getOWLDataFactory().getOWLObjectPropertyDomainAxiom(
                    thisProperty,
                    domainUnion);

            // Add the domain axiom to the ontology
            manager.addAxiom(ontology, domainAxiom);
        }

        if (rangeClassStrs == null)
            return;
        ///////////////////////////
        Set<OWLClassExpression> rangeClasses = rangeClassStrs.stream()
                .map(classStr -> manager.getOWLDataFactory().getOWLClass(IRI.create(classStr)))
                .collect(Collectors.toSet());

        // Create an OWLObjectUnionOf object to represent the union of the domain
        // classes
        OWLObjectUnionOf rangeUnion = manager.getOWLDataFactory().getOWLObjectUnionOf(rangeClasses);

        // Define the domain axiom stating that the union of the domain classes is the
        // domain of myProperty
        OWLObjectPropertyRangeAxiom rangeAxiom = manager.getOWLDataFactory().getOWLObjectPropertyRangeAxiom(
                thisProperty,
                rangeUnion);

        // Add the domain axiom to the ontology
        manager.addAxiom(ontology, rangeAxiom);
        //////////////////////////////

    }

    public void addSubObjectProperty(String thisPropertyStr, String otherPropertyStr, Ontology otherOnt,
            List<String> domainClassStrs, List<String> rangeClassStrs) {
        // Define the IRIs for banimi and address
        IRI thisPropertyIRI = IRI.create(this.prefix + thisPropertyStr);
        IRI otherPropertyIRI = IRI.create(otherOnt.prefix + otherPropertyStr);

        // Create OWLObjectProperty objects for banimi and address
        OWLObjectProperty thisProperty = manager.getOWLDataFactory().getOWLObjectProperty(thisPropertyIRI);
        OWLObjectProperty otherProperty = manager.getOWLDataFactory().getOWLObjectProperty(otherPropertyIRI);

        // Define the subclass axiom stating that myProperty is a subproperty of
        // schemaProperty
        OWLSubObjectPropertyOfAxiom subPropertyAxiom = manager.getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(
                thisProperty,
                otherProperty);

        // Add the subproperty axiom to the ontology
        manager.addAxiom(ontology, subPropertyAxiom);

        if (domainClassStrs != null) {
            Set<OWLClassExpression> domainClasses = domainClassStrs.stream()
                    .map(classStr -> manager.getOWLDataFactory().getOWLClass(IRI.create(classStr)))
                    .collect(Collectors.toSet());

            // Create an OWLObjectUnionOf object to represent the union of the domain
            // classes
            OWLObjectUnionOf domainUnion = manager.getOWLDataFactory().getOWLObjectUnionOf(domainClasses);

            // Define the domain axiom stating that the union of the domain classes is the
            // domain of myProperty
            OWLObjectPropertyDomainAxiom domainAxiom = manager.getOWLDataFactory().getOWLObjectPropertyDomainAxiom(
                    thisProperty,
                    domainUnion);

            // Add the domain axiom to the ontology
            manager.addAxiom(ontology, domainAxiom);
        }

        if (rangeClassStrs == null)
            return;
        ///////////////////////////
        Set<OWLClassExpression> rangeClasses = rangeClassStrs.stream()
                .map(classStr -> manager.getOWLDataFactory().getOWLClass(IRI.create(classStr)))
                .collect(Collectors.toSet());

        // Create an OWLObjectUnionOf object to represent the union of the domain
        // classes
        OWLObjectUnionOf rangeUnion = manager.getOWLDataFactory().getOWLObjectUnionOf(rangeClasses);

        // Define the domain axiom stating that the union of the domain classes is the
        // domain of myProperty
        OWLObjectPropertyRangeAxiom rangeAxiom = manager.getOWLDataFactory().getOWLObjectPropertyRangeAxiom(
                thisProperty,
                rangeUnion);

        // Add the domain axiom to the ontology
        manager.addAxiom(ontology, rangeAxiom);
        //////////////////////////////

    }

    public void addSubDataProperty(String thisPropertyStr, String otherPropertyStr, Ontology otherOnt,
            List<String> domainClassStrs) {
        // Define the IRIs for the properties
        IRI thisPropertyIRI = IRI.create(this.prefix + thisPropertyStr);
        IRI otherPropertyIRI = IRI.create(otherOnt.prefix + otherPropertyStr);

        // Create OWLDataProperty objects for the properties
        OWLDataProperty thisProperty = manager.getOWLDataFactory().getOWLDataProperty(thisPropertyIRI);
        OWLDataProperty otherProperty = manager.getOWLDataFactory().getOWLDataProperty(otherPropertyIRI);

        // Define the subclass axiom stating that thisProperty is a subproperty of
        // otherProperty
        OWLSubDataPropertyOfAxiom subPropertyAxiom = manager.getOWLDataFactory()
                .getOWLSubDataPropertyOfAxiom(thisProperty, otherProperty);

        // Add the subproperty axiom to the ontology
        manager.addAxiom(ontology, subPropertyAxiom);

        if (domainClassStrs == null)
            return;

        // Create domain classes
        Set<OWLClassExpression> domainClasses = domainClassStrs.stream()
                .map(classStr -> manager.getOWLDataFactory().getOWLClass(IRI.create(this.prefix + classStr)))
                .collect(Collectors.toSet());

        // Create an OWLObjectUnionOf object to represent the union of the domain
        // classes
        OWLObjectUnionOf domainUnion = manager.getOWLDataFactory().getOWLObjectUnionOf(domainClasses);

        // Define the domain axiom stating that the union of the domain classes is the
        // domain of thisProperty
        OWLDataPropertyDomainAxiom domainAxiom = manager.getOWLDataFactory().getOWLDataPropertyDomainAxiom(thisProperty,
                domainUnion);

        // Add the domain axiom to the ontology
        manager.addAxiom(ontology, domainAxiom);
    }

    public boolean isPropertyOfClass(String propertyStr, String classStr) throws Exception {
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

    public Set<String> getSuperClasses(String className) throws Exception {
        // Get the class object for the given class name
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass clazz = factory.getOWLClass(IRI.create(this.prefix + className));

        // Create a reasoner to infer superclasses
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(this.ontology);

        // Retrieve all superclasses of the given class
        Set<OWLClass> superClasses = reasoner.getSuperClasses(clazz, false).getFlattened();

        // Extract class names from IRIs
        Set<String> superClassNames = new HashSet<>();
        for (OWLClass superClass : superClasses) {
            superClassNames.add(superClass.getIRI().getFragment());
        }

        return superClassNames;
    }

    public String getSuperClass(String className) throws Exception {
        // Get the class object for the given class name
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass clazz = factory.getOWLClass(IRI.create(this.prefix + className));

        // Retrieve all superclasses of the given class
        Set<OWLSubClassOfAxiom> superClassAxioms = this.ontology.getSubClassAxiomsForSubClass(clazz);

        // Extract class names from IRIs
        for (OWLSubClassOfAxiom superClassAxiom : superClassAxioms) {
            OWLClassExpression superClass = superClassAxiom.getSuperClass();
            if (!superClass.isAnonymous()) {
                return superClass.asOWLClass().getIRI().getFragment();
            }
        }
        return "";
    }

    public String getSuperProperty(String propertyName) throws Exception {
        // Get the property object for the given property name
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(this.prefix + propertyName));

        // Retrieve all superproperties of the given property
        Set<OWLSubObjectPropertyOfAxiom> superPropertyAxioms = this.ontology
                .getObjectSubPropertyAxiomsForSubProperty(property);

        // Extract property names from IRIs
        for (OWLSubObjectPropertyOfAxiom superPropertyAxiom : superPropertyAxioms) {
            OWLObjectPropertyExpression superProperty = superPropertyAxiom.getSuperProperty();
            if (!superProperty.isAnonymous()) {
                return superProperty.asOWLObjectProperty().getIRI().getFragment();
            }
        }
        return "";
    }

    public List<String> getDomainClasses(String propertyName) throws Exception {
        // Load the Schema.org ontology from the URL

        // Get the property object for the given property name
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(this.prefix + propertyName));

        return this.ontology.objectPropertyDomainAxioms(property)
                .flatMap(axiom -> getClassNamesDomainAxiom(axiom, this.ontology))
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
    }

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

    public List<String> getRangeClasses(String propertyName) throws Exception {
        // Get the property object for the given property name
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(this.prefix + propertyName));

        return this.ontology.objectPropertyRangeAxioms(property)
                .flatMap(axiom -> getClassNamesRangeAxiom(axiom, this.ontology))
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

    public boolean isObjectProperty(String propertyName) throws Exception {
        // Check if the property is an object property

        OWLDataProperty dataProperty = ontology.getOWLOntologyManager().getOWLDataFactory()
                .getOWLDataProperty(IRI.create(prefix + propertyName));
        OWLObjectProperty objectProperty = ontology.getOWLOntologyManager().getOWLDataFactory()
                .getOWLObjectProperty(IRI.create(prefix + propertyName));

        if (ontology.containsDataPropertyInSignature(dataProperty.getIRI())) {
            return false;
        } else if (ontology.containsObjectPropertyInSignature(objectProperty.getIRI())) {
            return true;
        } else {
            throw new IllegalArgumentException(
                    propertyName + " is neither a datatype property nor an object property.");
        }
    }

    // Not sure if it works for other ontologies except SCHEMA.ORG
    // http://www.w3.org/1999/02/22-rdf-syntax-ns#type /////
    // This is also and object property but not in the schema.org ontology but used
    // there
    public boolean acceptsDataTypes(String propertyStr) throws Exception {
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

    public void addPropertyChain(OWLObjectProperty orderUserProperty, OWLObjectProperty userIdProperty) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        // IRI userIdIRI = IRI.create(this.prefix + chainProperty);
        // IRI orderUserIRI = IRI.create(this.prefix + baseProperty);
        // // Create data property
        // OWLObjectProperty userIdProperty = factory.getOWLObjectProperty(userIdIRI);
        // OWLObjectProperty orderUserProperty =
        // factory.getOWLObjectProperty(orderUserIRI);
        OWLSubPropertyChainOfAxiom propertyChainAxiom = factory.getOWLSubPropertyChainOfAxiom(
                java.util.Arrays.asList(userIdProperty),
                orderUserProperty);

        manager.addAxiom(ontology, propertyChainAxiom);
    }

    public List<String> getAllClassNames() {
        return ontology.getClassesInSignature()
                .stream()
                .map(OWLClass::getIRI)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public List<String> getAllSubClassNames() {
        return ontology.axioms(AxiomType.SUBCLASS_OF)
                .map(axiom -> axiom.getSubClass())
                .filter(concept -> concept instanceof OWLClass)
                .map(concept -> (OWLClass) concept)
                .map(OWLClass::getIRI)
                .map(IRI::getFragment)
                .collect(Collectors.toList());
    }

    public List<String> getClassesNotInRangeOfAnyObjectProperty() {
        Set<OWLClass> rangeClasses = new HashSet<>();

        ontology.axioms(AxiomType.OBJECT_PROPERTY_RANGE)
                .forEach(axiom -> {
                    OWLClassExpression range = axiom.getRange();
                    if (range instanceof OWLClass) {
                        rangeClasses.add((OWLClass) range);
                    } else if (range instanceof OWLObjectUnionOf) {
                        OWLObjectUnionOf union = (OWLObjectUnionOf) range;
                        union.operands()
                                .filter(operand -> operand instanceof OWLClass)
                                .map(operand -> (OWLClass) operand)
                                .forEach(rangeClasses::add);
                    }
                });

        Set<OWLClass> chainAxiomClasses = ontology.axioms(AxiomType.SUB_PROPERTY_CHAIN_OF)
                .map(axiom -> (OWLSubPropertyChainOfAxiom) axiom)
                .map(OWLSubPropertyChainOfAxiom::getSuperProperty)
                .flatMap(superProperty -> ontology.objectPropertyRangeAxioms(superProperty))
                .map(OWLObjectPropertyRangeAxiom::getRange)
                .filter(concept -> concept instanceof OWLClass)
                .map(concept -> (OWLClass) concept)
                .collect(Collectors.toSet());

        // print all classes iri in the chainAxiomClasses
        chainAxiomClasses.forEach(cls -> System.out.println(cls.getIRI()));

        rangeClasses.removeAll(chainAxiomClasses);

        return ontology.getClassesInSignature()
                .stream()
                .filter(cls -> !rangeClasses.contains(cls))
                .map(OWLClass::getIRI)
                .map(IRI::getFragment)
                .collect(Collectors.toList());
    }

    public List<String> getTopLevelClasses() {
        List<String> subClassNames = getAllSubClassNames();
        List<String> classesNotInRange = getClassesNotInRangeOfAnyObjectProperty();

        return subClassNames.stream()
                .distinct()
                .filter(classesNotInRange::contains)
                .collect(Collectors.toList());
    }

    public List<String> getSomething() {
        List<String> result = new ArrayList<>();
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom instanceof OWLSubPropertyChainOfAxiom) {
                OWLSubPropertyChainOfAxiom chainAxiom = (OWLSubPropertyChainOfAxiom) axiom;
                for (OWLObjectPropertyExpression property : chainAxiom.getPropertyChain()) {
                    result.add(property.asOWLObjectProperty().getIRI().toString());
                }
            }
        }
        return result;
    }

    public String getPropertyFromPropertyChainAxiom(String propertyName) {
        // Get the property object for the given property name
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(this.prefix + propertyName));

        // Get all axioms of the property
        Set<OWLSubPropertyChainOfAxiom> propertyChainAxioms = ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF);

        // Find the axiom that contains the given property
        for (OWLSubPropertyChainOfAxiom axiom : propertyChainAxioms) {
            if (axiom.getSuperProperty().equals(property)) {
                // Get the first property in the chain
                return axiom.getPropertyChain().get(0).asOWLObjectProperty().getIRI().getFragment();

            }
        }
        return null;
    }

    public UserDataGlobalFormat getGlobalFormat() throws Exception {
        UserDataGlobalFormat globalFormat = new UserDataGlobalFormat();

        List<String> topLevelClasses = getTopLevelClasses();
        List<Entity> entities = new ArrayList<>();
        globalFormat.setCollections(entities);

        for (String className : topLevelClasses) {
            Entity entity = new Entity();
            String superClass = getSuperClass(className);
            entity.setName(superClass);
            entity.setNameInDb(className);
            entity.setUserData(true);
            entities.add(entity);

            List<List<Instance>> documents = new ArrayList<List<Instance>>();
            List<Instance> document = new ArrayList<Instance>();
            documents.add(document);
            entity.setDocuments(documents);
            mapEntitiesRecursively(className, document);
            List<String> properties = getPropertiesWithDomain(className);
            for (String entity2 : properties) {
                System.out.println(entity2);

            }

        }

        return globalFormat;
    }

    private void mapEntitiesRecursively(String className, List<Instance> documents) throws Exception {
        List<String> properties = getPropertiesWithDomain(className);
        for (String property : properties) {
            Instance instance = new Instance();
            List<String> rangeClasses = getRangeClasses(property);
            String parentProperty = getSuperProperty(property);
            System.out.println("parentProperty: " + parentProperty);
            System.out.println("property: " + property);
            if(rangeClasses.size() > 0) System.out.println("asdfasdf" + "sdi cka leshi o vlla " + rangeClasses.get(0));
            if(rangeClasses.size() > 1) System.out.println("asdfasdf" + "sdi cka leshi o vlla " + rangeClasses.get(1));
            if (rangeClasses.size() < 1 || (rangeClasses.size() == 1 && rangeClasses.get(0).equals("UserDataReference"))) {
                System.out.println(10);
                instance.setField(parentProperty);
                instance.setDbField(property);

                String propertyChain = getPropertyFromPropertyChainAxiom(property);
                if (propertyChain != null) {
                    String propertyChainDomain = getDomainClasses(propertyChain).get(0);
                    instance.setReference(true);
                    instance.setReferenceClass(propertyChainDomain);
                    instance.setReferenceProperty(propertyChain);

                }
            } else if (rangeClasses.size() > 1 && (rangeClasses.get(0).equals("UserDataReference")
                    || rangeClasses.get(1).equals("UserDataReference"))) {
                        System.out.println(20);
                String rangeClass;
                if (!rangeClasses.get(0).equals("UserDataReference")) {
                    rangeClass = rangeClasses.get(0);
                } else if (!rangeClasses.get(1).equals("UserDataReference")) {
                    rangeClass = rangeClasses.get(1);
                } else {
                    System.out.println("asdfasdf" + "sdi cka leshi o vlla " + rangeClasses.get(0));
                    System.out.println("asdfasdf" + "sdi cka leshi o vlla " + rangeClasses.get(1));
                    rangeClass = null; // or some default value
                }
                System.out.println("asdfasdf" + "sdf sdf s d fsd fsd fsd af sd fsd f " + rangeClass);
                instance.setField(parentProperty);
                instance.setDbField(property);
                instance.setRange(getSuperClass(className));
                instance.setObjectProperty(true);
                List<Instance> fields = new ArrayList<Instance>();
                instance.setFields(fields);
                mapEntitiesRecursively(rangeClass, fields);

            } else {
                System.out.println(30);
                String rangeClass = rangeClasses.get(0);
                System.out.println("asdfasdf" + "sdf " + rangeClass);
                instance.setField(parentProperty);
                instance.setRange(getSuperClass(className));
                instance.setObjectProperty(true);
                List<Instance> fields = new ArrayList<Instance>();
                instance.setFields(fields);
                mapEntitiesRecursively(rangeClass, fields);
            }
            documents.add(instance);
        }

    }

    public List<String> getPropertiesWithDomain(String className) {
        // Get the class
        OWLClass owlClass = manager.getOWLDataFactory()
                .getOWLClass(IRI.create(prefix + className));

        // Get all properties with the class in their domain
        return ontology.getObjectPropertiesInSignature()
                .stream()
                .filter(property -> ontology.getObjectPropertyDomainAxioms(property)
                        .stream()
                        .anyMatch(axiom -> {
                            if (axiom.getDomain().equals(owlClass)) {
                                return true;
                            } else if (axiom.getDomain() instanceof OWLObjectUnionOf) {
                                OWLObjectUnionOf union = (OWLObjectUnionOf) axiom.getDomain();
                                return union.getOperands().contains(owlClass);
                            }
                            return false;
                        }))
                .map(property -> property.getIRI().getFragment())
                .collect(Collectors.toList());
    }

    public IRI createInstanceOfClass(String className) {

        OWLNamedIndividual individual = factory
                .getOWLNamedIndividual(IRI.create(prefix + className + "/" + java.util.UUID.randomUUID().toString()));
        OWLClass clazz = factory.getOWLClass(IRI.create(prefix + className));
        OWLClassAssertionAxiom personClassAssertion = factory.getOWLClassAssertionAxiom(clazz,
                individual);
        manager.addAxiom(this.ontology, personClassAssertion);

        return individual.getIRI();
    }

    public IRI createInstanceOfClassWithoutPrefix(String className) {

        OWLNamedIndividual individual = factory
                .getOWLNamedIndividual(IRI.create(className + "/" + java.util.UUID.randomUUID().toString()));
        OWLClass clazz = factory.getOWLClass(IRI.create(className));
        OWLClassAssertionAxiom personClassAssertion = factory.getOWLClassAssertionAxiom(clazz,
                individual);
        manager.addAxiom(this.ontology, personClassAssertion);

        return individual.getIRI();
    }

    public IRI createObjectPropertyAndLink(String propertyName, IRI domain, IRI range) {

        OWLNamedIndividual domainIndividual = factory
                .getOWLNamedIndividual(domain);

        OWLNamedIndividual rangeIndividual = factory
                .getOWLNamedIndividual(range);

        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(prefix + propertyName));
        OWLObjectPropertyAssertionAxiom objectPropertyAxiom = factory
                .getOWLObjectPropertyAssertionAxiom(
                        property, domainIndividual, rangeIndividual);
        manager.addAxiom(this.ontology, objectPropertyAxiom);

        return property.getIRI();
    }

    public IRI createObjectPropertyAndLinkWithoutPrefix(String propertyName, IRI domain, IRI range) {

        OWLNamedIndividual domainIndividual = factory
                .getOWLNamedIndividual(domain);

        OWLNamedIndividual rangeIndividual = factory
                .getOWLNamedIndividual(range);

        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(propertyName));
        OWLObjectPropertyAssertionAxiom objectPropertyAxiom = factory
                .getOWLObjectPropertyAssertionAxiom(
                        property, domainIndividual, rangeIndividual);
        manager.addAxiom(this.ontology, objectPropertyAxiom);

        return property.getIRI();
    }

    public IRI getInstanceOfClassWithPropertyValue(String className, String propertyName, String propertyValue) {

        OWLClass clazz = factory.getOWLClass(IRI.create(prefix + className));
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(prefix + propertyName));

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

        // Search for instances of the class
        NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(clazz, false);
        for (OWLNamedIndividual instance : instances.getFlattened()) {
            // Check the data property values of the instance
            for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(instance)) {
                if (axiom.getProperty().equals(property) && axiom.getObject().getLiteral().equals(propertyValue)) {
                    // Found an instance with the specified property value
                    return instance.getIRI();
                }
            }
        }

        return null;
    }

    public void addPropertyValueToInstanceWithoutPrefix(IRI instanceIri, String propertyName, String propertyValue) {

        OWLNamedIndividual instance = factory.getOWLNamedIndividual(instanceIri);
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(propertyName));
        OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(property, instance,
                propertyValue);
        manager.addAxiom(ontology, axiom);
    }

    public List<IRI> getInstancesWithPropertyValue(String propertyName, String propertyValue) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(prefix + propertyName));

        List<IRI> result = new ArrayList<>();

        // Search through all individuals in the ontology
        for (OWLNamedIndividual instance : ontology.getIndividualsInSignature()) {
            // Check the data property values of the instance
            for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(instance)) {
                if (axiom.getProperty().equals(property) && axiom.getObject().getLiteral().equals(propertyValue)) {
                    // Found an instance with the specified property value
                    result.add(instance.getIRI());
                }
            }
        }

        return result;
    }

    public List<IRI> getInstancesWithPropertyValueFromList(List<IRI> instanceIris, String propertyName,
            String propertyValue) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(prefix + propertyName));

        List<IRI> result = new ArrayList<>();

        // Search through the provided individuals
        for (IRI instanceIri : instanceIris) {
            OWLNamedIndividual instance = factory.getOWLNamedIndividual(instanceIri);
            // Check the data property values of the instance
            for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(instance)) {
                if (axiom.getProperty().equals(property) && axiom.getObject().getLiteral().equals(propertyValue)) {
                    // Found an instance with the specified property value
                    result.add(instance.getIRI());
                }
            }
        }

        return result;
    }

    public List<IRI> getInstancesWithPropertyValueWithoutPrefix(String propertyName, String propertyValue) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(propertyName));

        List<IRI> result = new ArrayList<>();

        // Search through all individuals in the ontology
        for (OWLNamedIndividual instance : ontology.getIndividualsInSignature()) {
            // Check the data property values of the instance
            for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(instance)) {
                if (axiom.getProperty().equals(property) && axiom.getObject().getLiteral().equals(propertyValue)) {
                    // Found an instance with the specified property value
                    result.add(instance.getIRI());
                }
            }
        }

        return result;
    }

    public List<IRI> getInstancesWithPropertyValueFromListWithoutPrefix(List<IRI> instanceIris, String propertyName,
            String propertyValue) {
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(propertyName));

        List<IRI> result = new ArrayList<>();

        // Search through the provided individuals
        for (IRI instanceIri : instanceIris) {
            OWLNamedIndividual instance = factory.getOWLNamedIndividual(instanceIri);
            // Check the data property values of the instance
            for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(instance)) {
                if (axiom.getProperty().equals(property) && axiom.getObject().getLiteral().equals(propertyValue)) {
                    // Found an instance with the specified property value
                    result.add(instance.getIRI());
                }
            }
        }

        return result;
    }

    public List<IRI> getIndividualsInRangeOfObjectProperty(String objectPropertyName) {
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(prefix + objectPropertyName));

        List<IRI> result = new ArrayList<>();

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty)) {
                result.add(axiom.getObject().asOWLNamedIndividual().getIRI());
            }
        }

        return result;
    }

    public List<IRI> getIndividualsInRangeOfObjectPropertyWithoutPrefix(String objectPropertyName) {
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(objectPropertyName));

        List<IRI> result = new ArrayList<>();

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty)) {
                result.add(axiom.getObject().asOWLNamedIndividual().getIRI());
            }
        }

        return result;
    }

    public void removeAxiomsWithIndividualInDomain(IRI individualIri, String objectPropertyName) {
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(prefix + objectPropertyName));
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty) && axiom.getSubject().equals(individual)) {

                ontology.removeAxiom(axiom);
            }
        }
    }

    public void removeAxiomsWithIndividualInDomainWithoutPrefix(IRI individualIri, String objectPropertyName) {
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(objectPropertyName));
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty) && axiom.getSubject().equals(individual)) {

                ontology.removeAxiom(axiom);
            }
        }
    }

    public String getObjectPropertyWithIndividualInRange(IRI individualIri) {
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getObject().equals(individual)) {
                return axiom.getProperty().asOWLObjectProperty().getIRI().getFragment();
            }
        }

        return null;
    }

    public String getPropertyValueForIndividual(IRI individualIri, String propertyName) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(prefix + propertyName));

        for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(individual)) {
            if (axiom.getProperty().equals(property)) {
                return axiom.getObject().getLiteral();
            }
        }

        return null;
    }

    public String getPropertyValueForIndividualWithoutPrefix(IRI individualIri, String propertyName) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(propertyName));

        for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(individual)) {
            if (axiom.getProperty().equals(property)) {
                return axiom.getObject().getLiteral();
            }
        }

        return null;
    }

    public List<IRI> getIndividualsOfClass(String className) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass owlClass = factory.getOWLClass(IRI.create(prefix + className));

        List<IRI> result = new ArrayList<>();

        for (OWLClassAssertionAxiom axiom : ontology.getAxioms(AxiomType.CLASS_ASSERTION)) {
            if (axiom.getClassExpression().equals(owlClass)) {
                result.add(axiom.getIndividual().asOWLNamedIndividual().getIRI());
            }
        }

        return result;
    }

    public List<IRI> getIndividualsOfClassWithoutPrefix(String className) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass owlClass = factory.getOWLClass(IRI.create(className));

        List<IRI> result = new ArrayList<>();

        for (OWLClassAssertionAxiom axiom : ontology.getAxioms(AxiomType.CLASS_ASSERTION)) {
            if (axiom.getClassExpression().equals(owlClass)) {
                result.add(axiom.getIndividual().asOWLNamedIndividual().getIRI());
            }
        }

        return result;
    }

    public IRI getRangeIndividualOfObjectProperty(IRI individualIri, String objectPropertyName) {
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(prefix + objectPropertyName));

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty) && axiom.getSubject().equals(individual)) {
                return axiom.getObject().asOWLNamedIndividual().getIRI();
            }
        }

        return null;
    }

    public IRI getRangeIndividualOfObjectPropertyWithoutPrefix(IRI individualIri, String objectPropertyName) {
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(objectPropertyName));

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty) && axiom.getSubject().equals(individual)) {
                return axiom.getObject().asOWLNamedIndividual().getIRI();
            }
        }

        return null;
    }

    public IRI getDomainIndividualOfObjectProperty(IRI individualIri, String objectPropertyName) {
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(prefix + objectPropertyName));

        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty) && axiom.getObject().equals(individual)) {
                return axiom.getSubject().asOWLNamedIndividual().getIRI();
            }
        }

        return null;
    }

    public IRI getDomainIndividualOfObjectPropertyWithoutPrefix(IRI individualIri, String objectPropertyName) {
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIri);
        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(objectPropertyName));
        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (axiom.getProperty().equals(objectProperty) && axiom.getObject().equals(individual)) {
                return axiom.getSubject().asOWLNamedIndividual().getIRI();
            }
        }

        return null;
    }

}

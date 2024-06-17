package com.database.federation.ontology;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class Ontology {
    public String prefix;
    public String filename;
    private OWLOntologyManager manager;
    private OWLOntology ontology;

    public Ontology(String prefix, String filename, boolean newOntology) throws Exception {
        this.prefix = prefix;
        this.filename = filename;
        manager = OWLManager.createOWLOntologyManager();

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

    public void addImportDeclaration(OWLImportsDeclaration imports) {
        manager.applyChange(new AddImport(ontology, imports));
    }

    public void loadOntologyFromURL(String url) throws Exception {
        InputStream inputStream = new URL(url).openStream();
        ontology = manager.loadOntologyFromOntologyDocument(inputStream);
    }

    public void loadOntologyFromFile(String filePath) throws OWLOntologyCreationException {
        File file = new File(filePath);
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
        // OWLObjectProperty orderUserProperty = factory.getOWLObjectProperty(orderUserIRI);
        OWLSubPropertyChainOfAxiom propertyChainAxiom = factory.getOWLSubPropertyChainOfAxiom(
                java.util.Arrays.asList(userIdProperty),
                orderUserProperty);

        manager.addAxiom(ontology, propertyChainAxiom);
    }
}

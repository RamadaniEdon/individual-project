package com.server.backend;

import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.server.backend.databaseLogic.Counter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class SchemaOrgOntologyChecker {

    // public static void main(String[] args) {
    // try {
    // // Load the ontology
    // OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    // File ontologyFile = new
    // File("./backend/src/main/resources/ontologies/schemaorg.owl"); // Replace
    // with the actual path
    // OWLOntology ontology =
    // ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);

    // // Check the familyName property
    // // checkPropertyDomain(ontologyManager, ontology,
    // "https://schema.org/familyName", "https://schema.org/Person");

    // // Check the identifier property
    // checkPropertyDomain(ontologyManager, ontology,
    // "https://schema.org/orderQuantity", "https://schema.org/OrderItem");

    // } catch (OWLOntologyCreationException e) {
    // e.printStackTrace();
    // }
    // }
    // private static boolean isClassOfDataPropertyDomain(OWLClass cls,
    // OWLDataProperty property) {

    // Set<OWLClass> classes = getDataPropertyDomainClasses(property);

    // for (OWLClass cl : classes) {
    // if (isSubclassOf(cls, cl)) {
    // return true;
    // }
    // }
    // return false;
    // }

    public static void main(String[] a){
        System.out.println(Counter.class);
    }

    // public static void main(String[] args) {
    //     try {
    //         // Load the ontology
    //         OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    //         File ontologyFile = new File("./backend/src/main/resources/ontologies/schemaorg.owl"); // Replace with the
    //                                                                                                // actual path
    //         OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);

    //         // Check the familyName property
    //         // checkPropertyDomain(ontologyManager, ontology,
    //         // "https://schema.org/familyName", "https://schema.org/Person");

    //         // Check the identifier property
    //         OWLDataProperty property = ontologyManager.getOWLDataFactory()
    //                 .getOWLDataProperty(IRI.create("https://schema.org/orderQuantity"));
    //         Set<OWLClass> classes = getDataPropertyDomainClasses(ontology, property);

    //         OWLObjectProperty objectProperty = ontologyManager.getOWLDataFactory()
    //                 .getOWLObjectProperty(IRI.create("https://schema.org/orderQuantity"));

    //         Set<OWLClass> classes2 = getObjectPropertyDomainTypes(ontology, objectProperty);
    //         for (OWLClass cl : classes) {
    //             System.out.println(cl.getIRI());
    //         }

    //     } catch (OWLOntologyCreationException e) {
    //         e.printStackTrace();
    //     }
    // }

    public static Set<OWLClass> getObjectPropertyDomainTypes(OWLOntology ontology, OWLObjectProperty property) {
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

    public static Set<OWLClass> getDataPropertyDomainClasses(OWLOntology ontology, OWLDataProperty dataProperty) {
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

    public static boolean isObjectProperty(OWLOntology ontology, String property) {

        IRI propertyIRIObject = IRI.create(property);
        OWLObjectProperty objectProperty = ontology.getOWLOntologyManager().getOWLDataFactory()
                .getOWLObjectProperty(propertyIRIObject);
        return ontology.getObjectPropertiesInSignature().contains(objectProperty);
    }

    public static boolean isDataProperty(OWLOntology ontology, String property) {

        IRI propertyIRIObject = IRI.create(property);
        OWLDataProperty dataProperty = ontology.getOWLOntologyManager().getOWLDataFactory()
                .getOWLDataProperty(propertyIRIObject);
        return ontology.getDataPropertiesInSignature().contains(dataProperty);
    }

    // public static void main(String[] args) throws OWLOntologyCreationException {
    // OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    // File ontologyFile = new
    // File("./backend/src/main/resources/ontologies/schemaorg.owl"); // Replace
    // with the// actual path

    // OWLOntology ontology =
    // ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);

    // System.out.println(isObjectProperty(ontology,
    // "https://schema.org/totalPaymentDue"));
    // System.out.println(isDataProperty(ontology,
    // "https://schema.org/totalPaymentDue"));
    // }

    // private static void checkPropertyDomain(OWLOntologyManager ontologyManager,
    // OWLOntology ontology, String propertyIRI, String desiredClassIRI) {
    // IRI propertyIRIObject = IRI.create(propertyIRI);
    // OWLObjectProperty property =
    // ontologyManager.getOWLDataFactory().getOWLObjectProperty(propertyIRIObject);

    // IRI desiredClassIRIObject = IRI.create(desiredClassIRI);
    // OWLClass desiredClass =
    // ontologyManager.getOWLDataFactory().getOWLClass(desiredClassIRIObject);

    // // Create a structural reasoner (you can use HermiT if it's available)
    // OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    // OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

    // // Get the axioms for the property
    // for (OWLObjectPropertyDomainAxiom domainAxiom :
    // ontology.getObjectPropertyDomainAxioms(property)) {
    // OWLClassExpression domain = domainAxiom.getDomain();

    // if (isClassInHierarchy(reasoner, domain, desiredClass)) {
    // System.out.println("Property '" + propertyIRI + "' has the type '" +
    // desiredClassIRI + "' for domain: " + domain);
    // }
    // }
    // }

    private static void checkPropertyDomain(OWLOntologyManager ontologyManager, OWLOntology ontology,
            String propertyIRI, String desiredClassIRI) {
        IRI propertyIRIObject = IRI.create(propertyIRI);
        OWLObjectProperty property = ontologyManager.getOWLDataFactory().getOWLObjectProperty(propertyIRIObject);

        IRI desiredClassIRIObject = IRI.create(desiredClassIRI);
        OWLClass desiredClass = ontologyManager.getOWLDataFactory().getOWLClass(desiredClassIRIObject);

        // Create a structural reasoner (you can use HermiT if it's available)
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        // Get the domain classes for the property
        for (OWLObjectPropertyDomainAxiom domainAxiom : ontology.getObjectPropertyDomainAxioms(property)) {
            OWLClassExpression domain = domainAxiom.getDomain();
            if (isClassInHierarchy(reasoner, (OWLClassExpression) domain, desiredClass)) {
                System.out.println(
                        "Property '" + propertyIRI + "' has the type '" + desiredClassIRI + "' for domain: " + domain);
                return; // Stop checking further domains once a match is found
            }
        }

        System.out.println(
                "Property '" + propertyIRI + "' does not have the type '" + desiredClassIRI + "' in its domain.");
    }

    private static boolean isClassInHierarchy(OWLReasoner reasoner, OWLClassExpression expression,
            OWLClass targetClass) {
        if (expression.isClassExpressionLiteral() && expression.asOWLClass().equals(targetClass)) {
            return true;
        }

        if (expression instanceof OWLObjectUnionOf) {
            OWLObjectUnionOf unionOf = (OWLObjectUnionOf) expression;
            for (OWLClassExpression classExpression : unionOf.getOperands()) {
                if (isClassInHierarchy(reasoner, classExpression, targetClass)) {
                    return true;
                }
            }
        }

        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(expression, true);
        return subClasses.containsEntity(targetClass);
    }

    public static boolean isSubclassOf(OWLOntology ontology, OWLClass subclass, OWLClass superclass) {
        // Create a structural reasoner (you can use HermiT if it's available)
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

    // public static void main(String[] args) {
    // try {
    // OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    // File ontologyFile = new
    // File("./backend/src/main/resources/ontologies/schemaorg.owl");
    // OWLOntology ontology =
    // ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);

    // IRI orderIRI = IRI.create("https://schema.org/Order");
    // IRI thingIRI = IRI.create("https://schema.org/Thing");

    // OWLClass orderClass =
    // ontologyManager.getOWLDataFactory().getOWLClass(orderIRI);
    // OWLClass thingClass =
    // ontologyManager.getOWLDataFactory().getOWLClass(thingIRI);

    // // Example usage
    // if (isSubclassOf(ontology, orderClass, thingClass)) {
    // System.out.println("Order is a subclass of Thing");
    // } else {
    // System.out.println("Order is not a subclass of Thing");
    // }
    // } catch (OWLOntologyCreationException e) {
    // e.printStackTrace();
    // }
    // }

    public static Set<OWLClass> getPropertyDomainTypes(OWLOntology ontology, OWLObjectProperty property) {
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

    // public static void main(String[] args) {
    // try {
    // OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    // File ontologyFile = new
    // File("./backend/src/main/resources/ontologies/schemaorg.owl");
    // OWLOntology ontology =
    // ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);

    // IRI familyNameIRI = IRI.create("https://schema.org/orderQuantity");
    // OWLObjectProperty familyNameProperty =
    // ontologyManager.getOWLDataFactory().getOWLObjectProperty(familyNameIRI);

    // // Example usage
    // Set<OWLClass> familyNameDomainTypes = getPropertyDomainTypes(ontology,
    // familyNameProperty);

    // System.out.println("Domain types of familyName property:");
    // for (OWLClass domainType : familyNameDomainTypes) {
    // System.out.println(domainType.getIRI());
    // }
    // } catch (OWLOntologyCreationException e) {
    // e.printStackTrace();
    // }
    // }
}

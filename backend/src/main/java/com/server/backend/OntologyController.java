package com.server.backend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ontology")
public class OntologyController {



  public static List<OWLObjectProperty> getObjectPropertiesWithRange(OWLOntology ontology, OWLClass cls) {
    List<OWLObjectProperty> objectProperties = new ArrayList<>();

    // Get all object properties defined in the ontology
    Set<OWLObjectProperty> allObjectProperties = ontology.getObjectPropertiesInSignature();

    // Iterate through each object property
    // int i = 0;
    for (OWLObjectProperty objectProperty : allObjectProperties) {
      // Check if the range of the property contains the given class
      for (OWLObjectPropertyDomainAxiom axiom : ontology.getObjectPropertyDomainAxioms(objectProperty)) {
        // if (i == 0) {
        // System.out.println("Axiom: " + axiom.toString());
        // System.out.println("Range: " + axiom.getRange().toString());
        // }
        // i++;
        OWLClassExpression range = axiom.getDomain();
        if (range instanceof OWLObjectUnionOf) {
          for (OWLClassExpression operand : ((OWLObjectUnionOf) range).getOperands()) {
            if (operand.equals(cls)) {
              objectProperties.add(objectProperty);
              break;
            }
          }
        } else {
          System.out.println("Range: " + range.toString());
        }
      }
    }

    return objectProperties;
  }
  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/helloworld")
  public String hello() {
    return "Hello World";
  }


  @GetMapping("/classes")
  public List<ClassInfo> getClasses() {
    List<ClassInfo> classesInfo = new ArrayList<>();

    try {
      // Load schema.org ontology from URL
      OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
      OWLOntology schemaOrg = manager.loadOntologyFromOntologyDocument(
          IRI.create("https://raw.githubusercontent.com/schemaorg/schemaorg/main/data/releases/26.0/schemaorg.owl"));

      // Get all classes defined in the ontology
      for (OWLClass cls : schemaOrg.getClassesInSignature()) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setType(cls.getIRI().toString());

        // Get properties for each class
        List<PropertyInfo> propertiesInfo = new ArrayList<>();
        List<OWLObjectProperty> objectProperties = getObjectPropertiesWithRange(schemaOrg, cls);
        for (OWLObjectProperty objp : objectProperties) {
          PropertyInfo propertyInfo = new PropertyInfo();
          propertyInfo.setProperty(objp.getIRI().toString());
          List<String> rangeTypes = new ArrayList<>();
          for (OWLObjectPropertyRangeAxiom axiom : schemaOrg.getObjectPropertyRangeAxioms(objp)) {
            // rangeTypes.add(axiom.getRange().asOWLClass().getIRI().toString());
            OWLClassExpression range = axiom.getRange();
            if (range instanceof OWLObjectUnionOf) {
              for (OWLClassExpression operand : ((OWLObjectUnionOf) range).getOperands()) {
                // if (operand.equals(cls)) {
                rangeTypes.add(operand.toString());
                // break;
                // }
              }
            }
          }

          propertyInfo.setRange(rangeTypes);
          propertiesInfo.add(propertyInfo);
        }

        // for (OWLAnnotationAssertionAxiom axiom :
        // schemaOrg.getAnnotationAssertionAxioms(cls.getIRI())) {
        // if
        // (axiom.getProperty().getIRI().toString().equals("http://www.w3.org/2000/01/rdf-schema#subClassOf"))
        // {
        // OWLLiteral superClassNameLiteral = (OWLLiteral) axiom.getValue();
        // String superClassName = superClassNameLiteral.getLiteral();
        // PropertyInfo propertyInfo = new PropertyInfo();
        // propertyInfo.setProperty("subClassOf");
        // List<String> rangeTypes = new ArrayList<>();
        // rangeTypes.add(superClassName);
        // propertyInfo.setRange(rangeTypes);
        // propertiesInfo.add(propertyInfo);
        // }
        // }

        classInfo.setProperties(propertiesInfo);
        classesInfo.add(classInfo);
      }
    } catch (OWLOntologyCreationException e) {
      e.printStackTrace();
    }

    return classesInfo;
  }

  // Inner class to represent class information
  static class ClassInfo {
    private String type;
    private List<PropertyInfo> properties;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public List<PropertyInfo> getProperties() {
      return properties;
    }

    public void setProperties(List<PropertyInfo> properties) {
      this.properties = properties;
    }
  }

  // Inner class to represent property information
  static class PropertyInfo {
    private String property;
    private List<String> range;

    public String getProperty() {
      return property;
    }

    public void setProperty(String property) {
      this.property = property;
    }

    public List<String> getRange() {
      return range;
    }

    public void setRange(List<String> range) {
      this.range = range;
    }
  }
}

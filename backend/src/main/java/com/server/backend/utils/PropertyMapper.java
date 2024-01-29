package com.server.backend.utils;

import java.util.HashMap;
import java.util.Map;

public class PropertyMapper {

  // Define a mapping of compound keys (target class + linking property) to their
  // indirect associations
  private static final Map<String, IndirectAssociation> propertyMapping = new HashMap<>();

  static {
    // Example: totalPaymentDue is linked with Order through Invoice
    propertyMapping.put(generateCompoundKey("totalPaymentDue", "Order"),
        new IndirectAssociation("Order", "Invoice", "partOfInvoice"));

    propertyMapping.put(generateCompoundKey("addressCountry", "Person"),
        new IndirectAssociation("Person", "PostalAddress", "address"));

    propertyMapping.put(generateCompoundKey("addressRegion", "Person"),
        new IndirectAssociation("Person", "PostalAddress", "address"));

    propertyMapping.put(generateCompoundKey("postalCode", "Person"),
        new IndirectAssociation("Person", "PostalAddress", "address"));

    propertyMapping.put(generateCompoundKey("streetAddress", "Person"),
        new IndirectAssociation("Person", "PostalAddress", "address"));

    propertyMapping.put(generateCompoundKey("price", "Product"), new IndirectAssociation("Product", "Offer", "offers"));

    // Add other property mappings as needed
  }

  // public static void main(String[] args) {
  //   System.out.println(propertyMapping.get(generateCompoundKey("totalPaymentDue", "Order")).getLinkingClass());
  // }

  public static String getLinkingClass(String property, String targetClass) {
    if (propertyMapping.get(generateCompoundKey(property, targetClass)) != null) {
      return propertyMapping.get(generateCompoundKey(property, targetClass)).getLinkingClass();
    } else {
      return null;
    }
  }

  public static String getLinkingProperty(String property, String targetClass) {
    return propertyMapping.get(generateCompoundKey(property, targetClass)).getLinkingProperty();
  }

  public static IndirectAssociation getIndirectAssociation(String property, String targetClass) {
    return propertyMapping.get(generateCompoundKey(property, targetClass));
  }

  private static String generateCompoundKey(String property, String targetClass) {
    return property + "-" + targetClass;
  }

  public static class IndirectAssociation {
    private final String targetClass;
    private final String linkingClass;
    private final String linkingProperty;

    public IndirectAssociation(String targetClass, String linkingClass, String linkingProperty) {
      this.targetClass = targetClass;
      this.linkingClass = linkingClass;
      this.linkingProperty = linkingProperty;
    }

    public String getTargetClass() {
      return targetClass;
    }

    public String getLinkingClass() {
      return linkingClass;
    }

    public String getLinkingProperty() {
      return linkingProperty;
    }
  }
}

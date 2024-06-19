package com.database.federation.userData;

import java.util.List;

public class Instance {
    private String field;
    private String dbField;
    private String value;
    private boolean objectProperty;
    private boolean reference;
    private boolean imaginaryProperty;
    private String range;
    private String referenceClass;
    private String referenceProperty;
    private List<Instance> fields;
    
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getDbField() {
        return dbField;
    }
    public void setDbField(String dbField) {
        this.dbField = dbField;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public boolean isObjectProperty() {
        return objectProperty;
    }
    public void setObjectProperty(boolean objectProperty) {
        this.objectProperty = objectProperty;
    }
    public boolean isReference() {
        return reference;
    }
    public void setReference(boolean reference) {
        this.reference = reference;
    }
    public boolean isImaginaryProperty() {
        return imaginaryProperty;
    }
    public void setImaginaryProperty(boolean imaginaryProperty) {
        this.imaginaryProperty = imaginaryProperty;
    }
    public String getRange() {
        return range;
    }
    public void setRange(String range) {
        this.range = range;
    }
    public String getReferenceClass() {
        return referenceClass;
    }
    public void setReferenceClass(String referenceProperty) {
        this.referenceClass = referenceProperty;
    }
    public String getReferenceProperty() {
        return referenceProperty;
    }
    public void setReferenceProperty(String referenceProperty) {
        this.referenceProperty = referenceProperty;
    }
    public List<Instance> getFields() {
        return fields;
    }
    public void setFields(List<Instance> fields) {
        this.fields = fields;
    }

    public static Instance copy(Instance i){
        Instance newInstance = new Instance();
        newInstance.setDbField(i.getDbField());
        newInstance.setField(i.getField());
        newInstance.setImaginaryProperty(i.isImaginaryProperty());
        newInstance.setObjectProperty(i.isObjectProperty());
        newInstance.setRange(i.getRange());
        newInstance.setReference(i.isReference());
        newInstance.setReferenceClass(i.getReferenceClass());
        newInstance.setReferenceProperty(i.getReferenceProperty());
        // newInstance.setValue(i.getValue());

        if(i.getFields() != null && !i.getFields().isEmpty()){
            List<Instance> newFields = i.getFields().stream().map(Instance::copy).toList();
            newInstance.setFields(newFields);
        }
        return newInstance;
    }

    
}

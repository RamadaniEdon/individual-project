package com.database.federation.utils;

import java.util.List;

public class ClassWithProperties {
    private String className;
    private List<PropertyWithRange> properties;
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public List<PropertyWithRange> getProperties() {
        return properties;
    }
    public void setProperties(List<PropertyWithRange> properties) {
        this.properties = properties;
    }
}

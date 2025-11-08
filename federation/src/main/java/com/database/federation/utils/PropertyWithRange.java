package com.database.federation.utils;

import java.util.List;

public class PropertyWithRange {
    private String propertyName;
    private List<String> range;
    public String getPropertyName() {
        return propertyName;
    }
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    public List<String> getRange() {
        return range;
    }
    public void setRange(List<String> range) {
        this.range = range;
    }
}

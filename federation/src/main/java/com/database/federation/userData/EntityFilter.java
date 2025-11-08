package com.database.federation.userData;

public class EntityFilter {
    // {
    //     // "databaseId": 
    //     "entities": [
    //         {
    //             "name": "Order",
    //             "property": "createdAt"
    //         },
    //         {
    //             "name": "Product",
    //             "property": "description",
    //             "value": "House"
    //         }
    //     ]
    // }
    private String name;
    private String property;
    private String value;
    private boolean bigger;
    private boolean smaller;
    private boolean date;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public boolean isBigger() {
        return bigger;
    }
    public void setBigger(boolean bigger) {
        this.bigger = bigger;
    }
    public boolean isSmaller() {
        return smaller;
    }
    public void setSmaller(boolean smaller) {
        this.smaller = smaller;
    }
    public boolean isDate() {
        return date;
    }
    public void setDate(boolean date) {
        this.date = date;
    }
}

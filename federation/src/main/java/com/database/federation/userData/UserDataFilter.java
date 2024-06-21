package com.database.federation.userData;

import java.util.List;

public class UserDataFilter {
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
    private String databaseId;
    private List<EntityFilter> entities;
    public String getDatabaseId() {
        return databaseId;
    }
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }
    public List<EntityFilter> getEntities() {
        return entities;
    }
    public void setEntities(List<EntityFilter> entities) {
        this.entities = entities;
    }
}

package com.database.federation.userData;

import java.util.List;

public class UserDataGlobalFormat {
    private String companyName;
    private List<Entity> collections;
    
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public List<Entity> getCollections() {
        return collections;
    }
    public void setCollections(List<Entity> collections) {
        this.collections = collections;
    }

}


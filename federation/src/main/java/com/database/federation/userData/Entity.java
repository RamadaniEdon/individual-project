package com.database.federation.userData;

import java.util.List;

public class Entity {
    private String name;
    private String nameInDb;
    private boolean userData;
    private List<List<Instance>> documents;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNameInDb() {
        return nameInDb;
    }
    public void setNameInDb(String dbName) {
        this.nameInDb = dbName;
    }
    public boolean isUserData() {
        return userData;
    }
    public void setUserData(boolean userData) {
        this.userData = userData;
    }
    public List<List<Instance>> getDocuments() {
        return documents;
    }
    public void setDocuments(List<List<Instance>> documents) {
        this.documents = documents;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Entity other = (Entity) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    
}

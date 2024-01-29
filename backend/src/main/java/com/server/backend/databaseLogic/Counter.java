package com.server.backend.databaseLogic;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counter")
public class Counter {
    @Id
    private String id;
    private int lastUsedId;

    // getters and setters
    public Counter(String id, int lastUsedId) {
        this.id = id;
        this.lastUsedId = lastUsedId;
    }
    
    public Counter() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLastUsedId() {
        return lastUsedId;
    }

    public void setLastUsedId(int lastUsedId) {
        this.lastUsedId = lastUsedId;
    }

    public String toString() {
        return "id: " + id + ", lastUsedId: " + lastUsedId;
    }
}
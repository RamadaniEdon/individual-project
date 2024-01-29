package com.server.backend.userDataLogic;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PrivateData")
public class PrivateData {

    @Id
    private String id;
    private String userAfm;
    private String dbId;
    private String table;
    private String column;
    private String privacyClass;
    
    public PrivateData(String userAfm, String dbId, String table, String column, String privacyClass) {
        this.userAfm = userAfm;
        this.dbId = dbId;
        this.table = table;
        this.column = column;
        this.privacyClass = privacyClass;
    }

    public PrivateData() {

    }

    public String getId() {
        return id;
    }   

    public String getUserAfm() {
        return userAfm;
    }

    public String getDbId() {
        return dbId;
    }

    public String getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }

    public String getPrivacyClass() {
        return privacyClass;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserAfm(String userAfm){
        this.userAfm = userAfm;
    }

    public void setDbId(String dbId){
        this.dbId = dbId;
    }

    public void setTable(String table){
        this.table = table;
    }

    public void setColumn(String column){
        this.column = column;
    }

    public void setPrivacyClass(String privacyClass){
        this.privacyClass = privacyClass;
    }


}
package com.server.backend.userDataLogic;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class User {

    @Id
    private String id;

    private String name;
    private String surname;
    private String afm;

    public User(String name, String surname, String afm) {
        this.name = name;
        this.surname = surname;
        this.afm = afm;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getAfm(){
        return afm;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public void setAfm(String afm){
        this.afm = afm;
    }

    


}


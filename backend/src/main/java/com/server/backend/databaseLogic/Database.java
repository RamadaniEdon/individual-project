package com.server.backend.databaseLogic;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Databases")
public class Database {

    @Id
    private int id;

    private String url;
    private String name;
    private String companyName;
    private String port;
    private String host;
    private String username;
    private String password;

    // Constructors, getters, setters, etc.
    public Database(String url, String name, String companyName, String port, String host, String username, String password) {
        this.url = url;
        this.name = name;
        this.companyName = companyName;
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public Database() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl(){
        return url;
    }

    public String getName(){
        return name;
    }

    public String getCompanyName(){
        return companyName;
    }

    public String getPort(){
        return port;
    }

    public String getHost(){
        return host;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }

    public void setPort(String port){
        this.port = port;
    }

    public void setHost(String host){
        this.host = host;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    

}

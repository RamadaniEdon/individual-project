package com.database.federation.database;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DatabaseRepository extends MongoRepository<DatabaseModel, String>{

    
}
package com.server.backend.databaseLogic;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatabaseRepository extends MongoRepository<Database, Integer> {
    // You can add custom queries or methods if needed
}

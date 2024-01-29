package com.server.backend.userDataLogic;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // You can add custom queries or methods if needed
}

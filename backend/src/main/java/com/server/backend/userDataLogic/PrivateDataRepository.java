package com.server.backend.userDataLogic;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivateDataRepository extends MongoRepository<PrivateData, String> {
    // You can add custom queries or methods if needed
}

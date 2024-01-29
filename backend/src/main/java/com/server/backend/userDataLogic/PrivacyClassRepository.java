package com.server.backend.userDataLogic;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivacyClassRepository extends MongoRepository<PrivacyClass, String> {
  
}

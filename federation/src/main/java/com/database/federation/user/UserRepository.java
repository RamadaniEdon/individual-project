package com.database.federation.user;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserModel, String> {
  UserModel findByNameAndSurname(String name, String surname);
}
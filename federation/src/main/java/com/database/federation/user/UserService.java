package com.database.federation.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private final UserRepository repository;

  @Autowired
  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public List<UserModel> getAllUsers() {
    return repository.findAll();
  }

  public UserModel addUser(UserModel user) {
    return repository.save(user);
  }

  public UserModel findUserByNameAndSurname(String name, String surname) {
    return repository.findByNameAndSurname(name, surname);
  }


  public UserModel findUserById(String id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteUserById(String id) {
    Optional<UserModel> userOptional = repository.findById(id);
    if (userOptional.isPresent()) {
      repository.deleteById(id);
    } else {
      throw new RuntimeException("User not found with id: " + id);
    }
  }
  // Other methods as needed
}

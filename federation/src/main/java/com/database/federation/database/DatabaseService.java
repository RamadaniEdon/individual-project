package com.database.federation.database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseService {

  private final DatabaseRepository repository;

  @Autowired
  public DatabaseService(DatabaseRepository repository) {
    this.repository = repository;
  }

  public List<DatabaseModel> getAllUsers() {
    return repository.findAll();
  }

  public DatabaseModel addUser(DatabaseModel database) {
    return repository.save(database);
  }

  public DatabaseModel findUserById(String id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteUserById(String id) {
    Optional<DatabaseModel> userOptional = repository.findById(id);
    if (userOptional.isPresent()) {
      repository.deleteById(id);
    } else {
      throw new RuntimeException("User not found with id: " + id);
    }
  }
  // Other methods as needed
}

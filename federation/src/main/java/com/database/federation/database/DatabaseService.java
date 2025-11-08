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

  public List<DatabaseModel> getAllDatabases() {
    return repository.findAll();
  }

  public DatabaseModel addDatabase(DatabaseModel database) {
    return repository.save(database);
  }

  public DatabaseModel findDatabaseById(String id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteDatabaseById(String id) {
    Optional<DatabaseModel> databaseOptional = repository.findById(id);
    if (databaseOptional.isPresent()) {
      repository.deleteById(id);
    } else {
      throw new RuntimeException("Database not found with id: " + id);
    }
  }
  // Other methods as needed
}

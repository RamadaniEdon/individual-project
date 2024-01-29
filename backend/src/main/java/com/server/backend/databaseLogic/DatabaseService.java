package com.server.backend.databaseLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

  private final DatabaseRepository databaseRepository;
  private final CounterService counterService;

  @Autowired
  public DatabaseService(DatabaseRepository databaseRepository, CounterService counterService) {
    this.databaseRepository = databaseRepository;
    this.counterService = counterService;
  }

  public int saveDatabase(Database database) {
    int nextId = counterService.getNextDatabaseId();
    database.setId(nextId);
    databaseRepository.save(database);
    return nextId;
  }

  public List<Database> getAllDatabases() {
    return databaseRepository.findAll();
  }
}

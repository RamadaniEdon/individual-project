package com.server.backend.utils;

import com.server.backend.components.DatabaseComponent;
import com.server.backend.databaseLogic.Database;

public class Helpers {
  
  public static Database databaseFromComponent(DatabaseComponent databaseComponent) {
    return new Database(databaseComponent.getUrl(), databaseComponent.getName(), databaseComponent.getPort(), databaseComponent.getCompanyName(), databaseComponent.getHost(), databaseComponent.getUsername(), databaseComponent.getPassword());
  }
}

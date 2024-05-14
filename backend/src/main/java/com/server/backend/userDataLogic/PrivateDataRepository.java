package com.server.backend.userDataLogic;

import java.util.List;

// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivateDataRepository extends MongoRepository<PrivateData, String> {

    // @Query("{ 'dbId' : ?0, 'table' : ?1 }")
    List<PrivateData> findByDbIdAndTable(String dbId, String table);
    // You can add custom queries or methods if needed
}

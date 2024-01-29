package com.server.backend.databaseLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String COLLECTION_NAME = "database_counter";

    public int getNextDatabaseId() {
        Counter counter = mongoTemplate.findById(COLLECTION_NAME, Counter.class, COLLECTION_NAME);
        System.out.println("counter: " + counter);


        if (counter == null) {
            counter = new Counter();
            counter.setId(COLLECTION_NAME);
            counter.setLastUsedId(0);
            mongoTemplate.save(counter, COLLECTION_NAME);
        }

        int nextId = counter.getLastUsedId() + 1;
        counter.setLastUsedId(nextId);
        mongoTemplate.save(counter, COLLECTION_NAME);

        return nextId;
    }

    public int getPreviousDatabaseId() {
        Counter counter = mongoTemplate.findById(COLLECTION_NAME, Counter.class);

        if (counter == null) {
            counter = new Counter();
            counter.setId(COLLECTION_NAME);
            counter.setLastUsedId(0);
            mongoTemplate.save(counter, COLLECTION_NAME);
        }

        int previousId = counter.getLastUsedId();

        return previousId;
    }
}
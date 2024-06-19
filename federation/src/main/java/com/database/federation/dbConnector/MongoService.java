package com.database.federation.dbConnector;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.List;

public class MongoService {

    MongoClient client;
    MongoDatabase database;

    public MongoService(String url, String database) {
        this.client = MongoClients.create(url);
        this.database = client.getDatabase(database);
    }

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:10020");
        MongoDatabase database = mongoClient.getDatabase("orders");

        MongoIterable<String> collectionNames = database.listCollectionNames();
        for (String name : collectionNames) {
            System.out.println("Connection closed");
            System.out.println(name);
            System.out.println("Connection closed");
        }

        MongoCollection<Document> usersCollection = database.getCollection("users");
        Bson filter = Filters.eq("name", "Edon");
        MongoCursor<Document> cursor = usersCollection.find(filter).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                printDocumentFields(doc);
                System.out.println("-------------------");
            }
        } finally {
            cursor.close();
        }

        mongoClient.close();
    }

    private static void printDocumentFields(Document doc) {
        for (String key : doc.keySet()) {
            Object value = doc.get(key);
            System.out.println(key + ": " + value);
            if (value instanceof Document) {
                printDocumentFields((Document) value);
            } else if (value instanceof List) {
                printListElements((List<?>) value);
            }
        }
    }

    private static void printListElements(List<?> list) {
        int index = 0;
        for (Object element : list) {
            System.out.println("Element " + index + ": " + element);
            if (element instanceof Document) {
                printDocumentFields((Document) element);
            }
            index++;
        }
    }

    private void closeConnection() {
        client.close();
    }

    private List<String> getCollectionNames() {
        MongoIterable<String> collectionNames = database.listCollectionNames();
        List<String> names = new ArrayList<>();
        for (String name : collectionNames) {
            names.add(name);
        }
        return names;
    }

    public void printUsersNamedEdon() {
        MongoCollection<Document> usersCollection = database.getCollection("users");
        Bson filter = Filters.eq("name", "Edon");
        MongoCursor<Document> cursor = usersCollection.find(filter).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                for (String key : doc.keySet()) {
                    System.out.println(key + ": " + doc.get(key));
                }
                System.out.println("-------------------");
            }
        } finally {
            cursor.close();
        }
    }

}

package com.database.federation.dbConnector;

import com.mongodb.client.MongoClients;
import com.database.federation.userData.Entity;
import com.database.federation.userData.Instance;
import com.database.federation.userData.UserDataGlobalFormat;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MongoService extends DbService {

    MongoClient client;
    MongoDatabase database;

    public MongoService(String url, String database) {
        String connUrl = "mongodb://" + url;
        this.client = MongoClients.create(connUrl);
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
        Bson filter = Filters.eq("_id", "66706d752fc2e20226e8b789");
        // MongoCursor<Document> cursor = usersCollection.find(filter).iterator();
        MongoCursor<Document> cursor = getDocumentsInCollectingByFieldValue(database, "users", "_id",
                "66706d752fc2e20226e8b789");

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

    public void mapDatabaseToGlobalFormat(UserDataGlobalFormat dataFormat) throws Exception {
        for (Entity e : dataFormat.getCollections()) {
            e.setUserData(false);
        }
        String afm = "123456789";

        Entity personEntity = getPersonEntity(dataFormat);
        personEntity.setUserData(true);
        Instance taxIDInstance = getTaxIDInstance(personEntity);

        MongoCursor<Document> res = getDocumentsInCollectingByFieldValue(personEntity.getNameInDb(), "afm", afm);

        int docNum = 1;
        while (res.hasNext()) {
            addDocumentToEntity(personEntity);
            Document doc = res.next();
            mapDocumentsRecursively(personEntity, doc, personEntity.getNameInDb(), docNum);
            docNum++;
        }

        List<Entity> iteratedEntities = new ArrayList<>();
        List<Entity> baseEntitiesList = new ArrayList<>();
        updatesEntitiesRecursively(personEntity, dataFormat, iteratedEntities, baseEntitiesList);

        for (Entity e : dataFormat.getCollections()) {
            e.getDocuments().remove(0);
            removeDuplicates(e);
        }

    }

    private void updatesEntitiesRecursively(Entity entity, UserDataGlobalFormat dataFormat,
            List<Entity> iteratedEntities, List<Entity> baseEntitiesList) {

        if (baseEntitiesList.contains(entity)) {
            return;
        }
        baseEntitiesList.add(entity);
        List<Entity> entitiesReferencingPerson = getEntitiesReferencingThis(entity, dataFormat);

        for (Entity e : entitiesReferencingPerson) {
            if (iteratedEntities.contains(e)) {
                continue;
            }
            iteratedEntities.add(e);
            Instance instance = getReferencingInstance(e, entity);

            for (int i = 0; i < entity.getDocuments().size(); i++) {
                String value = getValueOfEntityRowProperty(entity, instance.getReferenceProperty(), i);
                System.out.println("Value: " + value + " for " + e.getNameInDb() + "." + instance.getDbField());
                System.out.println(instance.getDbField().split("\\.").length);

                String dbField = instance.getDbField();
                String actualDbField = dbField.substring(dbField.indexOf(".")+1);

                MongoCursor<Document> rs = getDocumentsInCollectingByFieldValue(e.getNameInDb(),
                        actualDbField, value);

                int docNum2 = e.getDocuments().size();
                while (rs.hasNext()) {
                    addDocumentToEntity(e);
                    Document doc = rs.next();
                    mapDocumentsRecursively(e, doc, e.getNameInDb(), docNum2);

                    docNum2++;
                }
            }
            if (!e.isUserData())
                e.setUserData(entity.isUserData());

        }

        List<Entity> entitiesReferencedByEntity = getEntitiesReferencedByThis(entity, dataFormat);

        for (Entity e : entitiesReferencedByEntity) {
            if (iteratedEntities.contains(e)) {
                continue;
            }
            iteratedEntities.add(e);
            e.getName();
            System.out.println("Edon: " + e.getName());
            System.out.println("Edon: " + entity.getName());
            Instance instance = getReferencingInstance(entity, e);
            if (instance == null) {
                System.out.println("Edon: " + "null");
            } else {
                System.out.println("Edon: " + instance.getDbField());
            }

            for (int i = 0; i < entity.getDocuments().size(); i++) {
                String value = getValueOfEntityRowProperty(entity, instance.getDbField(), i);
                System.out.println("BaboValue: " + value + " for " + e.getNameInDb() + "." + instance.getDbField());
                System.out.println(instance.getDbField().split("\\.").length);


                String dbField = instance.getReferenceProperty();
                String actualDbField = dbField.substring(dbField.indexOf(".")+1);


                MongoCursor<Document> rs = getDocumentsInCollectingByFieldValue(e.getNameInDb(),
                        actualDbField, value);

                int docNum2 = e.getDocuments().size();

                System.out.println("Budakovaaaa: " + e.getDocuments().size());
                System.out.println("Budakovaaaa: " + docNum2);

                while (rs.hasNext()) {

                    addDocumentToEntity(e);
                    Document doc = rs.next();
                    mapDocumentsRecursively(e, doc, e.getNameInDb(), docNum2);

                    docNum2++;
                }
            }
        }

        for (Entity e : entitiesReferencingPerson) {
            updatesEntitiesRecursively(e, dataFormat, iteratedEntities, baseEntitiesList);
        }
        for (Entity e : entitiesReferencedByEntity) {
            updatesEntitiesRecursively(e, dataFormat, iteratedEntities, baseEntitiesList);
        }

    }

    public void mapDocumentsRecursively(Entity e, Document d, String parent, int docNum) {
        for (String columnName : d.keySet()) {
            Object value = d.get(columnName);
            if (value instanceof Document) {
                String newParent = parent + "." + columnName;
                mapDocumentsRecursively(e, ((Document) value), newParent, docNum);
            } else if (!(value instanceof List)) {
                Instance instance = getInstanceByDbField(e, parent + "." + columnName, docNum);
                if (instance != null) {
                    instance.setValue(value.toString());
                }
            }

        }
    }

    public static MongoCursor<Document> getDocumentsInCollectingByFieldValue(MongoDatabase database,
            String collectionName, String fieldName,
            String fieldValue) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Bson filter = Filters.eq(fieldName, fieldValue);
        MongoCursor<Document> cursorString = collection.find(filter).iterator();

        if (!cursorString.hasNext()) {
            Bson filterObjectId = Filters.eq(fieldName, new ObjectId(fieldValue));
            MongoCursor<Document> cursorObjectId = collection.find(filterObjectId).iterator();
            return cursorObjectId;
        }

        return cursorString;
    }

    public MongoCursor<Document> getDocumentsInCollectingByFieldValue(String collectionName, String fieldName,
            String fieldValue) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Bson filter = Filters.eq(fieldName, fieldValue);
        MongoCursor<Document> cursorString = collection.find(filter).iterator();

        if (!cursorString.hasNext()) {
            try {
                Bson filterObjectId = Filters.eq(fieldName, new ObjectId(fieldValue));
                MongoCursor<Document> cursorObjectId = collection.find(filterObjectId).iterator();
                return cursorObjectId;
                
            } catch (Exception e) {
                return cursorString;
            }
        }

        return cursorString;
    }

}

package com.database.federation.dbConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.database.federation.userData.Entity;
import com.database.federation.userData.Instance;
import com.database.federation.userData.UserDataGlobalFormat;

public class MySQLService {

    private String url;
    private String user;
    private String password;
    private Connection conn;

    public MySQLService(String url, String database, String user, String password) throws Exception {
        this.url = "jdbc:mysql://" + url + "/" + database;
        this.user = user;
        this.password = password;
        System.out.println("Connecting to database: " + url);
        conn = DriverManager.getConnection(this.url, this.user, this.password);
    }

    public Entity getPersonEntity(UserDataGlobalFormat format) {
        for (Entity entity : format.getCollections()) {
            if (entity.getName().equals("Person")) {
                return entity;
            }
        }
        return null;
    }

    public Instance getTaxIDInstance(Entity entity) {
        for (List<Instance> document : entity.getDocuments()) {
            for (Instance instance : document) {
                if (instance.getField().equals("taxID")) {
                    return instance;
                }
            }
        }
        return null;
    }

    public List<Instance> getInstanceChainByDbField(Entity entity, String field) {
        for (List<Instance> document : entity.getDocuments()) {
            for (Instance instance : document) {
                List<Instance> chain = new ArrayList<>();
                chain.add(instance);
                if (instance.getDbField().equals(field)) {
                    return chain;
                }
                chain = getInstancesRecursively(instance, field, chain);
                if (chain != null) {
                    return chain;
                }
            }
        }
        return null;
    }

    private List<Instance> getInstancesRecursively(Instance inst, String fieldName, List<Instance> chain) {
        for (Instance field : inst.getFields()) {
            List<Instance> newChain = new ArrayList<>(chain);
            newChain.add(field);
            if (field.getDbField().equals(fieldName)) {
                return newChain;
            }
            newChain = getInstancesRecursively(field, fieldName, newChain);
            if (newChain != null) {
                return newChain;
            }
        }
        return null;
    }

    public Instance getInstanceByDbField(Entity entity, String field, int docNum) {
        List<Instance> document = entity.getDocuments().get(docNum);
        for (Instance instance : document) {
            if (instance.getDbField() != null && instance.getDbField().equals(field)) {
                return instance;
            }
            Instance inst = getInstanceRecursively(instance, field);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    private Instance getInstanceRecursively(Instance inst, String fieldName) {
        if (inst.getFields() == null) {
            return null;
        }
        for (Instance field : inst.getFields()) {
            if (field.getDbField() != null && field.getDbField().equals(fieldName)) {
                return field;
            }
            Instance newInst = getInstanceRecursively(field, fieldName);
            if (newInst != null) {
                return newInst;
            }
        }
        return null;
    }

    private void addDocumentToEntity(Entity entity) {
        List<Instance> document = new ArrayList<>();
        for (Instance instance : entity.getDocuments().get(0)) {
            document.add(Instance.copy(instance));
        }
        entity.getDocuments().add(document);
        System.out.println("Document added to entity: " + entity.getName() + " : " + entity.getDocuments().size());
    }

    private List<Entity> getEntitiesReferencingThis(Entity entity, UserDataGlobalFormat dataFormat) {
        List<Entity> results = new ArrayList<>();
        for (Entity e : dataFormat.getCollections()) {
            List<Instance> document = e.getDocuments().get(0);
            for (Instance instance : document) {
                if (instance.isReference() && instance.getReferenceClass() != null
                        && instance.getReferenceClass().equals(entity.getNameInDb())) {
                    results.add(e);
                }
            }
        }

        return results;
    }

    private List<Entity> getEntitiesReferencedByThis(Entity entity, UserDataGlobalFormat dataFormat) {
        List<Entity> results = new ArrayList<>();
        for (Entity e : dataFormat.getCollections()) {
            List<Instance> document = entity.getDocuments().get(0);
            for (Instance instance : document) {
                if (instance.isReference() && instance.getReferenceClass() != null
                        && instance.getReferenceClass().equals(e.getNameInDb())) {
                    results.add(e);
                }
            }
        }

        return results;
    }

    private Instance getReferencingInstance(Entity entity, Entity referencedEntity) {
        List<Instance> document = entity.getDocuments().get(0);
        for (Instance instance : document) {
            if (instance.getReferenceClass() != null
                    && instance.getReferenceClass().equals(referencedEntity.getNameInDb())) {
                return instance;
            }
        }
        return null;
    }

    private String getValueOfEntityRowProperty(Entity entity, String property, int docNum) {
        List<Instance> document = entity.getDocuments().get(docNum);
        for (Instance instance : document) {
            System.out.println(
                    "Property: " + property + " instance: " + instance.getDbField() + " value: " + instance.getValue());
            if (instance.getDbField() != null && instance.getDbField().equals(property)) {
                return instance.getValue();
            }
        }
        return null;
    }

    public void mapDatabaseToGlobalFormat(UserDataGlobalFormat dataFormat) throws Exception {
        String afm = "123456789";

        Entity personEntity = getPersonEntity(dataFormat);
        Instance taxIDInstance = getTaxIDInstance(personEntity);

        // ResultSet res = getRowsFromTableWhere(personEntity.getNameInDb(),
        // taxIDInstance.getDbField(), afm);
        ResultSet res = getRowsFromTableWhere(personEntity.getNameInDb(), "afm", afm);

        int docNum = 0;
        System.out.println("Mapping database to global format");
        // System.out.println(res.getMetaData().getColumnCount());
        while (res.next()) {
            if (docNum != 0) {
                addDocumentToEntity(personEntity);
            }
            System.out.println("sdfasdfaSDFASDFsdafasdfasd");
            for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
                System.out.println(res.getMetaData().getColumnName(i) + ": " + res.getString(i));
                String columnName = res.getMetaData().getColumnName(i);
                // List<Instance> chain = getInstanceChainByDbField(personEntity, columnName);
                Instance instance = getInstanceByDbField(personEntity, personEntity.getNameInDb() + "." + columnName,
                        docNum);
                System.out.println("asdfsadf");
                String value = res.getString(i);
                System.out.println(personEntity.getNameInDb() + "." + columnName);

                if (instance != null)
                    instance.setValue(value);
            }
            docNum++;
        }
        List<Entity> iteratedEntities = new ArrayList<>();
        List<Entity> baseEntitiesList = new ArrayList<>();
        updatesEntitiesRecursively(personEntity, dataFormat, iteratedEntities, baseEntitiesList);

    }

    public void updatesEntitiesRecursively(Entity entity, UserDataGlobalFormat dataFormat,
            List<Entity> iteratedEntities, List<Entity> baseEntitiesList) throws Exception {
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
                ResultSet rs = getRowsFromTableWhere(e.getNameInDb(), instance.getDbField().split("\\.")[1], value);
                int docNum2 = e.getDocuments().size();
                while (rs.next()) {
                    addDocumentToEntity(e);
                    for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
                        String columnName = rs.getMetaData().getColumnName(j);
                        Instance inst = getInstanceByDbField(e, e.getNameInDb() + "." + columnName, docNum2);
                        String val = rs.getString(j);
                        if (inst != null) {
                            inst.setValue(val);
                        }
                    }
                    docNum2++;
                }
            }

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
                ResultSet rs = getRowsFromTableWhere(e.getNameInDb(), instance.getReferenceProperty().split("\\.")[1],
                        value);
                int docNum2 = e.getDocuments().size();

                System.out.println("Budakovaaaa: " + e.getDocuments().size());
                System.out.println("Budakovaaaa: " + docNum2);

                while (rs.next()) {
                    addDocumentToEntity(e);
                    for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
                        System.out.println("Pash babin:" + entity.getNameInDb() + ":" + e.getNameInDb() + ":"
                                + rs.getMetaData().getColumnCount());
                        String columnName = rs.getMetaData().getColumnName(j);
                        Instance inst = getInstanceByDbField(e, e.getNameInDb() + "." + columnName, docNum2);
                        String val = rs.getString(j);
                        System.out.println("Pash babin:" + entity.getNameInDb() + ":" + e.getNameInDb() + ":"
                                + columnName + " : " + val + " lujta " + docNum2);
                        if (inst != null) {
                            inst.setValue(val);
                        }
                    }
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

    public ResultSet getRowsFromTableWhere(String tableName, String columnName, String value) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        stmt = conn.createStatement();
        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = '" + value + "'";

        rs = stmt.executeQuery(query);

        return rs;
    }

}

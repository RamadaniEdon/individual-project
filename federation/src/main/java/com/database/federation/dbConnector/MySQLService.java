package com.database.federation.dbConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.database.federation.userData.Entity;
import com.database.federation.userData.Instance;
import com.database.federation.userData.UserDataGlobalFormat;

public class MySQLService extends DbService {

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
    

    public void mapDatabaseToGlobalFormat(UserDataGlobalFormat dataFormat, String userTaxId) throws Exception {
        for (Entity e : dataFormat.getCollections()) {
            e.setUserData(false);
        }
        String afm = userTaxId;

        Entity personEntity = getEntityHoldingTaxID(dataFormat);
        personEntity.setUserData(true);
        Instance taxIDInstance = getInstanceByName(personEntity, "taxID", 0);

        String dbFieldForTaxId = taxIDInstance.getDbField();
        String actualDbFieldForTaxId = dbFieldForTaxId.substring(dbFieldForTaxId.indexOf(".") + 1);
        // ResultSet res = getRowsFromTableWhere(personEntity.getNameInDb(),
        // taxIDInstance.getDbField(), afm);
        ResultSet res = getRowsFromTableWhere(personEntity.getNameInDb(), actualDbFieldForTaxId, afm);

        int docNum = 1;
        System.out.println("Mapping database to global format");
        // System.out.println(res.getMetaData().getColumnCount());

        while (res.next()) {
            addDocumentToEntity(personEntity);
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

        for (Entity e : dataFormat.getCollections()) {
            e.getDocuments().remove(0);
            removeDuplicates(e);
        }

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
            if(!e.isUserData()) e.setUserData(entity.isUserData());

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

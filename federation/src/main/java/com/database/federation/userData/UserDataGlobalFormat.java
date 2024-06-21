package com.database.federation.userData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.database.federation.dbConnector.DbService;
import com.database.federation.utils.DateParser;
import com.database.federation.utils.NumberParser;

public class UserDataGlobalFormat {
    public static DbService dbService = new DbService();
    private String databaseId;

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    private String companyName;
    private List<Entity> collections;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Entity> getCollections() {
        return collections;
    }

    public void setCollections(List<Entity> collections) {
        this.collections = collections;
    }

    public void applyFilters(UserDataFilter filter) {

        List<List<Instance>> requestedDocs = new ArrayList<>();
        boolean filterDocs = false;
        for (EntityFilter entityFilter : filter.getEntities()) {
            if (entityFilter.getValue() != null) {
                filterDocs = true;
                for (Entity entity : collections) {
                    for (List<Instance> document : entity.getDocuments()) {
                        boolean found = false;
                        if (entity.getName().equals(entityFilter.getName())) {
                            for (Instance instance : document) {
                                if (instance.getField() != null
                                        && instance.getField().equals(entityFilter.getProperty())) {

                                    if (entityFilter.isDate()) {
                                        System.out.println("Date filter: " + entityFilter.getValue());
                                        System.out.println("Date filter: " + instance.getValue());
                                        Instant filterDate = DateParser.parseDate(entityFilter.getValue());
                                        Instant dbDate = DateParser.parseDate(instance.getValue());
                                        System.out.println("Date: " + filterDate + " : " + dbDate);
                                        System.out.println("Forma: " + entityFilter.isBigger() + " : "
                                                + entityFilter.isSmaller());
                                        if (filterDate != null && dbDate != null) {
                                            if (entityFilter.isBigger() && filterDate.isBefore(dbDate)) {
                                                requestedDocs.add(document);
                                                found = true;
                                            } else if (entityFilter.isSmaller() && dbDate.isBefore(filterDate)) {
                                                requestedDocs.add(document);
                                                found = true;
                                            }

                                        }
                                    } else if (!entityFilter.isBigger() && !entityFilter.isSmaller()
                                            && instance.getValue().equals(entityFilter.getValue())
                                            && !requestedDocs.contains(document)) {
                                        requestedDocs.add(document);
                                        found = true;

                                    } else {
                                        Double filterNumber = NumberParser.parseStringToDouble(entityFilter.getValue());
                                        Double dbNumber = NumberParser.parseStringToDouble(instance.getValue());
                                        if (filterNumber != null && dbNumber != null) {
                                            if (entityFilter.isBigger() && dbNumber > filterNumber) {
                                                requestedDocs.add(document);
                                                found = true;
                                            } else if (entityFilter.isSmaller() && dbNumber < filterNumber) {
                                                requestedDocs.add(document);
                                                found = true;
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        if (!found) {
                            Instance res = findObjectPropertyInstanceByRange(document, companyName, companyName);
                            if (res != null && res.getValue() != null) {
                                if (entityFilter.isDate()) {
                                    System.out.println("Date filter: " + entityFilter.getValue());
                                    System.out.println("Date filter: " + res.getValue());
                                    Instant filterDate = DateParser.parseDate(entityFilter.getValue());
                                    Instant dbDate = DateParser.parseDate(res.getValue());
                                    System.out.println("Date: " + filterDate + " : " + dbDate);
                                    if (filterDate != null && dbDate != null) {
                                        if (entityFilter.isBigger() && filterDate.isBefore(dbDate)) {
                                            requestedDocs.add(document);
                                        } else if (entityFilter.isSmaller() && dbDate.isBefore(filterDate)) {
                                            requestedDocs.add(document);
                                        }

                                    }
                                } else if (!entityFilter.isBigger() && !entityFilter.isSmaller()
                                        && res.getValue().equals(entityFilter.getValue())
                                        && !requestedDocs.contains(document)) {
                                    requestedDocs.add(document);

                                } else {
                                    Double filterNumber = NumberParser.parseStringToDouble(entityFilter.getValue());
                                    Double dbNumber = NumberParser.parseStringToDouble(res.getValue());
                                    if (filterNumber != null && dbNumber != null) {
                                        if (entityFilter.isBigger() && dbNumber > filterNumber) {
                                            requestedDocs.add(document);
                                        } else if (entityFilter.isSmaller() && dbNumber < filterNumber) {
                                            requestedDocs.add(document);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        if (filterDocs) {
            List<List<Instance>> finalDocs = new ArrayList<>();
            System.out.println("Requested docs: " + requestedDocs.size());
            for (List<Instance> doc : requestedDocs) {
                for (Instance i : doc) {
                    System.out.println(i.getField() + " : " + i.getValue());
                }
                List<Entity> visitedEntities = new ArrayList<>();
                Entity parentEntity = findEntityContainingDocument(doc);
                List<Entity> referencingEntities = dbService.getEntitiesReferencingThis(parentEntity, this);
                for (Entity e : referencingEntities) {
                    Instance referencingInstanceTemplate = dbService.getReferencingInstance(e, parentEntity);
                    System.out.println("Referencing instance: " + referencingInstanceTemplate.getDbField());
                    for (List<Instance> document : e.getDocuments()) {
                        Instance referencingInstance = findInstanceByDbField(document,
                                referencingInstanceTemplate.getDbField());
                        System.out.println("Referencing instance value: " + referencingInstance.getDbField() + ":"
                                + referencingInstance.getValue());
                        Instance referenceInstance = findInstanceByDbField(doc,
                                referencingInstance.getReferenceProperty());
                        System.out.println("Reference instance value: " + referenceInstance.getDbField() + ":"
                                + referenceInstance.getValue());

                        if (referenceInstance.getValue().equals(referencingInstance.getValue())) {
                            finalDocs.add(document);
                        }
                    }
                }
                getLinkedDocumentsRecursively(doc, finalDocs, visitedEntities);
            }
            List<Entity> toRemove = new ArrayList<>();
            for (Entity entity : collections) {
                List<List<Instance>> newDocs = new ArrayList<>();
                for (List<Instance> docs : finalDocs) {
                    if (entity.getDocuments().contains(docs)) {
                        newDocs.add(docs);
                    }
                }
                if (newDocs.size() > 0) {
                    entity.setDocuments(newDocs);
                } else {
                    toRemove.add(entity);
                }
            }
            for (Entity entity : toRemove) {
                collections.remove(entity);
            }

        }

    }

    public void getLinkedDocumentsRecursively(List<Instance> doc, List<List<Instance>> finalDocs,
            List<Entity> visitedEntities) {
        if (finalDocs.contains(doc))
            return;
        finalDocs.add(doc);

        if (visitedEntities.contains(findEntityContainingDocument(doc)))
            return;

        Entity parentEntity = findEntityContainingDocument(doc);
        List<Entity> referencedEntities = dbService.getEntitiesReferencedByThis(parentEntity, this);
        for (Entity e : referencedEntities) {
            Instance referencingInstanceTemplate = dbService.getReferencingInstance(parentEntity, e);
            for (List<Instance> document : e.getDocuments()) {
                Instance referencingInstance = findInstanceByDbField(doc, referencingInstanceTemplate.getDbField());
                Instance referenceInstance = findInstanceByDbField(document,
                        referencingInstance.getReferenceProperty());
                if (referenceInstance.getValue().equals(referenceInstance.getValue())) {
                    getLinkedDocumentsRecursively(document, finalDocs, visitedEntities);
                }
            }

        }

    }

    public Instance findInstanceByDbField(List<Instance> docs, String dbField) {
        for (Instance instance : docs) {
            if (instance.getDbField() != null && instance.getDbField().equals(dbField)) {
                return instance;
            }
            if (instance.getFields() != null) {
                Instance result = findInstanceByDbField(instance.getFields(), dbField);
                if (result != null)
                    return result;
            }
        }
        return null;
    }

    public Entity findEntityContainingDocument(List<Instance> document) {
        for (Entity entity : collections) {
            for (List<Instance> doc : entity.getDocuments()) {
                if (doc.equals(document)) {
                    return entity;
                }
            }
        }
        return null;
    }

    public Instance findObjectPropertyInstanceByRange(List<Instance> docs, String rangeClass, String property) {
        for (Instance instance : docs) {
            if (instance.getRange() != null && instance.getRange().equals(rangeClass)) {
                for (Instance field : instance.getFields()) {
                    if (field.getField() != null && field.getField().equals(property)) {
                        return field;
                    }
                }
            }
            if (instance.getFields() != null) {
                Instance result = findObjectPropertyInstanceByRange(instance.getFields(), rangeClass, property);
                if (result != null)
                    return result;
            }
        }
        return null;
    }

}

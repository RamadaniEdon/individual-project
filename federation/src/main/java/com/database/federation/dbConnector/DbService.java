package com.database.federation.dbConnector;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import com.database.federation.userData.Entity;
import com.database.federation.userData.Instance;
import com.database.federation.userData.UserDataGlobalFormat;

public class DbService {

    public Entity getEntityHoldingTaxID(UserDataGlobalFormat format) {
        for (Entity entity : format.getCollections()) {
            Instance identifier = getInstanceByName(entity, "taxID", 0);
            if(identifier != null){
                return entity;
            }
        }
        return null;
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

    public void addDocumentToEntity(Entity entity) {
        List<Instance> document = new ArrayList<>();
        for (Instance instance : entity.getDocuments().get(0)) {
            document.add(Instance.copy(instance));
        }
        entity.getDocuments().add(document);
        System.out.println("Document added to entity: " + entity.getName() + " : " + entity.getDocuments().size());
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

    public Instance getInstanceByName(Entity entity, String field, int docNum) {
        List<Instance> document = entity.getDocuments().get(docNum);
        for (Instance instance : document) {
            if (instance.getField() != null && instance.getField().equals(field)) {
                return instance;
            }
            Instance inst = getInstanceByNameRecursively(instance, field);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    private Instance getInstanceByNameRecursively(Instance inst, String fieldName) {
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

    public void removeDuplicates(Entity e) {
        List<List<Instance>> toBeRemoved = new ArrayList<>();
        for (int i = 0; i < e.getDocuments().size(); i++) {
            List<Instance> document = e.getDocuments().get(i);
            for (Instance instance : document) {
                System.out.println("Checking for duplicates in " + e.getNameInDb() + " : " + instance.getField());
                if (instance.getField() != null && instance.getField().equals("identifier")) {
                    String value = instance.getValue();
                    // Check for duplicate in the rest of the documents
                    for (int j = i + 1; j < e.getDocuments().size(); j++) {
                        List<Instance> otherDocument = e.getDocuments().get(j);
                        if (otherDocument != document) { // Don't compare the document with itself
                            for (Instance otherInstance : otherDocument) {
                                if (otherInstance.getField() != null && otherInstance.getField().equals("identifier")
                                        && otherInstance.getValue() != null
                                        && otherInstance.getValue().equals(value)) {
                                    // Found a duplicate, remove the document
                                    // otherDocumentIterator.remove();
                                    toBeRemoved.add(otherDocument);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (List<Instance> document : toBeRemoved) {
            e.getDocuments().remove(document);
        }
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

    public List<Instance> getInstancesRecursively(Instance inst, String fieldName, List<Instance> chain) {
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

    public List<Entity> getEntitiesReferencingThis(Entity entity, UserDataGlobalFormat dataFormat) {
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

    public List<Entity> getEntitiesReferencedByThis(Entity entity, UserDataGlobalFormat dataFormat) {
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

    public Instance getReferencingInstance(Entity entity, Entity referencedEntity) {
        List<Instance> document = entity.getDocuments().get(0);
        for (Instance instance : document) {
            if (instance.getReferenceClass() != null
                    && instance.getReferenceClass().equals(referencedEntity.getNameInDb())) {
                return instance;
            }
        }
        return null;
    }

    public String getValueOfEntityRowProperty(Entity entity, String property, int docNum) {
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
}

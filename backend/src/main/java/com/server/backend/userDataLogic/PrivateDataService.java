package com.server.backend.userDataLogic;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PrivateDataService {

  private final PrivateDataRepository privateDataRepository;
  private final PrivacyClassRepository privacyClassRepository;

  @Autowired
  public PrivateDataService(PrivateDataRepository privateDataRepository, PrivacyClassRepository privacyClassRepository) {
    this.privateDataRepository = privateDataRepository;
    this.privacyClassRepository = privacyClassRepository;
  }

  public void addNewPrivacyClass(PrivacyClass privacyClass) {
    privacyClassRepository.save(privacyClass);
  }

  public void addNewPrivateData(PrivateData userData) {
    privateDataRepository.save(userData);
  }

  public List<PrivateData> getPrivateData() {
    return privateDataRepository.findAll();
  }

  public List<PrivacyClass> getPrivacyClasses() {
    return privacyClassRepository.findAll();
  }

  //update the contents of a privateData with a new one
  public void updateUser(PrivateData privateData, String id) {
    privateData.setId(id);
    privateDataRepository.save(privateData);
  }

  //find privateData by dbId and table
  public List<PrivateData> getPrivateDataByDbIdAndTable(int dbId, String table) {
    return privateDataRepository.findByDbIdAndTable("" +dbId, table);
  }

  public void filterTable(List<Map<String, Object>> tableResult, int dbId, String tableName, String primaryKey) {
    System.out.println("DBID: " + dbId + " TABLE: " + tableName + " PRIMARYKEY: " + primaryKey);
    List<PrivateData> privateDataList = getPrivateDataByDbIdAndTable(dbId, tableName);
    System.out.println("PrivateDataList: " + privateDataList.toString());
    for (Map<String, Object> row : tableResult) {
      for (PrivateData privateData : privateDataList) {
        System.out.println(row.get(primaryKey).toString() +  " is not equal to " + privateData.getPrimaryColumnValue().toString());
        if (row.get(primaryKey).toString().equals(privateData.getPrimaryColumnValue().toString())) {
          row.put(privateData.getColumn(), "PRIVATE");
        }
      }
    }
  }
  
  
}
package com.server.backend.userDataLogic;

import java.util.List;

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
  
  
}
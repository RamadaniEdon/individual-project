package com.server.backend.ontologyLogic;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.server.backend.components.DatabaseComponent;
import com.server.backend.components.DatabaseComponent.Column;
import com.server.backend.components.DatabaseComponent.Table;
import com.server.backend.utils.OntologyHelpers;

public class OntologyRepository {

  public OntologyRepository() {
  }

  public void mapDatabase(DatabaseComponent db, int dbId) {

    try {
      OntologyHelpers.reloadOntologyManager();
      OntologyHelpers.reloadOntology();
      OntologyHelpers.reloadNewOntology();
      for (Table table : db.getTables()) {
        String newType = db.getUrl() + "#" + table.getName();
        if(table.getMeaning() != null && !table.getMeaning().equals("")){
          OntologyHelpers.addNewSubclassType(newType,table.getMeaning());
        }
        for (Column column : table.getColumns()) {
          if(column.getMeaning() != null && !column.getMeaning().equals("")) {
            String newProperty = newType + "."+ column.getName();
            OntologyHelpers.addNewEquivalentProperty(table.getMeaning(), newProperty, column.getMeaning());
          }
        }
      }
      OntologyHelpers.saveNewOntology(dbId);
    } catch (OWLOntologyCreationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (OWLOntologyStorageException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

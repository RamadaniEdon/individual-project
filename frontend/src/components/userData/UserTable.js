import React from "react";
import {
  Box,
} from '@chakra-ui/react'

import { isDataTypeProperty, getReferenceClassProperty } from "../../utils/helpers";
import UserCell from "./UserCell";

//possibly check better if data holds any documents or not
const UserTable = ({ data, all }) => {

  const privateOption = data.userData;


  const generateRow = (row) => {
    return <div className="row">
      {row.fields.map((property) => {
        if (isDataTypeProperty(property)) {
          return <p>{property.field}</p>
        }
        else {
          return <div className="column">
            <p>{property.field}</p>
            {generateRow(property)}
          </div>
        }
      })}
    </div>
  }

  const printValuesOfObjectProperty = (objProp, rowId) => {
    return objProp.fields.map((property) => {
      if (isDataTypeProperty(property)) {
        return <UserCell data={property} userData={privateOption} rowId={rowId} database={all} collection={data}/>
       
      }
      else {
        return printValuesOfObjectProperty(objProp, rowId)
      }
    })
  }

  return (
    <Box m={3} className="Table">
      <h1>{data.name}</h1>

      {/*possibly check better if data holds any documents or not */}
      <div className="tabletable">
        {data.documents[0] && data.documents[0].map((property) => {
          let columnTitle;
          if (property.reference) {
            const propertyName = getReferenceClassProperty(all, property.referenceClass, property.referenceProperty)
            return <div className="column">
              <p>{propertyName}</p>
            </div>
          }
          else if (isDataTypeProperty(property)) {
            return <div className="column">
              <p>{property.field}</p>
            </div>
          }
          else {
            return <div className="column">
              <p>{property.field}</p>
              {generateRow(property)}
            </div>
          }
        })}
      </div>
      <div className="column data">
        {data.documents && data.documents.map((row) => {
          let rowId;
          row.forEach((property) => {
            if (property.field == "identifier") {
              rowId = property.value
            }
          })
          return <div className="row">
            {row?.map((property) => {
              if (isDataTypeProperty(property)) {
                return <UserCell data={property} userData={privateOption} rowId={rowId} database={all} collection={data}/>
                
              }
              else {
                return printValuesOfObjectProperty(property, rowId)
              }
            })}
          </div>
        })}

      </div>


    </Box>
  );
}

export default UserTable;

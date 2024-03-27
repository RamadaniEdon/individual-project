import React, { useState, useEffect } from "react";
import { Table, Thead, Tbody, Tr, Th, Td } from "@chakra-ui/react";
import { Card, CardBody, CardHeader, Heading, Flex, Text, Button } from "@chakra-ui/react";
import {
  FormControl,
  FormLabel,
  FormErrorMessage,
  FormHelperText,
  Input,
  Select,
  Box,
} from '@chakra-ui/react'

import { isDataTypeProperty } from "../../utils/helpers";

//possibly check better if data holds any documents or not
const UserTable = ({ data }) => {

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

  const printValuesOfObjectProperty = (objProp) => {
    return objProp.fields.map((property) => {
      if (isDataTypeProperty(property)) {
        return <div className="column dataValue">
          <p>{property.value}</p>
          {privateOption && <button>Make Private</button>}
        </div>
      }
      else {
        return printValuesOfObjectProperty(objProp)
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
          if (isDataTypeProperty(property)) {
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
          return <div className="row">
            {row.map((property) => {
              if (isDataTypeProperty(property)) {
                return <div className="column dataValue">
                  <p>{property.value}</p>
                  {privateOption && <button>Make Private</button>}
                </div>
              }
              else {
                return printValuesOfObjectProperty(property)
              }
            })}
          </div>
        })}

      </div>


    </Box>
  );
}

export default UserTable;

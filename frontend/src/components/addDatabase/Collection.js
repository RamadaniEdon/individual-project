import React, { useState, useEffect } from "react";
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
import Field from './Field';
import { generateLayerColor } from "../../utils/helpers";




const Collection = ({ data, me, color }) => {
  const { formData, setFormData } = data;
  const newLayerColor = generateLayerColor(color);
  

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    me[name] = value;
    setFormData({...formData});
  }


  return (
    <Box m={3} className="Collection">
      <Card width="100%" maxW="4xl" style={{ backgroundColor: newLayerColor,}}>
        <CardHeader justifyContent={'left'}>
          <Heading size='md' alignContent={'left'}>Collection: {me.collectionName ? me.collectionName : "NULL"}</Heading>
        </CardHeader>

        <CardBody width="100%">
          <FormControl isRequired>
            <FormLabel>Collection Name</FormLabel>
            <Input name="collectionName" placeholder='Collection Name' onChange={handleInputChange}/>
          </FormControl>
          <FormControl isRequired>
            <FormLabel>Meaning</FormLabel>
            <Input name="meaning" placeholder='meaning' onChange={handleInputChange}/>
          </FormControl>

          {me.fields?.map((field, i) => {
            return (
              <Field key={i} data={{formData, setFormData}} me={me.fields[i]} color={newLayerColor}/>
            );
          })}

          <Button onClick={() => {
            if(!me.fields) {
              me.fields = [];
            }
            me.fields.push({});
            setFormData({...formData});
          }}>Add Field</Button>

        </CardBody>
      </Card>
    </Box>
  );
}

export default Collection;

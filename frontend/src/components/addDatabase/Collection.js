import React, { useState, useEffect, useContext } from "react";
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
import { default as CustomSelect } from 'react-select';
import Field from './Field';
import { generateLayerColor } from "../../utils/helpers";
import { EntityContext } from "../../context/EntitiesContext";



const Collection = ({ data, me, color }) => {
  const { entities } = useContext(EntityContext);
  console.log(entities)
  const { formData, setFormData } = data;
  const newLayerColor = generateLayerColor(color);


  const handleInputChange = (e) => {
    const { name, value } = e.target;
    me[name] = value;
    setFormData({ ...formData });
  }


  return (
    <Box m={3} className="Collection">
      <Card width="100%" maxW="4xl" style={{ backgroundColor: newLayerColor, }}>
        <CardHeader justifyContent={'left'}>
          <Heading size='md' alignContent={'left'}>Collection: {me.collectionName ? me.collectionName : "NULL"}</Heading>
        </CardHeader>

        <CardBody width="100%">
          <FormControl isRequired>
            <FormLabel>Collection Name</FormLabel>
            <Input name="collectionName" placeholder='Collection Name' onChange={handleInputChange} />
          </FormControl>
          <FormControl isRequired>
            <FormLabel>Meaning</FormLabel>
            {/* <Input name="meaning" placeholder='meaning' onChange={handleInputChange} /> */}
            <CustomSelect
              value={me.meaning ? { value: me.meaning, label: me.meaning } : undefined}
              onChange={(e) => handleInputChange({ target: { name: "meaning", value: e?.value } })}
              options={entities.map((entity) => {
                return { value: entity.className, label: entity.className };
              })}
              isClearable={true}
              isSearchable={true}
              placeholder="Select Meaning"

            />
          </FormControl>

          {me.fields?.map((field, i) => {
            return (
              <Field key={i} data={{ formData, setFormData }} me={me.fields[i]} color={newLayerColor} className={me.meaning}/>
            );
          })}

          <Button onClick={() => {
            if (!me.fields) {
              me.fields = [];
            }
            me.fields.push({});
            setFormData({ ...formData });
          }}>Add Field</Button>

        </CardBody>
      </Card>
    </Box>
  );
}

export default Collection;

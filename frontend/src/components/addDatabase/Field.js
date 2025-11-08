import React, { useState, useEffect, useContext } from "react";
import { Card, CardBody, CardHeader, Heading, Flex, Text, Button, Checkbox } from "@chakra-ui/react";
import {
  FormControl,
  FormLabel,
  FormErrorMessage,
  FormHelperText,
  Input,
  Select,
  Box,
  Radio,
  RadioGroup,
  Stack,
} from '@chakra-ui/react'
import { default as CustomSelect } from 'react-select';
import { generateLayerColor } from "../../utils/helpers";
import { EntityContext } from "../../context/EntitiesContext";


const Field = ({ data, me, color, className }) => {
  const { getClass } = useContext(EntityContext);
  const { formData, setFormData } = data;
  const [fieldType, setFieldType] = useState(0);
  const newLayerColor = generateLayerColor(color);
  const [checkboxValue, setCheckboxValue] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    me[name] = value;
    setFormData({ ...formData });
  }


  return (
    <Box m={3} className="Collection">
      <Card width="100%" maxW="4xl" style={{ backgroundColor: newLayerColor, }} >
        <CardHeader justifyContent={'left'}>
          <Heading size='md' alignContent={'left'}>Field: {me.name}</Heading>
        </CardHeader>





        <CardBody width="100%">
          <RadioGroup onChange={(e) => {
            console.log("une jom Eja the boy: ", e)
            setFieldType(e)
            handleInputChange({ target: { name: "fieldType", value: e } })
          }} value={fieldType}>
            <Stack direction='row'>
              <Radio value="0">Datatype Property</Radio>
              <Radio value="1">Object Property</Radio>
              <Radio value="2">Reference Property</Radio>
              {formData.dbType == "nosql" && fieldType == 1 && <>
                <Checkbox isChecked={checkboxValue} onChange={(e) => setCheckboxValue(e.target.checked)}>
                  Is Object In Database
                </Checkbox>
              </>}
            </Stack>
          </RadioGroup>
          {fieldType == 2 && <>
            <FormControl isRequired>
              <FormLabel>{"Field Name"}</FormLabel>
              <Input name="name" placeholder='Field Name' onChange={handleInputChange} />
            </FormControl>
            <FormControl isRequired>
              <FormLabel>{"rangeClass"}</FormLabel>
              <Input name="rangeClass" placeholder='Foreign Class' onChange={handleInputChange} />
            </FormControl>
            <FormControl isRequired>
              <FormLabel>{"foreignKey"}</FormLabel>
              <Input name="foreignKey" placeholder='Foreign Field' onChange={handleInputChange} />
            </FormControl>

          </>}
          {fieldType < 2 && <>
            <FormControl isRequired>
              <FormLabel>{fieldType == 0 ? 'Field Name' : 'Property Name'}</FormLabel>
              {fieldType == 0 ?
                <Input name="name" placeholder='Field Name' onChange={handleInputChange} />
                :
                (formData.dbType == "nosql" && fieldType == 1 && checkboxValue ?
                  <Input name="name" placeholder='Field Name' onChange={handleInputChange} />
                  :
                  <CustomSelect
                    value={me.name ? { value: me.name, label: me.name } : undefined}
                    onChange={(e) => handleInputChange({ target: { name: "name", value: e?.value } })}
                    options={
                      getClass(className)?.properties.map((property) => {
                        return { value: property.propertyName, label: property.propertyName };
                      })}
                    isClearable={true}
                    isSearchable={true}
                    placeholder="Select Property"

                  />
                )

              }
            </FormControl>
            {formData.dbType == "nosql" && fieldType == 1 && checkboxValue && <>
              <CustomSelect
                value={me.meaning ? { value: me.meaning, label: me.meaning } : undefined}
                onChange={(e) => handleInputChange({ target: { name: "meaning", value: e?.value } })}
                options={
                  getClass(className)?.properties.map((property) => {
                    return { value: property.propertyName, label: property.propertyName };
                  })}
                isClearable={true}
                isSearchable={true}
                placeholder="Select Meaning"

              />
            </>}
            <FormControl isRequired>
              <FormLabel>{fieldType == 0 ? 'Meaning' : 'Property'}</FormLabel>
              {fieldType == 0 ?
                <CustomSelect
                  value={me.meaning ? { value: me.meaning, label: me.meaning } : undefined}
                  onChange={(e) => handleInputChange({ target: { name: "meaning", value: e?.value } })}
                  options={
                    getClass(className)?.properties.map((property) => {
                      return { value: property.propertyName, label: property.propertyName };
                    })}
                  isClearable={true}
                  isSearchable={true}
                  placeholder="Select Meaning"

                />
                :
                <CustomSelect
                  value={me.rangeClass ? { value: me.rangeClass, label: me.rangeClass } : undefined}
                  onChange={(e) => handleInputChange({ target: { name: "rangeClass", value: e?.value } })}
                  options={
                    getClass(className)?.properties.find((property) => {
                      if (formData.dbType == "nosql" && fieldType == 1 && checkboxValue) {
                        return property.propertyName == me.meaning
                      }
                      return property.propertyName == me.name
                    })?.range.map((rangeClass) => {
                      return { value: rangeClass, label: rangeClass };
                    })}
                  isClearable={true}
                  isSearchable={true}
                  placeholder="Select Range Class"

                />
              }
            </FormControl>
            {fieldType == 1 &&
              me.fields?.map((field, i) => {
                return (
                  <Field key={i} data={{ formData, setFormData }} me={me.fields[i]} color={newLayerColor} className={me.rangeClass} />
                );
              })
            }
            {fieldType == 1 && <Button onClick={() => {
              if (!me.fields) {
                me.fields = [];
              }
              me.fields.push({});
              setFormData({ ...formData });
            }}>Add Field for this Class</Button>}
          </>}

        </CardBody>
      </Card>

    </Box>
  );
}

export default Field;

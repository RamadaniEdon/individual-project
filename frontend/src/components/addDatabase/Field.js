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
  Radio,
  RadioGroup,
  Stack,
} from '@chakra-ui/react'

import { generateLayerColor } from "../../utils/helpers";



const Field = ({ data, me, color }) => {
  const { formData, setFormData } = data;
  const [property, setProperty] = useState('datatype');
  const newLayerColor = generateLayerColor(color);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    me[name] = value;
    setFormData({ ...formData });
  }


  return (
    <Box m={3} className="Collection">
      <Card width="100%" maxW="4xl" style={{ backgroundColor: newLayerColor,}} >
        <CardHeader justifyContent={'left'}>
          <Heading size='md' alignContent={'left'}>Field: NULL</Heading>
        </CardHeader>

        <CardBody width="100%">
          <RadioGroup onChange={setProperty} value={property}>
            <Stack direction='row'>
              <Radio value='datatype'>Datatype Property</Radio>
              <Radio value='object'>Object Property</Radio>
            </Stack>
          </RadioGroup>
          <FormControl isRequired>
            <FormLabel>{property == 'datatype' ? 'Field Name' : 'Class Name'}</FormLabel>
            <Input name="name" placeholder='Field Name' onChange={handleInputChange} />
          </FormControl>
          <FormControl isRequired>
            <FormLabel>{property == 'datatype' ? 'Meaning' : 'Property'}</FormLabel>
            <Input name="meaning" placeholder='meaning' onChange={handleInputChange} />
          </FormControl>
          {property == 'object' &&
            me.fields?.map((field, i) => {
              return (
                <Field key={i} data={{ formData, setFormData }} me={me.fields[i]} color={newLayerColor}/>
              );
            })
          }
          {property == 'object' && <Button onClick={() => {
            if (!me.fields) {
              me.fields = [];
            }
            me.fields.push({});
            setFormData({ ...formData });
          }}>Add Field for this Class</Button>}

        </CardBody>
      </Card>

    </Box>
  );
}

export default Field;

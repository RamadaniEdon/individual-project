import React, { useState, useEffect } from "react";
import { Box, Card, CardBody, CardHeader, Heading, Flex, Text, Button } from "@chakra-ui/react";
import {
  FormControl,
  FormLabel,
  FormErrorMessage,
  FormHelperText,
  Input,
  Select,

} from '@chakra-ui/react'

import Collection from '../components/addDatabase/Collection';



const AddDatabase = () => {
  const [formData, setFormData] = useState({});

  const newLayerColor = 'hsl(0, 0%, 96%)';

  const renderedCollections = [];

  if (formData.collections) {
    for (let i = 0; i < formData.collections.length; i++) {
      renderedCollections.push(<Collection key={i} me={formData.collections[i]} data={{formData, setFormData}} color={newLayerColor}/>);
    }
  }

  
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  }

  return (
    <div className="AddDatabase">
      <Flex mt="10" width="100%" justify="center" >
        <Card width="100%" maxW="4xl" style={{ backgroundColor: newLayerColor,}}>
          <CardHeader>
            <Heading size='md'>Add Database Form</Heading>
          </CardHeader>

          <CardBody width="100%">
            <FormControl isRequired>
              <FormLabel>Company Name</FormLabel>
              <Input name="companyName" onChange={handleInputChange} placeholder='Company Name' />
            </FormControl>
            <FormControl isRequired>
              <FormLabel>Database URL</FormLabel>
              <Input name="url" onChange={handleInputChange} placeholder='Database URL' />
            </FormControl>
            <FormControl isRequired>
              <FormLabel>Database Name</FormLabel>
              <Input name="dbName" onChange={handleInputChange} placeholder='Database Name' />
            </FormControl>
            <FormControl isRequired>
              <FormLabel>Username for database</FormLabel>
              <Input name="dbUsername" onChange={handleInputChange} placeholder='Username for database' />
            </FormControl>
            <FormControl isRequired>
              <FormLabel>Password for database</FormLabel>
              <Input name="dbPassword" onChange={handleInputChange} placeholder='Password for database' />
            </FormControl>
            <FormControl>
              <FormLabel>Database Type</FormLabel>
              <Select name="dbType" onChange={handleInputChange} placeholder='Select database type'>
                <option>mysql</option>
                <option>mongodb</option>
              </Select>
            </FormControl>
            {renderedCollections}
            <Button onClick={() => {
              if(!formData.collections) {
                setFormData({ ...formData, collections: [{}] });
                return;
              }
              const newFormData = { ...formData, collections: [...formData.collections, {}] };
              setFormData(newFormData);
            }}>Click Me</Button>
            <Button onClick={() => {
              console.log(formData);
            }}>Submit</Button>


          </CardBody>
        </Card>
      </Flex>
    </div>
  );
}

export default AddDatabase;

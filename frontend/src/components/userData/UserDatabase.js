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

import UserTable from "./UserTable";


const UserCollection = ({ data }) => {




  return (
    <Box m={3} className="Database">
      <h1>{data.companyName}</h1>
      {data.collections.map((table) => {
        return <UserTable data={table} />
      })}
    </Box>
  );
}

export default UserCollection;

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



const UserCell = ({ data }) => {
  



  return (
    <Box m={3} className="UserCell">
      <h1>{data.companyName}</h1>
      <Table variant="striped" colorScheme="teadal">
            <Thead>
              <Tr>
                <Th>Column 1</Th>
                <Th>Column 2</Th>
                <Th>Column 3</Th>
                <Th>Column 4</Th>
                <Th>Column 5</Th>
                <Th>Column 6</Th>
              </Tr>
            </Thead>
            <Tbody>
              <Tr>
                <Td rowSpan={3}>Cell 1</Td>
                <Td colSpan={3}>Cell 2</Td>
                {/* <Td>Cell 3</Td> */}
                {/* <Td>Cell 4</Td> */}
                <Td>Cell 5</Td>
                <Td>Cell 6</Td>
              </Tr>
              <Tr>
                <Td>Cell 1</Td>
                <Td rowSpan={2}>Cell 2</Td>
                <Td>Cell 3</Td>
                <Td>Cell 4</Td>
                <Td>Cell 5</Td>
                {/* <Td>Cell 6</Td> */}
              </Tr>
              <Tr>
                <Td>Cell 1</Td>
                <Td>Cell 2</Td>
                <Td>Cell 3</Td>
                <Td>Cell 4</Td>
                {/* <Td>Cell 5</Td> */}
                {/* <Td>Cell 6</Td> */}
              </Tr>
            </Tbody>
          </Table>
    </Box>
  );
}

export default UserCell;

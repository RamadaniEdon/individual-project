import React, { useState, useEffect, useContext } from "react";
import { Button, Modal, ModalOverlay, ModalContent, ModalHeader, ModalCloseButton, ModalBody, ModalFooter, useDisclosure, Box, List, Text, FormControl, FormLabel, Input, RadioGroup, Stack, Radio, Checkbox, Select } from "@chakra-ui/react";

import { getUserDataFromDb, getUserCategories, createCategory, getDatabases, filterUserData } from "../services/api";
import Database from "../components/userData/UserDatabase"
import { AuthContext } from "../context/AuthContext";
import { EntityContext } from "../context/EntitiesContext";
import { sortObjectsByAttribute } from "../utils/helpers";
import { default as CustomSelect } from 'react-select';

function UserData() {
  const { token } = useContext(AuthContext)
  const { entities, getClass } = useContext(EntityContext);
  const [userData, setUserData] = useState([])
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null)
  const [newCategory, setNewCategory] = useState({ categoryName: "", categoryPrice: "", categoryAccessControl: "" });
  const [updatedCategory, setUpdatedCategory] = useState({ categoryName: "", categoryPrice: "", categoryAccessControl: "" });
  const { isOpen, onOpen, onClose } = useDisclosure()
  const { isOpen: isCreateOpen, onOpen: onCreateOpen, onClose: onCreateClose } = useDisclosure()
  const { isOpen: isUpdateOpen, onOpen: onUpdateOpen, onClose: onUpdateClose } = useDisclosure()
  const [filters, setFilters] = useState([{ entityName: '', propertyName: '', value: '', comparisonType: 'equal', isDate: false }]);
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [selectedDatabase, setSelectedDatabase] = useState(null);
  const [databases, setDatabases] = useState([
    // { value: 'database1', label: 'Database 1' },
    // { value: 'database2', label: 'Database 2' },
    // { value: 'database3', label: 'Database 3' },
  ])
  const onFilterOpen = () => setIsFilterOpen(true);
  const onFilterClose = () => setIsFilterOpen(false);



  const addNewFilter = () => {
    setFilters([...filters, { entityName: '', propertyName: '', value: '', comparisonType: 'equal', isDate: false }]);
  };

  useEffect(() => {
    if (!isFilterOpen) {
      setFilters([{ entityName: '', propertyName: '', value: '', comparisonType: 'equal', isDate: false }]);
    }
  }, [isFilterOpen])

  useEffect(() => {
    getUserDataFromDb(token).then((data) => {
      data.forEach((database) => {
        sortObjectsByAttribute(database.collections, "name");
      });
      console.log(data)
      setUserData(data)
    })
    getUserCategories(token).then((categories) => {
      setCategories(categories)
      setSelectedCategory(categories?.[0] || null)
    });
    getDatabases(token).then((databases) => {
      setDatabases(databases.map((db) => ({ value: db.id, label: db.companyName })));
    });
  }, [token])

  const handleCategoryClick = (category) => {
    setSelectedCategory(category)
    setUpdatedCategory(category);
  }

  const handleInputChange = (event) => {
    setNewCategory({ ...newCategory, [event.target.name]: event.target.value });
  }

  const handleUpdateInputChange = (event) => {
    setUpdatedCategory({ ...updatedCategory, [event.target.name]: event.target.value });
  }

  const handleSubmit = () => {
    const newcategory = { ...newCategory };
    createCategory(token, newCategory).then(() => {
      setCategories([...categories, newcategory]);
      onCreateClose();
    });
  }

  const handleUpdate = () => {
    const updatedcategory = { ...updatedCategory };
    createCategory(token, updatedCategory).then(() => {
      const index = categories.findIndex((cat) => cat.categoryName === updatedcategory.categoryName);
      categories[index].categoryPrice = updatedCategory.categoryPrice;
      categories[index].categoryAccessControl = updatedCategory.categoryAccessControl;
      setCategories([...categories]);
      onUpdateClose();
    });
  }

  const handleComparisonTypeChange = (index, value) => {
    const newFilters = [...filters];
    newFilters[index].comparisonType = value;
    setFilters(newFilters);
  };

  const handleFilterChange = (filter, key, value) => {
    const newFilter = { ...filter, [key]: value };
    setFilters([...filters.map((f) => f === filter ? newFilter : f)]);

  };

  const handleCheckboxChange = (filter) => {
    const newFilter = { ...filter, isDate: !filter.isDate };
    setFilters([...filters.map((f) => f === filter ? newFilter : f)]);
    console.log([...filters.map((f) => f === filter ? newFilter : f)])
  }

  const handleChange = (selectedOption) => {
    setSelectedDatabase(selectedOption);
  };

  const onFilterSubmit = () => {
    console.log(filters);

    filterUserData(token, selectedDatabase?.value, filters).then((filteredData) => {
      setUserData(filteredData);
      onFilterClose();
    });

  }

  return (
    <div className="UserData">
      <Button onClick={onOpen} position="absolute" top={5} left={5}>
        Open Modal
      </Button>

      <Button position="absolute" top={5} right={5} onClick={onFilterOpen}>
        Filter
      </Button>

      <Modal isOpen={isFilterOpen} onClose={onFilterClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Filter Search</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <div>
              <CustomSelect
                value={selectedDatabase}
                onChange={handleChange}
                options={databases}
                isClearable={true}
                isSearchable={true}
                placeholder="Select database"
                mb={"100px"}
              />
            </div>
            <Box mb={4} />
            {filters.map((filter, index) => {
              const entityOptions = entities.map((entity) => ({ value: entity.className, label: entity.className }));

              let propertyOptions = [];
              console.log(filter)
              if (filter.entityName) {
                const entity = getClass(filter.entityName);

                propertyOptions = entity?.properties?.map((property) => ({ value: property.propertyName, label: property.propertyName }));
              }
              return (
                <Box key={index} mb={4}>
                  <CustomSelect
                    value={filter.entityName ? { value: filter.entityName, label: filter.entityName } : undefined}
                    onChange={(e) => handleFilterChange(filter, "entityName", e?.value)}
                    options={entityOptions}
                    isClearable={true}
                    isSearchable={true}
                    placeholder="Select Entity"
                  />
                  <CustomSelect
                    value={filter.propertyName ? { value: filter.propertyName, label: filter.propertyName } : undefined}
                    onChange={(e) => handleFilterChange(filter, "propertyName", e?.value)}
                    options={propertyOptions}
                    isClearable={true}
                    isSearchable={true}
                    placeholder="Select Property"

                  />


                  {/* <Input placeholder="Entity Name" value={filter.entityName} onChange={(e) => handleFilterChange(filter, "entityName", e.target.value)} />
                  <Input placeholder="Property Name" value={filter.propertyName} onChange={(e) => handleFilterChange(filter, "propertyName", e.target.value)} /> */}
                  <Input
                    type={filter.isDate ? "date" : "text"}
                    placeholder="Value"
                    value={filter.value}
                    onChange={(e) => handleFilterChange(filter, "value", e.target.value)}
                  />
                  <RadioGroup defaultValue="equal" onChange={(value) => handleComparisonTypeChange(index, value)}>
                    <Stack direction="row">
                      <Radio value="bigger">Bigger</Radio>
                      <Radio value="smaller">Smaller</Radio>
                      <Radio value="equal">Equal</Radio>
                    </Stack>
                  </RadioGroup>
                  <Checkbox
                    checked={filter.isDate}
                    onChange={() => handleCheckboxChange(filter)}
                  >
                    Is Date
                  </Checkbox>
                </Box>
              )
            })}
            <Button onClick={addNewFilter}>Add Another Filter</Button>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={onFilterSubmit}>
              Search
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent maxW="50%">
          <ModalHeader>Categories</ModalHeader>
          <ModalCloseButton />
          <ModalBody display="flex">
            <Box w="50%">
              <Button onClick={onCreateOpen}>Create New Category</Button>
              <List spacing={3}>
                {categories.map((category, index) => (
                  <Box
                    as="li"
                    key={index}
                    py={2}
                    px={4}
                    rounded="md"
                    _hover={{ bg: "gray.200", cursor: "pointer" }}
                    onClick={() => handleCategoryClick(category)}
                  >
                    {category.categoryName}
                  </Box>
                ))}
              </List>
            </Box>
            <Box w="50%">
              {selectedCategory && (
                <>
                  <Text>Category Name: {selectedCategory.categoryName}</Text>
                  <Text>Price: {selectedCategory.categoryPrice}</Text>
                  <Text>Access Control: {selectedCategory.categoryAccessControl}</Text>
                </>
              )}
            </Box>
          </ModalBody>
          <ModalFooter>
            <Button onClick={onUpdateOpen} disabled={!selectedCategory}>Update Selected Category</Button>

            <Button colorScheme="blue" mr={3} onClick={onClose}>
              Close
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      <Modal isOpen={isCreateOpen} onClose={onCreateClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Create New Category</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <form>
              <FormControl>
                <FormLabel>Category Name</FormLabel>
                <Input type="text" name="categoryName" value={newCategory.categoryName} onChange={handleInputChange} />
              </FormControl>
              <FormControl>
                <FormLabel>Category Price</FormLabel>
                <Input type="text" name="categoryPrice" value={newCategory.categoryPrice} onChange={handleInputChange} />
              </FormControl>
              <FormControl>
                <FormLabel>Access Control</FormLabel>
                <Input type="text" name="categoryAccessControl" value={newCategory.categoryAccessControl} onChange={handleInputChange} />
              </FormControl>
            </form>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={handleSubmit}>
              Submit
            </Button>
            <Button onClick={onCreateClose}>Cancel</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      <Modal isOpen={isUpdateOpen} onClose={onUpdateClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Update Category</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <form>
              <FormControl>
                <FormLabel>Category Name</FormLabel>
                <Input type="text" name="categoryName" value={updatedCategory.categoryName} onChange={handleUpdateInputChange} />
              </FormControl>
              <FormControl>
                <FormLabel>Category Price</FormLabel>
                <Input type="text" name="categoryPrice" value={updatedCategory.categoryPrice} onChange={handleUpdateInputChange} />
              </FormControl>
              <FormControl>
                <FormLabel>Access Control</FormLabel>
                <Input type="text" name="categoryAccessControl" value={updatedCategory.categoryAccessControl} onChange={handleUpdateInputChange} />
              </FormControl>
            </form>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={handleUpdate}>
              Update
            </Button>
            <Button onClick={onUpdateClose}>Cancel</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {userData.length !== 0 && userData.map((database) => {
        return <Database data={database} />
      })}
    </div>
  );
}

export default UserData;
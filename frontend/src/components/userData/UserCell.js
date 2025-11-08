import React, { useEffect, useState, useContext } from 'react';
import { Menu, MenuButton, MenuList, MenuItem, Button, useToast } from "@chakra-ui/react";
import { AuthContext } from '../../context/AuthContext';
import { getUserCategories, changeCategoryForData } from '../../services/api';

const UserCell = ({ data, userData, rowId, database, collection }) => {
  const {token} = useContext(AuthContext)
  const [selectedOption, setSelectedOption] = useState(data.categoryName || 'Public');
  const [categories, setCategories] = useState([]);
  const toast = useToast();

  useEffect(() => {
    // fetch categories from server
    getUserCategories(token).then((categories) => {
      setCategories(categories);
    });
  }, [token]);

  const updateCategoryForData = (categoryName) => {
    changeCategoryForData(token, collection.nameInDb, rowId, data.dbField, categoryName, database.databaseId).then(() => {
      setSelectedOption(categoryName);
      toast({
        title: "Category changed.",
        description: `The category has been changed to ${categoryName}.`,
        status: "success",
        duration: 5000,
        isClosable: true,
      });
    }).catch(() => {
      toast({
        title: "An error occurred.",
        description: "The category could not be changed.",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    });
  }

  return (
    <div className="column dataValue">
      <p>{data.value}</p>
      {userData && <Menu>
        <MenuButton as={Button}>
          {selectedOption}
        </MenuButton>
        <MenuList>
          {categories.map((category) => <MenuItem onClick={() => updateCategoryForData(category.categoryName)}>{category.categoryName}</MenuItem>)}
        </MenuList>
      </Menu>}
    </div>
  );
};

export default UserCell;
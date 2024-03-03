// AppBody.js
import React from 'react';
import { Route, Routes } from 'react-router-dom';

const AddDatabase = React.lazy(() => import("../pages/AddDatabase"));
const Login = React.lazy(() => import("../pages/Login"));
const UserData = React.lazy(() => import("../pages/UserData"));

const AppBody = () => {
  return (
    <div>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/userdata" element={<UserData />} />
        <Route path="/database" element={<AddDatabase />} />
      </Routes>
    </div>
  );
};

export default AppBody;
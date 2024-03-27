import React, { useState, useEffect } from "react";

import { getUserDataFromDb } from "../services/api";
import Database from "../components/userData/UserDatabase"

function UserData() {

  const [userData, setUserData] = useState([])

  useEffect(() => {
    getUserDataFromDb("").then((data) => {
      setUserData(data)
    })
  }, [userData])

  return (
    <div className="UserData">
      {userData.length !== 0 && userData.map((database) => {
        return <Database data={database} />
      })}
    </div>
  );
}

export default UserData;

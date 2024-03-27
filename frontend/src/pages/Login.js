import React, { useEffect } from "react";
import { testBackend } from "../services/api";



function Login() {

  const [ user, setUser ] = React.useState(null);

  useEffect(() => {
    testBackend().then((data) => {
      setUser(data);
    });
  }, [user]);

  return (
    <div className="Login">
      Hello from Login {user}
    </div>
  );
}

export default Login;
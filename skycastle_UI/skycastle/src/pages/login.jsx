import React from "react";
// import { useForm } from 'react-hook-form'


// file imports
import Tabs from "../components/tabs/tabs";


// file imports
import Input from "../components/inputs/input";
import Button from "../components/buttons/button";


const Login = () => {

  // const { register, handleSubmit, watch, errors } = useForm();

  const handleSubmit = () => {
    console.log('submitted for login!!!');
  }

  return (
    <div>
      <h1>Account Login</h1>
      <form onSubmit={handleSubmit}>
        <Tabs>
          <div label="Buyer">
            <div className="form-body">
              <Input type="email" name="email" placeholder="Email/Username" />
              <Input type="password" name="password" placeholder="Password" />
              <Button type="submit" name="login" value="Login" />
            </div>
          </div>
          <div label="Seller">
            <div className="form-body">
              <Input type="email" name="email" placeholder="Email/Username" />
              <Input type="password" name="password" placeholder="Password" />
              <Input type="text" name="country" placeholder="Country" />
              <Button type="submit" name="login" value="Login" />
            </div>
          </div>
        </Tabs>

      </form>
    </div>
  );
}

export default Login;
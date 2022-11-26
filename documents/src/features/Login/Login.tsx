import React from "react";
import { useForm } from "react-hook-form";
import { Redirect, useHistory } from "react-router-dom";
import { LoginRequest } from "../../models/LoginRequest";
import { Button } from "../../shared/components/Button/Button";
import Input from "../../shared/components/Input/Input";
import styles from "../Login/Login.module.css";
import { login } from "../../api/services/users";
const Login = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<LoginRequest>();
  const history = useHistory();
  const onSubmit = async ({ username, password }: LoginRequest) => {
    const data = {
      username,
      password,
    };

    try {
      const response = await login(JSON.stringify(data));

      if (response.status === 200) {
        const res = await response.json();
        const token = res.token;
        const role = res.user.role;
        if (token !== undefined) {
          localStorage.setItem("TOKEN", token);
          if (role === "admin") history.push("/system-admin");
          // const { token, ...user } = res;
        } else {
          history.push("/login");
        }
      }
    } catch (err) {
      console.log(err);
      reset();
    }
  };

  return (
    <div className={styles.container}>
      <h1>Sign in</h1>
      <form
        className={styles.form}
        onSubmit={handleSubmit((data) => onSubmit(data))}
      >
        <Input
          placeholder="Username"
          icon="user"
          type="text"
          {...register("username", {
            required: true,
          })}
        />
        <Input
          placeholder="Password"
          icon="password"
          type="password"
          {...register("password", {
            required: true,
          })}
        />
        <Button children="LOGIN" type="submit" />
      </form>
    </div>
  );
};

export default Login;

import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { Redirect, useHistory } from "react-router-dom";
import { LoginRequest } from "../../models/LoginRequest";
import styles from "../Login/Login.module.css";
import { checkCode, login } from "../../api/services/users";
import QrCode from "../QrCode/QrCode";
import { Button } from "../../shared/components/Button/Button";
import Input from "../../shared/components/Input/Input";
const Login = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<any>();
  const history = useHistory();
  const [qrImageUrl, setQrImageUrl] = useState("");
  const [showCodeInputField, setShowCodeInputField] = useState(false);
  const [username, setUserName] = useState("");
  const [loginError, setLoginError] = useState("");
  const onSubmit = async ({ username, password }: LoginRequest) => {
    const data = {
      username,
      password,
    };
    setUserName(username);
    //resetuj eror pri sljedecem submitu
    setLoginError("");
    console.log(data);
    try {
      const response = await login(JSON.stringify(data));
      let respData = await response.json();
      if (respData.status !== 404) {
        // const token = res.token;
        // //const role = res.user.role;
        // if (token !== undefined) {
        //   localStorage.setItem("USER", JSON.stringify(res));
        //   history.push("/documents");
        //   // const { token, ...user } = res;
        // } else {
        //   history.push("/login");
        // }
        setShowCodeInputField(false);
        setQrImageUrl(respData.message);
      } else {
        setLoginError(respData.message);
      }
    } catch (err) {
      console.log(err);
      reset();
    }
  };
  const onCodeEnter = async ({ code }: any) => {
    const data = {
      code,
      username,
    };
    console.log(data);
    try {
      const response = await checkCode(JSON.stringify(data));

      if (response.status === 200) {
        console.log(response);
        const res = await response.json();
        console.log(res);
        const token = res.token;
        //const role = res.user.role;
        if (token !== undefined) {
          localStorage.setItem("USER", JSON.stringify(res));
          history.push("/system-admin");
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
      {qrImageUrl !== "" && (
        <QrCode
          image={qrImageUrl}
          onClick={() => {
            setQrImageUrl("");
            setShowCodeInputField(true);
          }}
        ></QrCode>
      )}
      {showCodeInputField && (
        <div className={styles.codecontainer}>
          <form
            className={styles.codeform}
            onSubmit={handleSubmit((data) => onCodeEnter(data))}
          >
            <Input
              icon={"noicon"}
              placeholder={"Enter a code"}
              {...register("code", {
                required: true,
              })}
            ></Input>
            <Button children="SUBMIT" type="add" />
          </form>
        </div>
      )}
      <p className={styles.errorMsg}>{loginError}</p>
    </div>
  );
};

export default Login;

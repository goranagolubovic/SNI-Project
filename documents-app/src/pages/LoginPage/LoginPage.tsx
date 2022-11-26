import React from "react";
import Login from "../../features/Login/Login";
import Header from "../../layouts/Header/Header";
import Input from "../../components/Input/Input";
import styles from "../LoginPage/LoginPage.module.css";
const LoginPage = () => {
  return (
    <div className={styles.login}>
      <Header />
      <Login />
    </div>
  );
};

export default LoginPage;

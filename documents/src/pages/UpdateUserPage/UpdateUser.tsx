import React, { useEffect, useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import PrivilegiesContainer from "../../features/PrivilegiesContainer/PrivilegiesContainer";
import { AddUserRequest } from "../../models/AddUserRequest";
import { Button } from "../../shared/components/Button/Button";
import Input from "../../shared/components/Input/Input";
import { PasswordInput } from "../../shared/components/PasswordInput/PasswordInput";
import Select from "../../shared/components/Select/Select";
import styles from "../AddUserPage/AddUser.module.css";
import ErrorComponent from "../../shared/components/ErrorComponent/ErrorComponent";
import { useHistory, useLocation } from "react-router-dom";
import { add, getUser, updateUser } from "../../api/services/users";
import {
  ADD_FAILED,
  TOKEN_EXPIRED,
  UPDATE_FAILED,
} from "../../validation_errors";
export interface UpdateUserProps {
  username: string;
}
const UpdateUser = () => {
  const methods = useForm<AddUserRequest>({ mode: "onChange" });
  const [selectedRole, setSelectedRole] = useState("");
  const [updateError, setUpdateError] = useState(false);
  const [updatingFailed, setUpdatingFailed] = useState(false);
  const [username, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [ipAddress, setIpAddress] = useState("");
  const [userDir, setUserDir] = useState("");
  const [role, setRole] = useState("");
  const [isCreateApproved, setCreateApproved] = useState(0);
  const [isReadApproced, setReadApproved] = useState(0);
  const [isUpdateApproved, setUpdateApproved] = useState(0);
  const [isDeleteApproved, setDeleteApproved] = useState(0);

  const location = useLocation();

  const fetchUser = async (username: string) => {
    try {
      const response = await getUser(username);
      if (response.status === 200) {
        const data = await response.json();
        setUserName(data.username);
        setPassword(data.password);
        setRole(data.role);
        setIpAddress(data.ipAddress);
        setUserDir(data.userDir);
        setIsCreateAllowed(data.isCreateApproved);
        setIsReadAllowed(data.isReadApproved);
        setIsUpdateAllowed(data.isUpdateApproved);
        setIsDeleteAllowed(data.isDeleteApproved);
      } else if (response.status === 401) {
        setUpdateError(true);
      } else {
        setUpdatingFailed(true);
      }
    } catch (error) {
      // setRegistrationFailed(true);
    }
  };
  useEffect(() => {
    fetchUser(location.pathname.split("/")[3]);
  }, []);
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = methods;
  const [isClient, setIsClient] = useState(false);
  const [isCreateAllowed, setIsCreateAllowed] = useState(false);
  const [isReadAllowed, setIsReadAllowed] = useState(false);
  const [isUpdateAllowed, setIsUpdateAllowed] = useState(false);
  const [isDeleteAllowed, setIsDeleteAllowed] = useState(false);

  const history = useHistory();
  const roles = ["admin", "document_admin", "client"];
  const resetCRUDPrivilegies = () => {
    setIsCreateAllowed(false);
    setIsReadAllowed(false);
    setIsUpdateAllowed(false);
    setIsDeleteAllowed(false);
  };
  const changeCreateAllowedValue = (value: boolean) => {
    setIsCreateAllowed(value);
  };
  const changeReadAllowedValue = (value: boolean) => {
    setIsReadAllowed(value);
  };
  const changeUpdateAllowedValue = (value: boolean) => {
    setIsUpdateAllowed(value);
  };
  const changeDeleteAllowedValue = (value: boolean) => {
    setIsDeleteAllowed(value);
  };
  const onSubmit = async (userData: AddUserRequest) => {
    if (userData.role === "") {
      userData.role = role;
    }
    if (userData.username === "") {
      userData.username = username;
    }
    if (userData.role === "client") {
      userData.isReadApproved = isReadAllowed ? 1 : 0;
      userData.isCreateApproved = isCreateAllowed ? 1 : 0;
      userData.isUpdateApproved = isUpdateAllowed ? 1 : 0;
      userData.isDeleteApproved = isDeleteAllowed ? 1 : 0;
    }
    if (userData.userDir === "") {
      userData.userDir = userDir;
    }
    alert(JSON.stringify(userData));
    try {
      const response = await updateUser(JSON.stringify(userData));
      if (response.status === 200) {
        history.push("/system-admin");
      } else if (response.status === 401) {
        setUpdateError(true);
      } else {
        setUpdatingFailed(true);
      }
    } catch (error) {
      // setRegistrationFailed(true);
    }
  };
  const resetForm = () => {
    setUpdatingFailed(false);
  };
  return (
    <div className={styles.container}>
      <FormProvider {...methods}>
        <form
          id="form"
          className={styles.content}
          onSubmit={handleSubmit(onSubmit)}
          onFocus={resetForm}
        >
          <h1 className={styles.addUser}>Edit user</h1>

          <Input
            icon="noicon"
            value={username}
            placeholder="username"
            className={
              errors.username ? styles.componentWithError : styles.component
            }
            {...register("username")}
          />
          {errors.username?.type && (
            <ErrorComponent name="Username" type={errors.username?.type} />
          )}

          <PasswordInput
            button={"visible"}
            placeholder={"*****"}
            icon="noicon"
            className={
              errors.password ? styles.componentWithError : styles.component
            }
            {...register("password")}
          />
          {errors.password?.type && (
            <ErrorComponent name="Password" type={errors.password?.type} />
          )}

          <Input
            icon="noicon"
            placeholder={ipAddress === "" ? "Ip address" : ipAddress}
            className={
              errors.ipAddress ? styles.componentWithError : styles.component
            }
            {...register("ipAddress")}
          />
          {errors.ipAddress?.type && (
            <ErrorComponent name="Ip address" type={errors.username?.type} />
          )}
          <Input
            icon="noicon"
            placeholder={userDir === "" ? "Home directory" : userDir}
            className={
              errors.userDir ? styles.componentWithError : styles.component
            }
            {...register("userDir")}
          />
          {errors.userDir?.type && (
            <ErrorComponent name="Home directory" type={errors.userDir?.type} />
          )}
          <Select
            text={role}
            values={roles}
            //sendData={getData}
            className={
              errors.role ? styles.componentWithError : styles.component
            }
            {...register("role", {
              onChange: (e) => {
                setRole(e.target.value);
                resetCRUDPrivilegies();
              },
            })}
          />
          {errors.role?.type && (
            <ErrorComponent name="Role" type={errors.role?.type} />
          )}
          {role === "client" && (
            <PrivilegiesContainer
              readAllowed={changeReadAllowedValue}
              createAllowed={changeCreateAllowedValue}
              updateAllowed={changeUpdateAllowedValue}
              deleteAllowed={changeDeleteAllowedValue}
              checkedCreate={isCreateAllowed}
              checkedRead={isReadAllowed}
              checkedUpdate={isUpdateAllowed}
              checkedDelete={isDeleteAllowed}
            ></PrivilegiesContainer>
          )}

          {updateError && (
            <ErrorComponent
              name="Add Error"
              type={TOKEN_EXPIRED}
              className={styles.error}
            />
          )}
          {updatingFailed && (
            <ErrorComponent name="Update failed" type={UPDATE_FAILED} />
          )}
          <div className={styles.buttonContainer}>
            <Button
              type="submit"
              children="CHANGE"
              width="150px"
              className={
                updateError ? styles.registerBtnError : styles.registerBtn
              }
            />
            <Button
              type="cancel"
              children="CANCEL"
              width="150px"
              onClick={() => {
                history.push("/system-admin");
              }}
              className={
                updateError ? styles.registerBtnError : styles.registerBtn
              }
            />
          </div>
        </form>
      </FormProvider>
    </div>
  );
};

export default UpdateUser;

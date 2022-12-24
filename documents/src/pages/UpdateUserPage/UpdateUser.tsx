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
import { formatUserDir } from "../../util";
import { INITIAL_DIR, NOT_AUTHORIZED, SESSION_EXPIRED } from "../../constants";
export interface UpdateUserProps {
  username: string;
}
const UpdateUser = () => {
  const methods = useForm<AddUserRequest>({ mode: "onChange" });
  const [selectedRole, setSelectedRole] = useState("");
  const [updateError, setUpdateError] = useState("");
  const [updatingFailed, setUpdatingFailed] = useState(false);
  const [username, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [ipAddress, setIpAddress] = useState("");
  const [startDir, setStartDir] = useState("");
  const [role, setRole] = useState("");
  const [isCreateApproved, setCreateApproved] = useState(0);
  const [isReadApproced, setReadApproved] = useState(0);
  const [isUpdateApproved, setUpdateApproved] = useState(0);
  const [isDeleteApproved, setDeleteApproved] = useState(0);

  const location = useLocation();

  const fetchUser = async (username: string) => {
    try {
      const response = await getUser(username);
      const data = await response.json();
      if (data.status === undefined) {
        setUserName(data.username);
        setPassword(data.password);
        setRole(data.role);
        setIpAddress(data.ipAddress);
        setStartDir(data.userDir + "/");
        setIsCreateAllowed(data.isCreateApproved);
        setIsReadAllowed(data.isReadApproved);
        setIsUpdateAllowed(data.isUpdateApproved);
        setIsDeleteAllowed(data.isDeleteApproved);
      } else if (
        data.status === 404 ||
        data.status === 409 ||
        data.status === 500
      ) {
        setUpdateError(data.message);
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
    if (userData.role === "document_admin") {
      userData.ipAddress = "";
    }
    userData.userDir = formatUserDir(startDir + userData.userDir);
    if (userData.role === "admin") {
      userData.ipAddress = "";
      userData.userDir = INITIAL_DIR;
    }
    const { password, ...user } = userData;
    const updatedData = {
      user: user,
      password: password,
    };
    alert(JSON.stringify(updatedData));
    try {
      const response = await updateUser(JSON.stringify(updatedData));
      if (response.status === 401) {
        setUpdateError(SESSION_EXPIRED);
      } else if (response.status === 403) {
        setUpdateError(NOT_AUTHORIZED);
      } else {
        const responseData = await response.json();
        if (responseData.status === 200) {
          history.push("/system-admin");
        }
        if (responseData.status === 409) {
          setUpdateError(responseData.message);
        } else if (responseData.status === 404 || responseData.status === 500) {
          setUpdateError(responseData.message);
        } else {
          setUpdatingFailed(true);
        }
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
            placeholder={"Password"}
            icon="noicon"
            className={
              errors.password ? styles.componentWithError : styles.component
            }
            {...register("password", {
              required: true,
            })}
          />
          {errors.password?.type && (
            <ErrorComponent name="Password" type={errors.password?.type} />
          )}

          {role !== "admin" && role !== "document_admin" && (
            <div>
              <Input
                icon="noicon"
                placeholder={ipAddress === "" ? "Ip address" : ipAddress}
                className={
                  errors.ipAddress
                    ? styles.componentWithError
                    : styles.component
                }
                {...register("ipAddress")}
              />
              {errors.ipAddress?.type && (
                <ErrorComponent
                  name="Ip address"
                  type={errors.username?.type}
                />
              )}
            </div>
          )}
          {role !== "admin" && (
            <div className={styles.home_dir}>
              <p className={styles.dir}>{startDir}</p>
              <Input
                icon="noicon"
                placeholder="Home directory"
                className={
                  errors.userDir ? styles.componentWithError : styles.component
                }
                {...register("userDir")}
              />
              {errors.userDir?.type && (
                <ErrorComponent
                  name="Home directory"
                  type={errors.userDir?.type}
                />
              )}
            </div>
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

          {updateError !== "" && (
            <ErrorComponent
              // type={TOKEN_EXPIRED}
              name={updateError}
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

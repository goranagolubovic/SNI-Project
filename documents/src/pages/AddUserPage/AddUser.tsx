import React, { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import PrivilegiesContainer from "../../features/PrivilegiesContainer/PrivilegiesContainer";
import { AddUserRequest } from "../../models/AddUserRequest";
import { Button } from "../../shared/components/Button/Button";
import Input from "../../shared/components/Input/Input";
import { PasswordInput } from "../../shared/components/PasswordInput/PasswordInput";
import Select from "../../shared/components/Select/Select";
import styles from "../AddUserPage/AddUser.module.css";
import ErrorComponent from "../../shared/components/ErrorComponent/ErrorComponent";
import { useHistory } from "react-router-dom";
import { add } from "../../api/services/users";
import { ADD_FAILED, TOKEN_EXPIRED } from "../../validation_errors";
import { INITIAL_DIR, NOT_AUTHORIZED, SESSION_EXPIRED } from "../../constants";
import { formatUserDir } from "../../util";
export interface AddUserProps {
  isActionAdding: boolean;
}
const AddUser = () => {
  const methods = useForm<AddUserRequest>({ mode: "onChange" });
  const [selectedRole, setSelectedRole] = useState("");
  const [addError, setAddError] = useState("");
  const [addingFailed, setAddingFailed] = useState(false);
  const [username, setUsername] = useState("");
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = methods;
  const [isClient, setIsClient] = useState(true);
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
  const resetForm = () => {
    setAddingFailed(false);
  };
  const onSubmit = async (userData: AddUserRequest) => {
    setAddError("");
    if (userData.role === "client") {
      userData.isReadApproved = isReadAllowed ? 1 : 0;
      userData.isCreateApproved = isCreateAllowed ? 1 : 0;
      userData.isUpdateApproved = isUpdateAllowed ? 1 : 0;
      userData.isDeleteApproved = isDeleteAllowed ? 1 : 0;
    }
    if (userData.role === "document_admin") {
      userData.ipAddress = "";
    }
    userData.userDir = INITIAL_DIR + username + "/" + userData.userDir;
    userData.userDir = formatUserDir(userData.userDir);
    if (userData.role === "admin") {
      userData.ipAddress = "";
      userData.userDir = INITIAL_DIR;
    }

    const { password, ...user } = userData;
    const data = {
      user: user,
      password: password,
    };
    alert(JSON.stringify(data));
    try {
      const response = await add(JSON.stringify(data));
      if (response.status === 401) {
        setAddError(SESSION_EXPIRED);
      } else if (response.status === 403) {
        setAddError(NOT_AUTHORIZED);
      } else {
        const responseData = await response.json();
        if (responseData.status === 200) {
          history.push("/system-admin");
        }
        if (responseData.status === 409) {
          setAddError(responseData.message);
        } else if (responseData.status === 404 || responseData.status === 500) {
          setAddError(responseData.message);
        } else {
          setAddingFailed(true);
        }
      }
    } catch (error) {
      // setRegistrationFailed(true);
    }
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
          <h1 className={styles.addUser}>Add user</h1>

          <Input
            icon="noicon"
            placeholder="Username"
            onInput={(e: any) => setUsername(e.target.value)}
            className={
              errors.username ? styles.componentWithError : styles.component
            }
            {...register("username", {
              required: true,
              minLength: 2,
              maxLength: 100,
            })}
          />
          {errors.username?.type && (
            <ErrorComponent name="Username" type={errors.username?.type} />
          )}

          <PasswordInput
            placeholder="Password"
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

          {selectedRole !== "admin" && selectedRole !== "document_admin" && (
            <div>
              <Input
                icon="noicon"
                placeholder="Ip address"
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
          {selectedRole !== "admin" && (
            <div className={styles.home_dir}>
              <p className={styles.dir}>
                {INITIAL_DIR}
                {username}/
              </p>
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
            text="Choose a role"
            values={roles}
            //sendData={getData}
            className={
              errors.role ? styles.componentWithError : styles.component
            }
            {...register("role", {
              onChange: (e) => {
                setSelectedRole(e.target.value);
                resetCRUDPrivilegies();
              },
            })}
          />
          {errors.role?.type && (
            <ErrorComponent name="Role" type={errors.role?.type} />
          )}
          {selectedRole === "client" && (
            <PrivilegiesContainer
              readAllowed={changeReadAllowedValue}
              createAllowed={changeCreateAllowedValue}
              updateAllowed={changeUpdateAllowedValue}
              deleteAllowed={changeDeleteAllowedValue}
            ></PrivilegiesContainer>
          )}

          <ErrorComponent name={addError} />
          <div className={styles.buttonContainer}>
            <Button
              type="submit"
              children="ADD USER"
              width="150px"
              className={
                addError ? styles.registerBtnError : styles.registerBtn
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
                addError ? styles.registerBtnError : styles.registerBtn
              }
            />
          </div>
        </form>
      </FormProvider>
    </div>
  );
};

export default AddUser;

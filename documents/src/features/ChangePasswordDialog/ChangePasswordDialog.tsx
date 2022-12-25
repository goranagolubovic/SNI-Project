import React, { useState } from "react";
import styles from "./ChangePasswordDialog.module.css";
import { FormProvider, useForm } from "react-hook-form";
import { editPassword } from "../../api/services/users";
import { getUsername } from "../../util";
import { PasswordInput } from "../../shared/components/PasswordInput/PasswordInput";
import { Button } from "../../shared/components/Button/Button";
import { ChangePasswordRequest } from "../../models/ChangePasswordRequest";
import ErrorComponent from "../../shared/components/ErrorComponent/ErrorComponent";
export interface ChangePasswordDialogProps {
  onClick?: (data: any) => void;
  stateChanger: any;
}

const ChangePasswordDialog = ({
  onClick,
  stateChanger,
}: ChangePasswordDialogProps) => {
  const methods = useForm<ChangePasswordRequest>({ mode: "onChange" });
  const [changed, setChanged] = useState(false);
  const [message, setMessage] = useState("");
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = methods;
  const onSubmit = async (data: ChangePasswordRequest) => {
    const request = {
      username: getUsername(),
      password: data.password,
    };
    try {
      let res = await editPassword(JSON.stringify(request));
      let resData = await res.json();
      setMessage(resData.message);
      if (resData.status === 200) setChanged(true);
    } catch (err) {
      console.log(err);
    }
  };
  return (
    <div className={styles.container}>
      <FormProvider {...methods}>
        <form
          id="form"
          className={styles.content}
          onSubmit={handleSubmit(onSubmit)}
        >
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

          <PasswordInput
            placeholder="Confirm Password"
            className={
              errors.confirm ? styles.componentWithError : styles.component
            }
            {...register("confirm", {
              required: true,
              pattern: new RegExp(watch("password")),
            })}
          />
          {errors.confirm?.type && (
            <ErrorComponent
              name="Confirm password"
              type={errors.confirm?.type}
            />
          )}
          <p>{message}</p>
          <Button width="15vw" type="submit" children="SAVE" />
        </form>
      </FormProvider>
      <Button
        type="link"
        children="NEXT"
        onClick={() => stateChanger(changed)}
      />
    </div>
  );
};

export default ChangePasswordDialog;

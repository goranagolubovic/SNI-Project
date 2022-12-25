import React from "react";
import { INVALID_FORM, NOT_MATCH, REQUIRED } from "../../../validation_errors";
import styles from "./ErrorComponent.module.css";
import classNames from "classnames";

export interface ErrorProps {
  name: string;
  type?: string;
  className?: string;
}

const checkType = (name: string, type?: string) => {
  if (type === "required") {
    return name + REQUIRED;
  } else if (
    type === "minLength" ||
    type === "maxLength" ||
    (type === "pattern" && name !== "Confirm password")
  ) {
    return name + INVALID_FORM;
  } else if (name === "Confirm password") {
    return NOT_MATCH;
  } else {
    return name;
  }
};

const ErrorComponent = ({ name, type, className = "" }: ErrorProps) => {
  return (
    <p className={classNames(styles.error, className)}>
      {checkType(name, type)}
    </p>
  );
};

export default ErrorComponent;

import classNames from "classnames";
import React, { useState } from "react";
import styles from "./PasswordInput.module.css";

export type ButtonStyle = "visible" | "invisible";

export interface PasswordProps
  extends React.InputHTMLAttributes<HTMLInputElement> {
  id?: string;
  children?: React.ReactNode;
  width?: string;
}

export const PasswordInput = React.forwardRef(
  ({ id, width, className = "", ...rest }: PasswordProps, ref: any) => {
    return (
      <div className={classNames(styles.container, className)}>
        <input
          type="password"
          width={width}
          ref={ref}
          id={id}
          autoComplete="off"
          className={classNames(styles.input, className)}
          {...rest}
        />
      </div>
    );
  }
);

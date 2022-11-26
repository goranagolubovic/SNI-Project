import React from "react";
import styles from "./Input.module.css";
import classNames from "classnames";
export type InputType = "user" | "password" | "noicon";

const inputTypeClasses: Record<InputType, string> = {
  user: styles.user,
  password: styles.password,
  noicon: styles.noicon,
};
export interface InputProps
  extends React.DetailedHTMLProps<
    React.InputHTMLAttributes<HTMLInputElement>,
    HTMLInputElement
  > {
  icon: InputType;
  placeholder: string;
  type?: string;
  className?: string;
  width?: number;
}

const Input = React.forwardRef(
  (
    { icon, placeholder, type, className, width, ...rest }: InputProps,
    ref: any
  ) => {
    return (
      <div className={classNames(styles.container, className)}>
        <input
          placeholder={placeholder}
          className={classNames(styles.input, inputTypeClasses[icon])}
          ref={ref}
          type={type}
          autoComplete="off"
          width={width}
          {...rest}
        />
      </div>
    );
  }
);

export default Input;

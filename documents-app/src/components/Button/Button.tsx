import classNames from "classnames";
import React from "react";
import styles from "./Button.module.css";

export type ButtonType =
  | "submit"
  | "table"
  | "add"
  | "cancel"
  | "true"
  | "false";

const buttonTypeClasses: Record<ButtonType, string> = {
  submit: styles.submit,
  table: styles.table,
  add: styles.add,
  cancel: styles.cancel,
  true: styles.true,
  false: styles.false,
};

export interface ButtonProps
  extends Omit<
    React.DetailedHTMLProps<
      React.ButtonHTMLAttributes<HTMLButtonElement>,
      HTMLButtonElement
    >,
    "type"
  > {
  children?: React.ReactNode;
  width?: string;
  height?: string;
  type: ButtonType;
  onClick?: (data: any) => void;
}

export const Button = ({
  width,
  height,
  type,
  children,
  className = "",
  onClick,
  ...rest
}: ButtonProps) => {
  return (
    <button
      className={classNames(styles.button, buttonTypeClasses[type], className)}
      style={{ width: width, height: height }}
      onClick={onClick}
      {...rest}
    >
      {children}
    </button>
  );
};

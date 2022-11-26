import classNames from "classnames";
import React, { useState } from "react";
import styles from "./PasswordInput.module.css";

export type ButtonStyle = "visible" | "invisible";

const buttonStyle: Record<ButtonStyle, string> = {
  visible: styles.visible,
  invisible: styles.invisible,
};

export interface PasswordProps
  extends React.InputHTMLAttributes<HTMLInputElement> {
  id?: string;
  children?: React.ReactNode;
  width?: string;
  button: ButtonStyle;
  icon?: string;
}

export const PasswordInput = React.forwardRef(
  (
    { id, width, button, icon = "", className = "", ...rest }: PasswordProps,
    ref: any
  ) => {
    const [visible, setVisible] = useState(false);

    return (
      <div className={classNames(styles.container, className)}>
        <input
          width={width}
          ref={ref}
          id={id}
          autoComplete="off"
          className={classNames(styles.input, className)}
          type={visible ? "text" : "password"}
          style={{ width: width, background: `url("${icon}") no-repeat 10px` }}
          {...rest}
        />
        <button
          type="button"
          className={classNames(
            styles.button,
            visible ? buttonStyle["visible"] : buttonStyle["invisible"]
          )}
          onClick={() => setVisible(!visible)}
        ></button>
      </div>
    );
  }
);

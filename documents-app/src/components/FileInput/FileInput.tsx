import React from "react";
import styles from "./FileInput.module.css";

export interface FileInputProps
  extends React.DetailedHTMLProps<
    React.InputHTMLAttributes<HTMLInputElement>,
    HTMLInputElement
  > {
  placeholder: string;
}
const FileInput = React.forwardRef(
  ({ placeholder, ...rest }: FileInputProps, ref: any) => {
    return (
      <div>
        <input
          className={styles.inputStyle}
          placeholder={placeholder}
          ref={ref}
          type="text"
          {...rest}
        ></input>
      </div>
    );
  }
);

export default FileInput;

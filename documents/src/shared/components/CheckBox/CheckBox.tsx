import { StyleSharp } from "@mui/icons-material";
import React, { useState } from "react";
import styles from "../CheckBox/CheckBox.module.css";

export interface CheckBoxProps
  extends React.DetailedHTMLProps<
    React.InputHTMLAttributes<HTMLInputElement>,
    HTMLInputElement
  > {
  text?: string;
  className?: string;
  onChecked: (value: boolean) => void;
  checked?: boolean;
}

const CheckBox = ({ text, onChecked, checked }: CheckBoxProps) => {
  const [readValue, setReadValue] = useState(false);
  const handleChange = (event: any) => {
    const a = event.target.checked;
    console.log("check" + a);
    setReadValue(a);
    onChecked(event.target.checked);
  };
  return (
    <div className={styles.container}>
      <input type="checkbox" onChange={handleChange} checked={checked}></input>
      <p className={styles.content}>{text}</p>
    </div>
  );
};

export default CheckBox;

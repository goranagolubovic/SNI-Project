import React, { useState } from "react";
import styles from "./Select.module.css";
import classNames from "classnames";

export interface SelectProps
  extends React.SelectHTMLAttributes<HTMLSelectElement> {
  id?: string;
  text?: string;
  icon?: string;
  width?: string;
  values: string[] | number[];
  className: string;
  disabled?: boolean;
  // sendData: (data: string) => void;
  onChange: any;
}

const Select = React.forwardRef(
  (
    {
      text = "",
      icon = "",
      width,
      name,
      className = "",
      values,
      disabled = false,
      // sendData,
      onChange,
      ...rest
    }: SelectProps,
    ref: any
  ) => {
    const [selectedValue, setSelectedValue] = useState(text);
    console.log(selectedValue);
    const handleChange = (value: string) => {
      setSelectedValue(value);
      console.log("selecteed" + value);
      //sendData(selected);
    };
    return (
      <>
        <select
          className={classNames(styles.select, className)}
          style={{
            width: width,
            background: `url("${icon}") no-repeat 10px`,
            backgroundSize: "14px",
          }}
          ref={ref}
          name={name}
          disabled={disabled}
          onChange={onChange}
          {...rest}
        >
          {values.map((value: any, index: any) => {
            if (value === text) {
              return (
                <option value={value} selected defaultChecked key={index}>
                  {value}
                </option>
              );
            } else {
              return (
                <option value={value} key={index}>
                  {value}
                </option>
              );
            }
          })}
          <option value="" selected defaultChecked>
            {text}
          </option>
        </select>
      </>
    );
  }
);

export default Select;

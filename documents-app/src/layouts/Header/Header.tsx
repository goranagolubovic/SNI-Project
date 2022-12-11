import React from "react";
import styles from "../Header/Header.module.css";
export interface HeaderProps {
  text: string;
}
const Header = ({ text }: HeaderProps) => {
  return <div className={styles.header}>{text}</div>;
};

export default Header;

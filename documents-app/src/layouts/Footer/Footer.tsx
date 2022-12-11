import React from "react";
import styles from "./Footer.module.css";
export interface FooterProps {
  link: string;
  text: string;
}
const Footer = ({ link, text }: FooterProps) => {
  return (
    <div className={styles.container}>
      <a href={link}>{text}</a>
    </div>
  );
};

export default Footer;

import { FaceRetouchingNaturalOutlined } from "@mui/icons-material";
import { getRoles } from "@testing-library/react";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import { fetchRole } from "../../api/services/users";
import { getUsername } from "../../util";
import styles from "./Footer.module.css";
export interface FooterProps {
  link: string;
  text: string;
}
const Footer = ({ link, text }: FooterProps) => {
  const [role, setRole] = useState("");
  useEffect(() => {
    getRole();
  }, []);
  const getRole = async () => {
    const response = await fetchRole(getUsername());
    const data = await response.json();
    if (response.status === 401 || response.status === 403) {
      alert(data.message);
    } else {
      setRole(data.message);
    }
  };
  return (
    <div className={styles.container}>
      {role === "admin" && <a href={link}>{text}</a>}
    </div>
  );
};

export default Footer;

import React from "react";
import styles from "../SystemAdminPage/SystemAdmin.module.css";
import Header from "../../layouts/Header/Header";
import Table from "../../features/Table/Table";
import { Button } from "../../shared/components/Button/Button";
import PersonAdd from "@mui/icons-material/PersonAdd";
import { useHistory } from "react-router-dom";
const SystemAdmin = () => {
  const history = useHistory();
  return (
    <div className={styles.admin}>
      <Header />
      <div className={styles.addButton}>
        <Button
          type="add"
          width="150px"
          height="35px"
          onClick={() => history.push("/system-admin/create-user")}
        >
          <PersonAdd style={{ color: "white", fontSize: 20 }}></PersonAdd> ADD
          USER
        </Button>
      </div>
      <Table />
    </div>
  );
};

export default SystemAdmin;

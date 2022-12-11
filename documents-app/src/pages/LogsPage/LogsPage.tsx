import { ArrowBack } from "@mui/icons-material";
import React from "react";
import { useHistory } from "react-router-dom";
import { Button } from "../../components/Button/Button";
import LogsTable from "../../features/LogsTable/LogsTable";
import Header from "../../layouts/Header/Header";
import styles from "./LogsPage.module.css";

const LogsPage = () => {
  const history = useHistory();
  return (
    <div className={styles.logs}>
      <Header text="Logs history" />
      <div className={styles.backButton}>
        <Button
          type="add"
          width="150px"
          height="35px"
          onClick={() => history.push("/documents")}
        >
          <ArrowBack style={{ color: "white", fontSize: 20 }}></ArrowBack> GO
          BACK
        </Button>
      </div>
      <div className={styles.tableContainer}>
        <LogsTable />
      </div>
    </div>
  );
};

export default LogsPage;

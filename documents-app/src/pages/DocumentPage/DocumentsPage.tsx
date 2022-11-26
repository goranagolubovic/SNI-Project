import React from "react";
import DefaultDir from "../../features/DefaultDir/DefaultDir";
import Header from "../../layouts/Header/Header";
import styles from "./DocumentsPage.module.css";
const DocumentsPage = () => {
  return (
    <div className={styles.container}>
      <Header />
      <DefaultDir />
    </div>
  );
};

export default DocumentsPage;

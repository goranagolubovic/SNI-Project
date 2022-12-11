import React from "react";
import DefaultDir from "../../features/DefaultDir/DefaultDir";
import Footer from "../../layouts/Footer/Footer";
import Header from "../../layouts/Header/Header";
import styles from "./DocumentsPage.module.css";
const DocumentsPage = () => {
  return (
    <div className={styles.container}>
      <Header text="Documents" />
      <DefaultDir />
      <Footer link="/logs" text="LOGS HISTORY" />
    </div>
  );
};

export default DocumentsPage;

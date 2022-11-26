import { BlobOptions } from "buffer";
import React, { useState } from "react";
import CheckBox from "../../shared/components/CheckBox/CheckBox";
import styles from "../PrivilegiesContainer/PrivilegiesContainer.module.css";

export interface PrivilegiesContainerProps {
  readAllowed: (value: boolean) => void;
  createAllowed: (value: boolean) => void;
  updateAllowed: (value: boolean) => void;
  deleteAllowed: (value: boolean) => void;
  checkedCreate?: boolean;
  checkedRead?: boolean;
  checkedUpdate?: boolean;
  checkedDelete?: boolean;
}

const PrivilegiesContainer = ({
  readAllowed,
  createAllowed,
  updateAllowed,
  deleteAllowed,
  checkedCreate,
  checkedRead,
  checkedUpdate,
  checkedDelete,
}: PrivilegiesContainerProps) => {
  const handleReadAllowed = (value: boolean) => {
    readAllowed(value);
  };
  const handleCreateAllowed = (value: boolean) => {
    createAllowed(value);
  };
  const handleUpdateAllowed = (value: boolean) => {
    updateAllowed(value);
  };
  const handleDeleteAllowed = (value: boolean) => {
    deleteAllowed(value);
  };
  return (
    <div className={styles.privilegiesContainer}>
      <p className={styles.title}>Allow actions:</p>
      <div className={styles.checkBoxContainer}>
        <CheckBox
          text="Create"
          onChecked={handleCreateAllowed}
          checked={checkedCreate}
        ></CheckBox>
        <CheckBox
          text="Read"
          onChecked={handleReadAllowed}
          checked={checkedRead}
        ></CheckBox>
      </div>
      <div className={styles.checkBoxContainer}>
        <CheckBox
          text="Update"
          onChecked={handleUpdateAllowed}
          checked={checkedUpdate}
        ></CheckBox>
        <CheckBox
          text="Delete"
          onChecked={handleDeleteAllowed}
          checked={checkedDelete}
        ></CheckBox>
      </div>
    </div>
  );
};

export default PrivilegiesContainer;

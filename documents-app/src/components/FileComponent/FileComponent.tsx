import React from "react";
import styles from "./FileComponent.module.css";

export type FileType = "file" | "folder";
const fileTypeClasses: Record<FileType, string> = {
  file: styles.file,
  folder: styles.folder,
};

export interface FileProps
  extends Omit<
    React.DetailedHTMLProps<
      React.ButtonHTMLAttributes<HTMLButtonElement>,
      HTMLButtonElement
    >,
    "type"
  > {
  type: FileType;
  title: string;
  onClick?: () => void;
}
const FileComponent = ({ type, title, onClick }: FileProps) => {
  return (
    <div className={styles.container}>
      <button className={fileTypeClasses[type]} onClick={onClick}></button>
      <p className={styles.title}>{title}</p>
    </div>
  );
};

export default FileComponent;

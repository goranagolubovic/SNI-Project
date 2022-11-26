import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import { createFile, deleteFile } from "../../api/services/files";
import { fetchFiles } from "../../api/services/files";
import { Button } from "../../components/Button/Button";
import ErrorComponent from "../../components/ErrorComponent/ErrorComponent";
import File from "../../components/FileComponent/FileComponent";
import FileInput from "../../components/FileInput/FileInput";
import { CreateFileRequest } from "../../models/CreateFileRequest";
import styles from "./DefaultDir.module.css";
const DefaultDir = () => {
  const history = useHistory();
  const [files, setFiles] = useState([]);
  const defaultDir = JSON.parse(localStorage.getItem("USER") || "").user
    .userDir;
  const [dir, setDir] = useState(defaultDir);
  const [previousDir, setPreviousDir] = useState(defaultDir);
  const [tempPreviousDir, setTempPreviousDir] = useState(defaultDir);
  const [file, setFile] = useState("");
  const [isAddFileActive, setIsFileActive] = useState(false);
  const [isAddFOlderActive, setIsFolderActive] = useState(false);
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<CreateFileRequest>();

  const getFiles = async (dir: string) => {
    try {
      let res = await fetchFiles(JSON.stringify(previousDir));
      if (res.status === 200) {
        const data = await res.json();
        console.log(data);
        setFiles(data);
      } else if (res.status === 403 || res.status === 401) {
        history.push("/");
      }
    } catch (err) {
      console.log(err);
    }
  };

  // const previewAttachment = (event: any, file: any) => {
  //   event.preventDefault();
  //   let reader = new FileReader();
  //   reader.readAsDataURL(file);
  //   setTimeout(() => {
  //     window.open(reader.result, "_self");
  //   }, 500);
  // };

  const handleClick = (e: any) => {
    if (e.isDir === 1) {
      setDir(e.name);
      setPreviousDir(previousDir + "/" + e.name);
      setFile("");
    } else {
      setDir("");
      setTempPreviousDir(previousDir);
      setPreviousDir(previousDir + "/" + e.name);
      setFile(e.name);
    }
  };

  useEffect(() => {
    console.log(previousDir);
    getFiles(previousDir);
  }, [previousDir, file]);

  const onSubmit = async (data: CreateFileRequest) => {
    data.rootDir = previousDir;
    data.isDir = isAddFOlderActive ? 1 : 0;

    setIsFolderActive(false);
    setIsFileActive(false);

    try {
      let res = await createFile(JSON.stringify(data));
      if (res.status === 200) {
        getFiles(dir);
      } else if (res.status === 403 || res.status === 401) {
        history.push("/");
      }
    } catch (err) {
      console.log(err);
    }
  };

  const removeFile = async () => {
    try {
      console.log(previousDir);
      let res = await deleteFile(JSON.stringify(previousDir));
      if (res.status === 200) {
        setFile("");
        setPreviousDir(tempPreviousDir);
      } else if (
        res.status === 403 ||
        res.status === 401 ||
        res.status === 404
      ) {
        history.push("/");
      }
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <div className={styles.centralContainer}>
      {file === "" && (
        <div className={styles.actions}>
          <div className={styles.actionsContent}>
            <Button type="add" onClick={() => setIsFolderActive(true)}>
              CREATE A NEW FOLDER
            </Button>
            {isAddFOlderActive && (
              <div>
                <form
                  className={styles.controls}
                  onSubmit={handleSubmit((data) => onSubmit(data))}
                >
                  <FileInput
                    placeholder={"Name"}
                    {...register("name", {
                      required: true,
                    })}
                  ></FileInput>
                  <Button type={"true"} />
                  <Button
                    type={"false"}
                    onClick={() => setIsFolderActive(false)}
                  ></Button>
                </form>
              </div>
            )}
          </div>
          <div className={styles.actionsContent}>
            <Button type="add" onClick={() => setIsFileActive(true)}>
              CREATE A NEW FILE
            </Button>
            {isAddFileActive && (
              <div>
                <form
                  className={styles.controls}
                  onSubmit={handleSubmit((data) => onSubmit(data))}
                >
                  <FileInput
                    placeholder={"Name"}
                    {...register("name", {
                      required: true,
                    })}
                  ></FileInput>
                  <Button type={"true"} />
                  <Button
                    type={"false"}
                    onClick={() => setIsFileActive(false)}
                  ></Button>
                </form>
              </div>
            )}
          </div>
        </div>
      )}
      {file !== "" && (
        <div className={styles.actions}>
          <div className={styles.actionsContent}>
            <Button type="add" onClick={() => setIsFolderActive(true)}>
              DOWNLOAD FILE
            </Button>
          </div>
          <div className={styles.actionsContent}>
            <Button type="add" onClick={() => removeFile()}>
              DELETE FILE
            </Button>
          </div>
        </div>
      )}
      <div className={styles.container}>
        {files?.map((e: any) => (
          <File
            title={e.name}
            key={e.idfile}
            type={e.isDir === 1 ? "folder" : "file"}
            onClick={() => handleClick(e)}
          />
        ))}
      </div>
    </div>
  );
};

export default DefaultDir;

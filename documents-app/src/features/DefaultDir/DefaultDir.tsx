import userEvent from "@testing-library/user-event";
import { Blob } from "buffer";
import { readFile } from "fs/promises";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import {
  createFile,
  deleteFile,
  editFile,
  readFileContent,
} from "../../api/services/files";
import { fetchFiles } from "../../api/services/files";
import { Button } from "../../components/Button/Button";
import ErrorComponent from "../../components/ErrorComponent/ErrorComponent";
import File from "../../components/FileComponent/FileComponent";
import FileInput from "../../components/FileInput/FileInput";
import { BACKEND_URL } from "../../constants";
import { CreateFileRequest } from "../../models/CreateFileRequest";
import styles from "./DefaultDir.module.css";
const DefaultDir = () => {
  const history = useHistory();
  const [files, setFiles] = useState([]);
  const [fileContent, setFileContent] = useState<any>("");
  const [previousFileContent, setPreviousFileContent] = useState<any>("");
  const [editFileContent, setEditFileContent] = useState(false);
  const [isFileUploaded, setIsFileUploaded] = useState(false);
  const user = JSON.parse(localStorage.getItem("USER") || "").user;
  const token = JSON.parse(localStorage.getItem("USER") || "").token;
  const defaultDir = user.userDir;
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

  const cancelEditing = () => {
    setEditFileContent(false);
    setFileContent(previousFileContent);
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
      readFile(e.name);
    }
  };

  const changeFile = async () => {
    var data = {
      filePath: previousDir,
      fileContent: fileContent,
    };
    try {
      let res = await editFile(JSON.stringify(data));
      if (res.status === 200) {
        setEditFileContent(false);
        const message = await res.text();
        alert(message);
      } else if (res.status === 403 || res.status === 401) {
        history.push("/");
      }
    } catch (err) {
      console.log(err);
    }
  };

  const blobToFile = (theBlob: any, fileName: string) => {
    //A Blob() is almost a File() - it's just missing the two properties below which we will add
    theBlob.lastModifiedDate = new Date();
    theBlob.name = fileName;
    return theBlob;
  };

  const readFile = async (fileName: string) => {
    var data = {
      userDir: defaultDir,
      filePath: previousDir + "/" + fileName,
    };
    try {
      console.log(JSON.stringify(data));

      let res = await readFileContent(JSON.stringify(data));
      if (res.status === 200) {
        const response = await res.blob();
        var file = blobToFile(response, fileName);
        var reader = new FileReader();
        reader.onload = function (event) {
          // The file's text will be printed here
          setFileContent(event.target?.result);
        };
        if (fileName.endsWith(".png" || ".jpeg" || "jpg" || "svg"))
          reader.readAsDataURL(file);
        else reader.readAsText(file);
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
  const saveFile = () => {
    console.log(token);
    var data = {
      userDir: defaultDir,
      filePath: previousDir,
    };
    fetch(BACKEND_URL + "files/read", {
      method: "POST",
      headers: new Headers({
        authorization: "Bearer " + token,
      }),
      body: JSON.stringify(data),
      mode: "cors",
    })
      .then((response) => response.blob())
      .then((blob) => {
        var url = window.URL.createObjectURL(blob);
        var a = document.createElement("a");
        a.href = url;
        a.download = file;
        document.body.appendChild(a); // we need to append the element to the dom -> otherwise it will not work in firefox
        a.click();
        a.remove(); //afterwards we remove the element again
      });
  };
  useEffect(() => {
    if (file === "" || isFileUploaded) {
      setIsFileUploaded(false);
      getFiles(previousDir);
    }
  }, [previousDir, file, isFileUploaded]);

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
  const findFileChooser = () => {
    document.getElementById("getFile")?.click();
  };
  const chooseFile = async (e: any) => {
    const reader = new FileReader();

    if (e.target.files.length !== 0) {
      const fileChoosed = e.target.files[0];
      reader.readAsDataURL(fileChoosed);
      let formData = new FormData();
      formData.append("folderName", previousDir);
      formData.append("file", fileChoosed);

      reader.onloadend = async () => {
        const result = reader.result;

        fetch(BACKEND_URL + "files/upload", {
          method: "POST",
          headers: new Headers({
            authorization: "Bearer " + token,
          }),
          body: formData,
          mode: "cors",
        })
          .then((response) => Promise.all([response.status, response.text()]))
          .then(function ([status, myJson]) {
            if (status == 200) {
              console.log(myJson);
              alert(myJson);
              setIsFileUploaded(true);
            } else {
              history.push("/");
            }
          })
          .catch((error) => console.log(error.message));
      };
    }
  };
  const unableEditing = () => {
    setEditFileContent(true);
    setPreviousFileContent(fileContent);
  };
  return (
    <div className={styles.centralContainer}>
      {file === "" && (
        <div className={styles.centralContainer}>
          <div className={styles.actions}>
            <div className={styles.actionsContent}>
              {user.role !== "client" && (
                <Button type="add" onClick={() => setIsFolderActive(true)}>
                  CREATE A NEW FOLDER
                </Button>
              )}
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
              {user.role !== "client" && (
                <Button type="add" onClick={() => setIsFileActive(true)}>
                  CREATE A NEW FILE
                </Button>
              )}
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
            <div className={styles.actionsContent}>
              {user.role === "client" && (
                <div>
                  <Button type="add" onClick={() => findFileChooser()}>
                    UPLOAD A NEW FILE
                  </Button>
                  <input
                    type="file"
                    id="getFile"
                    className={styles.fileInput}
                    onChange={(e) => chooseFile(e)}
                  />
                </div>
              )}
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
          </div>

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
      )}
      <div className={styles.centralContainer}>
        {file !== "" && (
          <div className={styles.actions}>
            <div className={styles.actionsContent}>
              <Button type="add" onClick={() => saveFile()}>
                DOWNLOAD FILE
              </Button>
            </div>
            <div className={styles.actionsContent}>
              <Button type="add" onClick={() => removeFile()}>
                DELETE FILE
              </Button>
            </div>
            <div className={styles.actionsContent}>
              <Button type="add" onClick={() => unableEditing()}>
                EDIT FILE
              </Button>
            </div>
          </div>
        )}
        {/* <iframe src={fileContent}></iframe> */}
        {file != "" && (
          <div className={styles.editFileContainer}>
            {!file.endsWith(".png" || ".jpeg" || "jpg" || "svg") &&
              !editFileContent && <p>{fileContent}</p>}
            {file.endsWith(".png" || ".jpeg" || "jpg" || "svg") &&
              !editFileContent && (
                <div
                  className={styles.imageContainer}
                  style={{
                    backgroundImage: "url(" + fileContent + ")",
                    backgroundSize: "20vw",
                  }}
                ></div>
              )}
            {!file.endsWith(".png" || ".jpeg" || "jpg" || "svg") &&
              editFileContent && (
                <textarea
                  value={fileContent}
                  onChange={(e) => setFileContent(e.target?.value)}
                ></textarea>
              )}
            {file.endsWith(".png" || ".jpeg" || "jpg" || "svg") &&
              editFileContent && (
                <div
                  className={styles.imageContainer}
                  style={{
                    backgroundImage: "url(" + fileContent + ")",
                    backgroundSize: "20vw",
                  }}
                ></div>
              )}
            {editFileContent && (
              <div className={styles.editFileActions}>
                <Button type="add" onClick={() => changeFile()}>
                  SAVE
                </Button>
                <Button type="add" onClick={() => cancelEditing()}>
                  CANCEL
                </Button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default DefaultDir;

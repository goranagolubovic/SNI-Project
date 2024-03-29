import userEvent from "@testing-library/user-event";
import { Blob } from "buffer";
import { readFile } from "fs/promises";
import React, { useEffect, useReducer, useState } from "react";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import { logoutUser } from "../..";
import {
  createFile,
  deleteFile,
  editFile,
  fetchAvailableDirs,
  getParentDir,
  moveFileTo,
  readFileContent,
} from "../../api/services/files";
import { fetchFiles } from "../../api/services/files";
import { fetchUserInfo } from "../../api/services/users";
import { Button } from "../../components/Button/Button";
import ErrorComponent from "../../components/ErrorComponent/ErrorComponent";
import File from "../../components/FileComponent/FileComponent";
import FileInput from "../../components/FileInput/FileInput";
import Select from "../../components/Select/Select";
import { BACKEND_URL, DELETE_FOLDER_MESSAGE } from "../../constants";
import { AvailableDirsRequest } from "../../models/AvailableDirsRequest";
import { CreateFileRequest } from "../../models/CreateFileRequest";
import { getToken, getUsername, validateURL } from "../../util";
import ChangePasswordDialog from "../ChangePasswordDialog/ChangePasswordDialog";
import styles from "./DefaultDir.module.css";
const DefaultDir = () => {
  const history = useHistory();
  const [files, setFiles] = useState([]);
  const [fileContent, setFileContent] = useState<any>("");
  const [availableDirs, setAvailableDirs] = useState([]);
  const [previousFileContent, setPreviousFileContent] = useState<any>("");
  const [editFileContent, setEditFileContent] = useState(false);
  const [isDirContentChanged, setIsDirContentChanged] = useState(false);
  const [defaultDir, setDefaultDir] = useState("");
  const [currentDir, setCurrentDir] = useState(defaultDir);
  const [role, setRole] = useState("");
  const [username, setUsername] = useState("");
  const [file, setFile] = useState("");
  const [isAddFileActive, setIsFileActive] = useState(false);
  const [isPasswordChanged, setIsPasswordChanged] = useState(false);
  const [message, setMessage] = useState("");
  const [isAddFOlderActive, setIsFolderActive] = useState(false);
  const [isUserInfoFetchingCompleted, setIsUserInfoFetchingCompleted] =
    useState(false);
  const [isCreateAllowed, setIsCreateAllowed] = useState(false);
  const [isReadAllowed, setIsReadAllowed] = useState(false);
  const [isUpdateAllowed, setIsUpdateAllpwed] = useState(false);
  const [isDeleteAllowed, setIsDeleteAllowed] = useState(false);
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<CreateFileRequest>();

  const getFiles = async (dir: string) => {
    try {
      let res = await fetchFiles(JSON.stringify(currentDir));
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

  const getAvailableDirs = async () => {
    try {
      const data = {
        userDir: defaultDir,
        currentDir: currentDir,
      };
      let res = await fetchAvailableDirs(JSON.stringify(data));
      if (res.status === 200) {
        const data = await res.json();
        console.log(data);
        setAvailableDirs(data);
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
      setCurrentDir(currentDir + "/" + e.name);
      setFile("");
    } else {
      setCurrentDir(currentDir + "/" + e.name);
      setFile(e.name);
      readFile(e.name);
    }
  };

  const changeFile = async () => {
    var data = {
      filePath: currentDir,
      fileContent: fileContent,
      username: username,
    };
    try {
      let res = await editFile(JSON.stringify(data));
      let resData = await res.json();
      if (resData.status === 200) {
        setEditFileContent(false);
        alert(resData.message);
      } else if (res.status === 403 || res.status === 401) {
        history.push("/");
      } else if (resData.status === 404 || resData.status === 500) {
        alert(resData.message);
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
      filePath: currentDir + "/" + fileName,
      username: username,
      action: "read",
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
    var data = {
      userDir: defaultDir,
      filePath: currentDir,
      username: username,
      action: "download",
    };
    fetch(BACKEND_URL + "files/read", {
      method: "POST",
      headers: new Headers({
        authorization: "Bearer " + getToken(),
        "Content-Type": "application/json;charset=UTF-8",
      }),
      body: JSON.stringify(data),
      mode: "cors",
    })
      .then((response) => response.blob())
      .then((blob) => {
        var url = window.URL.createObjectURL(blob);
        var a = document.createElement("a");
        if (validateURL(url)) {
          url = "";
        }
        a.href = url;
        a.download = file;
        document.body.appendChild(a); // we need to append the element to the dom -> otherwise it will not work in firefox
        a.click();
        a.remove(); //afterwards we remove the element again
      });
  };
  useEffect(() => {
    getUserInfo(getUsername());
    setIsUserInfoFetchingCompleted(true);
  }, [getUsername()]);
  useEffect(() => {
    if ((isUserInfoFetchingCompleted && file === "") || isDirContentChanged) {
      setIsDirContentChanged(false);
      getFiles(currentDir);
      if (role == "document_admin") getAvailableDirs();
    }
  }, [currentDir, file, isDirContentChanged]);
  const getUserInfo = async (username: string) => {
    console.log(username);
    try {
      const response = await fetchUserInfo(username);
      const respData = await response.json();

      if (respData.status == 200) {
        setDefaultDir(respData.user.userDir);
        setCurrentDir(respData.user.userDir);
        setRole(respData.user.role);
        console.log("ROLEE" + role);
        setUsername(respData.user.username);
        setIsPasswordChanged(respData.user.isPasswordChanged === 1);
        setIsReadAllowed(respData.user.isReadApproved === 1);
        setIsCreateAllowed(respData.user.isCreateApproved === 1);
        setIsUpdateAllpwed(respData.user.isUpdateApproved === 1);
        setIsDeleteAllowed(respData.user.isDeleteApproved === 1);
      } else {
        setMessage(respData.loginMessage);
        console.log(respData.loginMessage);
      }
    } catch (err) {
      console.log(err);
      reset();
    }
  };
  const getPreviousDir = async () => {
    try {
      let res = await getParentDir(JSON.stringify(currentDir));
      let resData = await res.json();
      if (resData.status === 200) {
        const previousDir = resData.message;
        console.log("Now current dir is" + previousDir);
        setCurrentDir(previousDir);
      } else if (
        resData.status === 403 ||
        resData.status === 401 ||
        resData.status === 404
      ) {
        history.push("/");
      } else if (resData.status === 500) {
        alert(resData.message);
      }
    } catch (err) {
      console.log(err);
    }
    setFile("");
  };

  const onSubmit = async (data: CreateFileRequest) => {
    data.rootDir = currentDir;
    data.isDir = isAddFOlderActive ? 1 : 0;
    data.username = username;

    setIsFolderActive(false);
    setIsFileActive(false);

    try {
      let res = await createFile(JSON.stringify(data));
      let resData = await res.json();
      if (resData.status === 200) {
        getFiles(currentDir);
      } else if (res.status === 403 || res.status === 401) {
        history.push("/");
      } else if (
        resData.status === 404 ||
        resData.status === 409 ||
        resData.status === 500
      ) {
        alert(resData.message);
      }
    } catch (err) {
      console.log(err);
    }
  };

  const removeFile = async () => {
    const data = {
      path: currentDir,
      username: username,
    };
    try {
      console.log("REMOVING" + currentDir);
      let res = await deleteFile(JSON.stringify(data));
      let responseData = await res.json();
      if (responseData.status === 200) {
        setFile("");
      } else if (responseData.status === 403 || responseData.status === 401) {
        history.push("/");
      } else if (responseData.status === 404 || responseData.status === 500) {
        alert(responseData.message);
      }
      getPreviousDir();
    } catch (err) {
      console.log(err);
    }
  };

  const findFileChooser = () => {
    document.getElementById("getFile")?.click();
  };

  const logout = () => {
    logoutUser();
  };

  const chooseFile = async (e: any) => {
    const reader = new FileReader();

    if (e.target.files.length !== 0) {
      const fileChoosed = e.target.files[0];
      reader.readAsDataURL(fileChoosed);
      let formData = new FormData();
      formData.append("folderName", currentDir);
      formData.append("file", fileChoosed);
      formData.append("username", username);

      reader.onloadend = async () => {
        const result = reader.result;

        fetch(BACKEND_URL + "files/upload", {
          method: "POST",
          headers: new Headers({
            authorization: "Bearer " + getToken(),
          }),
          body: formData,
          mode: "cors",
        })
          .then((response) => Promise.all([response.status, response.json()]))
          .then(function ([status, myJson]) {
            if (status == 200) {
              console.log(myJson);
              alert(myJson.message);
              setIsDirContentChanged(true);
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
  const handleFolderDelete = (): void => {
    if (window.confirm(DELETE_FOLDER_MESSAGE)) {
      removeFile();
    }
  };

  const sendFileTo = async (value: any) => {
    const pathToFileToMove = currentDir;
    getPreviousDir();
    getFiles(currentDir);
    try {
      const data = {
        destinationDir: value,
        filePath: pathToFileToMove,
        fileName: file,
        username: username,
      };
      let res = await moveFileTo(JSON.stringify(data));
      let resData = await res.json();
      if (res.status === 403 || res.status === 401) {
        history.push("/");
      }
      if (resData.status === 404 || resData.status === 500) {
        alert(resData.message);
      }
      if (resData.status === 200) {
        setIsDirContentChanged(!isDirContentChanged);
      }
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <div className={styles.centralContainer}>
      {file === "" && message === "" && isPasswordChanged && (
        <div className={styles.centralContainer}>
          <div className={styles.actions}>
            <div className={styles.actionsContent}>
              {currentDir != defaultDir && (
                <Button
                  type={"previous"}
                  onClick={() => getPreviousDir()}
                ></Button>
              )}
            </div>
            <div className={styles.actionsContent}>
              {role !== "client" && role !== "admin" && (
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
                      {...register("fileName", {
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
              {role !== "client" && role !== "admin" && (
                <Button type="add" onClick={() => handleFolderDelete()}>
                  DELETE FOLDER
                </Button>
              )}
            </div>
            <div className={styles.actionsContent}>
              {role !== "admin" && isCreateAllowed && (
                <div>
                  <Button type="add" onClick={() => findFileChooser()}>
                    FILE UPLOAD
                  </Button>
                  <input
                    type="file"
                    id="getFile"
                    className={styles.fileInput}
                    onChange={(e) => chooseFile(e)}
                  />
                </div>
              )}
            </div>
            <div className={styles.actionsContent}>
              <Button type="add" onClick={() => logout()}>
                LOGOUT
              </Button>
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
        {file !== "" && isPasswordChanged && (
          <div className={styles.actions}>
            <div className={styles.actionsContent}>
              {currentDir != defaultDir && (
                <Button
                  type={"previous"}
                  onClick={() => getPreviousDir()}
                ></Button>
              )}
            </div>
            <div className={styles.actionsContent}>
              {isReadAllowed && (
                <Button type="add" onClick={() => saveFile()}>
                  DOWNLOAD FILE
                </Button>
              )}
            </div>
            <div className={styles.actionsContent}>
              {role !== "admin" && isDeleteAllowed && (
                <Button type="add" onClick={() => removeFile()}>
                  DELETE FILE
                </Button>
              )}
            </div>
            <div className={styles.actionsContent}>
              {role !== "admin" &&
                isUpdateAllowed &&
                !file.endsWith(".png" || ".jpeg" || "jpg" || "svg") && (
                  <Button type="add" onClick={() => unableEditing()}>
                    EDIT FILE
                  </Button>
                )}
            </div>
            {role === "document_admin" && (
              <div className={styles.actionsContent}>
                <Select
                  text="SEND TO"
                  values={availableDirs}
                  className={""}
                  onChange={(e: any) => sendFileTo(e.target.value)}
                ></Select>
              </div>
            )}
          </div>
        )}
        {/* <iframe src={fileContent}></iframe> */}
        {file != "" && message === "" && (
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
        {message !== "" && <p>{message}</p>}
        {!isPasswordChanged && (
          <ChangePasswordDialog
            stateChanger={setIsPasswordChanged}
          ></ChangePasswordDialog>
        )}
      </div>
    </div>
  );
};

export default DefaultDir;

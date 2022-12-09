import { BACKEND_URL } from "../../constants";
import { deleteWithCredentials, getWithCredentials, postWithCredentials } from "../client";

const headers = { "Content-Type": "application/json" };
const uploadHeaders = { "Content-Type": "multipart/form-data" };

const fetchFiles = async (dir: string) => {
    console.log("fetch" + dir)
    const requestOptions = {
        headers,
        body: dir,
    };
    const response = await postWithCredentials(BACKEND_URL + "files/all", requestOptions);
    return response;
};

const createFile = async (data: string) => {
    const requestOptions = {
        headers,
        body: data,
    };

    const response = await postWithCredentials(BACKEND_URL + "files/new", requestOptions);
    return response;
}

const deleteFile = async (pathToFile: string) => {
    const requestOptions = {
        headers,
        body: pathToFile,
    };
    console.log(pathToFile);
    const response = await deleteWithCredentials(BACKEND_URL + "files/delete", requestOptions);
    return response;
}

const readFileContent = async (data: string) => {
    const requestOptions = {
        headers,
        body: data,
    };
    console.log(data);
    const response = await postWithCredentials(BACKEND_URL + "files/read", requestOptions);
    return response;
}

const fetchAvailableDirs = async (data: any) => {
    const requestOptions = {
        headers,
        body: data,
    };
    const response = await postWithCredentials(BACKEND_URL + "files/availableDirs", requestOptions);
    return response;
}
const moveFileTo = async (data: any) => {
    console.log(data)
    const requestOptions = {
        headers,
        body: data,
    };
    const response = await postWithCredentials(BACKEND_URL + "files/sendTo", requestOptions);
    return response;
}
// const uploadFile = async (data: any) => {
//     const requestOptions = {
//         body: data,
//     };
//     console.log(data.get("folderName"));
//     const response = await postWithCredentials(BACKEND_URL + "files/upload", requestOptions);
//     return response;
// }

const editFile = async (data: any) => {
    const requestOptions = {
        headers,
        body: data,
    };
    console.log(JSON.stringify(data));
    const response = await postWithCredentials(BACKEND_URL + "files/edit", requestOptions);
    return response;
}

const getParentDir = async (currentDir: string) => {
    const requestOptions = {
        headers,
        body: currentDir,
    };
    const response = await postWithCredentials(BACKEND_URL + "files/parentDir", requestOptions);
    return response;
}

export { fetchFiles, createFile, deleteFile, readFileContent, editFile, getParentDir, fetchAvailableDirs, moveFileTo }
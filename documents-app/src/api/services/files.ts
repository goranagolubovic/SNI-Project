import { BACKEND_URL } from "../../constants";
import { deleteWithCredentials, getWithCredentials, postWithCredentials } from "../client";

const headers = { "Content-Type": "application/json" };

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
    const response = await deleteWithCredentials(BACKEND_URL + "files", requestOptions);
    return response;
}
export { fetchFiles, createFile, deleteFile }
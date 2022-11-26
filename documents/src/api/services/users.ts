import { BACKEND_URL } from "../../constants";
import { deleteWithCredentials, getWithCredentials, post, postWithCredentials, putWithCredentials } from "../client";


const headers = { "Content-Type": "application/json" };
const login = async (data: string) => {
    const requestOptions = {
        headers,
        body: data,
    };

    const response = await post(BACKEND_URL + "auth", requestOptions);
    return response;
};

const add = async (data: string) => {
    const requestOptions = {
        headers,
        body: data,
    };

    const response = await postWithCredentials(BACKEND_URL + "admin/users", requestOptions);
    return response;
}

const fetchUsers = async () => {
    const response = await getWithCredentials(BACKEND_URL + "admin/users");
    return response;
};

const deleteUser = async (username: string) => {
    const response = await deleteWithCredentials(BACKEND_URL + "admin/users/" + username);
    return response;
};

const getUser = async (username: string) => {
    const response = await getWithCredentials(BACKEND_URL + "admin/users/" + username);
    return response;
};

const updateUser = async (data: string) => {
    const requestOptions = {
        headers,
        body: data,
    };

    const response = await putWithCredentials(BACKEND_URL + "admin/users", requestOptions);
    return response;
}

export { login, add, fetchUsers, deleteUser, getUser, updateUser };
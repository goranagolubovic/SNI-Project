import { BACKEND_URL } from "../../constants";
import { deleteWithCredentials, getWithCredentials, post, postWithCredentials, putWithCredentials } from "../client";


const headers = { "Content-Type": "application/json" };
const login = async (data: string) => {
    const requestOptions = {
        headers,
        body: data,
    };
    console.log(data)
    const response = await post(BACKEND_URL + "auth", requestOptions);
    return response;
};

const fetchUserInfo = async (username: string) => {
    const requestOptions = {
        headers,
        body: username,
    };

    const response = await post(BACKEND_URL + "info", requestOptions);
    return response;
};


export { login, fetchUserInfo };
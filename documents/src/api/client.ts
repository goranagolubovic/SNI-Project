import { getToken } from "../util";

const send = async (url: RequestInfo, options?: RequestInit) => {
    const response = await fetch(url, options);
    return response;
};

const post = async (url: RequestInfo, options?: RequestInit) => {
    const response = await send(url, {
        ...options,
        method: "POST",
    });
    return response;
};

const put = async (url: RequestInfo, options?: RequestInit) => {
    const response = await send(url, {
        ...options,
        method: "PUT",
    });
    return response;
};

const remove = async (url: RequestInfo, options?: RequestInit) => {
    const response = await send(url, {
        ...options,
        method: "DELETE",
    });
    return response;
};
const postWithCredentials = async (url: RequestInfo, options?: RequestInit) => {
    const token = getToken();
    const requestOptions = {
        ...options,
        headers: {
            authorization: "Bearer " + token,
            "Content-Type": "application/json",
        },
    };

    return post(url, requestOptions);
};

const putWithCredentials = async (url: RequestInfo, options?: RequestInit) => {
    const token = getToken();
    const requestOptions = {
        ...options,
        headers: {
            authorization: "Bearer " + token,
            "Content-Type": "application/json",
        },
    };

    return put(url, requestOptions);
};
const deleteWithCredentials = async (url: RequestInfo, options?: RequestInit) => {
    const token = getToken();
    const requestOptions = {
        ...options,
        headers: {
            authorization: "Bearer " + token,
            "Content-Type": "application/json",
        },
    };

    return remove(url, requestOptions);
}

const get = async (url: RequestInfo, options?: RequestInit) => {
    const response = await send(url, {
        ...options,
        method: "GET",
    });
    return response;
};

const getWithCredentials = async (url: RequestInfo, options?: RequestInit) => {
    const token = getToken();
    const requestOptions = {
        ...options,
        headers: {
            authorization: "Bearer " + token,
        },
    };

    return get(url, requestOptions);
};

export { post, postWithCredentials, get, getWithCredentials, deleteWithCredentials, remove, put, putWithCredentials };
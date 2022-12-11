import { BACKEND_URL } from "../../constants";
import { getWithCredentials } from "../client";

const fetchLogs = async () => {
    const response = await getWithCredentials(BACKEND_URL + "logs");
    return response;
};

export {
    fetchLogs
}

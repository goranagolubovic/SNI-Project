export const getToken = () => {
    return JSON.parse(localStorage.getItem("TOKEN") || "");
};
export const getUsername = () => {
    return JSON.parse(localStorage.getItem("USERNAME") || "");
};
export const formatUserDir = (data: string) => {
    if (data.endsWith("/")) {
        data = data.substring(0, data.length - 1);
    }
    return data;
}
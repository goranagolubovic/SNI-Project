export const getToken = () => {
    //return JSON.parse(localStorage.getItem("USER") || "").token;
    return JSON.parse(localStorage.getItem("TOKEN") || "");
};

export const getUsername = () => {
    return JSON.parse(localStorage.getItem("USERNAME") || "");
}

export const validateURL = (url: string) => {
    return url.startsWith("javascript")
}
// export const modifeDirFileName = (name: string, dir: string) => {
//     //console.log(element)
//     const totalLength = name.length;
//     name = name.slice(-(totalLength - dir.length - 1))
//     return name;
//     //element = element.name.truncate(element.name, dir.length + 1)
// }

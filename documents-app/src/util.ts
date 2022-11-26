

export const getToken = () => {
    return JSON.parse(localStorage.getItem("USER") || "").token;
};

// export const modifeDirFileName = (name: string, dir: string) => {
//     //console.log(element)
//     const totalLength = name.length;
//     name = name.slice(-(totalLength - dir.length - 1))
//     return name;
//     //element = element.name.truncate(element.name, dir.length + 1)
// }

let dev = process.env.NODE_ENV == 'development';
console.log('当前是否开发模式', dev);
export const mapKey = "570a202cd2019bd9a2bbc7832dfbfdc6";

let port = dev ? "8080": location.port



export const serverUrl = location.protocol + "//" + location.hostname + ":" + port + "/";
export const serverPort = port;





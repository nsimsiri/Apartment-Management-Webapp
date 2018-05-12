var config = {
    API_ROOT: 'http://localhost:8080',
    defaultAxiosHeaders: (headers) => {
        return {
            ...headers,
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Credentials": true
        }
    },
}


export default config

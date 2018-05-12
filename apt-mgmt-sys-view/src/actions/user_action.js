import axios from 'axios'
import config from '../config'
const {API_ROOT, defaultAxiosHeaders} = config

const REQUESTING_USERS = "REQUESTING_USERS"
const RECEIVED_USERS = "RECEIVED_USERS"
const REQUEST_USER = "REQUEST_USER"
const RECEIVED_USER = "RECEIVED_USER"
const RECEIVED_USER_REMOVAL = "RECEIVED_USER_REMOVAL";

const sformat = require('string-format');

//actions

const _requestUsers = () => {
    return {
        type: REQUESTING_USERS
    }
}
const _receivedUsers = (response) => {
    let users = (response && response.data) ? response.data : [];
    return {
        type: RECEIVED_USERS,
        users: users
    }
}

const _receivedUser = (response) => {
    return {
        type: RECEIVED_USER,
        responseData: response.data
    }
}

const _receivedUserRemoval = (response) => {
    return {
        type: RECEIVED_USER_REMOVAL,
        userRemoved: response.data
    }
}

//ACTION CREATORS

class UserActionService {
    static buildEmptyState = () => {
        return {
            isRequesting: false,
            users: [],
            user: null,
            _type: "UserComponentObject"
        }
    }
    static fetchUsers = () => {
        return (dispatch) => {
            dispatch(_requestUsers());
            let url = API_ROOT + "/users/"
            axios.get(url, {
                headers: defaultAxiosHeaders({}),
                withCredentials: true
            }).then(response => {
                dispatch(_receivedUsers(response));
            }).catch(error => {
                console.log('[USER_ACTION ERR]');
                console.log(error);
                dispatch(_receivedUsers(null))
            })
        }
    }
    static fetchUserById = (userId) => {
        return (dispatch) => {
            dispatch(_requestUsers());
            let url = API_ROOT + "/users/" + userId
            axios.get(url, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                dispatch(_receivedUser(response));
            }).catch(error => {
                console.log('[USER_ACTION fetchByUserId ERR]')
                console.log(error);
                dispatch(_receivedUser(null));
            });
        }
    }

    static create = (user, onSuccess, onFailure) => {
        return (dispatch) => {
            const url = sformat("{0}/users/", API_ROOT);
            axios.post(url, user, {
                headers: defaultAxiosHeaders(),
                withCredentials:true
            }).then(response => {
                onSuccess();
            }).catch(error => {
                console.log("[USER_ACTION create ERR");
                console.log(error);
                dispatch(_receivedUser(null))
                onFailure();
            })
        }
    }

    static update = (id, updateSchema, onSuccess, onFailure) => {
        return (dispatch) => {
            const url = sformat("{0}/users/update/{1}", API_ROOT, id);
            dispatch(_requestUsers())
            axios.post(url, updateSchema, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                dispatch(_receivedUser(response));
                onSuccess();
            }).catch(error => {
                dispatch(_receivedUser(null));
                onFailure(error);
            })
        }
    }

    static remove = (id, onSuccess, onFailure) => {
        return (dispatch) => {
            const url = sformat("{0}/users/remove/{1}", API_ROOT, id);
            dispatch(_requestUsers());
            axios.post(url, {}, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                dispatch(_receivedUserRemoval(response.data))
                onSuccess(response);
            }).catch(error => {
                dispatch(_receivedUserRemoval(response.data))
                onFailure(error);
            });
        }
    }
}

export {UserActionService, REQUESTING_USERS, RECEIVED_USERS, RECEIVED_USER}

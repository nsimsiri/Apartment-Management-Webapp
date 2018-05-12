import {
    RECEIVED_USERS,
    REQUESTING_USERS,
    RECEIVED_USER,
    RECEIVED_USER_REMOVAL,
    UserActionService,
} from '../actions/user_action'
import {combinedReducers} from 'redux';
import _ from 'lodash'
const sformat = require('string-format');
const normalizeAndJoinUsers = (cachedUsers, userWrappers) => {
    let normalizedUsers = userWrappers.map( userWrapper => {
        return {
            ...userWrapper.user,
            roles: userWrapper.roles
        }
    });
    let compareF = user => { return user.id };
    let diffs = _.differenceBy(normalizedUsers, cachedUsers, compareF)
    return [...cachedUsers, ...diffs];

}

const UserReducer = (state = UserActionService.buildEmptyState(), action) => {
    switch(action.type){
        case REQUESTING_USERS:
            return {...state, isRequesting: true}
        case RECEIVED_USER_REMOVAL:
            return {
                ...state,
                user: {
                    ...state.user,
                    enabled: !action.userRemoved
                },
                isRequesting: false
            }
        case RECEIVED_USERS:
            // User will be wrapped List<Map<String, Object {Role, User}>>
            if (!action.users || action.users.length == 0){
                return {...state, isRequesting: false}
            }
            return {
                user: null,
                isRequesting: false,
                users: normalizeAndJoinUsers(state.users, action.users)
            }
        case RECEIVED_USER:
            console.log("[USER REDUCER]");
            console.log(action);
            if(!action.responseData) return {...state, isRequesting: false};
            return {
                ...state,
                user: {
                    ...action.responseData.user,
                    roles: (action.responseData && action.responseData.roles) ? action.responseData.roles : [],
                    employeeProfile: action.responseData.employeeProfile
                },
                isRequesting: false,
            }
        default:
            return state;
    }
}

export default UserReducer

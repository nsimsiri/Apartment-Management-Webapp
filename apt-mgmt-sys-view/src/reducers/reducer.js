import { TaskActionService, CREATE_TASK, UPDATE_TASK, DELETE_TASK } from '../actions/action';
import SessionReducer from './session_reducer';
import UserReducer from './user_reducer';
import RoleReducer from './role_reducer';
import CustomerProfileReducer from './customer_profile_reducer'
import RoomReducer from './room_reducer';
import ContractReducer from './contract_reducer';
import TransactionReducer from './transaction_reducer';
import BalanceReducer from './balance_reducer';
import TCPReducer from './tcp_reducer'
import FileEntityReducer from './file_entity_reducer'
import EntityPolicyEdgeReducer from './entity_policy_edge_reducer';
import {combineReducers} from 'redux';
import {reducer as sematable} from 'sematable';


let initState = {
    tasks: [{id: 1, text: "hello", done: false}, {id: 2, text: "world", done: false}]
};

const TaskReducer = (state = initState.tasks, action) => {

    switch (action.type){
        case CREATE_TASK:
            var k = [...state, action.task];
            return k;
        case UPDATE_TASK:
            return state.map((task) => {
                if (task.id == action.task.id){
                    task.text = task.text != action.task.text && action.task.text === undefined ? action.task.text : task.text;
                    task.done = task.done != action.task.done ? action.task.done : task.done;
                }
                return task;
            });
        case DELETE_TASK:
            return state.filter(task => task.id != action.task.id);
        default:
            return state;
    }
};

export default combineReducers({
    sematable,
    tasks: TaskReducer,
    session: SessionReducer,
    userComponent: UserReducer,
    roleComponent: RoleReducer,
    customerProfileComponent: CustomerProfileReducer,
    roomComponent: RoomReducer,
    contractComponent: ContractReducer,
    transactionComponent: TransactionReducer,
    balanceComponent: BalanceReducer,
    TCPComponent: TCPReducer,
    fileEntityComponent: FileEntityReducer,
    entityPolicyEdgeComponent: EntityPolicyEdgeReducer
});

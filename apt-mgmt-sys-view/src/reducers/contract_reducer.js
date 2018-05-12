import {
    REQUESTING,
    RECEIVED_CONTRACT,
    RECEIVED_CONTRACTS,
    RECEIVED_CONTRACT_WRAPPERS,
    RECEIVED_CONTRACT_UPDATE,
    RECEIVED_CONTRACT_REMOVAL,
    RESETTING,
    ContractActionService
} from '../actions/contract_action'
import {combinedReducers} from 'redux';
import _ from 'lodash'
const sformat = require('string-format');

const ContractReducer = (state = ContractActionService.buildEmptyState(), action) => {
    switch(action.type){
        case REQUESTING:
            return {
                isRequesting: true,
                id: action.id ? action.id : null,
                ...state
            }
        case RECEIVED_CONTRACTS:
            return {
                ...state,
                contracts: action.contracts ? action.contracts : state.contracts,
                id: action.id ? action.id : null,
                isRequesting: false
            }
        case RECEIVED_CONTRACT_WRAPPERS:
            return {
                ...state,
                contractWrappers: action.contractWrappers ? action.contractWrappers : state.contractWrappers,
                id: action.id ? action.id : null,
                isRequesting: false
            }
        case RECEIVED_CONTRACT:
            return {
                ...state,
                contract: action.contract ? action.contract : state.contract,
                id: action.id ? action.id : null,
                isRequesting: false
            }
        case RECEIVED_CONTRACT_UPDATE:
            return {
                ...state,
                contract: action.contract ? action.contract : state.contract,
                contracts: action.contract ?
                    [..._.filter(state.contracts, x => x.id != action.contract.id), action.contract] : state.contracts,
                id: action.id ? action.id : null,
                isRequesting: false
            }
        case RESETTING:
            return ContractActionService.buildEmptyState()
        default:
            return state;
    }
}

export default ContractReducer;

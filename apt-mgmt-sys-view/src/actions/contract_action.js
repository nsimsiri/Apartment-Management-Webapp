import _ from "lodash"
var sformat = require("string-format");
import axios from 'axios'
import config from '../config'
const {API_ROOT, defaultAxiosHeaders} = config;
const API_ROUTE = API_ROOT + "/contracts"

const REQUESTING = "REQUESTING";
const RECEIVED_CONTRACT = "RECEIVED_CONTRACT"
const RECEIVED_CONTRACTS = "RECEIVED_CONTRACTS"
const RECEIVED_CONTRACT_WRAPPERS = "RECEIVED_CONTRACT_WRAPPERS"
const RECEIVED_CONTRACT_REMOVAL = "RECEIVED_CONTRACT_REMOVAL"
const RECEIVED_CONTRACT_UPDATE = "RECEIVED_CONTRACT_UPDATE"
const RESETTING = "RESETTING";

const CONTRACT_TYPE_SHORT_TERM = "SHORT_TERM"
const CONTRACT_TYPE_LONG_TERM = "LONG_TERM"
const CONTRACT_TYPES = [CONTRACT_TYPE_SHORT_TERM, CONTRACT_TYPE_LONG_TERM]


const _request = () => {
    return {
        type: REQUESTING,
    }
}
const _reset = () => ({type: RESETTING});
const _receivedContract = (contract, id) => ({ type: RECEIVED_CONTRACT, contract: contract, id: id})
const _receivedContracts = (contracts, id) => ({ type: RECEIVED_CONTRACTS, contracts: contracts, id: id})
const _receivedContractUpdate = (contract, id) => ({ type: RECEIVED_CONTRACT_UPDATE, contract: contract, id: id})
const _receivedContractRemoval = (contract, id) => ({ type: RECEIVED_CONTRACT_REMOVAL, contract: contract, id: id})
const _receivedContractWrappers = (contractWrappers, id) => ({ type: RECEIVED_CONTRACT_WRAPPERS, contractWrappers: contractWrappers, id: id});


class ContractActionService {
    static buildEmptyState(){
        return {
            contracts: [],
            contractWrappers: [],
            contract: null,
            isRequesting: false,
            id: null
        }
    }

    static fetchAll(data, onSuccess, onFailure, options={id:null}){
        return dispatch => {
            dispatch(_request())
            var _arr = [];
            for(var key in data){ _arr.push(sformat("{0}={1}", key,data[key])); }
            const url = API_ROUTE+"?"+_arr.join("&")
            axios.get(url, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                onSuccess(response);
                dispatch(_receivedContracts(response.data, options.id));
            }).catch(error => {
                onFailure(error);
                dispatch(_receivedContracts([],options.id));
            });
        }
    }

    static searchByRooms(from, to, roomIds=[], onSuccess, onFailure, options={id:null}){
        const url = sformat("{0}/searchByRooms", API_ROUTE);
        const data = { from: from, to: to, rooms: roomIds, includeBalance: true}
        return dispatch => {
            dispatch(_request())
            axios.post(url, data, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                onSuccess(response);
                dispatch(_receivedContractWrappers(response.data.contractWrappers, options.id));
            }).catch(error => {
                onFailure(error);
                dispatch(_receivedContractWrappers([],options.id));
            });
        }
    }

    static fetchById(id, onSuccess, onFailure, options={id:null, includeBalance:false}){
        const url = sformat("{0}/{1}?includeBalance={2}", API_ROUTE, id, options.includeBalance);
        return dispatch => {
            dispatch(_request());
            axios.get(url,{
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                if(options.includeBalance && response.data.balance){
                    response.data.contract.balance = response.data.balance;
                }
                onSuccess(response);
                dispatch(_receivedContract(response.data.contract, options.id));
            }).catch(error => {
                onFailure(error);
                dispatch(_receivedContract(null,options.id));
            });
        }
    }

    static create(data, onSuccess, onFailure, options={id:null}){
        const url = API_ROUTE;
        return dispatch => {
            dispatch(_request());
            axios.post(url, data, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                onSuccess(response);
                dispatch(_receivedContractUpdate(response.data.contract, options.id));
            }).catch(error => {
                onFailure(error);
                dispatch(_receivedContractUpdate(null,options.id));
            })
        }
    }

    static updateById(id, data, onSuccess, onFailure, options={id:null}){
        const url = sformat("{0}/update/{1}", API_ROUTE, id);
        return dispatch => {
            dispatch(_request());
            axios.post(url, data, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                onSuccess(response);
                dispatch(_receivedContractUpdate(response.data.contract, options.id));
            }).catch(error => {
                onFailure(error);
                dispatch(_receivedContractUpdate(null,options.id));
            })
        }
    }

    static setEnableById(id, enable, onSuccess, onFailure, options={id:null}){
        const url = sformat("{0}/setEnable/{1}", API_ROUTE, id);
        return dispatch => {
            dispatch(_request());
            axios.post(url, {enabled: enable}, {
                headers: defaultAxiosHeaders(),
                withCredentials: true
            }).then(response => {
                onSuccess(response);
                dispatch(_receivedContractUpdate(response.data.contract, options.id));
            }).catch(error => {
                onFailure(error);
                dispatch(_receivedContractUpdate(null,options.id));
            })
        }
    }

    static reset = () => ( _reset() );
}

export {
    ContractActionService,
    RESETTING,
    REQUESTING,
    RECEIVED_CONTRACTS,
    RECEIVED_CONTRACT,
    RECEIVED_CONTRACT_REMOVAL,
    RECEIVED_CONTRACT_WRAPPERS,
    CONTRACT_TYPE_SHORT_TERM,
    CONTRACT_TYPE_LONG_TERM,
    CONTRACT_TYPES
}

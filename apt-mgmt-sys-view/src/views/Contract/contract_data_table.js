import React, {PropTypes} from 'react';
import {connect} from 'react-redux';
import sematable, {Table, makeSelectors} from 'sematable'
import {Button, Row, Col} from 'reactstrap'
import {NavLink, Link, withRouter} from 'react-router-dom';
var sformat = require('string-format')
const selectors = makeSelectors("ContractDataTable");
import {ContractActionService} from '../../actions/contract_action';
import RemarkModal from '../RemarkBox/remark_modal';
const util = require('../../utilities')
import _ from "lodash"
const {dformat} = util;
const ROUTE_URL = "/contracts"

//TODO refactor *URGENT*
//TODO stream data (onSort, onPaginate, onPageSizeIncreased, onFilterIncrased)
const _ActionButtonContainer = ({dispatch, row, history}) => {
    const onRemove = () => {
        const contractId = row.id
        dispatch(ContractActionService.setEnableById(contractId, false, response => {
            console.log(sformat("SET ENABLE {0} with response: ", contractId));
            console.log(response);
        }, error => {
            console.log(sformat("REMOVED id={0} FAILED WITH ERROR: ", contractId));
            console.log(error.response.data.error);
        }));
    }
    let style = {
        'width':'70px',
        'margin-left': '10px'
    }
    return(
        <Row>
            <Col sm='2' >
                <Button color="primary"
                        size="sm"
                        style={style}
                        onClick={row.toggle}
                        hidden={!row.remark || row.remark.length < 1}>
                    Remark
                </Button>
            </Col>
            <Col sm='2'>
                <Link to={sformat("{0}/{1}/view", ROUTE_URL, row.id)}>
                    <Button color="primary" size="sm" style={style}>View</Button>
                </Link>
            </Col>
            <Col sm='2'>
                <Link to={sformat("{0}/{1}/update", ROUTE_URL, row.id)}>
                    <Button size="sm" color="warning" style={style}>Update</Button>
                </Link>
            </Col>
            <Col sm='2' >
                <Button color="danger" size="sm" style={style} onClick={onRemove}>Remove</Button>
            </Col>
        </Row>
    )
}

let ActionButtonContainer = withRouter(connect(null)(_ActionButtonContainer));

const columns = [
    {key: 'id', header: 'ID', sortable: true, searchable: true, primaryKey: true, className:"my-data-table-td"},
    {key: 'owner', header: 'Owner', sortable: true, searchable: true, className:"my-data-table-td"},
    {key: 'roomNumber', header: 'Room', sortable: true, searchable: true, className:"my-data-table-td"},
    {key: 'startDate', header: 'Start Date', sortable: true, searchable: true, className:"my-data-table-td"},
    {key: 'endDate', header: 'endDate', sortable: true, searchable: true, className:"my-data-table-td"},
    {key: 'active', header: 'Active', sortable: true, searchable: true, className:"my-data-table-td"},
    {key: 'actions', header: "Actions", Component: ActionButtonContainer}
]

const propTypes = {
    headers: PropTypes.object.isRequired,
    data: PropTypes.array.isRequired,
    primaryKey: PropTypes.string.isRequired
}


class _ContractDataTable extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            remark: "",
            modal: false
        }
        this.toggle = this.toggle.bind(this);
    }

    toggle(){
        this.setState({modal: !this.state.modal})
    }
    componentWillReceiveProps(nextProps){
        if (nextProps.data.length > 0) {
            _.forEach(nextProps.data, x => {
                x.toggle = () => this.setState({remark: x.remark, modal: !this.state.modal}) ;
            })
        }
        // this.setState({users: nextProps.users});
        if (nextProps.filter.length > this.props.filter.length){
            // SEARCH NEXT FILTER TOKEN
            // send request for the data[length-1] and union with initialData
        }

        if (nextProps.tablePageInfo.page > this.props.tablePageInfo.page){
            // SEARCH NEXT PAGE RANGE -> [page*pageSize, (page+1)*pageSize]
        }

        if(nextProps.tablePageInfo.pageSize > this.props.tablePageInfo.pageSize){
            // SEARCH REMAINING INCREASE PAGEZE DIFFERENCE -> [page*pageSize, (page*newPageSize - page*pageSize)]

        }

    }
    render(){
        return(
            <div>
            <Table {...this.props}
            columns={columns}/>
            <RemarkModal className="ContractRemark" toggle={this.toggle} modal={this.state.modal} remark={this.state.remark}/>
            </div>
        )
    }
}

const contractsToData = (contracts) => {
    return _.map(contracts, contract => {
        return {
            id: String(contract.id),
            owner: sformat("{0} {1}", contract.customerProfile.firstname, contract.customerProfile.lastname),
            roomNumber: contract.bookedRoom.roomNumber,
            startDate: dformat(new Date(contract.startDate), {month_num: true}),
            endDate: dformat(new Date(contract.endDate), {month_num: true}),
            active: contract.active ? "YES" : "NO",
            remark: contract.remark,
            enabled: contract.enabled
        }
    });
}

const mapStateToProps = state => {
    
    return {
        tableFilter: selectors.getFilter(state),
        tablePageInfo: selectors.getPageInfo(state),
        table: state.sematable.ContractDataTable,
    }
}

_ContractDataTable.propTypes = propTypes;
const ContractDataTable = connect(mapStateToProps)(sematable('ContractDataTable', _ContractDataTable, columns, {defaultPageSize: 10}));
export {
    ContractDataTable,
    contractsToData
}

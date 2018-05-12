import React from 'react';
import {connect} from 'react-redux';
import {Row, Col, Container, Card, CardTitle, CardBody, CardBlock, CardSubtitle, Button,
FormGroup, Label, Input} from 'reactstrap'
import {ContractDataTable, contractsToData} from './contract_data_table';
import {ContractActionService} from '../../actions/contract_action';
import { makeSelectors } from 'sematable';
import {Link} from 'react-router-dom';
import DatePicker from 'react-date-picker'
const util = require('../../utilities')
import _ from "lodash"
const {dformat} = util;
const sformat = require('string-format')
const tableSelector = makeSelectors("ContractDataTable");
const COMPONENT_URL = "/contracts"

class ContractComponent extends React.Component {
    constructor(props){
        super(props);
        const _date = new Date();
        const _date2= new Date();
        _date2.setDate(_date.getDate()+1);
        this.state = {
            contracts: [],
            from: _date,
            to: _date2,
            radio: {nameRadio: true, roomRadio: false}
        }
        this.setRadio = this.setRadio.bind(this);
        this.fetchData = this.fetchData.bind(this);
    }

    setRadio(e){
        const name = e.target.name;
        const radio = this.state.radio;
        var newRadio = _.clone(radio);
        for(var key in radio){
            if (name==key) newRadio[key]=true;
            else newRadio[key]=false;
        }
        this.setState({radio: newRadio}, () => {console.log(this.state)});
    }

    componentWillReceiveProps(nextProps){
        if(this.props.filterText!=nextProps.filterText){
            this.fetchData(nextProps.filterText);
        }
        this.setState({contracts: nextProps.contracts});

    }

    fetchData(keyword){
        const {dispatch} = this.props;
        const queryJson = {
            from: this.state.from.getTime(),
            to: this.state.to.getTime(),
        }

        if (this.state.radio.roomRadio) queryJson.roomNumber = keyword!=null ? (keyword.length!=0 ? keyword : null) : null;
        if (this.state.radio.nameRadio) queryJson.customerName = keyword!=null? (keyword.length != 0 ? keyword : null) : null;
        dispatch(ContractActionService.fetchAll(queryJson, response => {}, error => {
            console.log(error);
            console.log(JSON.stringify(error));
        }));
    }

    componentDidMount(){
        this.fetchData("")

    }
    render(){
        const filterStyle = {'margin-top':'5px', 'margin-right':'5px'}
        const dateColStyle = {'margin-left':'-30px'}
        const filterButtonColStyle = {'margin-left':'-50px'}
        const radioColStyle = {'margin-left': '80px'}
        return (
            <Card>
                <CardBlock>
                    <CardTitle>Contract Management</CardTitle>
                    <hr/>
                    <CardSubtitle>
                        <Row>
                        <Col sm={2}><Link to={sformat("{0}/create", COMPONENT_URL)}><Button outline color="primary">Create</Button></Link></Col>
                        <Col sm={3} style={dateColStyle}>
                            <FormGroup row><span style={filterStyle}>From</span>
                                <DatePicker
                                    calendarClassName={"my-calendar-in-view"}
                                    onChange={e => {
                                        this.setState({from: new Date(e.getTime())}, this.fetchData(this.props.filterText))
                                    }}
                                    value={this.state.from}
                                />
                            </FormGroup>
                        </Col>
                        <Col sm={3} style={dateColStyle}>
                            <FormGroup row>
                            <span style={filterStyle}>To</span>
                            <DatePicker
                                calendarClassName={"my-calendar-in-view"}
                                onChange={e => {
                                    this.setState({to: new Date(e.getTime())}, ()=>this.fetchData(this.props.filterText))
                                }}
                                value={this.state.to}
                            />
                            </FormGroup>
                        </Col>
                        <Col sm={{'size':2, 'offset':1}} style={radioColStyle} >
                          <FormGroup check>
                            <Label check>
                              <Input type="radio" name="roomRadio"
                                onChange={this.setRadio}
                                checked={this.state.radio.roomRadio}
                              />{' '}
                              Room
                            </Label>
                          </FormGroup>
                          <FormGroup check>
                            <Label check>
                              <Input type="radio" name="nameRadio"
                                onChange={this.setRadio}
                                checked={this.state.radio.nameRadio}
                              />{' '}
                              Name
                            </Label>
                          </FormGroup>
                        </Col>
                        </Row>
                    </CardSubtitle>
                    <ContractDataTable data={contractsToData(this.state.contracts)}/>
                </CardBlock>
            </Card>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        contracts: state.contractComponent.contracts,
        filterText: state.sematable && state.sematable.ContractDataTable && state.sematable.ContractDataTable.filterText
                    ? state.sematable.ContractDataTable.filterText : ""
    }
}

export default connect(mapStateToProps)(ContractComponent);

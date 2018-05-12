import React from 'react';
import {connect} from 'react-redux';
import {Row, Col, Container, Card, CardTitle,
    CardBody, CardBlock, CardSubtitle, Button,
    Form, FormGroup, Label, Input, FormText, Alert, Badge
} from 'reactstrap'
import FileList from '../FileList/file_list';
import EntityPolicyModalForm from '../EntityPolicy/entity_policy_modal_form';
import EntityPolicyList from '../EntityPolicy/entity_policy_list';
import DatePicker from 'react-date-picker';
import Select from 'react-select'
import {ContractActionService, CONTRACT_TYPES} from '../../actions/contract_action';
import {CustomerProfileActionService} from '../../actions/customer_profile_action';
import {FileEntityAction} from '../../actions/file_entity_action';
import {RoomActionService} from '../../actions/room_action';
import {EntityPolicyEdgeAction} from '../../actions/entity_policy_edge_action';
import { TRANSACTION_TYPE_CONTRACT } from '../../actions/transaction_action';
import {Link, withRouter} from 'react-router-dom';
import _ from "lodash"
const sformat = require('string-format');

const MODE_UPDATE = "MODE_UPDATE"
const MODE_VIEW = "MODE_VIEW"
const MODE_CREATE = "MODE_CREATE"

const COMPONENT_URL = "/contracts";

class ContractView extends React.Component {
    constructor(props){
        super(props);
        const _date = new Date();
        const _date2= new Date();
        _date2.setDate(_date.getDate()+1);
        this.state = {
            contractId: this.props.match.params.id,
            mode: MODE_CREATE,
            localContract: {},
            rooms: [],
            _buttonStyle:{'width':'80px'},
            _defaultStartDate: _date,
            _defaultEndDate: _date2,
            files: [],
            initialFiles: [],
            entityPolicyEdges: [],
            initialEntityPolicyEdges: []
        }

        this.modifiableField_C_U = this.modifiableField_C_U.bind(this);
        this.getButtons = this.getButtons.bind(this);
        this.getTitle = this.getTitle.bind(this);
        this.create = this.create.bind(this);
        this.update = this.update.bind(this);
        this.remove = this.remove.bind(this);
        this.onChangedInput = this.onChangedInput.bind(this);
        this.getSelectOptions = this.getSelectOptions.bind(this);
        this.onFileUpload = this.onFileUpload.bind(this);
        this.removeFile = this.removeFile.bind(this);
        this.onPushEdge = this.onPushEdge.bind(this);
        this.removeEdge = this.removeEdge.bind(this);
    }
    // View Configuration - Method that triggers form to render based on mode //
    configureViewType(nextProps){
        if (nextProps.match && nextProps.match.params){
            const method = nextProps.match.params.method;
            if (typeof(method) == 'string'){
                const x  = method.toLowerCase();
                switch(x){
                    case 'update': this.setState({mode: MODE_UPDATE}); break;
                    default: this.setState({mode: MODE_VIEW});
                }
            }
        } else if (this.state.mode == MODE_CREATE){
            this.setState({localContract: {}})
        }
    }

    getSelectOptions(In, extract){ // extract -> [value, label]
        return _.map(In, x => { let tuple = extract(x); let a = tuple[0]; let b = tuple[1]; return {value: a, label: b} });
    }

    onChangedInput(key, value){
        const newObj = this.state.localContract;
        newObj[key]=value;
        this.setState({
            localContract: newObj
        })
    }

    //
    modifiableField_C_U(createBool, updateBool){
        switch(this.state.mode){
            case(MODE_UPDATE): return !updateBool;
            case(MODE_CREATE): return !createBool;
            default: return true;
        }
    }

    jsonFromForm(){
        const localContract = this.state.localContract;
        return {
            bookedRoomId: localContract.bookedRoom.id,
            customerProfileId: localContract.customerProfile.id,
            startDate: localContract.startDate ? localContract.startDate : this.state._defaultStartDate.getTime(),
            endDate: localContract.endDate ? localContract.endDate : this.state._defaultEndDate.getTime(),
            active: localContract.active,
            remark: localContract.remark,
            contractType: localContract.contractType,
            fileEntities: _.map(this.state.files, x => x.id),
            entityPolicyEdges: _.map(this.state.entityPolicyEdges, edge => ({
                policyId: edge.policy ? edge.policy.id : null,
                startDate: edge.startDate ? edge.startDate : null,
                id: edge.id ? edge.id : -1
            })),
            active: new Boolean(localContract.active)
        };
    }
    ////////////////////////////
    // FileEntity Manipulation//
    ////////////////////////////
    onFileUpload(e){
        const { dispatch } = this.props;
        var file = e.target.files[0];
        dispatch(FileEntityAction.create(e.target.files[0], TRANSACTION_TYPE_CONTRACT, res => {
            if (res.data.fileEntity && res.data.fileEntity.id){
                this.setState({files: [...this.state.files, res.data.fileEntity]});
            }
        }, err => {}));
        e.target.value = null;
    }

    removeFile(i){
        this.setState({files: _.filter(this.state.files, (x,j) => i != j )});
    }

    //////////////////////////////////
    // EntityPolicyEdge Manipulation//
    /////////////////////////////////
    onPushEdge(edge){
        // edge = {policy: x, starTime: y};
        this.setState({entityPolicyEdges: [...this.state.entityPolicyEdges, edge]});
    }

    removeEdge(i){
        this.setState({entityPolicyEdges: _.filter(this.state.entityPolicyEdges, (x,j) => i != j )});
    }

    create(){
        const {dispatch} = this.props;
        dispatch(ContractActionService.create(this.jsonFromForm(), () => {
            this.props.history.replace(COMPONENT_URL);
        }, () => {
            console.log("FAILED");
        }));
    }

    update(){
        const {dispatch} = this.props;
        const json = this.jsonFromForm();
        dispatch(ContractActionService.updateById(this.state.contractId, json, ()=>{
            this.props.history.push(sformat("{0}/{1}/view", COMPONENT_URL, this.state.contractId));
        }, (error) => {
            console.log("UPDATE FAILED");
            alert("Update failed " + ((error.response && error.response.data) ? ": " + error.response.data.error : ""));
        }))
    }

    remove(){
        const {dispatch} = this.props;
        dispatch(ContractActionService.setEnableById(this.state.contractId, false, (response)=>{
            this.props.history.replace(COMPONENT_URL);
        }, (error)=>{
            console.log("-> FAIL REMOVAL");
            alert("Removal failed " + (error.response && error.response.data) ? ": " + error.response.data.error : "");
        }));
    }
    // Render Methods ===========
    getButtons(){
        const mode = this.state.mode;
        switch(mode){
            case MODE_VIEW:
                return (
                    <Row>
                    <Col sm="1">
                        <Link to={sformat("{0}/{1}/update", COMPONENT_URL, this.state.contractId)}>
                            <Button color="warning" style={this._buttonStyle}>Edit</Button>
                        </Link>
                    </Col>
                    </Row>
                );
            case MODE_UPDATE:
                return (<Row>
                    <Col sm="1"><Button color="warning" style={this._buttonStyle} onClick={this.update}>Update</Button></Col>
                    <Col sm="1"><Button color="danger" style={this._buttonStyle} onClick={this.remove}>Remove</Button></Col>
                    </Row>
                )
            default: // MODE_CREATE
                return (<Row><Col sm="1"><Button outline color="primary" style={this._buttonStyle} onClick={this.create}>Create</Button></Col></Row>)
        }
    }
    getTitle(){
        const localContract = this.state.localContract;
        switch(this.state.mode){
            case MODE_CREATE:
                return (<div>Create Contract</div>)
            default:
                return (<div>
                    Contract #{localContract.id} {
                        (() => {
                            if (this.state.localContract.enabled) return (<Badge color="success">Is Active</Badge>)
                            else return <Badge color="danger">Deactivated</Badge>
                        })()
                    } </div>
                )
        }
    }

    // Component Cycle...=========
    componentDidMount(){
        const { dispatch } = this.props;
        dispatch(ContractActionService.reset());
        dispatch(RoomActionService.reset());
        dispatch(CustomerProfileActionService.reset());
        dispatch(FileEntityAction.reset());
        dispatch(EntityPolicyEdgeAction.reset());

        if (this.props.match && this.props.match.params){
            const id = this.props.match.params.id;
            if (id && !isNaN(parseInt(id))){
                const fetchOption = { includeBalance: true }
                dispatch(ContractActionService.fetchById(id, (response) => {}, (error) => {
                    console.log("FETCH CONTRACT BY ERR");
                    console.log(error);
                }, fetchOption));
                dispatch(FileEntityAction.listByIdAndType(id, TRANSACTION_TYPE_CONTRACT, res=>{}, err=>{}));
                dispatch(EntityPolicyEdgeAction.listByIdAndType(id, TRANSACTION_TYPE_CONTRACT, res=>{}, err=>{}));
            }
        }
        dispatch(RoomActionService.fetchRooms((reponse)=>{}, (error)=>{}))
        dispatch(CustomerProfileActionService.fetchCustomerProfiles((response)=>{}, (error)=>{}))

        this.configureViewType(this.props);
    }
    componentWillReceiveProps(nextProps){
        const {dispatch} = this.props;
        const files = this.state.files;
        if (nextProps.contract){
            this.setState({
                localContract: nextProps.contract,
            });
        }
        this.setState({
            rooms: nextProps.rooms,
            customerProfiles: nextProps.customerProfiles,
        })
        if(!_.isEqual(nextProps.entityPolicyEdges, this.state.initialEntityPolicyEdges)){
            this.setState({initialEntityPolicyEdges: nextProps.entityPolicyEdges, entityPolicyEdges: nextProps.entityPolicyEdges});
        }
        if(!_.isEqual(nextProps.files, this.state.initialFiles)){
            this.setState({initialFiles: nextProps.files, files: nextProps.files});
        }
        if(!_.isEqual(nextProps.match.params, this.props.match.params)){
            if (this.props.match && this.props.match.params){
                const id = this.props.match.params.id;
                if (id && !isNaN(parseInt(id))){
                    dispatch(EntityPolicyEdgeAction.listByIdAndType(id, TRANSACTION_TYPE_CONTRACT, res=>{}, err=>{}));
                }
            }
            this.configureViewType(nextProps);
        }
    }

    render(){
        const size = 5;
        console.log(this.state);
``
        const profileId = this.state.localContract &&
                          this.state.localContract.customerProfile &&
                          this.state.localContract.customerProfile.id ? this.state.localContract.customerProfile.id : null;
        const balanceId = this.state.localContract && this.state.localContract.balance ? this.state.localContract.balance.id : null;
        const contractTypeSelectMapper = (x) => x ? [x, x.split("_").join(" ")] : ["",""];
        const roomToLabel = (x) => sformat("{0} - {1}", x.roomNumber, x && x.roomType ? x.roomType.split("_").join(" ").toLowerCase() : "");
        const customerToLabel = (x) => sformat("{0} - {1} {2}", x ? x.id : -1, x ? x.firstname : "none", x ? x.lastname : "");
        return (
            <Card>
                <CardBlock>
                    <CardTitle>{this.getTitle()}</CardTitle>
                    <hr/>
                        <FormGroup row>
                            <Label sm={2}>Customer</Label>
                            <Col sm={size}>
                                <Select
                                options={
                                    this.getSelectOptions(this.state.customerProfiles,
                                        x=>[x, customerToLabel(x)])
                                }
                                onChange={e => this.onChangedInput("customerProfile", e.value)}
                                value={this.state.localContract && this.state.localContract.customerProfile
                                     ? {value: this.state.localContract.customerProfile, label: customerToLabel(this.state.localContract.customerProfile)} : ""}
                                disabled={this.modifiableField_C_U(true, false)}
                                />
                            </Col>
                            <Col sm={1} hidden={this.state.mode != MODE_VIEW && profileId == null}>
                                <Link to={sformat("/customerProfileManagement/customerProfiles/{0}/view", profileId)}
                                      style={{'margin-left':'-18px'}}>
                                    <Button color="primary"><i className="fa fa-user"/>{' '}Profile</Button>
                                </Link>
                            </Col>
                            <Col sm={1} hidden={this.state.mode != MODE_VIEW && balanceId == null}>
                                <Link to={sformat("/balances/{0}/view", balanceId)}
                                      style={{'margin-left':'-5px'}}>
                                    <Button color="primary"><i className="fa fa-money"/>{' '}Balance</Button>
                                </Link>
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>Room</Label>
                            <Col sm={size}>
                            <Select
                                options={
                                    this.getSelectOptions(this.state.rooms,
                                        x=>[x, roomToLabel(x)])
                                }
                                onChange={e => this.onChangedInput("bookedRoom", e.value)}
                                value={this.state.localContract && this.state.localContract.bookedRoom
                                     ? {value: this.state.localContract.bookedRoom, label: roomToLabel(this.state.localContract.bookedRoom)}: null}
                                disabled={this.modifiableField_C_U(true, true)}
                            />
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>Start Date</Label>
                            <Col sm={size}>
                                <DatePicker
                                    calendarClassName={"my-calendar-in-view"}
                                    onChange={(e)=>{
                                        if(this.state.mode != MODE_VIEW) this.onChangedInput("startDate", e.getTime())

                                    }}
                                    value={this.state.localContract.startDate ?
                                        new Date(this.state.localContract.startDate) : this.state._defaultStartDate}
                                />
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>End Date</Label>
                            <Col sm={size}>
                                <DatePicker
                                    calendarClassName={"my-calendar-in-view"}
                                    onChange={(e)=>{
                                        if(this.state.mode != MODE_VIEW) this.onChangedInput("endDate", e.getTime())

                                    }}
                                    value={this.state.localContract.endDate ?
                                        new Date(this.state.localContract.endDate) : this.state._defaultEndDate}
                                />
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>File</Label>
                            <Col sm={size}>
                                <Input type="file" onChange={this.onFileUpload} hidden={this.state.mode==MODE_VIEW}/>
                                <FileList files={this.state.files} removeFile={this.removeFile} mode={this.state.mode}/>
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>Contract Type</Label>
                            <Col sm={size}>
                                <Select
                                    options={this.getSelectOptions(CONTRACT_TYPES, contractTypeSelectMapper)}
                                    value={this.getSelectOptions([this.state.localContract.contractType], contractTypeSelectMapper)[0]}
                                    onChange={(e)=> this.onChangedInput("contractType", e.value)}
                                    disabled={this.modifiableField_C_U(true, true)}
                                />
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>Transaction Policies</Label>
                            <Col sm={size}>
                                <EntityPolicyModalForm
                                    mode={this.state.mode}
                                    transactionType={TRANSACTION_TYPE_CONTRACT}
                                    onPushEdge={this.onPushEdge}
                                />
                                <EntityPolicyList
                                    edges={this.state.entityPolicyEdges}
                                    removeEdge={this.removeEdge}
                                    mode={this.state.mode}
                                />
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>Remark</Label>
                            <Col sm={size}>
                                <Input type="textarea" placeholder="remark here.."
                                    value={this.state.localContract.remark}
                                    onChange={e => this.onChangedInput("remark", e.target.value)}
                                    disabled={this.modifiableField_C_U(true, true)}
                                />
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Label sm={2}>Active</Label>
                            <Col sm={size}>
                                <FormGroup check>
                                <Label check className={'label-static'}>
                                    <Input type="checkbox"
                                         value={this.state.localContract.active}
                                         onChange={e => this.onChangedInput("active", e.target.value) }
                                         disabled={this.modifiableField_C_U(true, true)}
                                    />{' '}
                                </Label>
                                </FormGroup>
                            </Col>
                        </FormGroup>
                        <FormGroup>
                            {this.getButtons()}
                        </FormGroup>

                </CardBlock>
            </Card>
        )
    }
}

const mapStateToProps = state => {
    return {
        contract: state.contractComponent.contract,
        rooms: state.roomComponent.rooms,
        customerProfiles: state.customerProfileComponent.customerProfiles,
        files: state.fileEntityComponent.files,
        entityPolicyEdges: state.entityPolicyEdgeComponent.entityPolicyEdges
    }
}
export default withRouter(connect(mapStateToProps)(ContractView));

/*
<AsyncComponent
    multi={false}
    value={this.state.value}
    onChange={this.onChange}
    onValueClick={this.gotoUser}
    loadOptions={this.getUsers}
    backspaceRemoves={this.state.backspaceRemoves} />
</Col>
*/

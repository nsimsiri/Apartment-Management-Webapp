import React from 'react';
import {connect} from 'react-redux';
import {Row, Col, Container, Card, CardTitle, CardBody, CardBlock, CardSubtitle, Button} from 'reactstrap'
import UserDataTable from './user_data_table';
import {UserActionService} from '../../actions/user_action';
import { makeSelectors } from 'sematable';
import {Link} from 'react-router-dom';
import _ from "lodash"
const sformat = require('string-format')
const tableSelector = makeSelectors("UserDataTable");

class UserComponent extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            users: []
        }
        this.userToDataTableData = this.userToDataTableData.bind(this);
        console.log("UserComponent");
        console.log(this.props);
    }
    componentDidMount(){
        this.props.dispatch(UserActionService.fetchUsers());
    }
    componentWillReceiveProps(nextProps){
        // console.log("[UserComponent componentWillReceiveProps]");
        // console.log(nextProps);
        this.setState({users: nextProps.users});
    }

    userToDataTableData(users){
        if(!users) return [];
        var renderedUsers = _.map(users, (user => {
            return {
                id: String(user.id),
                username: String(user.username),
                password: String(user.password),
                roles: _.chain(user.roles).map(x => x.role).reduce((a, b) => a + ", " + b).value(),
                enabled: String(user.enabled)
            }
        }));
        return renderedUsers;
    }
    render(){
        return (
            <Card>
                <CardBlock>
                    <CardTitle>User Management</CardTitle>
                    <hr/>
                    <CardSubtitle>
                        <Link to={sformat("{0}/create", this.props.match.url)}><Button outline color="primary">Create User</Button></Link>
                    </CardSubtitle>
                    <UserDataTable data={this.userToDataTableData(this.state.users)} bob={()=>{return 1}}    />
                </CardBlock>
            </Card>
        )
    }
}

const mapStateToProps = state => {
    return {
        users: state.userComponent.users,
        // tableFilter: tableSelector.getFilter(state),
        // tablePageInfo: tableSelector.getPageInfo(state)
        // UserDataTable: state.sematable.UserDataTable
    }
}

export default connect(mapStateToProps)(UserComponent);

// const dummyData = [
//     {
//         id: 1,
//         username: "Kappa",
//         password: "Keepo",
//         enabled: 'true',
//         roles: ['a','b'].reduce((a,b)=>{return a+', '+b})
//     },
//     {
//         id: 2,
//         username: "PogChamp",
//         password: "4Head",
//         enabled: 'true',
//         roles: ['a']
//     },
//     {
//         id: 3,
//         username: "PogawddfChamp",
//         password: "4Head",
//         enabled: 'true',
//         roles: ['a']
//     }
// ]

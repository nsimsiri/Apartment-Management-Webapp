import React, {Component} from 'react';
import {Switch, Route, Redirect} from 'react-router-dom';
import {Container} from 'reactstrap';
import Header from '../../components/Header/';
import Sidebar from '../../components/Sidebar/';
import Breadcrumb from '../../components/Breadcrumb/';
import Aside from '../../components/Aside/';
import Footer from '../../components/Footer/';
import Dashboard from '../../views/Dashboard/';

import webglComponent from '../../views/webgl'

// Business Logic Component
import UserComponent from '../../views/User/user_component';
import UserView from '../../views/User/user_view';
import RoleComponent from '../../views/Role/role_component';
import RoleView from '../../views/Role/role_view';
import CustomerProfileComponent from '../../views/CustomerProfile/customer_profile_component';
import CustomerProfileView from '../../views/CustomerProfile/customer_profile_view';
import RoomComponent from '../../views/Room/room_component';
import RoomView from '../../views/Room/room_view';
import ContractComponent from '../../views/Contract/contract_component';
import ContractView from '../../views/Contract/contract_view';
import BalanceComponent from '../../views/Balance/balance_component';
import BalanceView from '../../views/Balance/balance_view';
import TransactionView from '../../views/Transaction/transaction_view';
import TransactionComponent from '../../views/Transaction/transaction_component';
import TCPView from '../../views/TCP/tcp_view';
import TCPComponent from '../../views/TCP/tcp_component';
import AppContainer from '../../components/AppContainerComponent';
import {SessionActionService} from '../../actions/session_action';
import {connect} from 'react-redux';

class Full extends Component {
    constructor(props){
        console.log("(￣▽￣)ノ APP INITIALIZED (￣▽￣)ノ");
        super(props);
        this.state = {
            localSession: {}
        }
    }
    isAuthenticated(){
        const {dispatch} = this.props;
        dispatch(SessionActionService.requestAuthentication());
    }

    componentDidMount(){
        this.isAuthenticated();
    }

    componentWillReceiveProps(nextProps){
        this.setState({localSession: nextProps.session})
    }

  render() {
      if (this.state.localSession.hasReceived && !this.state.localSession.isAuthenticated) return (<Redirect from="/" to="/login"/>);
    else if (this.state.localSession.hasReceived && this.state.localSession.isAuthenticated) { return (
      <div className="app root-style">
        <Header />
        <div className="app-body">
          <Sidebar {...this.props}/>
          <main className="main">
            <Breadcrumb />
            <Container fluid>
              <Switch>
                <Route path="/todoapp/tasks" name="AppContainer" component={AppContainer}/>
                <Route path="/dashboard" name="Dashboard" component={Dashboard}/>
                <Route path="/userManagement/users/:id/:method" name="UserView" component={UserView}/>
                <Route path="/userManagement/users/create" name="UserView" component={UserView}/>
                <Route path="/userManagement/users" name="UserComponent" component={UserComponent}/>

                <Route path="/roleManagement/roles/:id/view" name="RoleView" component={RoleView}/>
                <Route path="/roleManagement/roles/create" name="RoleView" component={RoleView}/>
                <Route path="/roleManagement/roles" name="RoleComponent" component={RoleComponent}/>

                <Route path="/customerProfileManagement/customerProfiles/:id/:method" name="CustomerProfileView" component={CustomerProfileView}/>
                <Route path="/customerProfileManagement/customerProfiles/create" name="CustomerProfileView" component={CustomerProfileView}/>
                <Route path="/customerProfileManagement/customerProfiles" name="CustomerProfileComponent" component={CustomerProfileComponent}/>

                <Route path="/rooms/create" name="RoomView" component={RoomView}/>
                <Route path="/rooms/:id/:method" name="RoomView" component={RoomView}/>
                <Route path="/rooms" name="RoomComponent" component={RoomComponent}/>

                <Route path="/contracts/create" name="ContractView" component={ContractView}/>
                <Route path="/contracts/:id/:method" name="ContractView" component={ContractView}/>
                <Route path="/contracts" name="ContractComponent" component={ContractComponent}/>

                <Route path="/transactions/create" name="TransactionView" component={TransactionView}/>
                <Route path="/transactions/:id/:method" name="TransactionView" component={TransactionView}/>
                <Route path="/transactions/" name="TransactionComponent" component={TransactionComponent}/>

                <Route path="/balances/:id/:method" name="BalanceView" component={BalanceView}/>
                <Route path="/balances/" name="BalanceComponent" component={BalanceComponent}/>

                <Route path="/policies/create" name="TCPView" component={TCPView}/>
                <Route path="/policies/:id/:method" name="TCPView" component={TCPView}/>
                <Route path="/policies" name="TCPComponent" component={TCPComponent}/>

                <Route path="/webgl" names="webglComponent" component={webglComponent}/>
                <Redirect from="/" to="/dashboard"/>
              </Switch>
            </Container>
          </main>
          <Aside />
        </div>
        <Footer />
      </div>
  )} else return (<h1> Loading.... </h1>)
  }
}


// const mapDispatchToProps = (dispatch) => {
//     return SessionActionService.requestAuthentication();
// }
const mapStateToProps = (state) => {
    return {
        session: state.session
    }
}

export default connect(mapStateToProps)(Full);

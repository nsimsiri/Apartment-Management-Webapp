import React, {Component} from 'react';
import {
  Badge,
  Dropdown,
  DropdownMenu,
  DropdownItem,
  Nav,
  NavItem,
  NavLink,
  NavbarToggler,
  NavbarBrand,
  DropdownToggle
} from 'reactstrap';
import {connect} from 'react-redux';
import {SessionActionService} from '../../actions/session_action';
import {Link, withRouter} from 'react-router-dom';
const sformat = require("string-format");


class Header extends Component {

  constructor(props) {
    super(props);
    this.toggle = this.toggle.bind(this);
    this.state = {
      dropdownOpen: false,
      localSession: this.props.session
    };
    this.logout = this.logout.bind(this);
  }

  logout(){
      this.props.dispatch(SessionActionService.logout())
  }

  componentWillReceiveProps(nextProps){
    //   if (this.isAuthenticated) this.props.history.push("/dashboard")
    //   this.setState({localSession: nextProps.session ? nextProps.session : this.state.localSession})
  }

  toggle() {
    this.setState({
      dropdownOpen: !this.state.dropdownOpen
    });
  }

  sidebarToggle(e) {
    e.preventDefault();
    document.body.classList.toggle('sidebar-hidden');
  }

  sidebarMinimize(e) {
    e.preventDefault();
    document.body.classList.toggle('sidebar-minimized');
  }

  mobileSidebarToggle(e) {
    e.preventDefault();
    document.body.classList.toggle('sidebar-mobile-show');
  }

  asideToggle(e) {
    e.preventDefault();
    document.body.classList.toggle('aside-menu-hidden');
  }
  /*
  <NavItem className="d-md-down-none">
    <NavLink href="#"><i className="icon-bell"></i><Badge pill color="danger">5</Badge></NavLink>
  </NavItem>
  <NavItem className="d-md-down-none">
    <NavLink href="#"><i className="icon-list"></i></NavLink>
  </NavItem>
  <NavItem className="d-md-down-none">
    <NavLink href="#"><i className="icon-location-pin"></i></NavLink>
  </NavItem>
  */

  render() {
    return (
      <header className="app-header navbar">
        <NavbarToggler className="d-lg-none" onClick={this.mobileSidebarToggle}>&#9776;</NavbarToggler>
        <NavbarBrand href="#"></NavbarBrand>
        <NavbarToggler className="d-md-down-none" onClick={this.sidebarToggle}>&#9776;</NavbarToggler>
        <Nav className="d-md-down-none" navbar>
          <NavItem className="px-3">
            <NavLink href="/#/dashboard">Dashboard</NavLink>
          </NavItem>
        </Nav>
        <Nav className="ml-auto" navbar>
          <NavItem>
            <Dropdown isOpen={this.state.dropdownOpen} toggle={this.toggle}>
              <DropdownToggle className="nav-link dropdown-toggle">
                <img src={'img/avatars/default_avatar.png'} className="img-avatar"/>
                <span className="d-md-down-none">{this.state.localSession.user.username}</span>
              </DropdownToggle>
              <DropdownMenu right className={this.state.dropdownOpen ? 'show' : ''}>
                  <DropdownItem header tag="div" className="text-center"><strong>Account</strong></DropdownItem>
                  <Link to={sformat("/userManagement/users/{0}/view", this.state.localSession.user.id)}>
                    <DropdownItem>
                        <i className="fa fa-user"></i>Profile
                        </DropdownItem>
                  </Link>
                  <Link to={sformat("/balances/{0}/view", this.state.localSession.balanceId)}>
                    <DropdownItem><i className="fa fa-money"></i> Balance</DropdownItem>
                    </Link>

                  <DropdownItem onClick={this.logout}><i className="fa fa-lock"></i> <a>Logout</a></DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </NavItem>
        </Nav>
        <NavbarToggler className="d-md-down-none" type="button" onClick={this.asideToggle}>&#9776;</NavbarToggler>
      </header>
    )
  }
}

const mapStateToProps = (state)=>{
    return {
        session: state.session
    }
}

// export default Header
export default withRouter(connect(mapStateToProps)(Header));

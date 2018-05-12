import {EMPLOYEE, ADMIN} from '../../actions/role_action';
export default {
  items: [
    {
      name: 'Dashboard',
      url: '/dashboard',
      icon: 'icon-speedometer',
      badge: {
        variant: 'info'
        // text: 'NEW'
      }
    },
    // {
    //   title: true,
    //   name: '',
    //   wrapper: {            // optional wrapper object
    //     element: '',        // required valid HTML5 element tag
    //     attributes: {}        // optional valid JS object with JS API naming ex: { className: "my-class", style: { fontFamily: "Verdana" }, id: "my-id"}
    //   },
    //   class: ''             // optional class names space delimited list for title item ex: "text-center"
    // },
    {
        name: 'Rooms',
        url: '/rooms',
        icon: 'circle-o',
        roles: []
    },
    {
        name: 'Customers',
        url: '/customerProfileManagement/customerProfiles',
        icon: 'circle-o',
        roles: []
    },
    {
        name: 'Contracts',
        url: '/contracts',
        icon: 'circle-o',
        roles: []
    },
    {
        name: 'Finances',
        url: '/finances',
        icon: 'circle-o',
        roles: [],
        children: [
            {
                name: 'Transactions',
                url: '/transactions',
                icon: 'circle-o',
                roles: []
            },
            {
                name: 'Balance',
                url: '/balances',
                icon: 'circle-o',
                roles: []
            },
            {
                name: 'Transaction Policy',
                url: '/policies',
                icon: 'circle-o',
                roles: []
            }
        ]
    },
    {
      name: 'User Management',
      url: '/userManagement',
      icon: 'circle-o',
      roles: [ADMIN],
      children: [
        {
          name: 'Users',
          url: '/userManagement/users',
          icon: 'circle-o',
          roles: [ADMIN]
        },
        {
          name: 'Role',
          url: '/roleManagement/roles',
          icon: 'circle-o',
          roles: [ADMIN]
        },
      ]
    },
  ]
};

package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.models.Authority;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.security.Role;
import com.apt_mgmt_sys.APIServer.services.AuthorityService;
import com.apt_mgmt_sys.APIServer.services.EmployeeProfileService;
import com.apt_mgmt_sys.APIServer.services.UserService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AuthenticationController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private EmployeeProfileService employeeProfileService;
    @Autowired
    private BalanceEntityEdgeService balanceEntityEdgeService;
    @RequestMapping(value = "/attemptAuthentication", method = RequestMethod.GET)
    public ResponseEntity<Map> isAuthenticated(Authentication auth){
        Map m = null;
        try {
            m = new HashMap<>();
            org.springframework.security.core.userdetails.User springUser= (org.springframework.security.core.userdetails.User)auth.getPrincipal();
            if (springUser == null || auth == null) throw new Exception("Not logged in");

            m.put("isAuthenticated", auth.isAuthenticated());
            m.put("username", springUser.getUsername());
            System.out.println(springUser.getUsername());
            User user = userService.getUserByUsername(springUser.getUsername());
            List<Role> roles = springUser.getAuthorities()
                    .stream()
                    .map(x -> Role.valueOf(x.getAuthority())).collect(Collectors.toList());
            m = Utilities.buildAuthenticationResponse(auth.isAuthenticated(), springUser.getUsername(), user, roles, HttpStatus.OK.value(), null);
            EmployeeProfile profile = employeeProfileService.getByUser(user);
            IBalanceEntityEdge<?> edge = balanceEntityEdgeService.getByEntity(profile, TransactionType.EMPLOYEE_PROFILE);
            Balance balance = edge.getBalance();
            if (balance!=null) {
                m.put("balanceId", balance.getId());
            }
            System.out.println(auth.isAuthenticated());
        } catch (Exception e){
            e.printStackTrace();
            m = Utilities.buildAuthenticationResponse(false, null, null, null, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        } finally {
            m = (m == null) ? new HashMap<>() : m;
            return new ResponseEntity<>(m, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/customLogout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletRequest req, HttpServletResponse res){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            SecurityContextLogoutHandler logoutHandler =  new SecurityContextLogoutHandler();
            logoutHandler.logout(req, res, auth);
            logoutHandler.setInvalidateHttpSession(true);
        }
        return new ResponseEntity(HttpStatus.OK);

    }

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity PREFLIGHT_BYPASS(){
        return new ResponseEntity(HttpStatus.OK);
    }

}

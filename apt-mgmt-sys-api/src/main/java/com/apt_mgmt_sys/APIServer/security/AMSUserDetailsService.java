package com.apt_mgmt_sys.APIServer.security;

import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.services.AuthorityService;
import com.apt_mgmt_sys.APIServer.services.UserService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class AMSUserDetailsService implements UserDetailsService {

    private UserService userService;
    private AuthorityService authService;

    public AMSUserDetailsService(UserService userService, AuthorityService authService){
        this.authService = authService;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username){

        try {
            User user = userService.getUserByUsername(username);
            System.out.format("[USER-DETAILS-SERVICE] %s\n", username);
            if (user == null) {
                System.err.format("[%s]: Cannot find user of username <%s>\n", this.getClass().getSimpleName(), username);
                return null;
            }
            Set<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(user);
            System.out.format("[USER-DETAILS-SERVICE] authorities: %s\n", grantedAuthorities);
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Set<GrantedAuthority> getGrantedAuthorities(User user){
        Set<GrantedAuthority> grantedAuthorities =  authService.getAuthoritiesbyUser(user)
                .stream()
                .map((x)->new SimpleGrantedAuthority(x.getRole().toString()))
                .collect(Collectors.toSet());
        return grantedAuthorities.isEmpty() ?
                new HashSet<GrantedAuthority>(Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(Role.NONE.toString())})) :
                grantedAuthorities;
    }

}

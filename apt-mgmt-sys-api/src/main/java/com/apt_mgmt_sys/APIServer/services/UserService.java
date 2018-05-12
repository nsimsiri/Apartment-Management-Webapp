package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.repositories.AuthorityRepository;
import com.apt_mgmt_sys.APIServer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserRepository user_db;
    @Autowired
    AuthorityRepository auth_db;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EmployeeProfileService employeeProfileService;
    @Autowired
    AuthorityService authorityService;

    public User getUserById(Long id){
        return user_db.findOne(id);
    }

    public User getUserByUsername(String username){
        return user_db.findByUsername(username);
    }

    public List<User> getUsers(){
        List<User> users = new ArrayList<>();
        user_db.findAll().forEach(users::add);
        return users;
    }
    public User create(String username, String password) throws Exception {
        User _user = user_db.findByUsername(username);
        if (_user != null) {
            throw new Exception("Cannot create new user - Non-unique username");
        }
        String encodedPassword = passwordEncoder.encode(password); // encode this using spring-security bcrypt.
        User user = new User(username, encodedPassword);
        user = user_db.save(user);
        return user;

    }

    public User updatePasswordByIdForJson(Long id, Map<String, Object> json){
        String password = (String) json.getOrDefault("password", "");
        String confirmPassword = (String) json.getOrDefault("confirmPassword", "");
        return updatePasswordById(id, password, confirmPassword);
    }

    public User updatePasswordById(Long id, String password, String confirmPassword){
        User user = getUserById(id);
        if (user == null) return null;
        if (confirmPassword.length() == 0) return user;
        if (!password.equals(confirmPassword)) throw new IllegalArgumentException("Password doens't match Confirmed Password");
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        return  user_db.save(user);
    }

    public User removeById(Long id){
        User user = getUserById(id);
        auth_db.getAuthoritiesByUser(user).stream().forEach(x -> {
            authorityService.remove(x);
        });
        employeeProfileService.remove(employeeProfileService.getByUser(user));
        user.setEnabled(false);
        return user_db.save(user);
    }

    public boolean deleteById(Long id){
        User user = getUserById(id);
        auth_db.getAuthoritiesByUser(user).stream().forEach(x -> {
            auth_db.delete(x);
        });
        EmployeeProfile profile = employeeProfileService.getByUser(user);
        if (profile != null) employeeProfileService.deleteById(profile.getId());
        user_db.delete(id);

        user = getUserById(id);
        return user == null ? true : false;
    }

    public User getUserFromSession() throws Exception{
        User user = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) throw new IllegalAccessException("Security Contxt should be obtained from an authenticated path");
        Authentication authentication = securityContext.getAuthentication();
        if (authentication== null)
            throw new NullPointerException("NULL Authentication object obtained from securityContext when trying to get UserSession in UserService.getUserFromSession");
        Object principal =  authentication.getPrincipal();
        if (principal == null)
            throw new NullPointerException("NULL Principal obtained from authentication when trying to get UserSession in UserService.getUserFromSession");
        if (principal instanceof  org.springframework.security.core.userdetails.User){
            org.springframework.security.core.userdetails.User sessionSpringUser = (org.springframework.security.core.userdetails.User) principal;
            String username = sessionSpringUser.getUsername();
            if (username == null) throw new NullPointerException("Spring User Object contains null username. Spring User Object:  " + sessionSpringUser);
            user = getUserByUsername(username);
        }
        return user;
    }



}

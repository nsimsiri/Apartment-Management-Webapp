package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.models.Authority;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.security.Role;
import com.apt_mgmt_sys.APIServer.services.AuthorityService;
import com.apt_mgmt_sys.APIServer.services.UserService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/authorities")
public class AuthorityController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;

    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<List<Authority>> getAll(){
        return new ResponseEntity(authorityService.getAuthorities(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson = new HashMap<>();
        Authority auth = authorityService.getAuthorityById(id);
        if (auth == null){
            responseJson.put("error","Cannot find authority with id " + id);
            return ResponseEntity.badRequest().body(responseJson);
        }
        responseJson.put("authority", auth);
        return ResponseEntity.ok().body(responseJson);
    }

    @RequestMapping(value = {"/",""}, method = RequestMethod.POST)
    public ResponseEntity<Map> create(@RequestBody Map<String, Object> json){
        User user = null;
        Role role = null;
        Map<String, Object> responseJson = null;
        try {
            responseJson =new HashMap<>();
            if (json.containsKey("userId")){
                user = userService.getUserById((Long)Utilities.getFieldFromJSON(json, "userId", Long.class));
            } else if (json.containsKey("username")){
                user = userService.getUserByUsername((String)Utilities.getFieldFromJSON(json, "username", String.class));
            }
            if (user == null) throw new IllegalArgumentException("Unable to find User with JSON : " + json.toString());
            if (json.containsKey("role")){
                role = Role.valueOf((String) Utilities.getFieldFromJSON(json, "role", String.class));
            }
            if (role == null) throw new IllegalArgumentException("Unable to create Role from JSON : " + json.toString());

            Authority auth = authorityService.create(user, role);
            responseJson.put("authority", auth);
            return ResponseEntity.ok().body(responseJson);
        } catch (Exception e){
            responseJson = new HashMap<>();
            e.printStackTrace();
            responseJson =  new HashMap<>();
            responseJson.put("error",e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateById(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        return ResponseEntity.ok().body(null);
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> removeById(@PathVariable(value = "id") Long id){
        Authority auth = authorityService.getAuthorityById(id);
        Map<String ,Object> responseJson = new HashMap<>();
        if (auth!=null){
            List<Authority> usersAuths = authorityService.getAuthoritiesbyUser(auth.getUser());
            if (usersAuths.size() == 1){
                responseJson.put("error", "Cannot delete User's only role.");
                return ResponseEntity.badRequest().body(responseJson);
            }
            authorityService.deleteById(auth.getId());
            responseJson.put("ok", true);

        }
        return ResponseEntity.ok().body(responseJson);
    }

//    @RequestMapping(value = "/ping", method = RequestMethod.GET)
//    public ResponseEntity<String> ping(){
//        return new ResponseEntity<String>("ping", HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/test/form_data", method = RequestMethod.POST)
//    public ResponseEntity test_form_data(String x, Integer y){
//        Map<String, Object> m = new HashMap();
//        m.put("string", x);
//        m.put("integer", y);
//        return new ResponseEntity(m, HttpStatus.OK);
//
//    }
//
//    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
//    public ResponseEntity PREFLIGHT_BYPASS(){
//        return new ResponseEntity(HttpStatus.OK);
//    }

}

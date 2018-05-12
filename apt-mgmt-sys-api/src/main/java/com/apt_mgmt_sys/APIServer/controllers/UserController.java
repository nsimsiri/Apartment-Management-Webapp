package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.Authority;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.security.Role;
import com.apt_mgmt_sys.APIServer.services.AuthorityService;
import com.apt_mgmt_sys.APIServer.services.EmployeeProfileService;
import com.apt_mgmt_sys.APIServer.services.FileEntityService;
import com.apt_mgmt_sys.APIServer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.R;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AuthorityService authorityService;
    @Autowired
    EmployeeProfileService employeeProfileService;
    @Autowired
    FileEntityService fileEntityService;

    // REGISTRATION PROCESS
    @RequestMapping(value = {"","/"}, method = RequestMethod.POST)
    public ResponseEntity registration(@RequestBody Map<String, Object> json){
        String username = (String) Utilities.getFieldFromJSON(json, "username", String.class);
        String password = (String) Utilities.getFieldFromJSON(json, "password", String.class);
        String confirmPassword = (String) Utilities.getFieldFromJSON(json, "confirmPassword", String.class);

        if (username == null || password == null || password == confirmPassword){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("incomplete fields.");
        }
        if (!password.equals(confirmPassword)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password and password confirmation are not the same");
        }
        User user = null;
        Map<String,Object> jsonMap = new HashMap<>();
        try {
            user = userService.create(username, password);
            if (user == null) throw new Exception("Unable to create user in \"/registration\" controller");
            System.out.println(user);
            Authority role = authorityService.create(user, Role.EMPLOYEE);
            if (role == null) throw new Exception("Unable to create Authority in UserController");
            EmployeeProfile profile = employeeProfileService.createWithJson(user, json);
            if (profile == null) throw new Exception("Unable to create EMPLOYEE_PROFILE Profile");
            jsonMap.put("user", user);
            jsonMap.put("roles",new ArrayList<Authority>(Arrays.asList(new Authority[]{role})));
            jsonMap.put("employeeProfile", profile);
            fileEntityService.bulkUpdateAndRemoveFileEntity(json, profile.getId(), TransactionType.EMPLOYEE_PROFILE);
        } catch (Exception e){
            //rollback this transaction;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity(jsonMap, HttpStatus.OK);
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAll(){
        List<User> users = userService.getUsers();
        List userWrappers = users.stream().map(user -> {
            Map<String, Object> userWrapper = new HashMap<>();
            userWrapper.put("user", user);
            userWrapper.put("roles", authorityService.getAuthoritiesbyUser(user));
            return userWrapper;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(userWrappers, HttpStatus.OK);
    }

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable(value = "id") Long id){
        Map<String, Object> jsonMap = new HashMap<>();
        try {
            User user = userService.getUserById(id);
            List<Authority> roles = new ArrayList<>();
            if (user == null) throw new Exception("Unable to get User with userId: " + id);
            roles = authorityService.getAuthoritiesbyUser(user);
            EmployeeProfile profile = employeeProfileService.getByUser(user);
            jsonMap.put("user", user);
            jsonMap.put("roles", roles);
            jsonMap.put("employeeProfile", profile);

        } catch (Exception e){
            e.printStackTrace();
            jsonMap = new HashMap<>();
            jsonMap.put("error", e.getMessage());
            return new ResponseEntity<>(jsonMap, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(jsonMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateById(@PathVariable(value="id") Long id, @RequestBody Map<String, Object> json){
        System.out.println(json);
        Map<String, Object> responseJson = new HashMap<>();
        try {
            EmployeeProfile profile =  employeeProfileService.updateUserById(id, json);
            if (profile == null) throw new Exception("Unable to update EmployeeProfile in User Update Ctrl");
            User user = userService.updatePasswordByIdForJson(id, json);
            if (user == null) throw new Exception("Cannot update user");
            List<Authority> authorities = authorityService.getAuthoritiesbyUser(user);
            responseJson.put("user", user);
            responseJson.put("employeeProfile", profile);
            responseJson.put("roles", authorities);
            fileEntityService.bulkUpdateAndRemoveFileEntity(json, profile.getId(), TransactionType.EMPLOYEE_PROFILE);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return new ResponseEntity<>(responseJson, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(responseJson, HttpStatus.OK);
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public ResponseEntity<Boolean> removeById(@PathVariable(value = "id") Long id){
        User user = userService.removeById(id);
        return new ResponseEntity<Boolean>(!(user == null || user.isEnabled()), HttpStatus.OK);

    }

    @RequestMapping(value = "/whoami", method = RequestMethod.GET)
    public ResponseEntity<Map> whoami(Principal principal){
        String username = principal.getName();
        System.out.println("/whoami == > " + username);
        Map m = new HashMap();
        m.put("username", username);
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

}



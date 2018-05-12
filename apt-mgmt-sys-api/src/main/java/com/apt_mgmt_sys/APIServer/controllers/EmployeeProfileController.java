package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.services.EmployeeProfileService;
import com.apt_mgmt_sys.APIServer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/employeeProfile")
public class EmployeeProfileController {
    @Autowired
    private EmployeeProfileService employeeProfileService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateById(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        try {
            EmployeeProfile profile = employeeProfileService.updateUserById(id, json);
            if (profile == null) throw new Exception("Unable to find EmployeeProfile with id: " + id);
            responseJson.put("employeeProfile", profile);

        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        } finally {
            return new ResponseEntity<>(responseJson, status);
        }
    }
}

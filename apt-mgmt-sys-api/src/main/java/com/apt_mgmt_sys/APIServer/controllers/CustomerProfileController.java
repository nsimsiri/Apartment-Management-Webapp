package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.CustomerProfile;
import com.apt_mgmt_sys.APIServer.services.CustomerProfileService;
import com.apt_mgmt_sys.APIServer.services.FileEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/customerProfiles")
public class CustomerProfileController {
    @Autowired
    CustomerProfileService customerProfileService;
    @Autowired
    FileEntityService fileEntityService;
    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<List<CustomerProfile>> getAll(){
        return ResponseEntity.ok().body(customerProfileService.getAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getById(@PathVariable(value = "id") Long id){
        CustomerProfile profile = customerProfileService.getById(id);
        Map<String, Object> responseJson = new HashMap<>();
        if (profile == null){
            responseJson.put("error", "unable to find CustomerProfile for id " + id);
            return ResponseEntity.badRequest().body(responseJson);
        }
        responseJson.put("customerProfile", profile);
        return ResponseEntity.ok().body(responseJson);
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity<Map> create(@RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            CustomerProfile profile = customerProfileService.create(json);
            fileEntityService.bulkUpdateAndRemoveFileEntity(json, profile.getId(), TransactionType.CUSTOMER_PROFILE);
            responseJson.put("customerProfile", profile);
        } catch (Exception e) {
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok().body(responseJson);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateById(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            CustomerProfile profile = customerProfileService.updateById(id, json);
            fileEntityService.bulkUpdateAndRemoveFileEntity(json, profile.getId(), TransactionType.CUSTOMER_PROFILE);
            responseJson.put("customerProfile", profile);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok().body(responseJson);
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> removeById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson = null;
        try {
            responseJson = new HashMap<>();
            CustomerProfile disabledProfile = customerProfileService.removeById(id);
            responseJson.put("customerProfile", disabledProfile);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getCause());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok().body(responseJson);
    }

}

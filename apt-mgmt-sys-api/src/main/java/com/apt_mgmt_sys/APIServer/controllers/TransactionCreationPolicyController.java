package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.BaseTCP;
import com.apt_mgmt_sys.APIServer.services.transaction_creation_policies.BaseTCPEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/policies")
@Controller
public class TransactionCreationPolicyController {
    @Autowired
    private BaseTCPEntityService tcpService;
    private final String SINGULAR = "policy";
    private final String PLURAL = "policies";

    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<Map> getAllTCPs(@RequestParam(value="type",required=false)String type){
        Map<String, Object> responseJson = new HashMap<>();
        List<BaseTCP> tcps;
        try {
            if (type != null){
                TransactionType transactionType = TransactionType.valueOf(type);
                tcps = tcpService.listByTransactionType(transactionType);
            } else {
                tcps = tcpService.getAll();
            }
            responseJson.put(PLURAL, tcps);
        } catch(Exception e){
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getTCPById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson;
        try {
            BaseTCP tcp = tcpService.getById(id);
            if(tcp == null) throw new IllegalArgumentException("unable to find BaseTCP with id " + id);
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, tcp);
        } catch(Exception e){
            responseJson = new HashMap<>();
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<Map> createTCP(@RequestBody Map<String, Object> json){
        Map<String, Object> responseJson;
        try {
            BaseTCP tcp = tcpService.create(json);
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, tcp);
        } catch(Exception e){
            responseJson = new HashMap<>();
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateTCP(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson;
        try {
            BaseTCP tcp = tcpService.updateById(id, json);
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, tcp);
        } catch(Exception e){
            responseJson = new HashMap<>();
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/setEnabled/{id}", method = RequestMethod.POST)
    public ResponseEntity removeTCP(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson;
        try {
            Boolean enabled = (Boolean) Utilities.getFieldFromJSON(json, "enabled", Boolean.class);
            BaseTCP tcp = tcpService.setEnabledById(id, enabled);
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, tcp);
        } catch(Exception e){
            responseJson = new HashMap<>();
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            responseJson.put("ok", 0);
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }
}

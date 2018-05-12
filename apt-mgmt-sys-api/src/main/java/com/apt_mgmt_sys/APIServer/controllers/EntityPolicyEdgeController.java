package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdge;
import com.apt_mgmt_sys.APIServer.services.transaction_creation_policies.EntityPolicyEdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/entityPolicyEdges")
@Controller
public class EntityPolicyEdgeController {
    @Autowired
    private EntityPolicyEdgeService policyService;

    private final String SINGULAR = "entityPolicyEdge";
    private final String PLURAL = "entityPolicyEdges";

    @RequestMapping(value = {"/{type}/", "/{type}"}, method = RequestMethod.GET)
    public ResponseEntity<List> getAllPolicies(@PathVariable(value = "type") String type){
        if(type == null){
            return ResponseEntity.ok(policyService.getAll());
        } else {
            try {
                TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
                return ResponseEntity.ok(policyService.getAll(transactionType));
            } catch(Exception e){
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new ArrayList());
            }
        }

    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<Map> getAllPolicies(@RequestParam(value="entityId",required=false) Long entityId,
                                               @RequestParam(value="type",required=false) String type){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            if (entityId == null || type == null){
                responseJson.put(PLURAL, policyService.getAll());
            } else { // id!=null && type!=null
                TransactionType transactionType = TransactionType.valueOf(type);
                List<? extends IEntityPolicyEdge<?>> edges = policyService.getByEntityId(entityId, transactionType);
                responseJson.put(PLURAL, edges);
            }
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);

    }

    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getPolicyById(@PathVariable(value = "type") String type, @PathVariable(value = "id") Long id){
        Map<String, Object> responseJson;
        try {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            IEntityPolicyEdge edge = policyService.getById(id, transactionType);
            if (edge == null) throw new IllegalArgumentException("null EntityPolicyEdge for type: " + transactionType + " + and id : " + id);
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, edge);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/remove/{type}/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> removeById(@PathVariable(value = "id") Long id,
                                          @PathVariable(value = "type") String type){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            if (type == null || id == null) throw new IllegalArgumentException(String.format("Following is null id=%s or type=%s", id, type));
            TransactionType transactionType = TransactionType.valueOf(type);
            policyService.removeById(id, transactionType);
            responseJson.put("ok", 1);
        }catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            responseJson.put("ok", 0);
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }
}

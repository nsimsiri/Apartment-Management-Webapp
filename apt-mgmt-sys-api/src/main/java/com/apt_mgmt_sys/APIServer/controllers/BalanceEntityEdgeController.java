package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/balanceEntityEdges")
public class BalanceEntityEdgeController {
    public static final String SINGULAR = "balanceEntityEdge";
    public static final String PLURAL = "balanceEntityEdges";
    @Autowired
    private BalanceEntityEdgeService edgeService;
    @Autowired
    private BalanceService balanceService;
    private Set<String> rawTransactionTypeSet = new HashSet(Arrays.asList(new String[]{
            TransactionType.CONTRACT.toString(),
            TransactionType.EMPLOYEE_PROFILE.toString()
    }));

    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getBalanceEntityById(@PathVariable(value = "id") Long id, @PathVariable(value = "type") String type){
        Map<String, Object> responseJson = null;
        try{
            TransactionType transactionType = TransactionType.valueOf(type);
            IBalanceEntityEdge<?> edge = edgeService.getById(id, transactionType);
            if (edge == null) throw new IllegalArgumentException(String.format("unable to find BalanceEntityEdge for id=%s type=%s", id, type));
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, edge);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Map> getByBalance(@RequestParam(value = "balanceId", required=false) Long id,
                                            @RequestParam(value="type", required=false) String type){
        IBalanceEntityEdge<?> edge = null;
        Map<String, Object> responseJson;
        TransactionType transactionType = type == null ? null : TransactionType.valueOf(type);

        if(id == null){
            if (transactionType == null || !rawTransactionTypeSet.contains(transactionType)){
                responseJson = new HashMap<>();
                responseJson.put(PLURAL, edgeService.getAll());
                return ResponseEntity.ok(responseJson);
            } else {
                responseJson = new HashMap<>();
                responseJson.put(PLURAL, edgeService.getAll(transactionType));
                return ResponseEntity.ok(responseJson);
            }
        }

        try {
            Balance balance = balanceService.getById(id);
            if (balance == null) throw new IllegalArgumentException("Can't find balance " + id);
            if (transactionType == null){
                for(String _type : rawTransactionTypeSet){
                    edge = edgeService.getByBalance(balance, TransactionType.valueOf(_type));
                    if (edge != null) break;
                }
            } else {
                edge = edgeService.getByBalance(balance, transactionType);
            }
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

    // TODO: AUTHORIZED ONLY ADMIN ***
    @RequestMapping(value = {"/{type}/", "/{type}"}, method = RequestMethod.POST)
    public ResponseEntity<Map> createBalanceEntity(@PathVariable(value = "type") String type, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = null;
        try {
            TransactionType transactionType = TransactionType.valueOf(type);
            IBalanceEntityEdge edge = edgeService.create(json, transactionType);
            if (edge == null) throw new IllegalArgumentException("Unable to create an IBalanceEntityEdge - server error probably");
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, edge);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    /**
     * re-use balance deletion.
     * */
    @RequestMapping(value = {"/remove/{type}/{id}"}, method = RequestMethod.POST)
    public ResponseEntity<Map> deleteBalanceEntity(@PathVariable(value = "id") Long id, @PathVariable(value = "type") String type){
        Map<String, Object> responseJson = null;
        try {
            TransactionType transactionType = TransactionType.valueOf(type);
            edgeService.removeById(id, transactionType);
            responseJson.put("ok", 1);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            responseJson.put("ok", 0);
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

}

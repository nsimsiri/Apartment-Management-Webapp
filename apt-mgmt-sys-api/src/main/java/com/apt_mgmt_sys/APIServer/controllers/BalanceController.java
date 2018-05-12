package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceService;
import com.apt_mgmt_sys.APIServer.services.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/balances")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceEntityEdgeService balanceEntityEdgeService;

    @Autowired
    private TransactionService transactionService;

    public static final String SINGULAR = "balance";
    public static final String PLURAL = "balances";

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ResponseEntity<List> getAllBalances(){
        List<Balance> balances = balanceService.getAll();
        return ResponseEntity.ok(balances);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getBalanceById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson = new HashMap<>();
        Balance balance = balanceService.getById(id);
        if (balance == null) {
            responseJson.put("error", "unable to find balance with id " + id);
            return ResponseEntity.badRequest().body(responseJson);
        }
        responseJson.put("balance", balance);
        return ResponseEntity.ok().body(responseJson);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Map> createBalance(@RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = null;
        try {
            if (json == null) throw new IllegalArgumentException("null json: " + json);
            Balance balance = balanceService.create(json);
            if (balance == null) throw new Exception("Server error");
            responseJson.put("balance", balance);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok().body(responseJson);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<Map> searchBalance(@RequestParam(value="name") String name,
                                             @RequestParam(value="type") String type){
        Map<String, Object> responseJson;
        try {
            TransactionType transactionType = TransactionType.valueOf(type);
            List<? extends IBalanceEntityEdge<?>> balanceEntityEdges =  balanceEntityEdgeService.findByBalanceName(name, transactionType);
            List<Balance> balances = balanceEntityEdges
                    .stream()
                    .map(edge -> edge.getBalance())
                    .collect(Collectors.toList());
            responseJson = new HashMap<>();
            responseJson.put(PLURAL, balances);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateBalanceById(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = null;
        try {
            responseJson = new HashMap<>();
            Balance updatedBalance = balanceService.updateById(id, json);
            responseJson.put("balance", updatedBalance);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<String, Object>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/setEnabled/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> setEnabledbyId(@PathVariable(value = "id") Long id, Boolean enabled){
        Map<String, Object> responseJson = null;
        try {
            Balance balance = balanceService.getById(id);
            if (balance == null) throw new IllegalArgumentException("unable to find Balance with id " + id);
            Balance updatedBalance = balanceService.setEnabled(id, enabled == null ? balance.getEnabled() : enabled);
            responseJson = new HashMap<>();
            responseJson.put("balance", updatedBalance);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> deleteById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson = null;
        try {
            balanceService.removeById(id);
            responseJson = new HashMap<>();
            responseJson.put("ok", 1);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/recalculateCachedAmount/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> cachedAmountEvicted(@PathVariable(value = "id")Long id){
        Map<String, Object> responseJson;
        try {
            responseJson = new HashMap<>();
            Balance balance = balanceService.getById(id);
            if (balance == null) throw new IllegalArgumentException("Unable to find Balance " + id);
            List<Transaction> transactions = transactionService.listByBalance(balance, 0, null);
            balance = balanceService.updateCachedAmount(balance, transactions);
            responseJson.put(SINGULAR, balance);
        } catch (Exception e) {
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }
}

package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.PageRequest;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.services.UserService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceService;
import com.apt_mgmt_sys.APIServer.services.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private UserService userService;
    public static String SINGULAR = "transaction";

    @RequestMapping(value = {"","/"}, method = RequestMethod.GET)
    public ResponseEntity<List> getAllTransactions(@RequestParam(value="balanceId",required=false) Long balanceId,
                                                   @RequestParam(value="offset",required=false) Integer offset,
                                                   @RequestParam(value="limit",required=false) Integer limit){
        List<Transaction> transactions = null;
        if (balanceId != null){
            Balance balance = balanceService.getById(balanceId);
            if (balance == null) return ResponseEntity.badRequest().body(null);
            transactions = transactionService.listByBalance(balance, offset, limit);

        } else if (offset!=null || limit!=null){
            transactions = transactionService.getAll(PageRequest.buildWithOrder(offset, limit, TransactionService.DEFAULT_ORDER_FIELD, TransactionService.DEFAULT_ORDER_DIRECTION));
            return ResponseEntity.ok(transactions);
        } else {
            transactions = transactionService.getAll(PageRequest.buildWithOrder(null, null, TransactionService.DEFAULT_ORDER_FIELD, TransactionService.DEFAULT_ORDER_DIRECTION));
        }
        return ResponseEntity.ok(transactions);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getTransactionById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson = null;
        try {
            Transaction transaction = transactionService.getById(id);
            if (transaction == null) throw new IllegalArgumentException("Unable to find transaction with id : " + id);
            responseJson = new HashMap<>();
            responseJson.put("transaction", transaction);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }


    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity<Map> createTransaction(@RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = null;

        try {
            User user = userService.getUserFromSession();
            if (user == null) throw new IllegalStateException("Cannot obtain User Session in current request: \'POST /transactions/\'");
            json.put("user", user);
            json.put("userId", user.getId()); // optional
            Transaction transaction = transactionService.create(json);
            if (transaction == null) throw new IllegalArgumentException("Cannot create transaction -- server error");
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, transaction);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateTransactionById(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = null;
        try {
            Transaction transaction = transactionService.updateฺById(id, json);
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, transaction);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/setEnabled/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> setEnabledTransactionById(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = null;
        try {
            Boolean enabled = (Boolean) Utilities.getFieldFromJSON(json, "enabled", Boolean.class);
            Transaction transaction = transactionService.getById(id);
            if (transaction == null) throw new IllegalArgumentException("can't find transaction with id " + id);
            transaction = transactionService.setEnabledById(id, enabled);
            responseJson = new HashMap<>();
            responseJson.put(SINGULAR, transaction);
        } catch (Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

}

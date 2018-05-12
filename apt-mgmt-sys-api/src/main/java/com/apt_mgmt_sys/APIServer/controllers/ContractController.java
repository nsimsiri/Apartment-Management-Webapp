package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.PageRequest;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.CustomerProfile;
import com.apt_mgmt_sys.APIServer.models.Room;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.repositories.ContractRepository;
import com.apt_mgmt_sys.APIServer.services.ContractService;
import com.apt_mgmt_sys.APIServer.services.CustomerProfileService;
import com.apt_mgmt_sys.APIServer.services.FileEntityService;
import com.apt_mgmt_sys.APIServer.services.RoomService;
import com.apt_mgmt_sys.APIServer.services.transaction_creation_policies.EntityPolicyEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import javafx.util.Pair;
import org.aspectj.bridge.ILifecycleAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "/contracts")
public class ContractController {
    @Autowired
    ContractService contractService;
    @Autowired
    RoomService roomService;
    @Autowired
    CustomerProfileService customerProfileService;
    @Autowired
    FileEntityService fileEntityService;
    @Autowired
    EntityPolicyEdgeService entityPolicyEdgeService;
    @Autowired
    BalanceEntityEdgeService balanceEntityEdgeService;

    public static final String SINGULAR = "contract";
    public static final String PLURAL = "contracts";
    public static final String PLURAL_WR = "contractWrappers";

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<List> getAllContracts(@RequestParam(value="profileId",required=false) Long profileId,
                                                @RequestParam(value="from",required=false) Long from,
                                                @RequestParam(value="to",required=false) Long to,
                                                @RequestParam(value="roomNumber",required=false) String roomNumber,
                                                @RequestParam(value="customerName",required=false) String customerName,
                                                @RequestParam(value="offset",required=false) Integer offset,
                                                @RequestParam(value="limit",required=false) Integer limit,
                                                @RequestParam(value="sortField",required=false) String sortField,
                                                @RequestParam(value="sortDirection",required=false) Integer sortDirection){
        Map<String, Object> json = new TreeMap<>();
        json.put("from", from);
        json.put("to",to);
        json.put("profileId", profileId);
        json.put("roomNumber", roomNumber);
        json.put("customerName", customerName);
        json.put("offset", offset);
        json.put("limit", limit);
        json.put("sortField", sortField);
        json.put("sortDirection", sortDirection);
        return ResponseEntity.ok().body(contractService.query(json));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getContractById(@PathVariable(value = "id") Long id,
                                               @RequestParam(value = "includeBalance", required=false) Boolean _includeBalance){
        Map<String, Object> responseJson;
        boolean includeBalance = _includeBalance == null ? false :_includeBalance.booleanValue();
        try{
            responseJson = new HashMap<>();
            Contract contract = contractService.getById(id);
            responseJson.put(ContractController.SINGULAR, contract);
            if (contract == null) throw new IllegalArgumentException("Unable to find CONTRACT id : " + id);
            if (includeBalance){
                IBalanceEntityEdge<?> edge = balanceEntityEdgeService.getByEntity(contract, TransactionType.CONTRACT);
                Balance balance = edge.getBalance();
                responseJson.put(BalanceController.SINGULAR, balance);
            }
            return ResponseEntity.ok().body(responseJson);

        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }

    }

    /*
    * input:
    *   from : Date(epoch),
    *   to : Date(epoch),
    *   includeBalance : return wrapper with JSON,
    *   rooms : query contracts within date range for room list.
    * */

    @RequestMapping(value = "/searchByRooms", method = RequestMethod.POST)
    public ResponseEntity<Map> search(@RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            Number from = (Number) Utilities.getFieldFromJSON(json, "from", Number.class);
            Number to = (Number) Utilities.getFieldFromJSON(json, "to", Number.class);
            Boolean _includeBalance= (Boolean) Utilities.getFieldFromJSON(json, "includeBalance", Boolean.class);
            final boolean includeBalance = _includeBalance == null ? false : _includeBalance.booleanValue();

            List<Number> queriedRooms = (List<Number>) Utilities.getFieldFromJSON(json ,"rooms", List.class);
            if (from == null || to == null) throw new IllegalArgumentException("no from or to in request json " + json);
            List<Room> rooms = queriedRooms == null || queriedRooms.size() == 0 ?
                roomService.getAll() :
                queriedRooms.stream().map(roomId-> roomService.getById(roomId.longValue())).collect(Collectors.toList());
            Date fromDate = new Date(from.longValue());
            Date toDate = new Date(to.longValue());
            Object filteredContractWrappers = rooms.stream()
                .flatMap(_room -> {
                    /* flatten List<List<Contract>> -> List<Contract>*/
                    Stream<Map> _stream = contractService.getContractsForRoomWithinDateRange(_room, fromDate, toDate).stream()
                        .map(_contract -> {
                            /* map List<Contract> -> List<{contract: contract, balance: balance}> */
                            if (!includeBalance) {
                                Map<String,Object> contractWrapper = Utilities.buildMap(new Pair[]{
                                        new Pair(ContractController.SINGULAR, _contract)
                                });
                                return contractWrapper;
                            } else {
                                IBalanceEntityEdge<?> edge = balanceEntityEdgeService.getByEntity(_contract, TransactionType.CONTRACT);
                                if (edge == null) throw new IllegalArgumentException("contract " + _contract.getId() + " does not have balance");
                                Balance _balance = edge.getBalance();
                                Map<String,Object> contractWrapper = Utilities.buildMap(new Pair[]{
                                        new Pair(BalanceController.SINGULAR, _balance),
                                        new Pair(ContractController.SINGULAR, _contract)
                                });
                                return contractWrapper;
                            }
                        });
                    return _stream;

                })
                .collect(Collectors.toList());
            responseJson.put(PLURAL_WR, filteredContractWrappers);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity<Map> createContract(@RequestBody Map<String, Object> json){
        Map<String, Object> responseJson;
        try{
            responseJson = new HashMap<>();

            Number roomId = (Number) Utilities.getFieldFromJSON(json ,"bookedRoomId", Number.class);
            Number customerProfileId  = (Number) Utilities.getFieldFromJSON(json ,"customerProfileId", Number.class);
            if (roomId == null || customerProfileId == null)
                throw new IllegalArgumentException("no roomId or customerProfileId fields in request body : " + json);

            Room room = roomService.getById(roomId.longValue());
            CustomerProfile customerProfile = customerProfileService.getById(customerProfileId.longValue());
            if (room == null) throw new IllegalArgumentException("Unable to find room with id " + roomId);
            if (customerProfile == null) throw new IllegalArgumentException("Unable to find customerProfile with id : "  + customerProfileId);

            json.put("bookedRoom", room);
            json.put("customerProfile", customerProfile);

            Contract contract = contractService.create(json);
            fileEntityService.bulkUpdateAndRemoveFileEntity(json, contract.getId(), TransactionType.CONTRACT);
            entityPolicyEdgeService.bulkAppendAndRemove(json, contract.getId(), TransactionType.CONTRACT);

            responseJson.put("contract", contract);

            return ResponseEntity.ok().body(responseJson);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateContract(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson;
        try{
            responseJson = new HashMap<>();

            Number roomId = (Number) Utilities.getFieldFromJSON(json ,"bookedRoomId", Number.class);
            Number customerProfileId  = (Number) Utilities.getFieldFromJSON(json ,"customerProfileId", Number.class);
            Room room = roomId == null ? null : roomService.getById(roomId.longValue());
            CustomerProfile customerProfile = customerProfileId == null ? null : customerProfileService.getById(customerProfileId.longValue());
            if (room!=null) json.put("bookedRoom", room);
            if (customerProfile!=null) json.put("customerProfile", customerProfile);
            Contract contract = contractService.updateById(id, json);
            fileEntityService.bulkUpdateAndRemoveFileEntity(json, contract.getId(), TransactionType.CONTRACT);
            entityPolicyEdgeService.bulkAppendAndRemove(json, contract.getId(), TransactionType.CONTRACT);
            responseJson.put("contract", contract);

            return ResponseEntity.ok().body(responseJson);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @RequestMapping(value = "/setEnable/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> removeContract(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map responseJson = null;
        try {
            responseJson = new HashMap<>();
            Boolean enable = (Boolean)Utilities.getFieldFromJSON(json, "enabled", Boolean.class);
            if (enable == null) throw new IllegalArgumentException("unable to find enable key,value in json :" + json);
            Contract contract = contractService.setEnable(id, enable);
            responseJson.put("contract", contract);
            responseJson.put("ok", enable == contract.getEnabled());
            return ResponseEntity.ok().body(responseJson);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

}


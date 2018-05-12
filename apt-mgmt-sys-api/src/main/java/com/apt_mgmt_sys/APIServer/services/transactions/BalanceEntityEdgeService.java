package com.apt_mgmt_sys.APIServer.services.transactions;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BalanceEntityEdgeService {
    @Autowired
    private BalanceContractEdgeService balanceContractEdgeService;
    @Autowired
    private BalanceEmployeeEdgeService balanceEmployeeEdgeService;

    private Map<TransactionType, IBalanceEntityEdgeService<?>> typeToServiceMap = new HashMap<>();


    public IBalanceEntityEdge<?> getById(Long id, TransactionType type) {
        initializeServiceMap();
        IBalanceEntityEdgeService service = typeToServiceMap.get(type);
        return service.getById(id);
    }


    public List<? extends IBalanceEntityEdge<?>> getAll() {
        initializeServiceMap();
        return typeToServiceMap
                .entrySet()
                .stream()
                .flatMap(x -> x.getValue().getAll().stream())
                .collect(Collectors.toList());
    }

    public List<? extends IBalanceEntityEdge<?>> getAll(TransactionType type){
        initializeServiceMap();
        IBalanceEntityEdgeService<?> service = typeToServiceMap.get(type);
        return service.getAll();
    }

    public List<? extends IBalanceEntityEdge<?>> findByBalanceName(String name, TransactionType type){
        initializeServiceMap();
        IBalanceEntityEdgeService<?> service = typeToServiceMap.get(type);
        return service.findByBalanceName(name);
    }

    public IBalanceEntityEdge<?> getByBalance(Balance balance, TransactionType type) {
        if (balance == null) throw new IllegalArgumentException("Cannot have null Balance");
        initializeServiceMap();
        IBalanceEntityEdgeService service = typeToServiceMap.get(type);
        return service.getByBalance(balance);
    }


    public IBalanceEntityEdge<?> getByEntity(Object entity, TransactionType type) {
        initializeServiceMap();
        IBalanceEntityEdgeService service = typeToServiceMap.get(type);
        if(type.equals(TransactionType.CONTRACT) && entity instanceof Contract){
            return ((BalanceContractEdgeService)service).getByEntity((Contract) entity);
        } else if (type.equals(TransactionType.EMPLOYEE_PROFILE) && entity instanceof EmployeeProfile){
            return ((BalanceEmployeeEdgeService) service).getByEntity((EmployeeProfile) entity);
        }
        throw new IllegalArgumentException(
                Utilities.FAIL_STR(String.format("unidentical matching of types between TransactionType: %s and entity:\n%s\n", type, entity))
        );
    }

    public IBalanceEntityEdge<?> create(Map<String, Object> json, TransactionType type) {
        initializeServiceMap();
        IBalanceEntityEdgeService<?> service = typeToServiceMap.get(type);
        return service.create(json);
    }

    public IBalanceEntityEdge<?> updateById(Long id, Map<String, Object> json) {
        throw new UnsupportedOperationException("updated operation not supported");
    }

    public void removeById(Long id, TransactionType type) {
        initializeServiceMap();
        IBalanceEntityEdgeService<?> service = typeToServiceMap.get(type);
        service.removeById(id);
    }

    private void initializeServiceMap(){
        boolean noContactKey = !typeToServiceMap.containsKey(TransactionType.CONTRACT);
        boolean noProfileKey = !typeToServiceMap.containsKey(TransactionType.EMPLOYEE_PROFILE);
        if (noContactKey || noProfileKey){
            typeToServiceMap = Utilities.buildMap(
                    new Pair[]{
                            new Pair(TransactionType.CONTRACT, balanceContractEdgeService),
                            new Pair(TransactionType.EMPLOYEE_PROFILE, balanceEmployeeEdgeService)
                    }
            );
        }

    }
}

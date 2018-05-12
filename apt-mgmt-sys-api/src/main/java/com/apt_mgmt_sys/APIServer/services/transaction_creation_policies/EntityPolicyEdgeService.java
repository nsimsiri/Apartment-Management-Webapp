package com.apt_mgmt_sys.APIServer.services.transaction_creation_policies;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdge;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdgeService;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.ContractPolicyEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityPolicyEdgeService {
    @Autowired
    private ContractPolicyEdgeService contractPolicyEdgeService;
    @Autowired
    private EmployeePolicyEdgeService employeePolicyEdgeService;
    @Autowired
    private BaseTCPEntityService baseTCPEntityService;

    private Map<TransactionType, IEntityPolicyEdgeService<?>> typeToService = new HashMap<>();

    public List<? extends IEntityPolicyEdge> getAll(){
        initializeServiceMap();
        return this.typeToService
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().getAll().stream())
                .collect(Collectors.toList());
    }

    public List<? extends IEntityPolicyEdge> getAll(TransactionType type){
        initializeServiceMap();
        IEntityPolicyEdgeService<?> service = typeToService.get(type);
        if (service == null) throw new IllegalArgumentException("no service for transaction type " + type);
        return service.getAll();
    }

    public IEntityPolicyEdge<?> getById(Long id, TransactionType type){
        initializeServiceMap();
        IEntityPolicyEdgeService<?> service = typeToService.get(type);
        if (service == null) throw new IllegalArgumentException("no service for transaction type " + type);
        return service.getById(id);
    }

    public List<? extends IEntityPolicyEdge<?>>  getByEntityId(Long entityId, TransactionType type){
        if (type == null || entityId == null) throw new IllegalArgumentException(String.format("The following is null id=%s type=%s", entityId, type));
        initializeServiceMap();
        IEntityPolicyEdgeService<?> service = typeToService.get(type);
        if (service == null) throw new IllegalArgumentException("no service for transaction type " + type);
        return service.getByEntityId(entityId);
    }

    public IEntityPolicyEdge<?> create(Map<String, Object> json, TransactionType type){
        initializeServiceMap();
        IEntityPolicyEdgeService<?> service = typeToService.get(type);
        if (service == null) throw new IllegalArgumentException("no service for transaction type " + type);
        return service.create(json);
    }

    public List<? extends IEntityPolicyEdge<?>> bulkAppendAndRemove(Map<String, Object> json, Long entityId, TransactionType type){
        if (entityId == null || type == null) throw new IllegalArgumentException(String.format("One of the following is null entityId=%s type=%s", entityId, type));
        initializeServiceMap();
        IEntityPolicyEdgeService<?> service = typeToService.get(type);
        if (service == null) throw new IllegalArgumentException("no service for transaction type " + type);
        List<? extends IEntityPolicyEdge<?>> oldEdges = getByEntityId(entityId, type);
        System.out.println("oldEdges:\n"+oldEdges);
        List<Map> edgeJsonList = (List<Map>)Utilities.getFieldFromJSON(json, "entityPolicyEdges", List.class);
        Set<Long> maintainedIds = new HashSet<>();
        List<? extends IEntityPolicyEdge<?>> Out = edgeJsonList.stream()
                .map((Map rawEdge) -> {
                    Map<String, Object> edgeJson = (Map<String, Object>)rawEdge;
                    edgeJson.put("entityId", entityId);

                    Number _id = (Number)Utilities.getFieldFromJSON(edgeJson, "id", Number.class);
                    if (_id==null || _id.longValue() == -1){
                        System.out.println("adding " + edgeJson);
                        IEntityPolicyEdge<?> edge = service.create(edgeJson);
                        return edge;
                    } else {
                        System.out.println("ignoring " + edgeJson);
                        maintainedIds.add(_id.longValue());
                    }
                    return null;
                })
                .filter(x -> x!=null)
                .collect(Collectors.toList());

        oldEdges.stream().map(x -> {
            System.out.printf("%s in %s ? ==> no..=> %s\n", x.getId(), maintainedIds, !maintainedIds.contains(x.getId().longValue()));
            if(!maintainedIds.contains(x.getId().longValue())){
                service.removeById(x.getId());
                return null;
            }
            return x;
        }).filter(x -> x!=null).collect(Collectors.toList());
        return Out;
    }

    public IEntityPolicyEdge<?> updateById(Long id, Map<String, Object> json, TransactionType type){
        initializeServiceMap();
        IEntityPolicyEdgeService<?> service = typeToService.get(type);
        if (service == null) throw new IllegalArgumentException("no service for transaction type " + type);
        return service.updateById(id, json);
    }

    public void removeById(Long id, TransactionType type){
        initializeServiceMap();
        IEntityPolicyEdgeService<?> service = typeToService.get(type);
        if (service == null) throw new IllegalArgumentException("no service for transaction type " + type);
        service.removeById(id);
    }

    private void initializeServiceMap(){
        boolean noContactKey = !typeToService.containsKey(TransactionType.CONTRACT);
        boolean noProfileKey = !typeToService.containsKey(TransactionType.EMPLOYEE_PROFILE);
        if (noContactKey || noProfileKey){
            typeToService = Utilities.buildMap(
                    new Pair[]{
                            new Pair(TransactionType.CONTRACT, contractPolicyEdgeService),
                            new Pair(TransactionType.EMPLOYEE_PROFILE, employeePolicyEdgeService)
                    }
            );
        }

    }

}

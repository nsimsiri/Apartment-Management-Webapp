package com.apt_mgmt_sys.APIServer.services.transactions;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.BalanceContractEdge;
import com.apt_mgmt_sys.APIServer.repositories.BalanceContractEdgeRepository;
import com.apt_mgmt_sys.APIServer.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BalanceContractEdgeService implements IBalanceEntityEdgeService<Contract> {
    @Autowired
    private BalanceContractEdgeRepository repository;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private ContractService contractService;

    public IBalanceEntityEdge<Contract> getById(Long id){
        return repository.findOne(id);
    }

    public List<? extends IBalanceEntityEdge<Contract>> getAll(){
        List<? extends IBalanceEntityEdge<Contract>> Out = Utilities.iterableToList(repository.findAll());
        return Out;

    }

    public IBalanceEntityEdge<Contract> getByBalance(Balance balance){
        if (balance == null) throw new IllegalArgumentException("Null balance");
        List<? extends IBalanceEntityEdge> Out = repository.getBalanceContractEdgesByBalance(balance);
        return Out.isEmpty() ? null : Out.get(0);

    }

    public IBalanceEntityEdge<Contract> getByEntity(Contract contract){
        if (contract == null) throw new IllegalArgumentException("Null contract");
        List<? extends IBalanceEntityEdge> Out = repository.getBalanceContractEdgesByEntity(contract);
        return Out.isEmpty() ? null : Out.get(0);
    }

    public List<? extends IBalanceEntityEdge<Contract>> findByBalanceName(String title){
        return repository.findByBalanceNameContaining(title);
    }

    public IBalanceEntityEdge<Contract> create(Map<String, Object> json){
        BalanceContractEdge edge = new BalanceContractEdge();
        Utilities.updateBlock("balance", json, Balance.class, x -> edge.setBalance(x));
        Utilities.updateBlock("entity", json, Contract.class, x -> edge.setEntity(x));

        if (edge.getBalance() == null){
            Number balanceId = (Number) Utilities.getFieldFromJSON(json, "balanceId", Number.class);
            if (balanceId == null) throw new IllegalArgumentException("balance id null in json : " + json);
            Balance balance = balanceService.getById(balanceId.longValue());
            if (balance == null) throw new IllegalArgumentException("unable to find balance with id " + balanceId);
            edge.setBalance(balance);
        }
        if (edge.getEntity() == null){
            Number contractId = (Number) Utilities.getFieldFromJSON(json, "entityId", Number.class);
            if (contractId== null) throw new IllegalArgumentException("contract id null in json : " + json);
            Contract contract = contractService.getById(contractId.longValue());
            if (contract == null) throw new IllegalArgumentException("unable to find contract with id " + contractId);
            edge.setEntity(contract);
        }

        // At this point we know vertices are never null.
        Balance balance = edge.getBalance();
        Contract contract = edge.getEntity();
        if(!areVtxUnique(contract, balance)){
            throw new IllegalArgumentException(String.format("contract: %s and balance: %s already belonged to another edge", contract.getId(), balance.getId()));
        }
        return repository.save(edge);
    }

    public IBalanceEntityEdge<Contract> updateById(Long id, Map<String, Object> json){
        throw new UnsupportedOperationException("update edge operation no longer needed.");
    }

    public void removeById(Long id){
        repository.delete(id);
    }

    /// Ensures each vertex (contract and balance) has degree of 0.
    private boolean areVtxUnique(Contract contract, Balance balance){
        List<BalanceContractEdge> edgeForBalanceList = repository.getBalanceContractEdgesByBalance(balance);
        List<BalanceContractEdge> edgeForContractList = repository.getBalanceContractEdgesByEntity(contract);
        return edgeForBalanceList.isEmpty() && edgeForContractList.isEmpty();
    }

}

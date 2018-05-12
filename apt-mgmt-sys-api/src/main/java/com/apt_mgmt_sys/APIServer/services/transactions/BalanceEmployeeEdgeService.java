package com.apt_mgmt_sys.APIServer.services.transactions;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.BalanceEmployeeEdge;
import com.apt_mgmt_sys.APIServer.repositories.BalanceEmployeeEdgeRepository;
import com.apt_mgmt_sys.APIServer.services.EmployeeProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BalanceEmployeeEdgeService implements IBalanceEntityEdgeService<EmployeeProfile>{
    @Autowired
    private BalanceEmployeeEdgeRepository repository;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private EmployeeProfileService employeeProfileService;

    @Override
    public IBalanceEntityEdge<EmployeeProfile> getById(Long id) {
        return repository.findOne(id);
    }

    @Override
    public List<? extends IBalanceEntityEdge<EmployeeProfile>> getAll() {
        return Utilities.iterableToList(repository.findAll());
    }

    @Override
    public IBalanceEntityEdge<EmployeeProfile> getByBalance(Balance balance) {
        if (balance == null) throw new IllegalArgumentException("null balance");
        List<BalanceEmployeeEdge> Out = repository.getBalanceEmployeeEdgesByBalance(balance);
        return Out.isEmpty() ? null : Out.get(0);
    }

    @Override
    public List<? extends IBalanceEntityEdge<EmployeeProfile>> findByBalanceName(String title){
        return repository.findByBalanceNameContaining(title);
    }

    @Override
    public IBalanceEntityEdge<EmployeeProfile> getByEntity(EmployeeProfile entity) {
        if (entity == null) throw new IllegalArgumentException("null EmployeeProfile");
        List<BalanceEmployeeEdge> Out = repository.getBalanceEmployeeEdgesByEntity(entity);
        return Out.isEmpty() ? null : Out.get(0);
    }

    @Override
    public IBalanceEntityEdge<EmployeeProfile> create(Map<String, Object> json) {
        BalanceEmployeeEdge edge = new BalanceEmployeeEdge();
        Utilities.updateBlock("balance", json, Balance.class, x -> edge.setBalance(x));
        Utilities.updateBlock("entity", json, EmployeeProfile.class, x -> edge.setEntity(x));
        if (edge.getBalance() == null){
            Number balanceId = (Number) Utilities.getFieldFromJSON(json, "balanceId", Number.class);
            if (balanceId == null) throw new IllegalArgumentException("Null balance id in json " + json);
            Balance balance = balanceService.getById(balanceId.longValue());
            if (balance == null) throw new IllegalArgumentException("uanble to find balance id : " + balanceId);
            edge.setBalance(balance);
        }

        if (edge.getEntity() == null){
            Number epId = (Number) Utilities.getFieldFromJSON(json, "entityId", Number.class);
            if (epId == null) throw new IllegalArgumentException("unable to find employee profile id with json " + json);
            EmployeeProfile ep = employeeProfileService.getById(epId.longValue());
            if (ep == null) throw new IllegalArgumentException("cannot find employee profile with id " + epId);
            edge.setEntity(ep);
        }
        EmployeeProfile profile = edge.getEntity();
        Balance balance = edge.getBalance();
        if(!areVtxUnique(profile, balance)){
            throw new IllegalArgumentException(String.format("profile: %s and balance: %s already belonged to another edge", profile.getId(), balance.getId()));
        }
        return repository.save(edge);
    }

    @Override
    public IBalanceEntityEdge<EmployeeProfile> updateById(Long id, Map<String, Object> json) {
        throw new UnsupportedOperationException("update operation no longer supported");
    }

    @Override
    public void removeById(Long id) {
        repository.delete(id);
    }

    /// Ensures each vertex (contract and balance) has degree of 0.
    private boolean areVtxUnique(EmployeeProfile employeeProfile, Balance balance){
        List<BalanceEmployeeEdge> edgeForBalanceList = repository.getBalanceEmployeeEdgesByBalance(balance);
        List<BalanceEmployeeEdge> edgeForProfileList = repository.getBalanceEmployeeEdgesByEntity(employeeProfile);
        return edgeForBalanceList.isEmpty() && edgeForProfileList.isEmpty();
    }
}

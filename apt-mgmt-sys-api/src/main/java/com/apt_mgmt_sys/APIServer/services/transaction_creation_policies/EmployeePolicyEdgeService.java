package com.apt_mgmt_sys.APIServer.services.transaction_creation_policies;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.PolicyType;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdge;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdgeService;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.BaseTCP;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.EmployeePolicyEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.BalanceContractEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.BalanceEmployeeEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.repositories.EmployeePolicyEdgeRepository;
import com.apt_mgmt_sys.APIServer.services.EmployeeProfileService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class EmployeePolicyEdgeService implements IEntityPolicyEdgeService<EmployeeProfile> {
    @Autowired
    private EmployeePolicyEdgeRepository repository;
    @Autowired
    private EmployeeProfileService employeeProfileService;
    @Autowired
    private BaseTCPEntityService policyService;
    @Autowired
    private BalanceEntityEdgeService balanceEntityEdgeService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public List<? extends IEntityPolicyEdge<EmployeeProfile>> getAll() {
        return Utilities.iterableToList(repository.findAll());
    }

    @Override
    public List<? extends IEntityPolicyEdge<EmployeeProfile>> getByEntity(EmployeeProfile entity) {
        return repository.getEmployeePolicyEdgeByEntity(entity);
    }

    @Override
    public List<? extends IEntityPolicyEdge<EmployeeProfile>> getByEntityId(Long entityId){
        if (entityId == null) throw new IllegalArgumentException("Id cannot be null");
        EmployeeProfile profile = employeeProfileService.getById(entityId);
        if (profile == null) throw new IllegalArgumentException("Cannot find Employee Profile " + entityId);
        return getByEntity(profile);
    }

    @Override
    public IEntityPolicyEdge<EmployeeProfile> getById(Long id) {
        return repository.findOne(id);
    }

    @Override
    public IEntityPolicyEdge<EmployeeProfile> create(Map<String, Object> json) {
        EmployeePolicyEdge epe = new EmployeePolicyEdge();
        Utilities.updateBlock("entity", json, EmployeeProfile.class, x -> epe.setEntity(x));
        Utilities.updateBlock("policy", json, BaseTCP.class, x -> epe.setPolicy(x));
        if (epe.getEntity() == null){
            Number id = (Number) Utilities.getFieldFromJSON(json, "entityId", Number.class);
            if (id == null) throw new IllegalArgumentException("No entity id in json " + json);
            EmployeeProfile employeeProfile = employeeProfileService.getById(id.longValue());
            if (employeeProfile == null) throw new IllegalArgumentException("no contract found for id " + id);
            epe.setEntity(employeeProfile);
        }
        if (epe.getEntity() == null){
            Number id = (Number) Utilities.getFieldFromJSON(json, "policyId", Number.class);
            if (id == null) throw new IllegalArgumentException("No policy id in json " + json);
            BaseTCP policy = policyService.getById(id.longValue());
            if (policy== null) throw new IllegalArgumentException("no contract found for id " + id);
            epe.setPolicy(policy);
        }
        return repository.save(epe);
    }

    @Override
    public IEntityPolicyEdge<EmployeeProfile> updateById(Long id, Map<String, Object> json) {
        EmployeePolicyEdge epe = (EmployeePolicyEdge) getById(id);
        if (epe == null) throw new IllegalArgumentException("no EmployeePolicyEdge found with id " + id);
        if (epe.getEntity() == null){
            Number eid = (Number) Utilities.getFieldFromJSON(json, "entityId", Number.class);
            if (eid == null) throw new IllegalArgumentException("No entity id in json " + json);
            EmployeeProfile profile = employeeProfileService.getById(eid.longValue());
            if (profile == null) throw new IllegalArgumentException("no profile found for id " + eid);
            epe.setEntity(profile);
        }
        if (epe.getEntity() == null){
            Number pid = (Number) Utilities.getFieldFromJSON(json, "policyId", Number.class);
            if (pid == null) throw new IllegalArgumentException("No policy id in json " + json);
            BaseTCP policy = policyService.getById(pid.longValue());
            if (policy== null) throw new IllegalArgumentException("no pocliy found for id " + pid);
            epe.setPolicy(policy);
        }
        return repository.save(epe);
    }

    @Override
    public void removeById(Long id) {
        repository.delete(id);
    }

    @Override
    public IEntityPolicyEdge<EmployeeProfile> renewPolicyTimestamp(Long id){
        IEntityPolicyEdge<EmployeeProfile> employeePolicyEdge  = getById(id);
        if (employeePolicyEdge == null) throw new IllegalArgumentException("Unable to find Employee Policy Edge with id " + id);
        employeePolicyEdge.setRenewDate(new Date());
        return repository.save((EmployeePolicyEdge) employeePolicyEdge);
    }

    @Override
    public Transaction buildTransactions(IEntityPolicyEdge<EmployeeProfile> cpe, User creator){
        if (creator == null) throw new IllegalArgumentException("Creator of transaction cannot be null");
        if (cpe == null) throw new IllegalArgumentException("Contract Policy Edge cannot be null");

        EmployeeProfile profile = cpe.getEntity();
        BaseTCP policy = cpe.getPolicy();

        if (profile == null) throw new IllegalArgumentException(String.format("null contract for ContractPolicyEdge id : " + cpe.getId()));
        if (policy == null) throw new IllegalArgumentException(String.format("null BaseTCP (policy) for ContractPolicyEdge id : " + cpe.getId()));

        BalanceEmployeeEdge balanceEmployeeEdge = (BalanceEmployeeEdge) balanceEntityEdgeService.getByEntity(profile, TransactionType.EMPLOYEE_PROFILE);

        Balance balance = balanceEmployeeEdge.getBalance();
        if (balance == null) throw new IllegalStateException("BalanceEmployeeEdge with id: " + balance.getId() + " contains null balance");

        PolicyType policyType = policy.getPolicyType();
        if(policyType == null) throw new IllegalArgumentException(String.format("null policyType for BaseTCP id " + policy.getId()));

        Double transactionAmount = 0.0;

        if(policyType.equals(PolicyType.PERCENT)){
            transactionAmount =  profile.getSalary() * (1 + policy.getConstant());
        } else if (policyType.equals(PolicyType.CONSTANT)){
            transactionAmount = profile.getSalary() + policy.getConstant();
        } else if (policyType.equals(PolicyType.RAW)){
            transactionAmount = policy.getConstant();
        }

        final int PAYMENT_FACTOR = -1; // apartment pays

        Transaction transaction = transactionService.create(
                balance,
                creator,
                policyService.getAmountWithPaymentDirection(policy, transactionAmount),
                policy.getTitle() + " for Employee ID: " + profile.getId(),
                "This transaction was automatically charged",
                null, true);

        renewPolicyTimestamp(cpe.getId());
        return transaction;
    }
}

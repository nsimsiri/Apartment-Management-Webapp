package com.apt_mgmt_sys.APIServer.services.transaction_creation_policies;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.DiscreteTime;
import com.apt_mgmt_sys.APIServer.constants.PolicyType;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdge;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdgeService;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.Room;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.BaseTCP;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.ContractPolicyEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.BalanceContractEdge;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.repositories.ContractPolicyEdgeRepository;
import com.apt_mgmt_sys.APIServer.services.ContractService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContractPolicyEdgeService implements IEntityPolicyEdgeService<Contract> {
    @Autowired
    private ContractPolicyEdgeRepository repository;
    @Autowired
    private ContractService contractService;
    @Autowired
    private BaseTCPEntityService policyService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BalanceEntityEdgeService balanceEntityEdgeService;


    @Override
    public List<? extends IEntityPolicyEdge<Contract>> getAll() {
        return Utilities.iterableToList(repository.findAll());
    }

    @Override
    public List<? extends IEntityPolicyEdge<Contract>> getByEntity(Contract entity) {
        return repository.getContractPolicyEdgesByEntity(entity);
    }

    @Override
    public List<? extends IEntityPolicyEdge<Contract>> getByEntityId(Long entityId){
        if (entityId == null) throw new IllegalArgumentException("Id cannot be null");
        Contract contract = contractService.getById(entityId);
        if (contract == null) throw new IllegalArgumentException("Cannot find contract " + entityId);
        return getByEntity(contract);
    }

    @Override
    public IEntityPolicyEdge<Contract> getById(Long id) {
        return repository.findOne(id);
    }

    @Override
    public IEntityPolicyEdge<Contract> create(Map<String, Object> json) {
        ContractPolicyEdge cpe = new ContractPolicyEdge();
        Utilities.updateBlock("entity", json, Contract.class, x -> cpe.setEntity(x));
        Utilities.updateBlock("policy", json, BaseTCP.class, x -> cpe.setPolicy(x));
        Utilities.updateBlock("startDate", json, Number.class, x -> {
            Date startDate = new Date(x.longValue());
            cpe.setStartDate(startDate);
            cpe.setRenewDate(startDate);
        });

        if (cpe.getEntity() == null){
            Number id = (Number) Utilities.getFieldFromJSON(json, "entityId", Number.class);
            if (id == null) throw new IllegalArgumentException("No entity id in json " + json);
            Contract contract = contractService.getById(id.longValue());
            if (contract == null) throw new IllegalArgumentException("no contract found for id " + id);
            cpe.setEntity(contract);
        }
        if (cpe.getPolicy() == null){
            Number id = (Number) Utilities.getFieldFromJSON(json, "policyId", Number.class);
            if (id == null) throw new IllegalArgumentException("No policy id in json " + json);
            BaseTCP policy = policyService.getById(id.longValue());
            if (policy== null) throw new IllegalArgumentException("no contract found for id " + id);
            cpe.setPolicy(policy);
        }
        BaseTCP policy = cpe.getPolicy();
        Calendar cal = Calendar.getInstance();
        DiscreteTime timePeriodType = policy.getRenewPeriod();
        Date startDate = cpe.getStartDate();
        if (startDate == null) throw new IllegalArgumentException("ContractPolicyEdge should contain startDate in json : " + json);
        Integer freq = policy.getActiveFrequency()-1;
        cal.setTime(startDate);
        if (freq == null){
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1000);
        } else {
            if (timePeriodType.equals(DiscreteTime.DAY)){
                cal.set(Calendar.DATE, cal.get(Calendar.DATE) + freq);
            } else if (timePeriodType.equals(DiscreteTime.MONTH)){
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + freq);
            } else if (timePeriodType.equals(DiscreteTime.ANNUAL)){
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + freq);
            } else if (timePeriodType.equals(DiscreteTime.BI_ANNUAL)) {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 6*freq);
            } else if (timePeriodType.equals(DiscreteTime.QUARTER)){
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 4*freq);
            }
        }
        cpe.setEndDate(cal.getTime());
        System.out.println("START===> : " + cpe.getStartDate());
        System.out.println("END===> : " + cpe.getEndDate());
        return repository.save(cpe);
    }

    @Override
    public IEntityPolicyEdge<Contract> updateById(Long id, Map<String, Object> json) {
        ContractPolicyEdge cpe = (ContractPolicyEdge) getById(id);
        if (cpe == null) throw new IllegalArgumentException("no ContractPolicyEdge found with id " + id);
        if (cpe.getEntity() == null){
            Number cid = (Number) Utilities.getFieldFromJSON(json, "entityId", Number.class);
            if (cid == null) throw new IllegalArgumentException("No entity id in json " + json);
            Contract contract = contractService.getById(cid.longValue());
            if (contract == null) throw new IllegalArgumentException("no contract found for id " + cid);
            cpe.setEntity(contract);
        }
        if (cpe.getPolicy() == null){
            Number pid = (Number) Utilities.getFieldFromJSON(json, "policyId", Number.class);
            if (pid == null) throw new IllegalArgumentException("No policy id in json " + json);
            BaseTCP policy = policyService.getById(pid.longValue());
            if (policy== null) throw new IllegalArgumentException("no contract found for id " + pid);
            cpe.setPolicy(policy);
        }

        return repository.save(cpe);
    }

    @Override
    public void removeById(Long id) {
        repository.delete(id);
    }

    @Override
    public IEntityPolicyEdge<Contract> renewPolicyTimestamp(Long id){
        IEntityPolicyEdge<Contract> contractPolicyEdge = getById(id);
        if (contractPolicyEdge == null) throw new IllegalArgumentException("unable to retreive contractPolicyEdge with id: " + id);
        contractPolicyEdge.setRenewDate(new Date());
        return repository.save((ContractPolicyEdge)contractPolicyEdge);
    }

    @Override
    public Transaction buildTransactions(IEntityPolicyEdge<Contract> cpe, User creator){
        if (creator == null) throw new IllegalArgumentException("Creator of transaction cannot be null");
        if (cpe == null) throw new IllegalArgumentException("Contract Policy Edge cannot be null");
        Contract contract = cpe.getEntity();
        BaseTCP policy = cpe.getPolicy();
        if (contract == null) throw new IllegalArgumentException(String.format("null contract for ContractPolicyEdge id : " + cpe.getId()));
        if (policy == null) throw new IllegalArgumentException(String.format("null BaseTCP (policy) for ContractPolicyEdge id : " + cpe.getId()));

        BalanceContractEdge balanceEntityEdge = (BalanceContractEdge) balanceEntityEdgeService.getByEntity(contract, TransactionType.CONTRACT);
        Balance balance = balanceEntityEdge.getBalance();
        if (balance == null) throw new IllegalStateException("BalanceContractEdge with id: " + balance.getId() + " contains null balance");
        PolicyType policyType = policy.getPolicyType();
        if(policyType == null) throw new IllegalArgumentException(String.format("null policyType for BaseTCP id " + policy.getId()));
        Room room = contract.getBookedRoom();
        if (room == null) throw new IllegalStateException(String.format("Contract with id: %s contains null room field", room.getId()));
        Double transactionAmount = 0.0;
        if(policyType.equals(PolicyType.PERCENT)){
            transactionAmount =  room.getPrice() * (1 + policy.getConstant());
        } else if (policyType.equals(PolicyType.CONSTANT)){
            transactionAmount = room.getPrice() + policy.getConstant();
        } else if (policyType.equals(PolicyType.RAW)){
            transactionAmount = policy.getConstant();
        }
        Transaction transaction = transactionService.create(
                balance,
                creator,
                policyService.getAmountWithPaymentDirection(policy, transactionAmount),
                policy.getTitle() + " for Contract ID: " + contract.getId(),
                "This transaction was automatically charged",
                null, true);
        renewPolicyTimestamp(cpe.getId());
        return transaction;
    }
}

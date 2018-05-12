package com.apt_mgmt_sys.APIServer.services.transaction_creation_policies;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.DiscreteTime;
import com.apt_mgmt_sys.APIServer.constants.PolicyType;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.BaseTCP;
import com.apt_mgmt_sys.APIServer.repositories.BaseTCPRepository;
import com.apt_mgmt_sys.APIServer.services.ContractService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BaseTCPEntityService {
    @Autowired
    private BaseTCPRepository repository;
    @Autowired
    private ContractService contractService;
    public List<BaseTCP> getAll(){
        return Utilities.iterableToList(this.repository.findAll());
    }
    public BaseTCP getById(Long id){
        return repository.findOne(id);
    }

    public BaseTCP create(Map<String, Object> json){
        BaseTCP tcp = new BaseTCP();
        Utilities.updateBlock("title", json, String.class, x -> tcp.setTitle(x)).required();
        Utilities.updateBlock("remark", json, String.class, x -> tcp.setRemark(x));
        Utilities.updateBlock("constant", json, Number.class, x -> tcp.setConstant(x.doubleValue())).required();;
        Utilities.updateBlock("policyType", json, String.class, x -> tcp.setPolicyType(PolicyType.valueOf(x))).required();;
        Utilities.updateBlock("renewPeriod", json, String.class, x -> tcp.setRenewPeriod(DiscreteTime.valueOf(x))).required();;
        Utilities.updateBlock("activeFrequency", json, Number.class, x -> tcp.setActiveFrequency(x.intValue())).required();;
        return repository.save(tcp);
    }

    public BaseTCP updateById(Long id, Map<String, Object> json){
        BaseTCP tcp = getById(id);
        if (tcp == null) throw new IllegalArgumentException("no contract found for id " + id);
        Utilities.updateBlock("title", json, String.class, x -> tcp.setTitle(x));
        Utilities.updateBlock("remark", json, String.class, x -> tcp.setRemark(x));
        Utilities.updateBlock("constant", json, Number.class, x -> tcp.setConstant(x.doubleValue()));
        Utilities.updateBlock("policyType", json, String.class, x -> tcp.setPolicyType(PolicyType.valueOf(x)));
        Utilities.updateBlock("renewPeriod", json, String.class, x -> tcp.setRenewPeriod(DiscreteTime.valueOf(x)));
        Utilities.updateBlock("activeFrequency", json, Number.class, x -> tcp.setActiveFrequency(x.intValue()));
        return repository.save(tcp);
    }

    public BaseTCP setEnabledById(Long id, Boolean enabled){
        BaseTCP tcp = getById(id);
        if (tcp == null) throw new IllegalArgumentException("no contract found for id " + id);
        tcp.setEnabled(enabled == null ? tcp.getEnabled() : enabled);
        return repository.save(tcp);
    }

    public void removeById(Long id){
        repository.delete(id);
    }

    // normalize payment factor - constant shouldn't be negative
    public Double getAmountWithPaymentDirection(BaseTCP policy, Double amount){
        final Double _amount = Math.abs(amount);
        return policy.getIsNegative() ? -1 * _amount : _amount;
    }

    public List<BaseTCP> listByTransactionType(TransactionType type){
        return repository.findByTransactionType(type);
    }

    public static List<BaseTCP> makeTestData(BaseTCPEntityService service){
        final BaseTCP policy1 = service.create(Utilities.buildMap(
                new Pair[]{
                        new Pair("title", "Salary Policy"),
                        new Pair("remark", "default policy that will generate a transaction for an Employee Balance every month. 1.06*salary for 7 days."),
                        new Pair("constant", 0.6),
                        new Pair("transactionType", TransactionType.EMPLOYEE_PROFILE.toString()),
                        new Pair("policyType", PolicyType.PERCENT.toString()),
                        new Pair("isNegative", true),
                        new Pair("renewPeriod", DiscreteTime.DAY.toString()),
                        new Pair("activeFrequency", 7)
                }
        ));
        final BaseTCP policy2 = service.create(Utilities.buildMap(
                new Pair[]{
                        new Pair("title", "Rent Policy"),
                        new Pair("remark", "default policy that will generate a transaction for an Employee Balance every month"),
                        new Pair("constant", 0.0),
                        new Pair("transactionType", TransactionType.CONTRACT.toString()),
                        new Pair("policyType", PolicyType.RAW.toString()),
                        new Pair("isNegative", true),
                        new Pair("renewPeriod", DiscreteTime.MONTH.toString()),
                        new Pair("activeFrequency", 12)
                }
        ));

        List<BaseTCP> Out = new ArrayList<>();
        Out.add(policy1);
        Out.add(policy2);
//        System.out.println(Out);
        return Out;
    }

}

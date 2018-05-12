package com.apt_mgmt_sys.APIServer.services.transaction_creation_policies;

import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdge;
import com.apt_mgmt_sys.APIServer.services.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TCPService - Transaction Creation Policy Service
 * -> decides how to build transactions
 * -> does creation of this package's stack in payable entities (Contract, Employeeprofile)
 * ->
 * */

@Service
public class TCPService {
    @Autowired
    private BaseTCPEntityService tcpEntityService;
    @Autowired
    private EntityPolicyEdgeService entityPolicyEdgeService;
    @Autowired
    private TransactionService transactionService;

    public void buildTransactions(List<IEntityPolicyEdge> entityPolicyEdges){
        for(int i = 0; i  < entityPolicyEdges.size(); i++){
            IEntityPolicyEdge<?> edge = entityPolicyEdges.get(i);

        }
    }


}

package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.ContractPolicyEdge;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ContractPolicyEdgeRepository extends PagingAndSortingRepository<ContractPolicyEdge, Long> {
    public List<ContractPolicyEdge> getContractPolicyEdgesByEntity(Contract entity);

}

package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.BalanceContractEdge;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceContractEdgeRepository extends PagingAndSortingRepository<BalanceContractEdge, Long>{
    List<BalanceContractEdge> getBalanceContractEdgesByBalance(Balance balance);
    List<BalanceContractEdge> getBalanceContractEdgesByEntity(Contract contract);
    List<BalanceContractEdge> findByBalanceNameContaining(String name);
}

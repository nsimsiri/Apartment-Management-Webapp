package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.BalanceEmployeeEdge;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceEmployeeEdgeRepository extends PagingAndSortingRepository<BalanceEmployeeEdge, Long> {
    List<BalanceEmployeeEdge> getBalanceEmployeeEdgesByBalance(Balance balance);
    List<BalanceEmployeeEdge> getBalanceEmployeeEdgesByEntity(EmployeeProfile employeeProfile);
    List<BalanceEmployeeEdge> findByBalanceNameContaining(String name);
}

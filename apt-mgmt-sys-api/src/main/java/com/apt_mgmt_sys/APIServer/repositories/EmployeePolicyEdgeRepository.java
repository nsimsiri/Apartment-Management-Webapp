package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.EmployeePolicyEdge;
import org.springframework.data.repository.PagingAndSortingRepository;


import java.util.List;

public interface EmployeePolicyEdgeRepository extends PagingAndSortingRepository<EmployeePolicyEdge, Long> {
    public List<EmployeePolicyEdge> getEmployeePolicyEdgeByEntity(EmployeeProfile ep);
}

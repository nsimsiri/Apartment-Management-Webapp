package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.CustomerProfile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerProfileRepository extends PagingAndSortingRepository<CustomerProfile, Long> {
    public CustomerProfile getCustomerProfileByCitizenId(String id);
}

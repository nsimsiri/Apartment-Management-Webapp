package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeProfileRepository extends PagingAndSortingRepository<EmployeeProfile, Long> {
    EmployeeProfile getEmployeeProfileByCitizenId(String citizenId);
    EmployeeProfile getByUser(User user);

}

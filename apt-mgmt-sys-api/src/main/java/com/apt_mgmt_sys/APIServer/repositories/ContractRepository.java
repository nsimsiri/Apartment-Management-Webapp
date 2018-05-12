package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.constants.ContractType;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.CustomerProfile;
import com.apt_mgmt_sys.APIServer.models.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContractRepository extends PagingAndSortingRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
//    List<Contract> findAll(Specification<Contract> specification, Pageable pageable);
    List<Contract> getContractsByCustomerProfile(CustomerProfile profile, Pageable pageRequest);
    List<Contract> getContractsByBookedRoom(Room bookedRoom);
    List<Contract> getContractsByBookedRoomAndStartDateGreaterThanEqualAndEndDateLessThanEqual(Room bookedRoom, Date startDate, Date endDate);
    List<Contract> getContractsByContractType(ContractType contractType);

}

package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.BaseTCP;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BaseTCPRepository extends PagingAndSortingRepository<BaseTCP, Long> {
      public List<BaseTCP> getBaseTCPsByTitle(String title);
      public List<BaseTCP> findByTransactionType(TransactionType transactionType);

}

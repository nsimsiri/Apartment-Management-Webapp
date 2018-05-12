package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BalanceRepository extends PagingAndSortingRepository<Balance, Long> {
}

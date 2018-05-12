package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
    List<Transaction> findByBalanceOrderByTargetDateAsc(Balance balance, Pageable page);
    List<Transaction> findByBalanceOrderByTargetDateDesc(Balance balance, Pageable page);
    List<Transaction> findByBalanceAndEnabled(Balance balance, Boolean enabled, Pageable page);
    List<Transaction> findByBalance(Balance balance, Pageable page);
    Page<Transaction> findAll(Pageable page);
}

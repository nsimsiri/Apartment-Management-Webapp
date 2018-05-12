package com.apt_mgmt_sys.APIServer.interfaces;

import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;

import java.util.List;
import java.util.Map;

public interface ITransactionService {
    public Transaction create(Map<String, Object> json);
    public List<Transaction> getAll();
    public Transaction getById(Long id);
    public Transaction updateฺById(Long id, Map<String, Object> json);
    public Transaction removeById(Long id);
    public Transaction save(Transaction transaction);
    public Double getBalance(Long id);
}

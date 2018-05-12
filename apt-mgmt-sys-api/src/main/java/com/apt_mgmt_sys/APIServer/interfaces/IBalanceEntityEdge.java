package com.apt_mgmt_sys.APIServer.interfaces;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;

public interface IBalanceEntityEdge<T> {
    public T getEntity();
    public void setEntity(T entity);
    public Balance getBalance();
    public void setBalance(Balance balance);
    public TransactionType getType();
}

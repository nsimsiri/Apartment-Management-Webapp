package com.apt_mgmt_sys.APIServer.interfaces;

import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;

public interface ITransactionCreationPolicy {
    public Double getTransactionAmount();
}

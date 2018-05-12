package com.apt_mgmt_sys.APIServer.constants;

public enum TransactionState {
    PENDING("PENDING"),
    COMPLETE("COMPLETE"),
    OVERDUED("OVERDUE"),
    INVALIDATED("INVALIDATED");
    private String transactionState;
    TransactionState(String x){
        this.transactionState = x;
    }
}

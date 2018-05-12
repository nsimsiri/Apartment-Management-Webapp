package com.apt_mgmt_sys.APIServer.constants;

public enum TransactionType {
    CONTRACT("CONTRACT"),
    EMPLOYEE_PROFILE("EMPLOYEE_PROFILE"),
    CUSTOMER_PROFILE("CUSTOMER_PROFILE"),
    MISC("MISC");
    private String transactionType;
    TransactionType(String x){
        this.transactionType = x;
    }
}

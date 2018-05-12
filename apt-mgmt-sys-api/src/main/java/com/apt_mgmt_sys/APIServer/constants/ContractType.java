package com.apt_mgmt_sys.APIServer.constants;

public enum ContractType {
    SHORT_TERM("SHORT_TERM"),
    LONG_TERM("LONG_TERM");
    private String contractTypeString = "";
    ContractType(String x){
        this.contractTypeString = x;
    }
}

package com.apt_mgmt_sys.APIServer.constants;

public enum PolicyType {
    PERCENT("PERCENT"),
    CONSTANT("CONSTANT"),
    RAW("RAW");
    private String str;
    PolicyType(String x){
        this.str = x;
    }
}

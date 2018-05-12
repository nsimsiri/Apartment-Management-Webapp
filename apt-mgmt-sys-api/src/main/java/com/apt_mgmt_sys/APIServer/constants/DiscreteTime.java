package com.apt_mgmt_sys.APIServer.constants;

public enum DiscreteTime {
    MONTH("MONTH"),
    DAY("DAY"),
    ANNUAL("ANNUAL"),
    QUARTER("QUARTER"),
    BI_ANNUAL("BI_ANNUAL");
    private String val;
    DiscreteTime(String val){ this.val = val;}

}

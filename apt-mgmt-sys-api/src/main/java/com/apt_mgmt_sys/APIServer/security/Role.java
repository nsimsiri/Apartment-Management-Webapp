package com.apt_mgmt_sys.APIServer.security;

public enum Role {
    ADMIN("ADMIN"),
    EMPLOYEE("EMPLOYEE"),
    CUSTOMER("CUSTOMER"),
    NONE("NONE");

    private String roleString;
     Role(String roleStr){
        this.roleString = roleStr;
    }
}

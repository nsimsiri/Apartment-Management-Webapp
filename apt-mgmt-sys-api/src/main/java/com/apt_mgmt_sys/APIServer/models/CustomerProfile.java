package com.apt_mgmt_sys.APIServer.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CustomerProfile {
    @Id
    @GeneratedValue Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String citizenId;
    private String address;
    private boolean enabled;
    private String remark;

    public CustomerProfile(){
        this.id = null;
        this.firstname = ""; this.lastname = ""; this.email = "";
        this.phoneNumber = ""; this.citizenId = ""; this.address = "";
        this.enabled = false; this.remark = "";
    }

    public CustomerProfile(String firstname, String lastname, String email, String phoneNumber,
                    String citizenId, String address){
        this.firstname = firstname; this.lastname = lastname; this.email = email;
        this.phoneNumber = phoneNumber; this.citizenId = citizenId; this.address = address;
        this.enabled = true; this.remark = "";
    }

    public CustomerProfile(String firstname, String lastname, String email, String phoneNumber,
                           String citizenId, String address, String remark){
        this.firstname = firstname; this.lastname = lastname; this.email = email;
        this.phoneNumber = phoneNumber; this.citizenId = citizenId; this.address = address;
        this.enabled = true; this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

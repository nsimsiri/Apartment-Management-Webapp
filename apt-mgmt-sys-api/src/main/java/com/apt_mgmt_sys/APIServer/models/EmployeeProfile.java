package com.apt_mgmt_sys.APIServer.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class EmployeeProfile {
    @Id
    @GeneratedValue private Long id;

    @OneToOne
    private User user;

    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String citizenId;
    private String address;
    private Double salary;
    private boolean enabled;
    private boolean validEmployee;
    public EmployeeProfile(){
        this.firstname = ""; this.lastname = ""; this.email = "";
        this.phoneNumber = ""; this.citizenId = ""; this.address = "";
        this.salary = 0.0; this.enabled = true; this.validEmployee = false;

    }
    public EmployeeProfile(String firstname, String lastname, String email, String phoneNumber,
                           String address, String citizenId, Double salary, boolean validEmployee){
        this.firstname = firstname; this.lastname = lastname; this.email = email;
        this.phoneNumber = phoneNumber; this.citizenId = citizenId; this.address = address;
        this.salary = salary; this.enabled = true; this.validEmployee = validEmployee;
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

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isValidEmployee() {
        return validEmployee;
    }

    public void setValidEmployee(boolean validEmployee) {
        this.validEmployee = validEmployee;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

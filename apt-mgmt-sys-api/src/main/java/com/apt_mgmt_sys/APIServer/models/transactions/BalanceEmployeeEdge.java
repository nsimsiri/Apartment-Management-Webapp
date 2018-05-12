package com.apt_mgmt_sys.APIServer.models.transactions;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BalanceEmployeeEdge implements IBalanceEntityEdge<EmployeeProfile>{
    @Id
    @GeneratedValue private Long id;
    @OneToOne
    private Balance balance;
    @OneToOne
    private EmployeeProfile entity;
    private final TransactionType type = TransactionType.EMPLOYEE_PROFILE;

    public BalanceEmployeeEdge(){
        this.balance = null;
        this.entity = null;
    }

    public BalanceEmployeeEdge(Balance balance, EmployeeProfile employeeProfile){
        this.balance = balance;
        this.entity = employeeProfile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Balance getBalance() {
        return balance;
    }

    @Override
    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public EmployeeProfile getEntity(){ return this.entity; }
    public void setEntity(EmployeeProfile e){ this.entity = e; }

    @Override
    public TransactionType getType() {
        return type;
    }
}

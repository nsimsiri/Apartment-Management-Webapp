package com.apt_mgmt_sys.APIServer.models.transactions;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.models.Contract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BalanceContractEdge implements IBalanceEntityEdge<Contract>{
    @Id
    @GeneratedValue private Long id;
    @OneToOne
    private Balance balance;
    @OneToOne
    private Contract entity;
    private final TransactionType type = TransactionType.CONTRACT;

    public BalanceContractEdge(){
        this.balance= null;
        this.entity = null;
    }

    public BalanceContractEdge(Balance balance, Contract entity){
        this.balance = balance;
        this.entity = entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }


    public Contract getEntity(){
        return this.entity;
    }

    public void setEntity(Contract c){
        this.entity = c;
    }

    public TransactionType getType() {
        return type;
    }
}

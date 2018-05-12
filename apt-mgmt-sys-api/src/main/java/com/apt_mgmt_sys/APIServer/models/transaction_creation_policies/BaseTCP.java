package com.apt_mgmt_sys.APIServer.models.transaction_creation_policies;

import com.apt_mgmt_sys.APIServer.constants.DiscreteTime;
import com.apt_mgmt_sys.APIServer.constants.PolicyType;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity

public class BaseTCP {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String remark;
    private Double constant;
    private TransactionType transactionType;
    private PolicyType policyType;
    private Boolean enabled;
    private Boolean isNegative; //true = money going outside, false = money comign in.
    private DiscreteTime renewPeriod;
    private Integer activeFrequency;

    public BaseTCP(){
        this.title = "";
        this.remark = "";
        this.constant = 0.0;
        this.transactionType = TransactionType.CONTRACT;
        this.policyType = PolicyType.RAW;
        this.enabled = true;
        this.isNegative = false;
        this.renewPeriod = DiscreteTime.MONTH;
        this.activeFrequency = 0;

    }

    public BaseTCP(String title, String remark, Double constant, boolean isNegative, TransactionType transactionType,
                   PolicyType policyType, DiscreteTime discreteTime, int activeFrequency){
        this.title = title;
        this.remark = remark;
        this.constant = constant;
        this.isNegative = isNegative;
        this.transactionType = transactionType;
        this.policyType = policyType;
        this.enabled = true;
        this.activeFrequency = activeFrequency;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getConstant() {
        return constant;
    }

    public void setConstant(Double constant) {
        this.constant = constant;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public DiscreteTime getRenewPeriod() {
        return renewPeriod;
    }

    public void setRenewPeriod(DiscreteTime renewPeriod) {
        this.renewPeriod = renewPeriod;
    }

    public Integer getActiveFrequency() {
        return activeFrequency;
    }

    public void setActiveFrequency(Integer activeFrequency) {
        this.activeFrequency = activeFrequency;
    }

    public Boolean getIsNegative() {
        return isNegative;
    }

    public void setIsNegative(Boolean negative) {
        isNegative = negative;
    }

    @Override
    public String toString(){
        try {
            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            return om.writeValueAsString(this);
        } catch(Exception e){
            e.printStackTrace();
            return "ERR";
        }
    }
}

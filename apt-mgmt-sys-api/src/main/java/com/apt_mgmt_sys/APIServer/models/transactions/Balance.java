package com.apt_mgmt_sys.APIServer.models.transactions;

import com.apt_mgmt_sys.APIServer.constants.TransactionState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Balance{
    @Id
    @GeneratedValue
    private Long id;

    private TransactionState state;
    private Double cachedAmount;
    private Long latestTransactionId;
    private String name;
    private String remark;
    private Boolean enabled;

    @CreatedBy
    private Date createDate;

    @LastModifiedBy
    private Date modifiedDate;



    public Balance (){
        this.state = TransactionState.COMPLETE;
        this.cachedAmount = 0.0;
        this.latestTransactionId = null;
        this.name = "";
        this.remark = "";
        this.enabled = true;
    }

    public Balance(String name, String remark, TransactionState state){
        this.state = state;
        this.cachedAmount = 0.0;
        this.latestTransactionId = null;
        this.name = name;
        this.remark = remark;
        this.enabled = true;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    public Double getCachedAmount() {
        return cachedAmount;
    }

    public void setCachedAmount(Double cachedAmount) {
        this.cachedAmount = cachedAmount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getLatestTransactionId() {
        return latestTransactionId;
    }

    public void setLatestTransactionId(Long latestTransactionId) {
        this.latestTransactionId = latestTransactionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString(){
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            return om.writeValueAsString(this);
        } catch (Exception e){
            e.printStackTrace();
            return "Balance[ERROR]";
        }
    }
}

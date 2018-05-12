package com.apt_mgmt_sys.APIServer.models.transaction_creation_policies;

import com.apt_mgmt_sys.APIServer.constants.PolicyType;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IEntityPolicyEdge;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.Room;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
public class EmployeePolicyEdge implements IEntityPolicyEdge<EmployeeProfile> {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private EmployeeProfile entity;
    @OneToOne
    private BaseTCP policy;
    private Date startDate;
    private Date endDate;
    private Date renewDate;

    public EmployeePolicyEdge(){
        this.entity = null;
        this.policy = null;
    }

    public EmployeePolicyEdge(EmployeeProfile entity, BaseTCP policy){
        this.entity = entity;
        this.policy = policy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public EmployeeProfile getEntity() {
        return entity;
    }

    public void setEntity(EmployeeProfile entity) {
        this.entity = entity;
    }

    @Override
    public BaseTCP getPolicy() {
        return policy;
    }

    @Override
    public void setPolicy(BaseTCP policy) {
        this.policy = policy;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Date getRenewDate() {
        return renewDate;
    }

    @Override
    public void setRenewDate(Date renewDate) {
        this.renewDate = renewDate;
    }
}

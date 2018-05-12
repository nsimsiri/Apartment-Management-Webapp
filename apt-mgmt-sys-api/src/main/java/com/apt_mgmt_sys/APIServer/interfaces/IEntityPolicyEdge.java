package com.apt_mgmt_sys.APIServer.interfaces;

import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.BaseTCP;

import java.util.Date;


public interface IEntityPolicyEdge<T> {
    public T getEntity();
    public void setEntity(T entity);
    public BaseTCP getPolicy();
    public void setPolicy(BaseTCP policy);
    public Long getId();
    public void setId(Long id);
    public void setStartDate(Date date);
    public Date getStartDate();
    public void setEndDate(Date date);
    public Date getEndDate();
    public void setRenewDate(Date renewDate);
    public Date getRenewDate();
}

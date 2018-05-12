package com.apt_mgmt_sys.APIServer.interfaces;

import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Contain business logic on transaction creation
 * */
public interface IEntityPolicyEdgeService<T> {
    public List<? extends IEntityPolicyEdge<T>> getAll();
    public List<? extends IEntityPolicyEdge<T>> getByEntity(T entity);
    public List<? extends IEntityPolicyEdge<T>> getByEntityId(Long entityId);
    public IEntityPolicyEdge<T> getById(Long id);
    public IEntityPolicyEdge<T> create(Map<String, Object> json);
    public IEntityPolicyEdge<T> updateById(Long id, Map<String, Object> json);
    public void removeById(Long id);
    public Transaction buildTransactions(IEntityPolicyEdge<T> entityPolicyEdge, User creator);
    public IEntityPolicyEdge<T> renewPolicyTimestamp(Long id);
}

package com.apt_mgmt_sys.APIServer.interfaces;

import com.apt_mgmt_sys.APIServer.models.transactions.Balance;

import java.util.List;
import java.util.Map;

public interface IBalanceEntityEdgeService<T> {
    public IBalanceEntityEdge<T> getById(Long id);
    public List<? extends IBalanceEntityEdge<T>> getAll();
    public List<? extends IBalanceEntityEdge<T>> findByBalanceName(String name);
//    public List<? extends IBalanceEntityEdge<T>> getByBalance(Balance balance);
//    public List<? extends IBalanceEntityEdge<T>> getByEntity(T entity);
    public IBalanceEntityEdge<T> getByBalance(Balance balance);
    public IBalanceEntityEdge<T> getByEntity(T entity);
    public IBalanceEntityEdge<T> create(Map<String, Object> json);
    public IBalanceEntityEdge<T> updateById(Long id, Map<String, Object> json);
    public void removeById(Long id);

}

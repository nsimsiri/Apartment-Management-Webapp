package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository  extends PagingAndSortingRepository<User, Long> {
    public User findByUsername(String username);
    public List<User> findByEnabled(boolean enabled);
}

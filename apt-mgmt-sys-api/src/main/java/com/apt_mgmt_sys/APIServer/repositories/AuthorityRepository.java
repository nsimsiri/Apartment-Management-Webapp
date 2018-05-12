package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.security.Role;
import com.apt_mgmt_sys.APIServer.models.Authority;
import com.apt_mgmt_sys.APIServer.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends PagingAndSortingRepository<Authority, Long> {
    public Authority getAuthorityById(Long id);
    public Authority getAuthorityByUserAndRole(User user, Role role);
    public List<Authority> getAuthoritiesByUser(User user);
    public List<Authority> getAuthoritiesByRole(Role role);
}

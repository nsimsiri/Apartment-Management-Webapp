package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.models.Authority;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.repositories.AuthorityRepository;
import com.apt_mgmt_sys.APIServer.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorityService {
    @Autowired
    private AuthorityRepository auth_db;
    public Authority getAuthorityById(Long id){
        return auth_db.findOne(id);
    }
    public Authority getAuthorityByUserAndRole(User user, Role role){
        return auth_db.getAuthorityByUserAndRole(user, role);
    }

    public Authority getAuthorityByUserAndRole(User user, String roleStr){
        try {
            Role role = Role.valueOf(roleStr);
            return getAuthorityByUserAndRole(user, role);
        } catch (Exception e){
            return null;
        }
    }

    public List<Authority> getAuthoritiesbyUser(User user){
        return auth_db.getAuthoritiesByUser(user);
    }
    public List<Authority> getAuthoritiesByRole(Role role) {return auth_db.getAuthoritiesByRole(role); }

    public Authority create(User user, Role role){
        Authority maybeDupAuth = getAuthorityByUserAndRole(user, role);
        if (maybeDupAuth != null){
            throw new IllegalArgumentException(String.format("Duplicated Authority/Role entry with user: %s role: %s\n", user.getUsername(), role));
        }
        Authority au = new Authority(user, role);
        try {
            au = auth_db.save(au);
            return au;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Authority> getAuthorities(){
        List<Authority> Out = new ArrayList<>();
        auth_db.findAll().forEach(Out::add);
        return Out;
    }

    public Authority removeById(Long id){
        Authority auth = getAuthorityById(id);
        if (auth == null) return null;
        return remove(auth);
    }

    public Authority remove(Authority auth){
        auth.setEnabled(false);
        return auth_db.save(auth);
    }

    public boolean deleteById(Long id){
        auth_db.delete(id);
        return getAuthorityById(id) == null ? true : false;
    }

}

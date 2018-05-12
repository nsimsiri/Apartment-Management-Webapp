package com.apt_mgmt_sys.APIServer.models;

import com.apt_mgmt_sys.APIServer.security.Role;

import javax.persistence.*;

@Entity
public class Authority {
    @Id
    @GeneratedValue Long id;

    @OneToOne
    private User user;
    @Column(updatable = false)
    private Role role;
    private boolean enabled;
    Authority(){ this.role=Role.NONE;  this.user = null; }
    public Authority(User user, Role role){
        this.role = role;
        this.user= user;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Role getRole() {
        return role == null ? Role.NONE : role;
    }
    public void setRole(Role role) { this.role = role == null ? Role.NONE : role; }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString(){
        return String.format("Authority[%s ROLE: %s]", user, role.toString());
    }
}

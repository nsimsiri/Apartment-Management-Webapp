package com.apt_mgmt_sys.APIServer.models;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class FileEntity {
    @Id
    @GeneratedValue private Long id;
    private String location;
    private String name;
    private TransactionType entityType;
    private Long entityId;
    @CreatedBy
    private Date created;

    @LastModifiedBy
    private Date lastModified;

    public FileEntity(){
        this.location = "";
        this.name = "";
        this.entityType = null;
        this.entityId = null;
    }

    public FileEntity(String name, String location, TransactionType entityType, Long entityId){
        this.name = name;
        this.location = location;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionType getEntityType() {
        return entityType;
    }

    public void setEntityType(TransactionType entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Boolean getTemp() {
        return this.entityId == null;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString(){
        try {
            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            return om.writeValueAsString(this);
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}

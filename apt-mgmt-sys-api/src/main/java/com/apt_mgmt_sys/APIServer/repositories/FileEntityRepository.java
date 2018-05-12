package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileEntityRepository extends PagingAndSortingRepository<FileEntity, Long> {
    public List<FileEntity> findByEntityTypeAndEntityId(TransactionType entityType, Long entityId, Pageable page);
    public Page<FileEntity> findAll(Pageable page);
}

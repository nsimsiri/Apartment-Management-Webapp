package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.Task;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
}

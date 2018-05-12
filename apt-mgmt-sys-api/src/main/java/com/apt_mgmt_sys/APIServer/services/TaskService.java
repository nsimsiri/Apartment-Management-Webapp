package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.models.Task;
import com.apt_mgmt_sys.APIServer.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    @Autowired private TaskRepository task_db;

    public Task getById(long id){
        return task_db.findOne(id);
    }

    public List<Task> getAll(){
        List<Task> tasks = new ArrayList<>();
        task_db.findAll().forEach(tasks::add);
        return tasks;
    }
    public Task create(String text) {
        Task t = new Task(text);
        t = task_db.save(t);
        return t;
    }

    public Task updateById(long id, String text, boolean done){
        Task t = this.getById(id);
        if (t==null) return null;
        t.setText(text);
        t.setDone(done);
        t = task_db.save(t);
        return t;
    }

    public void deleteById(long id){
        task_db.delete(id);
    }
}

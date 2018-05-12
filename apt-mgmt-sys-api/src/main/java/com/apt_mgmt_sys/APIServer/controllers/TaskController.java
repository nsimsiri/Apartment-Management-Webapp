package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.models.Task;
import com.apt_mgmt_sys.APIServer.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/tasks")
public class TaskController {
    @Autowired
    TaskService taskService;

    @RequestMapping(value={"/",""}, method= RequestMethod.POST)
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> req){

        String text = (String) req.get("text");
        Task t = taskService.create(text);
        return new ResponseEntity<Task>(t, HttpStatus.OK);
    }

    @RequestMapping(value={"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<List<Task>> getAllTasks(){

        return new ResponseEntity<List<Task>>(taskService.getAll(), HttpStatus.OK);
    }

    @RequestMapping(value={"/{id}"}, method = RequestMethod.GET)
    public ResponseEntity<Task> getById(@PathVariable(value="id") Long id){
        Task t = taskService.getById(id);
        return new ResponseEntity<Task>(t, HttpStatus.OK);
    }

    @RequestMapping(value ="/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Task> updateById(@PathVariable(value="id") Long id, @RequestBody Map<String, Object> req){
        Task t = taskService.getById(id);

        if (t!=null){
            t.setText((req.containsKey("text") && !req.get("text").equals(t.getText())) ? (String)req.get("text") : t.getText());
            t.setDone((req.containsKey("done") && !req.get("done").equals(t.isDone())) ? (boolean)req.get("done") : t.isDone());
            taskService.updateById(id, t.getText(), t.isDone());
        }
        return t == null ? new ResponseEntity<Task>(HttpStatus.BAD_REQUEST) : new ResponseEntity<Task>(t, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<Boolean> removeById(@PathVariable(value="id") Long id){
        taskService.deleteById(id);
        Task t = taskService.getById(id);
        return t == null ? new ResponseEntity<>(true, HttpStatus.OK) : new ResponseEntity<>(false, HttpStatus.OK);
    }

}

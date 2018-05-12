package com.apt_mgmt_sys.APIServer.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Task {
    @Id
    @GeneratedValue Long id;
    private String text;
    private boolean done;
    public Task(){this.text="";this.done=false;}
    public Task(String text){ this.text=text; this.done=false; }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }
    @Override
    public String toString(){ return String.format("Task[%s %s]", text, done); }
}

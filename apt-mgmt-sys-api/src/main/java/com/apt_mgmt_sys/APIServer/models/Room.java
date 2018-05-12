package com.apt_mgmt_sys.APIServer.models;


import com.apt_mgmt_sys.APIServer.constants.RoomType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Room {
     @Id
     @GeneratedValue Long id;
     private String roomNumber;
     private Double price;
     private RoomType roomType;
     private String description;
     private boolean enabled;

     public Room(){
         this.roomNumber = "";
         this.price = -1.0;
         this.roomType = null;
         this.description = "";
     }

     public Room(String roomNumber, Double price, RoomType roomType, String description){
         this.roomNumber = roomNumber; this.price = price;
         this.roomType = roomType; this.description=  description;
     }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

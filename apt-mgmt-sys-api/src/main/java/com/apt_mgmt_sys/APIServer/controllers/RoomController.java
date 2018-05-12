package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.models.Room;
import com.apt_mgmt_sys.APIServer.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(value = "/rooms")
public class RoomController {
    @Autowired
    RoomService roomService;
    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<List<Room>> getAll(){
        return ResponseEntity.ok().body(roomService.getAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> getById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson;
        try {
            responseJson = new HashMap<>();
            Room room = roomService.getById(id);
            responseJson.put("room", room);

        } catch (Exception e) {
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok().body(responseJson);
    }

    @RequestMapping(value = "/searchAvailable", method = RequestMethod.GET)
    public ResponseEntity<List> listAvailableRooms(@RequestParam(value="from") Long from,
                                                  @RequestParam(value="to") Long to){
        List<Room> rooms = new ArrayList<>();
        try {
            Date fromDate = new Date(from);
            Date toDate = new Date(to);
            rooms = roomService.getAvailableRooms(fromDate, toDate, true);
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(rooms);
        }
        return ResponseEntity.ok(rooms);
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity<Map> create(@RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            responseJson = new HashMap<>();
            Room room = roomService.create(json);
            responseJson.put("room", room);
        } catch (Exception e){
            responseJson = new HashMap<>();
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok().body(responseJson);

    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateById(@PathVariable(value = "id") Long id, @RequestBody Map<String, Object> json){
        Map<String, Object> responseJson = null;
        try {
            responseJson = new HashMap<>();
            Room room = roomService.updateById(id, json);
            responseJson.put("room", room);
        } catch (Exception e){
            responseJson = new HashMap<>();
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok().body(responseJson);

    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public ResponseEntity<Map> removeById(@PathVariable(value = "id") Long id){
        Map<String, Object> responseJson = null;
        try {
            responseJson = new HashMap<>();

        } catch (Exception e){
            responseJson = new HashMap<>();
        }
        return ResponseEntity.ok().body(responseJson);
    }
}

package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.RoomType;
import com.apt_mgmt_sys.APIServer.models.Room;
import com.apt_mgmt_sys.APIServer.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    ContractService contractService;

    public List<Room> getAll(){
        List<Room> Out = new ArrayList<>();
        roomRepository.findAll().forEach(x -> Out.add(x));
        return Out;
    }

    public List<Room> getAvailableRooms(Date from, Date to, Boolean invert){
        List<Room> allRooms = getAll();
        return allRooms.stream()
                .filter(room -> contractService.getContractsForRoomWithinDateRange(room, from ,to).size() == 0)
                .collect(Collectors.toList());
    }

    public Room getById(Long id){
        return roomRepository.findOne(id);
    }

    public Room create(String roomNumber, Double price, RoomType roomType, String description){
        Room room = new Room(roomNumber, price, roomType, description);
        return roomRepository.save(room);
    }

    public Room create(Map<String, Object> json){
        Room room = new Room();
        if (!json.containsKey("roomNumber")) throw new IllegalArgumentException("json requires \"roomNumber\" field");
        String roomNumber = (String)Utilities.getFieldFromJSON(json, "roomNumber", String.class);
        Room maybeDupRoom = roomRepository.getRoomByRoomNumber(roomNumber);
        if (maybeDupRoom!= null)
            throw new IllegalArgumentException(String.format("Non-unique roomNumber in Room creation. Duplicated with Room entity id %s", maybeDupRoom.getId()));
        Utilities.updateBlock("roomNumber", json, String.class, x -> room.setRoomNumber(x));
        Utilities.updateBlock("roomType", json, String.class, x -> room.setRoomType(RoomType.valueOf(x)));
        Utilities.updateBlock("price", json, Number.class, x -> room.setPrice(x.doubleValue()));
        Utilities.updateBlock("description", json, String.class, x->room.setDescription(x));
        room.setEnabled(true);
        return roomRepository.save(room);
    }

    public Room updateById(Long id, Map<String, Object> json){
        Room room = getById(id);
        if (room == null) throw new IllegalArgumentException("unable to find Room with id : " + id);

        if (!json.containsKey("roomNumber")) throw new IllegalArgumentException("json requires \"roomNumber\" field");
        String roomNumber = (String)Utilities.getFieldFromJSON(json, "roomNumber", String.class);
        Room maybeDupRoom = roomRepository.getRoomByRoomNumber(roomNumber);
        if (maybeDupRoom!= null && (room.getId()!=maybeDupRoom.getId()))
            throw new IllegalArgumentException(String.format("Non-unique roomNumber in Room Update. Duplicated with Room entity id %s", maybeDupRoom.getId()));
        Utilities.updateBlock("roomNumber", json, String.class, x -> room.setRoomNumber(x));
        Utilities.updateBlock("roomType", json, String.class, x -> room.setRoomType(RoomType.valueOf(x)));
        Utilities.updateBlock("price", json, Number.class, x -> room.setPrice(x.doubleValue()));
        Utilities.updateBlock("description", json, String.class, x->room.setDescription(x));
        Utilities.updateBlock("enabled", json, Boolean.class, x->room.setEnabled(x == null ? false : x));
        return roomRepository.save(room);
    }

    public void deleteById(Long id){
        roomRepository.delete(id);
    }

    public static List<Room> initializeData(RoomService service){
        List<Room> rooms = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            String roomNum = "A" + (i+1);
            if (i%2 == 0){
                rooms.add(service.create(roomNum, 1500.0, RoomType.DAILY_SINGLE, "Single bed daily room"));
            } else {
                rooms.add(service.create(roomNum, 1200.0, RoomType.DAILY_TWIN, "Twin bed daily room"));
            }

        }
        for(int i = 0; i < 38; i++){
            String roomNum = "B" + (i+1);
            rooms.add(service.create(roomNum, 7800.0, RoomType.MONTHLY, "Monthly rentable rooms"));
        }
        return rooms;
    }
}


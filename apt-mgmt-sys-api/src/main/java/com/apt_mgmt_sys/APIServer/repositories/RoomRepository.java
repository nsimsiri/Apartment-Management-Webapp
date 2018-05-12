package com.apt_mgmt_sys.APIServer.repositories;

import com.apt_mgmt_sys.APIServer.models.Room;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends PagingAndSortingRepository<Room, Long> {
    public Room getRoomByRoomNumber(String roomNumber);
}

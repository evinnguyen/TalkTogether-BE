package com.talktogether.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talktogether.backend.entity.Room;
import com.talktogether.backend.entity.enums.Language;
import com.talktogether.backend.entity.enums.Level;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    
    // Lấy danh sách các phòng đang mở ở Sảnh chờ Lobby, có hỗ trợ bộ lọc theo Ngôn ngữ và Trình độ
    @Query("SELECT r FROM ROOM r WHERE (:language IS NULL OR r.language = :language)" + 
        "AND (:level IS NULL OR r.level = :level) ORDER BY r.createdAt DESC"
    )
    List<Room> findRoomForLobby(@Param("language") Language language, @Param("level") Level level);
}

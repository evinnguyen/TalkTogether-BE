package com.talktogether.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talktogether.backend.entity.RoomMember;

@Repository 
public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {

    Optional<RoomMember> findRoomByUserId(UUID userId);

    long countUserInRoom(UUID roomId);

    @Query("SELECT rm FROM RoomMember rm JOIN FETCH rm.user WHERE rm.room.id = :roomId ORDER BY rm.joinedAt ASC")
    List<RoomMember> findMembersByRoomId(@Param ("roomId") UUID roomId);
    // 4. Cho user rời khỏi phòng (xóa phiên có mặt của user)
    void deleteByUserId(UUID userId);
    // 5. Kiểm tra user có phải đang ở trong phòng này không
    boolean existsByRoomIdAndUserId(UUID roomId, UUID userId);
    
}

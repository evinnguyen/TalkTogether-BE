package com.talktogether.backend.service;

import java.util.List;
import java.util.UUID;

import com.talktogether.backend.dto.response.RoomResponse;
import com.talktogether.backend.dto.request.CreateRoomRequest;
import com.talktogether.backend.entity.enums.Language;
import com.talktogether.backend.entity.enums.Level;

public interface RoomService {
    
   // 1. Lấy danh sách các phòng ở sảnh Lobby (có lọc theo Ngôn ngữ và Trình độ)
    List<RoomResponse> getLobbyRooms(Language language, Level level);
    // 2. Tạo phòng mới (tạo xong tự động đưa người tạo vào làm HOST)
    RoomResponse createRoom(CreateRoomRequest request);
    // 3. Tham gia vào một phòng
    RoomResponse joinRoom(UUID roomId);
    // 4. Rời khỏi phòng hiện tại (nếu phòng hết người sẽ tự động xóa)
    void leaveCurrentRoom();
    // 5. Lấy thông tin phòng hiện tại người dùng đang tham gia (phục vụ F5 trang web)
    RoomResponse getCurrentRoom();
}

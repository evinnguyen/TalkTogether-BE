package com.talktogether.backend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talktogether.backend.dto.request.CreateRoomRequest;
import com.talktogether.backend.dto.response.CreatorDto;
import com.talktogether.backend.dto.response.MemberResponse;
import com.talktogether.backend.dto.response.RoomResponse;
import com.talktogether.backend.entity.Room;
import com.talktogether.backend.entity.RoomMember;
import com.talktogether.backend.entity.User;
import com.talktogether.backend.entity.enums.Language;
import com.talktogether.backend.entity.enums.Level;
import com.talktogether.backend.entity.enums.RoomRole;
import com.talktogether.backend.exception.AppException;
import com.talktogether.backend.exception.ErrorCode;
import com.talktogether.backend.repository.RoomMemberRepository;
import com.talktogether.backend.repository.RoomRepository;
import com.talktogether.backend.security.SecurityUtils;
import com.talktogether.backend.service.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getLobbyRooms(Language language, Level level) {
        List<Room> rooms = roomRepository.findRoomForLobby(language, level);
        return rooms.stream().map(this::mapToRoomResponse).toList();

    }

    @Override
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();

        // 1. Nếu người dùng đang ở phòng khác, tự động rút họ ra khỏi phòng cũ trước
        leaveRoomIfPresent(currentUser);

        // 2. Tạo phòng mới
        Room room = Room.builder()
                .title(request.getTitle())
                .language(request.getLanguage())
                .level(request.getLevel())
                .maxParticipants(request.getMaxParticipants())
                .creator(currentUser)
                .build();

        Room savedRoom = roomRepository.save(room);

        // 3. Đưa người tạo vào phòng với vai trò HOST
        RoomMember hostMember = RoomMember.builder()
                .room(savedRoom)
                .user(currentUser)
                .role(RoomRole.OWNER)
                .build();

        roomMemberRepository.save(hostMember);

        return mapToRoomResponse(savedRoom);
    }

    @Override
    @Transactional
    public RoomResponse joinRoom(UUID roomId) {
        User currentUser = SecurityUtils.getCurrentUser();

        Room targetRoom = roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        // Kiểm tra xem người này có đang ngồi ở phòng nào không
        Optional<RoomMember> currentMemberShip = roomMemberRepository.findRoomByUserId(currentUser.getId());
        if (currentMemberShip.isPresent()) {
            if (currentMemberShip.get().getRoom().getId().equals(roomId)) {
                throw new AppException(ErrorCode.ALREADY_IN_THIS_ROOM);
            }

            // Nếu đang ở phòng khác -> Rút khỏi phòng cũ trước
            leaveRoomInternal(currentMemberShip.get());
        }

        // Kiểm tra phòng đã đầy người chưa
        long currentMemberInRoom = roomMemberRepository.countUserInRoom(roomId);
        if (currentMemberInRoom >= targetRoom.getMaxParticipants()) {
            throw new AppException(ErrorCode.ROOM_FULL);
        }

        // Thêm người dùng vào phòng mới với vai trò MEMBER
        RoomMember newMember = RoomMember.builder()
                .room(targetRoom)
                .user(currentUser)
                .role(RoomRole.MEMBER)
                .build();

        roomMemberRepository.save(newMember);

        return mapToRoomResponse(targetRoom);
    }

    @Override
    @Transactional
    public void leaveCurrentRoom() {
        User currentUser = SecurityUtils.getCurrentUser();

        RoomMember member = roomMemberRepository.findRoomByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_IN_ROOM));

        leaveRoomInternal(member);
    }

    @Override
    @Transactional
    public RoomResponse getCurrentRoom() {
        User currentUser = SecurityUtils.getCurrentUser();

        return roomMemberRepository.findRoomByUserId(currentUser.getId())
                .map(member -> mapToRoomResponse(member.getRoom()))
                .orElse(null);

    }

    // Hàm rút người dùng ra khỏi phòng và kích hoạt cơ chế tự hủy nếu phòng hết
    // người

    private void leaveRoomInternal(RoomMember member) {
        Room room = member.getRoom();
        UUID roomId = room.getId();

        // 1. Xóa thành viên khỏi phòng
        roomMemberRepository.delete(member);
        roomMemberRepository.flush();

        // 2. Cơ chế tự hủy: Nếu phòng không còn ai, xóa luôn phòng khỏi hệ thống
        long remainMember = roomMemberRepository.countUserInRoom(roomId);
        if (remainMember == 0) {
            roomRepository.delete(room);
        }
    }

    private void leaveRoomIfPresent(User user) {
        roomMemberRepository.findRoomByUserId(user.getId())
                .ifPresent(this::leaveRoomInternal);
    }

    private RoomResponse mapToRoomResponse(Room room) {
        List<RoomMember> members = roomMemberRepository.findMembersByRoomId(room.getId());

        List<MemberResponse> memberResponses = members.stream()
                .map(m -> MemberResponse.builder()
                        .userId(m.getUser().getId())
                        .fullName(m.getUser().getFullName())
                        .avatarUrl(m.getUser().getAvatarUrl())
                        .role(m.getRole())
                        .build())
                .toList();

        CreatorDto creatorDto = CreatorDto.builder()
                .id(room.getCreator().getId().toString())
                .name(room.getCreator().getFullName())
                .avatar(room.getCreator().getAvatarUrl())
                .isVerified(false)
                .build();

        return RoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .language(room.getLanguage())
                .level(room.getLevel())
                .currentParticipants(memberResponses.size())
                .maxParticipants(room.getMaxParticipants())
                .creator(creatorDto)
                .members(memberResponses)
                .createdAt(room.getCreatedAt())
                .build();

    }

}

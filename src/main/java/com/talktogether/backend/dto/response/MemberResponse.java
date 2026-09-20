package com.talktogether.backend.dto.response;

import java.util.UUID;

import com.talktogether.backend.entity.enums.RoomRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private UUID userId;
    private String fullName;
    private String avatarUrl;
    private RoomRole role;
}

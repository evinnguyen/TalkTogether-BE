package com.talktogether.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.talktogether.backend.entity.enums.Language;
import com.talktogether.backend.entity.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class RoomResponse {
    private UUID id;
    private String title;
    private Language language;
    private Level level;
    private int currentParticipants; 
    private int maxParticipants;     
    private CreatorDto creator;       
    private List<MemberResponse> members; 
    private LocalDateTime createdAt;
}

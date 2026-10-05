package com.talktogether.backend.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatorDto {
    private UUID id;
    private String name;
    private String avatar;
    private boolean isVerified;
}

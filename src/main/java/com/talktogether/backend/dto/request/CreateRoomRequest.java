package com.talktogether.backend.dto.request;

import java.util.logging.Level;

import com.talktogether.backend.entity.enums.Language;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class CreateRoomRequest {
    @NotBlank(message = "Tiêu đề phòng không được để trống")
    private String title;

     @NotNull (message = "Ngôn ngữ không được để trống")
    private Language language;

    @NotNull(message = "Trình độ không được để trống")
    private Level level;
    
    @Min(value = 2, message = "Số lượng người tối thiểu là 2")
    @Max(value = 10, message = "Số lượng người tối đa là 10")
    @Builder.Default
    private int maxParticipants = 4;
}

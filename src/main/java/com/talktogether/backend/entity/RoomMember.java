package com.talktogether.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.management.relation.Role;

import com.talktogether.backend.entity.enums.RoomRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_members", uniqueConstraints = {
    // Đảm bảo 1 user không thể tham gia 2 lần vào cùng 1 room
    @UniqueConstraint(columnNames = {"room_id", "user_id"})
})
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class RoomMember {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoomRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;
    
    @PrePersist
    protected void onCreate(){
        this.joinedAt = LocalDateTime.now();
        this.lastReadAt = LocalDateTime.now();
    }
    
    
}

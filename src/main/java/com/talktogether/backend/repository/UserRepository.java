package com.talktogether.backend.repository;

import com.talktogether.backend.entity.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

        Optional<User> findByEmail(String email);

        Boolean existsByEmail(String email);

        @Query("SELECT u FROM User u WHERE (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
                        "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND u.id != :currentUserId")
        List<User> searchUsers(@Param("keyword") String keyword,
                        @Param("currentUserId") UUID currentUserId,
                        Pageable pageable);

}

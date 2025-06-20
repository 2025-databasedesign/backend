package com.example.moviebook.repository;

import com.example.moviebook.entity.UserEntity;
import com.example.moviebook.util.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    Optional<UserEntity> findByNameAndPhone(String name, String phone);

    Optional<UserEntity> findByEmailAndNameAndPhone(String email, String name, String phone);

    List<UserEntity> findAllByStatus(UserStatus status);
}

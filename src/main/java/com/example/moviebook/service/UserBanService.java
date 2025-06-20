package com.example.moviebook.service;

import com.example.moviebook.entity.UserEntity;
import com.example.moviebook.util.UserStatus;
import com.example.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBanService {

    private final UserRepository userRepository;

    // 사용자 밴
    public void banUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    // 밴 해제
    public void unbanUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    // 밴된 사용자 목록 조회
    public List<UserEntity> getBannedUsers() {
        return userRepository.findAllByStatus(UserStatus.BANNED);
    }
}
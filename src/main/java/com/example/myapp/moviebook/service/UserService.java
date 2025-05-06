package com.example.moviebook.service;

import com.example.moviebook.dto.UserDto;
import com.example.moviebook.entity.UserEntity;
import com.example.moviebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입
    public UserDto registerUser(UserDto userDto, String rawPassword) {
        // 이메일 형식 검증
        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
            throw new IllegalArgumentException("올바르지 않은 이메일 형식입니다.");
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 길이 확인
        if (rawPassword.length() < 8 || rawPassword.length() > 15) {
            throw new IllegalArgumentException("비밀번호는 8자에서 15자 사이여야 합니다.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));

        UserEntity savedUser = userRepository.save(user);
        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                null);
    }

    // 로그인
    public Map<String, Object> authenticateUser(String email, String password) {

        // 1. 이메일 형식 검증
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }

        // 2. 비밀번호 길이 확인
        if (password.length() < 8 || password.length() > 15) {
            throw new IllegalArgumentException("비밀번호는 8자에서 15자 사이여야 합니다.");
        }

        // 3. 이메일 또는 비밀번호가 일치하지 않는 경우
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 응답에 포함할 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("exprTime", "3600"); // 1시간
        response.put("name", user.getUsername());

        return response;
    }
}

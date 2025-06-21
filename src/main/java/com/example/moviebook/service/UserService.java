package com.example.moviebook.service;

import com.example.moviebook.dto.UserDto;
import com.example.moviebook.entity.UserEntity;
import com.example.moviebook.repository.UserRepository;
import com.example.moviebook.security.JwtTokenProvider;
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

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setGender(userDto.getGender());
        user.setBirthDate(userDto.getBirthDate());
        user.setPhone(userDto.getPhone());

        UserEntity savedUser = userRepository.save(user);
        return new UserDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPassword(),
                savedUser.getGender(),
                savedUser.getBirthDate(),
                savedUser.getPhone(),
                0
        );
    }

    // 로그인
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

        // 4. JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getId());

        // 응답에 포함할 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("exprTime", "3600"); // 1시간
        response.put("name", user.getName());

        return response;
    }

    // 이메일로 사용자 정보 조회
    public UserDto getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null,
                user.getGender(),
                user.getBirthDate(),
                user.getPhone(),
                user.getMoney()
        );
    }

    // 이메일로 사용자 정보 업데이트
    public UserDto updateUserByEmail(String email, UserDto userDto) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            if (userDto.getPassword().length() < 8 || userDto.getPassword().length() > 15) {
                throw new IllegalArgumentException("비밀번호는 8자에서 15자 사이여야 합니다.");
            }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setName(userDto.getName());
        user.setBirthDate(userDto.getBirthDate());
        user.setGender(userDto.getGender());
        user.setPhone(userDto.getPhone());

        UserEntity updatedUser = userRepository.save(user);
        return new UserDto(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                null,
                updatedUser.getGender(),
                updatedUser.getBirthDate(),
                updatedUser.getPhone(),
                updatedUser.getMoney()
        );
    }

    // 아이디(이메일) 찾기 메서드
    public String findEmail(String name, String phone) {
        Optional<UserEntity> user;
        user = userRepository.findByNameAndPhone(name, phone);
        return user.orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보를 찾을 수 없습니다.")).getEmail();
    }

    // 비밀번호 재설정 메서드
    public void resetPassword(String email, String name, String phone, String newPassword) {
        // 유효성 검사
        if (newPassword.length() < 8 || newPassword.length() > 15) {
            throw new IllegalArgumentException("비밀번호는 8자에서 15자 사이여야 합니다.");
        }
        Optional<UserEntity> user;

        user = userRepository.findByEmailAndNameAndPhone(email, name, phone);

        UserEntity userEntity = user.orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보를 찾을 수 없습니다."));
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    //계정삭제
    public void deleteUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    // 돈 충전
    public void chargeMoney(String email, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setMoney(user.getMoney() + amount);
        userRepository.save(user);
    }
}

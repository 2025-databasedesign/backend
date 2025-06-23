package com.example.moviebook.controller;

import com.example.moviebook.dto.UserDto;
import com.example.moviebook.util.ApiResponse;
import com.example.moviebook.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import com.example.moviebook.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisTemplate redisTemplate;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@RequestBody UserDto userDto) {
        UserDto registeredUser = userService.registerUser(userDto, userDto.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "회원가입이 완료되었습니다.", registeredUser));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> loginUser(@RequestBody UserDto userDto) {
        try {
            // 로그인 성공 시 토큰과 사용자 정보 반환
            var loginResponse = userService.authenticateUser(userDto.getEmail(), userDto.getPassword());
            return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", loginResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            long remainingTime = jwtTokenProvider.getRemainingTime(token); // 남은 유효시간 계산
            redisTemplate.opsForValue().set(token, "logout", remainingTime, TimeUnit.MILLISECONDS);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "로그아웃 되었습니다.", null));
    }

    // 사용자 정보 조회 (이메일로)
    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@PathVariable String email) {
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "사용자 조회 성공", user));
    }

    // 사용자 정보 조회 (id로)
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<String>> getUserEmailById(@PathVariable Long id) {
        try {
            String email = userService.getUserEmailById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "이메일 조회 성공", email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // 사용자 정보 업데이트 (이메일로)
    @PutMapping("/{email}")
    public ResponseEntity<ApiResponse<UserDto>> updateUserByEmail(@PathVariable String email, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUserByEmail(email, userDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "사용자 정보 업데이트 성공", updatedUser));
    }

    // 아이디 찾기
    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<String>> findEmail(
            @RequestParam String name,
            @RequestParam String phone) {

        String email = userService.findEmail(name, phone);
        return ResponseEntity.ok(new ApiResponse<>(true, "이메일 찾기 성공", email));
    }

    // 비밀번호 찾기 (재설정 링크 전송 혹은 즉시 재설정 처리)
    @PostMapping("/find-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String newPassword) {

        userService.resetPassword(email, name, phone, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "비밀번호 재설정 성공", null));
    }

    // 계정삭제
    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> deleteUserByEmail(@PathVariable String email) {
        try {
            userService.deleteUserByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "사용자 삭제 성공", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    //금액 충전
    @PostMapping("/{email}/charge")
    public ResponseEntity<ApiResponse<Void>> chargeUserMoney(
            @PathVariable String email,
            @RequestParam int amount
    ) {
        try {
            userService.chargeMoney(email, amount);
            return ResponseEntity.ok(new ApiResponse<>(true, "충전이 완료되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

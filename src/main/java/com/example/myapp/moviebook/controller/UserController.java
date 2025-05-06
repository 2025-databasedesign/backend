package com.example.moviebook.controller;

import com.example.moviebook.dto.UserDto;
import com.example.moviebook.util.ApiResponse;
import com.example.moviebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    //회원가입
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
}

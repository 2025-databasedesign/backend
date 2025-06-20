package com.example.moviebook.controller;

import com.example.moviebook.entity.UserEntity;
import com.example.moviebook.service.UserBanService;
import com.example.moviebook.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserBanController {

    @Autowired
    private UserBanService userBanService;

    @Operation(summary = "사용자 밴")
    @PostMapping("/{userId}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable Long userId) {
        userBanService.banUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "사용자를 밴했습니다.", null));
    }

    @Operation(summary = "사용자 밴 해제")
    @PostMapping("/{userId}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable Long userId) {
        userBanService.unbanUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "사용자 밴을 해제했습니다.", null));
    }

    @Operation(summary = "밴된 사용자 목록 조회")
    @GetMapping("/banned")
    public ResponseEntity<ApiResponse<List<UserEntity>>> getBannedUsers() {
        List<UserEntity> bannedUsers = userBanService.getBannedUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "밴된 사용자 목록 조회 성공", bannedUsers));
    }
}

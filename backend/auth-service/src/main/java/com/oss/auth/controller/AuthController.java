package com.oss.auth.controller;

import com.oss.auth.dto.LoginRequest;
import com.oss.auth.dto.RegisterRequest;
import com.oss.auth.dto.TokenResponse;
import com.oss.auth.entity.User;
import com.oss.auth.repository.UserRepository;
import com.oss.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "인증", description = "회원가입, 로그인, JWT 토큰 관리")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입 후 토큰 반환")
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인 후 accessToken + refreshToken 반환")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @Operation(summary = "토큰 갱신", description = "refreshToken으로 새 accessToken 발급")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
    }

    @Operation(summary = "로그아웃", description = "Redis에 저장된 refreshToken 삭제")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-User-Id") Long userId) {
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 정보 조회", description = "게이트웨이가 주입한 X-User-Id, X-User-Role 반환")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
        @RequestHeader("X-User-Id") String userId,
        @RequestHeader("X-User-Role") String role
    ) {
        return ResponseEntity.ok(Map.of("userId", userId, "role", role));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(Map.of("id", user.getId(), "nickname", user.getNickname()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(
            userRepository.findAll().stream()
                .map(u -> Map.<String, Object>of(
                    "id", u.getId(),
                    "email", u.getEmail(),
                    "nickname", u.getNickname(),
                    "role", u.getRole().name(),
                    "banned", u.isBanned(),
                    "createdAt", u.getCreatedAt().toString()
                ))
                .collect(Collectors.toList())
        );
    }

    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<Void> toggleBan(@PathVariable Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setBanned(!user.isBanned());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}

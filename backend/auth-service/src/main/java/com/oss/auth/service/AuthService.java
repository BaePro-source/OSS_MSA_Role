package com.oss.auth.service;

import com.oss.auth.dto.LoginRequest;
import com.oss.auth.dto.RegisterRequest;
import com.oss.auth.dto.TokenResponse;
import com.oss.auth.entity.User;
import com.oss.auth.repository.UserRepository;
import com.oss.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_PREFIX = "refresh:";
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    public TokenResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = User.builder()
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .nickname(req.getNickname())
            .role(User.Role.USER)
            .latitude(req.getLatitude())
            .longitude(req.getLongitude())
            .build();
        userRepository.save(user);
        return issueTokens(user);
    }

    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (user.isBanned()) {
            throw new IllegalStateException("정지된 계정입니다.");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return issueTokens(user);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtUtil.isValid(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
        Long userId = jwtUtil.getUserId(refreshToken);
        String stored = redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);
        if (!refreshToken.equals(stored)) {
            throw new IllegalArgumentException("만료되거나 재사용된 Refresh Token입니다.");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return issueTokens(user);
    }

    public void logout(Long userId) {
        redisTemplate.delete(REFRESH_PREFIX + userId);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        redisTemplate.opsForValue().set(REFRESH_PREFIX + user.getId(), refreshToken, REFRESH_TTL);
        return new TokenResponse(accessToken, refreshToken, user.getRole().name(), user.getId(), user.getNickname());
    }
}

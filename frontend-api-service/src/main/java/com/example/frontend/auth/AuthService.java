package com.example.frontend.auth;

import com.example.frontend.auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("username already exists");
        }
        User u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        String role = req.role() != null ? req.role() : "ROLE_USER";
        u.setRoles(Set.of(role));
        u.setCreatedAt(Instant.now());
        userRepository.save(u);
    }

    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        var roles = user.getRoles().stream().toList();
        String access = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), roles);
        String refresh = jwtUtil.generateRefreshToken(user.getId());

        // persist refresh token
        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setToken(refresh);
        rt.setExpiresAt(Instant.now().plusSeconds(jwtUtil.getRefreshValiditySeconds()));
        rt.setCreatedAt(Instant.now());
        refreshTokenRepository.save(rt);

        return new TokenResponse(access, refresh, "Bearer", jwtUtil.getAccessValiditySeconds());
    }

    public TokenResponse refresh(RefreshRequest req) {
        String token = req.refreshToken();
        var opt = refreshTokenRepository.findByToken(token);
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid refresh token");
        RefreshToken rt = opt.get();
        if (rt.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(rt);
            throw new IllegalArgumentException("Refresh token expired");
        }
        User user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var roles = user.getRoles().stream().toList();
        String access = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), roles);
        String refresh = jwtUtil.generateRefreshToken(user.getId());

        // rotate refresh token
        rt.setToken(refresh);
        rt.setExpiresAt(Instant.now().plusSeconds(jwtUtil.getRefreshValiditySeconds()));
        refreshTokenRepository.save(rt);

        return new TokenResponse(access, refresh, "Bearer", jwtUtil.getAccessValiditySeconds());
    }
}

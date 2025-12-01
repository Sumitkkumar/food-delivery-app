package com.example.frontend.auth;

import com.example.frontend.auth.dto.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${google.clientId}")
    private String googleClientId;

    // verify id token, create user if not exist, issue tokens
    public TokenResponse signInWithGoogle(String idTokenString) {
        try {
            var verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Invalid ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleSub = payload.getSubject(); // unique Google user id
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // find or create user
            Optional<User> opt = userRepository.findByUsername(email); // use email as username
            User user;
            if (opt.isPresent()) {
                user = opt.get();
            } else {
                user = new User();
                user.setUsername(email);
                user.setEmail(email);
                user.setPasswordHash(null); // no password for oauth users
                user.setRoles(Set.of("ROLE_USER"));
                user.setCreatedAt(Instant.now());
                userRepository.save(user);
            }

            // issue our tokens
            List<String> roles = new ArrayList<>(user.getRoles());
            String access = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), roles);
            String refresh = jwtUtil.generateRefreshToken(user.getId());

            // persist refresh token
            RefreshToken rt = new RefreshToken();
            rt.setToken(refresh);
            rt.setUserId(user.getId());
            rt.setExpiresAt(Instant.now().plusSeconds(jwtUtil.getRefreshValiditySeconds()));
            rt.setCreatedAt(Instant.now());
            refreshTokenRepository.save(rt);

            return new TokenResponse(access, refresh, "Bearer", jwtUtil.getAccessValiditySeconds());

        } catch (Exception ex) {
            throw new IllegalArgumentException("Google token verification failed: " + ex.getMessage());
        }
    }
}

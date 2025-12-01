package com.example.frontend.auth;

import com.example.frontend.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/oauth")
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final OAuthService oauthService;

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> googleSignIn(@RequestBody Map<String,String> body) {
        String idToken = body.get("idToken");
        TokenResponse resp = oauthService.signInWithGoogle(idToken);
        return ResponseEntity.ok(resp);
    }
}

package com.Dolkara.auth_service.controller;

import com.Dolkara.auth_service.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;

    @Autowired
    public OAuth2Controller(OAuth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
    }

    @GetMapping("/providers")
    public Map<String, String> getAvailableProviders() {
        Map<String, String> providers = new HashMap<>();
        providers.put("google", "/auth/oauth2/google/auth-url");
        providers.put("github", "/auth/oauth2/github/auth-url");
        return providers;
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Map<String, String>> handleGoogleCallback(@RequestParam("code") String code, @RequestParam(name ="state", defaultValue = "") String state) {
        return oauth2Service.handleCallback(code, "google");
    }

    @GetMapping("/github/callback")
    public ResponseEntity<Map<String, String>> handleGithubCallback(@RequestParam("code") String code, @RequestParam(name ="state", defaultValue = "") String state) {
        return oauth2Service.handleCallback(code, "github");
    }

    @GetMapping("/google/auth-url")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl() {
        return oauth2Service.getAuthorizationUrl("google");
    }

    @GetMapping("/github/auth-url")
    public ResponseEntity<Map<String, String>> getGithubAuthUrl() {
        return oauth2Service.getAuthorizationUrl("github");
    }
}

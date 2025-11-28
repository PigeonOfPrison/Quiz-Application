package com.Dolkara.auth_service.controller;

import com.Dolkara.auth_service.entity.Credentials;
import com.Dolkara.auth_service.entity.Users;
import com.Dolkara.auth_service.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class RegistrationController {

    private final RegistrationService regService;

    @Autowired
    public RegistrationController(RegistrationService regService) {
        this.regService = regService;
    }

    @PostMapping("register")
    public Users register(@RequestBody Credentials cred) {
        return regService.register(cred);
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Credentials cred) {
        return regService.verify(cred);
    }

    @PostMapping("userdata")
    public ResponseEntity<Map<String, String>> userdata(HttpServletRequest req) {
        String token = req.getHeader("Authorization");

        if(token != null && token.startsWith("Bearer ")) token = token.substring(7);
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Bearer Token not Provided with Correct Header"));


        return regService.getUserData(token);
    }
}

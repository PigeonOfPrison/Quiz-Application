package com.Dolkara.auth_service.controller;

import com.Dolkara.auth_service.entity.Role;
import com.Dolkara.auth_service.entity.Users;
import com.Dolkara.auth_service.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final RegistrationService regService;

    @Autowired
    public AuthController(RegistrationService regService) {
        this.regService = regService;
    }

    @GetMapping("csrf-token")
    public CsrfToken csrfToken(HttpServletRequest req) {
        return (CsrfToken) req.getAttribute("_csrf");
    }

    @GetMapping("home")
    public String home() {
        return "You Are authenticated right now";
    }

    @GetMapping("test/users")
    public List<Users> getAllUsers() {
        List<Users> users = regService.getAllUsers();
        return users;
    }

    @GetMapping("test/roles")
    public List<Role> getAllRoles() {
        List<Role> roles = regService.getAllRoles();
        return roles;
    }
}

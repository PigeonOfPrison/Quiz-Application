package com.Dolkara.auth_service.service;

import com.Dolkara.auth_service.dao.RoleRepo;
import com.Dolkara.auth_service.dao.UserRepo;
import com.Dolkara.auth_service.entity.Credentials;
import com.Dolkara.auth_service.entity.Role;
import com.Dolkara.auth_service.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RegistrationService {

    private final UserRepo userRepo;
    private final PasswordEncoder encoder;
    private final RoleRepo roleRepo;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Autowired
    public RegistrationService(UserRepo userRepo, RoleRepo roleRepo, JwtService jwtService, AuthenticationManager authManager, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.roleRepo = roleRepo;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public Users register(@RequestBody Credentials cred) {

        Role role = roleRepo.findByRoleName("ROLE_USER");
        if(role == null) {
            throw new RuntimeException("Default role 'ROLE_USER' not found");
        }

        Users user = new Users(cred.getUsername(), encoder.encode(cred.getPassword()), cred.getEmail(), Set.of(role));
        user.setEnabled(true);
        //Users user = new Users(cred.getUsername(), cred.getPassword(), cred.getEmail());
        return userRepo.save(user);
    }

    public List<Users> getAllUsers() {
        return userRepo.findAll();
    }

    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    public Users getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public ResponseEntity<Map<String, String>> verify(Credentials cred) {
        Authentication auth  = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(cred.getUsername(), cred.getPassword()));

        return auth.isAuthenticated() ?
                ResponseEntity.ok(Map.of("Bearer", jwtService.generateToken(cred.getUsername())))  :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        //return jwtService.generateToken(cred.getUsername());
    }


    public ResponseEntity<Map<String, String>> getUserData(String token) {
        
        Map<String, String> userData = new HashMap<>();
        
        try {
            userData.put("name", jwtService.extractUserName(token));
            userData.put("email", jwtService.extractEmail(token));
            userData.put("roles", jwtService.extractRoles(token).toString());
        }
        catch (Exception e) {
            System.out.println("error while extracting user data in registrationService : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userData);
        }
        return ResponseEntity.ok(userData);
    }
}

package com.attendance.controller;

import com.attendance.model.Role;
import com.attendance.model.User;
import com.attendance.repository.UserRepository;
import com.attendance.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/success")
    public ResponseEntity<?> handleGoogleLoginSuccess(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Autentificarea cu Google a eșuat!");
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setNume(name);
            user.setRole(Role.STUDENT);
            userRepository.save(user);
        }

        String jwtToken = jwtUtils.generateToken(email);

        return ResponseEntity.ok("Autentificare reusită! Token JWT: " + jwtToken);
    }

    @GetMapping("/role")
    public ResponseEntity<?> getCurrentUserRole(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Neautentificat");
        }

        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && userOpt.get().getRole() != null) {
            return ResponseEntity.ok(java.util.Map.of("role", userOpt.get().getRole().name()));
        }

        return ResponseEntity.ok(java.util.Map.of("role", "STUDENT"));
    }


}

package com.attendance.controller;

import com.attendance.model.Role;
import com.attendance.model.User;
import com.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. Obține lista tuturor utilizatorilor
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. Schimbă rolul unui utilizator (ex: din STUDENT în TEACHER)
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            try {
                Role newRole = Role.valueOf(request.get("role"));
                user.setRole(newRole);
                userRepository.save(user);
                return ResponseEntity.ok("Rol actualizat cu succes!");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Rol invalid!");
            }
        }
        return ResponseEntity.status(404).body("Utilizatorul nu a fost găsit!");
    }

    // 3. Șterge un utilizator
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("Utilizator șters cu succes!");
        }
        return ResponseEntity.status(404).body("Utilizatorul nu a fost găsit!");
    }
}
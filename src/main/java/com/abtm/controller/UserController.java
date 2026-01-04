package com.abtm.controller;

import com.abtm.model.User;
import com.abtm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String fullName = request.get("fullName");
            String roleStr = request.get("role");

            if (username == null || password == null || email == null || roleStr == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // In production, hash the password!
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(User.Role.valueOf(roleStr.toUpperCase()));

            User savedUser = userService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("username", savedUser.getUsername());
            response.put("email", savedUser.getEmail());
            response.put("fullName", savedUser.getFullName());
            response.put("role", savedUser.getRole());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("Username and password required");
            }

            User user = userService.authenticate(username, password);

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("fullName", user.getFullName());
                response.put("role", user.getRole());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Login failed: " + e.getMessage());
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("fullName", user.getFullName());
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<?> getUserStatistics(@PathVariable Long id) {
        try {
            Map<String, Object> stats = userService.getUserStatistics(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Update failed: " + e.getMessage());
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok("User deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Delete failed: " + e.getMessage());
        }
    }
}

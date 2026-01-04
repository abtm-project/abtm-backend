package com.abtm.service;

import com.abtm.model.User;
import com.abtm.repository.ScenarioRepository;
import com.abtm.repository.UserPerformanceRepository;
import com.abtm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPerformanceRepository performanceRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        if (user != null) {
            if (updatedUser.getFullName() != null) {
                user.setFullName(updatedUser.getFullName());
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
                user.setEmail(updatedUser.getEmail());
            }
            return userRepository.save(user);
        }
        return null;
    }

    public Map<String, Object> getUserStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        User user = getUserById(userId);
        if (user == null) {
            return stats;
        }
        
        // Completed modules
        Long completedModules = performanceRepository.countCompletedModulesByUserId(userId);
        stats.put("completedModules", completedModules != null ? completedModules : 0);
        
        // Exercises attempted
        Long exercisesAttempted = performanceRepository.countExercisesByUserId(userId);
        stats.put("exercisesAttempted", exercisesAttempted != null ? exercisesAttempted : 0);
        
        // Average score
        Double averageScore = performanceRepository.getAverageScoreByUserId(userId);
        stats.put("averageScore", averageScore != null ? Math.round(averageScore * 10.0) / 10.0 : 0.0);
        
        // Scenarios submitted
        long scenariosSubmitted = scenarioRepository.countByUser(user);
        stats.put("scenariosSubmitted", scenariosSubmitted);
        
        return stats;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

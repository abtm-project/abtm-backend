package com.abtm.service;

import com.abtm.model.Exercise;
import com.abtm.model.Scenario;
import com.abtm.model.User;
import com.abtm.model.UserPerformance;
import com.abtm.repository.ExerciseRepository;
import com.abtm.repository.ScenarioRepository;
import com.abtm.repository.UserPerformanceRepository;
import com.abtm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScenarioService {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserPerformanceRepository performanceRepository;

    @Autowired
    private ScenarioAnalyzer scenarioAnalyzer;

    /**
     * Submit and analyze a scenario
     */
    public Scenario submitScenario(Long userId, Long exerciseId, String content) {
        // Get user and exercise
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Check for existing scenarios
        List<Scenario> existingScenarios = scenarioRepository.findByUserAndExercise(user, exercise);
        int submissionNumber = existingScenarios.size() + 1;

        // Analyze the scenario
        ScenarioAnalyzer.AnalysisResult analysisResult = scenarioAnalyzer.analyze(content);

        // Create scenario entity
        Scenario scenario = new Scenario();
        scenario.setUser(user);
        scenario.setExercise(exercise);
        scenario.setContent(content);
        scenario.setSubmissionNumber(submissionNumber);

        // Set dimension scores
        scenario.setClarityScore(analysisResult.getClarityScore());
        scenario.setBusinessValueScore(analysisResult.getBusinessValueScore());
        scenario.setGherkinScore(analysisResult.getGherkinScore());
        scenario.setTestabilityScore(analysisResult.getTestabilityScore());
        scenario.setSpecificityScore(analysisResult.getSpecificityScore());
        scenario.setDuplicationScore(analysisResult.getDuplicationScore());

        // Calculate overall SQS
        scenario.calculateOverallSqs();

        // Set feedback and status
        scenario.setFeedback(analysisResult.getFeedback());
        scenario.setDetectedAntipatterns(String.join("; ", analysisResult.getDetectedAntipatterns()));
        scenario.setIsAutomationReady(analysisResult.isAutomationReady());

        // Determine status based on overall score
        if (scenario.getOverallSqs() >= 8.0) {
            scenario.setStatus(Scenario.ScenarioStatus.PASSED);
        } else if (scenario.getOverallSqs() >= 6.0) {
            scenario.setStatus(Scenario.ScenarioStatus.NEEDS_IMPROVEMENT);
        } else {
            scenario.setStatus(Scenario.ScenarioStatus.FAILED);
        }

        // Save scenario
        Scenario savedScenario = scenarioRepository.save(scenario);

        // Update user performance
        updateUserPerformance(user, exercise, scenario.getOverallSqs());

        return savedScenario;
    }

    /**
     * Save scenario with analysis result (alternative method for controller)
     */
    public Scenario saveScenario(Long userId, Long exerciseId, String content, 
                                 ScenarioAnalyzer.AnalysisResult analysisResult) {
        // Get user and exercise
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Check for existing scenarios
        List<Scenario> existingScenarios = scenarioRepository.findByUserAndExercise(user, exercise);
        int submissionNumber = existingScenarios.size() + 1;

        // Create scenario entity
        Scenario scenario = new Scenario();
        scenario.setUser(user);
        scenario.setExercise(exercise);
        scenario.setContent(content);
        scenario.setSubmissionNumber(submissionNumber);

        // Set dimension scores
        scenario.setClarityScore(analysisResult.getClarityScore());
        scenario.setBusinessValueScore(analysisResult.getBusinessValueScore());
        scenario.setGherkinScore(analysisResult.getGherkinScore());
        scenario.setTestabilityScore(analysisResult.getTestabilityScore());
        scenario.setSpecificityScore(analysisResult.getSpecificityScore());
        scenario.setDuplicationScore(analysisResult.getDuplicationScore());

        // Calculate overall SQS
        scenario.calculateOverallSqs();

        // Set feedback and status
        scenario.setFeedback(analysisResult.getFeedback());
        scenario.setDetectedAntipatterns(String.join("; ", analysisResult.getDetectedAntipatterns()));
        scenario.setIsAutomationReady(analysisResult.isAutomationReady());

        // Determine status based on overall score
        if (scenario.getOverallSqs() >= 8.0) {
            scenario.setStatus(Scenario.ScenarioStatus.PASSED);
        } else if (scenario.getOverallSqs() >= 6.0) {
            scenario.setStatus(Scenario.ScenarioStatus.NEEDS_IMPROVEMENT);
        } else {
            scenario.setStatus(Scenario.ScenarioStatus.FAILED);
        }

        // Save scenario
        Scenario savedScenario = scenarioRepository.save(scenario);

        // Update user performance
        updateUserPerformance(user, exercise, scenario.getOverallSqs());

        return savedScenario;
    }

    /**
     * Get all scenarios for a user
     */
    public List<Scenario> getUserScenarios(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return scenarioRepository.findByUser(user);
    }

    /**
     * Get scenarios for a specific exercise
     */
    public List<Scenario> getExerciseScenarios(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercise not found"));
        return scenarioRepository.findByExercise(exercise);
    }

    /**
     * Get user's scenarios for a specific exercise
     */
    public List<Scenario> getUserExerciseScenarios(Long userId, Long exerciseId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercise not found"));
        return scenarioRepository.findByUserAndExercise(user, exercise);
    }

    /**
     * Get scenario by ID
     */
    public Scenario getScenarioById(Long id) {
        return scenarioRepository.findById(id).orElse(null);
    }

    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Total scenarios
        long totalScenarios = scenarioRepository.countByUser(user);
        stats.put("totalScenarios", totalScenarios);

        // Passed scenarios
        long passedScenarios = scenarioRepository.countByUserAndStatus(user, Scenario.ScenarioStatus.PASSED);
        stats.put("passedScenarios", passedScenarios);

        // Failed scenarios
        long failedScenarios = scenarioRepository.countByUserAndStatus(user, Scenario.ScenarioStatus.FAILED);
        stats.put("failedScenarios", failedScenarios);

        // Needs improvement
        long needsImprovement = scenarioRepository.countByUserAndStatus(user, Scenario.ScenarioStatus.NEEDS_IMPROVEMENT);
        stats.put("needsImprovement", needsImprovement);

        // Average score
        List<Scenario> allScenarios = scenarioRepository.findByUser(user);
        if (!allScenarios.isEmpty()) {
            double avgScore = allScenarios.stream()
                .filter(s -> s.getOverallSqs() != null)
                .mapToDouble(Scenario::getOverallSqs)
                .average()
                .orElse(0.0);
            stats.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
        } else {
            stats.put("averageScore", 0.0);
        }

        // Completed modules
        Long completedModules = performanceRepository.countCompletedModulesByUserId(userId);
        stats.put("completedModules", completedModules != null ? completedModules : 0);

        return stats;
    }

    /**
     * Reanalyze an existing scenario
     */
    public Scenario reanalyzeScenario(Long scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
            .orElseThrow(() -> new RuntimeException("Scenario not found"));

        // Analyze the scenario content
        ScenarioAnalyzer.AnalysisResult analysisResult = scenarioAnalyzer.analyze(scenario.getContent());

        // Update dimension scores
        scenario.setClarityScore(analysisResult.getClarityScore());
        scenario.setBusinessValueScore(analysisResult.getBusinessValueScore());
        scenario.setGherkinScore(analysisResult.getGherkinScore());
        scenario.setTestabilityScore(analysisResult.getTestabilityScore());
        scenario.setSpecificityScore(analysisResult.getSpecificityScore());
        scenario.setDuplicationScore(analysisResult.getDuplicationScore());

        // Recalculate overall SQS
        scenario.calculateOverallSqs();

        // Update feedback and status
        scenario.setFeedback(analysisResult.getFeedback());
        scenario.setDetectedAntipatterns(String.join("; ", analysisResult.getDetectedAntipatterns()));
        scenario.setIsAutomationReady(analysisResult.isAutomationReady());

        // Update status
        if (scenario.getOverallSqs() >= 8.0) {
            scenario.setStatus(Scenario.ScenarioStatus.PASSED);
        } else if (scenario.getOverallSqs() >= 6.0) {
            scenario.setStatus(Scenario.ScenarioStatus.NEEDS_IMPROVEMENT);
        } else {
            scenario.setStatus(Scenario.ScenarioStatus.FAILED);
        }

        return scenarioRepository.save(scenario);
    }

    /**
     * Delete scenario
     */
    public boolean deleteScenario(Long id) {
        if (scenarioRepository.existsById(id)) {
            scenarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Update user performance based on scenario submission
     */
    private void updateUserPerformance(User user, Exercise exercise, Double score) {
        List<UserPerformance> existingPerformance = performanceRepository.findByUserAndExercise(user, exercise);

        UserPerformance performance;
        if (existingPerformance.isEmpty()) {
            performance = new UserPerformance();
            performance.setUser(user);
            performance.setExercise(exercise);
            performance.setAttemptCount(1);
        } else {
            performance = existingPerformance.get(0);
            performance.setAttemptCount(performance.getAttemptCount() + 1);
        }

        // Update performance score (use highest score or average, depending on strategy)
        if (performance.getPerformanceScore() == null || score > performance.getPerformanceScore()) {
            performance.setPerformanceScore(score * 10); // Convert 0-10 to 0-100
        }

        performanceRepository.save(performance);
    }
}

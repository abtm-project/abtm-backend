package com.abtm.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ScenarioAnalyzer Service - Core quality analysis engine for BDD scenarios
 * 
 * Implements the 6-dimensional Scenario Quality Rubric:
 * 1. Clarity & Readability (20%)
 * 2. Business Value Alignment (20%)
 * 3. Gherkin Correctness (20%)
 * 4. Testability (20%)
 * 5. Specificity (10%)
 * 6. Duplication Avoidance (10%)
 */
@Service
public class ScenarioAnalyzer {
    
    // Anti-pattern keywords
    private static final Set<String> UI_KEYWORDS = new HashSet<>(Arrays.asList(
        "click", "button", "textbox", "dropdown", "checkbox", "radio",
        "menu", "link", "icon", "navigate", "scroll", "drag", "hover"
    ));
    
    private static final Set<String> VAGUE_TERMS = new HashSet<>(Arrays.asList(
        "properly", "correctly", "successfully", "appropriately", "adequately",
        "efficiently", "quickly", "slowly", "well", "badly"
    ));
    
    private static final Set<String> IMPLEMENTATION_KEYWORDS = new HashSet<>(Arrays.asList(
        "database", "api", "rest", "json", "xml", "sql", "http", "get", "post",
        "put", "delete", "endpoint", "service", "repository", "controller"
    ));
    
    public ScenarioAnalyzer() {
        // No initialization needed for regex-based parsing
    }
    
    /**
     * Analyze a BDD scenario and return quality scores
     */
    public AnalysisResult analyze(String scenarioContent) {
        AnalysisResult result = new AnalysisResult();
        
        try {
            if (scenarioContent == null || scenarioContent.trim().isEmpty()) {
                result.setParseError("Scenario content is empty");
                result.setAllScoresToZero();
                return result;
            }
            
            // Parse scenario using regex
            List<String> lines = Arrays.asList(scenarioContent.split("\\r?\\n"));
            List<String> steps = extractSteps(lines);
            
            if (steps.isEmpty()) {
                result.setParseError("No Given/When/Then steps found");
                result.setAllScoresToZero();
                return result;
            }
            
            // Calculate dimension scores
            result.setClarityScore(analyzeClarity(steps));
            result.setBusinessValueScore(analyzeBusinessValue(steps));
            result.setGherkinScore(analyzeGherkin(steps));
            result.setTestabilityScore(analyzeTestability(steps));
            result.setSpecificityScore(analyzeSpecificity(steps));
            result.setDuplicationScore(analyzeDuplication(steps));
            
            // Detect anti-patterns
            result.setDetectedAntipatterns(detectAntipatterns(steps));
            
            // Check automation readiness
            result.setAutomationReady(checkAutomationReadiness(result));
            
            // Generate feedback
            result.setFeedback(generateFeedback(result));
            
            // Calculate overall SQS
            result.calculateOverallSqs();
            
        } catch (Exception e) {
            result.setParseError("Error analyzing scenario: " + e.getMessage());
            result.setAllScoresToZero();
        }
        
        return result;
    }
    
    /**
     * Extract Given/When/Then steps from scenario lines
     */
    private List<String> extractSteps(List<String> lines) {
        List<String> steps = new ArrayList<>();
        Pattern stepPattern = Pattern.compile("^\\s*(Given|When|Then|And|But)\\s+(.+)$", Pattern.CASE_INSENSITIVE);
        
        for (String line : lines) {
            if (stepPattern.matcher(line).matches()) {
                steps.add(line.trim());
            }
        }
        
        return steps;
    }
    
    /**
     * Dimension 1: Clarity & Readability (0-5 scale)
     */
    private double analyzeClarity(List<String> steps) {
        double score = 5.0;
        
        // Check step clarity
        for (String step : steps) {
            String text = step.toLowerCase();
            
            // Penalize vague terms
            for (String vague : VAGUE_TERMS) {
                if (text.contains(vague)) {
                    score -= 0.2;
                }
            }
            
            // Penalize overly long steps (> 15 words)
            int wordCount = text.split("\\s+").length;
            if (wordCount > 15) {
                score -= 0.3;
            }
            
            // Penalize technical jargon
            if (text.contains("_") || text.matches(".*[a-z][A-Z].*")) {
                score -= 0.2;
            }
        }
        
        return Math.max(0.0, Math.min(5.0, score));
    }
    
    /**
     * Dimension 2: Business Value Alignment (0-5 scale)
     */
    private double analyzeBusinessValue(List<String> steps) {
        double score = 5.0;
        
        for (String step : steps) {
            String text = step.toLowerCase();
            
            // Penalize UI-specific steps
            for (String uiKeyword : UI_KEYWORDS) {
                if (text.contains(uiKeyword)) {
                    score -= 0.5;
                }
            }
            
            // Penalize implementation details
            for (String implKeyword : IMPLEMENTATION_KEYWORDS) {
                if (text.contains(implKeyword)) {
                    score -= 0.4;
                }
            }
        }
        
        // Check for user-focused language
        boolean hasUserFocus = steps.stream()
            .anyMatch(s -> s.toLowerCase().contains("user") ||
                          s.toLowerCase().contains("customer") ||
                          s.toLowerCase().contains(" i "));
        
        if (!hasUserFocus) {
            score -= 0.5;
        }
        
        return Math.max(0.0, Math.min(5.0, score));
    }
    
    /**
     * Dimension 3: Gherkin Correctness (0-5 scale)
     */
    private double analyzeGherkin(List<String> steps) {
        double score = 5.0;
        
        // Check for proper Given/When/Then structure
        boolean hasGiven = steps.stream().anyMatch(s -> s.trim().toLowerCase().startsWith("given"));
        boolean hasWhen = steps.stream().anyMatch(s -> s.trim().toLowerCase().startsWith("when"));
        boolean hasThen = steps.stream().anyMatch(s -> s.trim().toLowerCase().startsWith("then"));
        
        if (!hasGiven) score -= 1.5;
        if (!hasWhen) score -= 1.5;
        if (!hasThen) score -= 1.5;
        
        // Check for proper step format
        Pattern stepPattern = Pattern.compile("^(Given|When|Then|And|But)\\s+.+$", Pattern.CASE_INSENSITIVE);
        for (String step : steps) {
            if (!stepPattern.matcher(step.trim()).matches()) {
                score -= 0.5;
            }
        }
        
        return Math.max(0.0, Math.min(5.0, score));
    }
    
    /**
     * Dimension 4: Testability (0-5 scale)
     */
    private double analyzeTestability(List<String> steps) {
        double score = 5.0;
        
        // Check for Then steps (assertions)
        List<String> thenSteps = steps.stream()
            .filter(s -> s.trim().toLowerCase().startsWith("then"))
            .collect(Collectors.toList());
        
        if (thenSteps.isEmpty()) {
            score -= 2.0;
        }
        
        // Check for concrete expected outcomes
        boolean hasConcreteOutcome = thenSteps.stream()
            .anyMatch(s -> s.matches(".*\\d+.*") || // Contains numbers
                          s.toLowerCase().contains("should") ||
                          s.toLowerCase().contains("must") ||
                          s.toLowerCase().contains("is") ||
                          s.toLowerCase().contains("are"));
        
        if (!hasConcreteOutcome && !thenSteps.isEmpty()) {
            score -= 1.0;
        }
        
        // Penalize ambiguous assertions
        for (String then : thenSteps) {
            String lower = then.toLowerCase();
            if (lower.contains("something") || lower.contains("anything") || 
                lower.contains("some") || lower.contains("any")) {
                score -= 0.5;
            }
        }
        
        return Math.max(0.0, Math.min(5.0, score));
    }
    
    /**
     * Dimension 5: Specificity (0-5 scale)
     */
    private double analyzeSpecificity(List<String> steps) {
        double score = 5.0;
        
        for (String step : steps) {
            String lower = step.toLowerCase();
            
            // Penalize generic terms
            String[] genericTerms = {"something", "anything", "some", "any", "several", "few", "many"};
            for (String generic : genericTerms) {
                if (lower.contains(generic)) {
                    score -= 0.3;
                }
            }
            
            // Reward concrete values (numbers, specific strings)
            boolean hasConcrete = step.matches(".*\\d+.*") || // Has numbers
                                 step.matches(".*\"[^\"]+\".*") || // Has quoted strings
                                 step.matches(".*'[^']+'.*"); // Has single-quoted strings
            
            if (!hasConcrete) {
                score -= 0.2;
            }
        }
        
        return Math.max(0.0, Math.min(5.0, score));
    }
    
    /**
     * Dimension 6: Duplication Avoidance (0-5 scale)
     */
    private double analyzeDuplication(List<String> steps) {
        double score = 5.0;
        
        // Check for duplicate steps
        Set<String> uniqueSteps = new HashSet<>(steps);
        int duplicates = steps.size() - uniqueSteps.size();
        
        score -= (duplicates * 0.5);
        
        // Check for similar steps (high similarity)
        for (int i = 0; i < steps.size(); i++) {
            for (int j = i + 1; j < steps.size(); j++) {
                double similarity = calculateSimilarity(steps.get(i), steps.get(j));
                if (similarity > 0.8) {
                    score -= 0.3;
                }
            }
        }
        
        return Math.max(0.0, Math.min(5.0, score));
    }
    
    /**
     * Calculate similarity between two strings (0.0 to 1.0)
     */
    private double calculateSimilarity(String s1, String s2) {
        String longer = s1.length() > s2.length() ? s1 : s2;
        String shorter = s1.length() > s2.length() ? s2 : s1;
        
        if (longer.length() == 0) {
            return 1.0;
        }
        
        int distance = calculateLevenshteinDistance(shorter, longer);
        return (longer.length() - distance) / (double) longer.length();
    }
    
    /**
     * Calculate Levenshtein distance between two strings
     */
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Detect anti-patterns in steps
     */
    private List<String> detectAntipatterns(List<String> steps) {
        List<String> antipatterns = new ArrayList<>();
        
        for (String step : steps) {
            String lower = step.toLowerCase();
            
            // UI-centric anti-patterns
            for (String uiKeyword : UI_KEYWORDS) {
                if (lower.contains(uiKeyword)) {
                    antipatterns.add("UI-centric: " + step.trim());
                    break;
                }
            }
            
            // Implementation details
            for (String implKeyword : IMPLEMENTATION_KEYWORDS) {
                if (lower.contains(implKeyword)) {
                    antipatterns.add("Implementation detail: " + step.trim());
                    break;
                }
            }
            
            // Vague language
            for (String vague : VAGUE_TERMS) {
                if (lower.contains(vague)) {
                    antipatterns.add("Vague language: " + step.trim());
                    break;
                }
            }
            
            // Overly complex steps
            int wordCount = step.split("\\s+").length;
            if (wordCount > 15) {
                antipatterns.add("Too complex: " + step.trim());
            }
        }
        
        return antipatterns;
    }
    
    /**
     * Check if scenario is automation-ready
     */
    private boolean checkAutomationReadiness(AnalysisResult result) {
        // Automation ready if:
        // 1. Has proper Given/When/Then structure (Gherkin score >= 3.0)
        // 2. Steps are concrete and testable (Testability >= 3.0)
        // 3. No major anti-patterns (< 3 patterns detected)
        
        return result.getGherkinScore() >= 3.0 &&
               result.getTestabilityScore() >= 3.0 &&
               result.getDetectedAntipatterns().size() < 3;
    }
    
    /**
     * Generate feedback based on analysis
     */
    private String generateFeedback(AnalysisResult result) {
        StringBuilder feedback = new StringBuilder();
        
        // Overall assessment
        double overallSqs = result.calculateOverallSqs();
        if (overallSqs >= 4.5) {
            feedback.append("Excellent scenario! ");
        } else if (overallSqs >= 3.5) {
            feedback.append("Good scenario with room for improvement. ");
        } else if (overallSqs >= 2.5) {
            feedback.append("Fair scenario - needs significant improvements. ");
        } else {
            feedback.append("Poor scenario - major revisions needed. ");
        }
        
        // Specific dimension feedback
        if (result.getClarityScore() < 3.0) {
            feedback.append("Improve clarity by using simple, direct language. ");
        }
        
        if (result.getBusinessValueScore() < 3.0) {
            feedback.append("Focus on business value rather than UI or implementation details. ");
        }
        
        if (result.getGherkinScore() < 3.0) {
            feedback.append("Ensure proper Given-When-Then structure. ");
        }
        
        if (result.getTestabilityScore() < 3.0) {
            feedback.append("Add concrete expected outcomes in Then steps. ");
        }
        
        if (result.getSpecificityScore() < 3.0) {
            feedback.append("Use specific values instead of vague terms like 'some' or 'something'. ");
        }
        
        if (result.getDuplicationScore() < 4.0) {
            feedback.append("Remove duplicate or highly similar steps. ");
        }
        
        // Anti-pattern feedback
        if (!result.getDetectedAntipatterns().isEmpty()) {
            feedback.append(String.format("Found %d anti-patterns to address. ", 
                result.getDetectedAntipatterns().size()));
        }
        
        // Automation readiness
        if (result.isAutomationReady()) {
            feedback.append("✓ Scenario is automation-ready!");
        } else {
            feedback.append("✗ Scenario needs improvements before automation.");
        }
        
        return feedback.toString();
    }
    
    /**
     * Analysis Result inner class
     */
    public static class AnalysisResult {
        private double clarityScore;
        private double businessValueScore;
        private double gherkinScore;
        private double testabilityScore;
        private double specificityScore;
        private double duplicationScore;
        private double overallSqs;
        private List<String> detectedAntipatterns = new ArrayList<>();
        private boolean automationReady;
        private String feedback;
        private String parseError;
        
        // Weighted scoring (matching research paper Section 3.4)
        private static final double CLARITY_WEIGHT = 0.20;
        private static final double BUSINESS_VALUE_WEIGHT = 0.20;
        private static final double GHERKIN_WEIGHT = 0.20;
        private static final double TESTABILITY_WEIGHT = 0.20;
        private static final double SPECIFICITY_WEIGHT = 0.10;
        private static final double DUPLICATION_WEIGHT = 0.10;
        
        public double calculateOverallSqs() {
            this.overallSqs = (clarityScore * CLARITY_WEIGHT) +
                             (businessValueScore * BUSINESS_VALUE_WEIGHT) +
                             (gherkinScore * GHERKIN_WEIGHT) +
                             (testabilityScore * TESTABILITY_WEIGHT) +
                             (specificityScore * SPECIFICITY_WEIGHT) +
                             (duplicationScore * DUPLICATION_WEIGHT);
            return this.overallSqs;
        }
        
        public void setAllScoresToZero() {
            this.clarityScore = 0.0;
            this.businessValueScore = 0.0;
            this.gherkinScore = 0.0;
            this.testabilityScore = 0.0;
            this.specificityScore = 0.0;
            this.duplicationScore = 0.0;
            this.overallSqs = 0.0;
        }
        
        // Getters and Setters
        public double getClarityScore() { return clarityScore; }
        public void setClarityScore(double clarityScore) { this.clarityScore = clarityScore; }
        
        public double getBusinessValueScore() { return businessValueScore; }
        public void setBusinessValueScore(double businessValueScore) { this.businessValueScore = businessValueScore; }
        
        public double getGherkinScore() { return gherkinScore; }
        public void setGherkinScore(double gherkinScore) { this.gherkinScore = gherkinScore; }
        
        public double getTestabilityScore() { return testabilityScore; }
        public void setTestabilityScore(double testabilityScore) { this.testabilityScore = testabilityScore; }
        
        public double getSpecificityScore() { return specificityScore; }
        public void setSpecificityScore(double specificityScore) { this.specificityScore = specificityScore; }
        
        public double getDuplicationScore() { return duplicationScore; }
        public void setDuplicationScore(double duplicationScore) { this.duplicationScore = duplicationScore; }
        
        public double getOverallSqs() { return overallSqs; }
        public void setOverallSqs(double overallSqs) { this.overallSqs = overallSqs; }
        
        public List<String> getDetectedAntipatterns() { return detectedAntipatterns; }
        public void setDetectedAntipatterns(List<String> detectedAntipatterns) { this.detectedAntipatterns = detectedAntipatterns; }
        
        public boolean isAutomationReady() { return automationReady; }
        public void setAutomationReady(boolean automationReady) { this.automationReady = automationReady; }
        
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
        
        public String getParseError() { return parseError; }
        public void setParseError(String parseError) { this.parseError = parseError; }
    }
}

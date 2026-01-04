package com.abtm.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScenarioAnalyzer {

    /**
     * Analyze a BDD scenario and return scores for 6 dimensions
     */
    public AnalysisResult analyze(String scenarioContent) {
        AnalysisResult result = new AnalysisResult();
        
        if (scenarioContent == null || scenarioContent.trim().isEmpty()) {
            return result; // Return zeros
        }
        
        String content = scenarioContent.trim();
        
        // Analyze each dimension
        result.setClarityScore(analyzeClarityAndReadability(content));
        result.setBusinessValueScore(analyzeBusinessValueAlignment(content));
        result.setGherkinScore(analyzeGherkinCorrectness(content));
        result.setTestabilityScore(analyzeTestability(content));
        result.setSpecificityScore(analyzeSpecificity(content));
        result.setDuplicationScore(analyzeDuplicationAvoidance(content));
        
        // Calculate overall score
        result.calculateOverallScore();
        
        // Detect anti-patterns
        result.setDetectedAntipatterns(detectAntipatterns(content));
        
        // Generate feedback
        result.setFeedback(generateFeedback(result));
        
        // Check automation readiness
        result.setAutomationReady(isAutomationReady(result));
        
        return result;
    }
    
    /**
     * Analyze clarity and readability (0-10)
     */
    private double analyzeClarityAndReadability(String content) {
        double score = 5.0; // Base score
        
        // Check for Given/When/Then structure
        if (content.toLowerCase().contains("given") && 
            content.toLowerCase().contains("when") && 
            content.toLowerCase().contains("then")) {
            score += 2.0;
        }
        
        // Check line length (prefer concise lines)
        String[] lines = content.split("\n");
        int longLines = 0;
        for (String line : lines) {
            if (line.length() > 100) {
                longLines++;
            }
        }
        if (longLines == 0) {
            score += 1.5;
        } else if (longLines < 3) {
            score += 0.5;
        }
        
        // Check for clear step separation
        if (content.contains("\n")) {
            score += 1.0;
        }
        
        // Penalize if too short or too long
        if (content.length() < 50) {
            score -= 2.0;
        } else if (content.length() > 1000) {
            score -= 1.0;
        }
        
        return Math.max(0, Math.min(10, score));
    }
    
    /**
     * Analyze business value alignment (0-10)
     */
    private double analyzeBusinessValueAlignment(String content) {
        double score = 5.0;
        
        // Check for business domain terms
        String[] businessTerms = {"user", "customer", "system", "application", "service", "account", "order", "product"};
        for (String term : businessTerms) {
            if (content.toLowerCase().contains(term)) {
                score += 0.5;
                break;
            }
        }
        
        // Check for value-oriented language
        if (content.toLowerCase().contains("should") || content.toLowerCase().contains("must")) {
            score += 1.0;
        }
        
        // Check for concrete outcomes
        if (content.toLowerCase().contains("then")) {
            score += 1.5;
        }
        
        // Penalize technical jargon
        String[] technicalTerms = {"api", "database", "query", "function", "method", "class"};
        for (String term : technicalTerms) {
            if (content.toLowerCase().contains(term)) {
                score -= 0.5;
            }
        }
        
        return Math.max(0, Math.min(10, score));
    }
    
    /**
     * Analyze Gherkin correctness (0-10)
     */
    private double analyzeGherkinCorrectness(String content) {
        double score = 0.0;
        
        String lower = content.toLowerCase();
        
        // Check for Given
        if (lower.contains("given")) {
            score += 3.0;
        }
        
        // Check for When
        if (lower.contains("when")) {
            score += 3.0;
        }
        
        // Check for Then
        if (lower.contains("then")) {
            score += 3.0;
        }
        
        // Bonus for proper order (Given before When before Then)
        int givenIndex = lower.indexOf("given");
        int whenIndex = lower.indexOf("when");
        int thenIndex = lower.indexOf("then");
        
        if (givenIndex >= 0 && whenIndex >= 0 && thenIndex >= 0) {
            if (givenIndex < whenIndex && whenIndex < thenIndex) {
                score += 1.0;
            }
        }
        
        return Math.max(0, Math.min(10, score));
    }
    
    /**
     * Analyze testability (0-10)
     */
    private double analyzeTestability(String content) {
        double score = 5.0;
        
        // Check for concrete, testable assertions
        if (content.toLowerCase().contains("should")) {
            score += 1.5;
        }
        
        // Check for specific values
        Pattern numberPattern = Pattern.compile("\\d+");
        Matcher matcher = numberPattern.matcher(content);
        if (matcher.find()) {
            score += 1.5;
        }
        
        // Penalize vague terms
        String[] vagueTerms = {"some", "maybe", "might", "could", "possibly"};
        for (String term : vagueTerms) {
            if (content.toLowerCase().contains(term)) {
                score -= 1.0;
            }
        }
        
        // Check for action verbs in When clause
        if (content.toLowerCase().contains("when")) {
            String[] actionVerbs = {"click", "enter", "submit", "select", "choose", "create", "delete", "update"};
            for (String verb : actionVerbs) {
                if (content.toLowerCase().contains(verb)) {
                    score += 1.0;
                    break;
                }
            }
        }
        
        return Math.max(0, Math.min(10, score));
    }
    
    /**
     * Analyze specificity (0-10)
     */
    private double analyzeSpecificity(String content) {
        double score = 5.0;
        
        // Check for specific data
        if (content.matches(".*\\d+.*")) {
            score += 2.0;
        }
        
        // Check for quoted strings (specific values)
        if (content.contains("\"")) {
            score += 1.5;
        }
        
        // Penalize generic terms
        String[] genericTerms = {"something", "anything", "stuff", "things"};
        for (String term : genericTerms) {
            if (content.toLowerCase().contains(term)) {
                score -= 2.0;
            }
        }
        
        // Reward concrete examples
        if (content.contains("example") || content.contains("e.g.")) {
            score += 1.5;
        }
        
        return Math.max(0, Math.min(10, score));
    }
    
    /**
     * Analyze duplication avoidance (0-10)
     */
    private double analyzeDuplicationAvoidance(String content) {
        // This is a simple check - in real implementation would check against database
        double score = 7.0; // Base score assuming no duplication
        
        // Check for repetitive patterns within the scenario
        String[] lines = content.split("\n");
        Map<String, Integer> lineCount = new HashMap<>();
        
        for (String line : lines) {
            String trimmed = line.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                lineCount.put(trimmed, lineCount.getOrDefault(trimmed, 0) + 1);
            }
        }
        
        // Penalize repeated lines
        for (Integer count : lineCount.values()) {
            if (count > 1) {
                score -= 1.5;
            }
        }
        
        return Math.max(0, Math.min(10, score));
    }
    
    /**
     * Detect anti-patterns in the scenario
     */
    private List<String> detectAntipatterns(String content) {
        List<String> antipatterns = new ArrayList<>();
        String lower = content.toLowerCase();
        
        // Check for UI-specific steps
        String[] uiTerms = {"click button", "click on", "press button", "fill form"};
        for (String term : uiTerms) {
            if (lower.contains(term)) {
                antipatterns.add("UI-dependent steps detected - prefer behavior over implementation");
                break;
            }
        }
        
        // Check for technical implementation details
        String[] techTerms = {"database", "api call", "function", "method"};
        for (String term : techTerms) {
            if (lower.contains(term)) {
                antipatterns.add("Technical implementation details should be avoided");
                break;
            }
        }
        
        // Check for missing structure
        if (!lower.contains("given") || !lower.contains("when") || !lower.contains("then")) {
            antipatterns.add("Missing proper Given-When-Then structure");
        }
        
        // Check for vague assertions
        if (lower.contains("works") || lower.contains("is ok")) {
            antipatterns.add("Vague assertions - be more specific about expected behavior");
        }
        
        return antipatterns;
    }
    
    /**
     * Generate feedback based on analysis results
     */
    private String generateFeedback(AnalysisResult result) {
        StringBuilder feedback = new StringBuilder();
        
        // Overall assessment
        if (result.getOverallScore() >= 8.0) {
            feedback.append("Excellent scenario! Well-structured and testable.\n\n");
        } else if (result.getOverallScore() >= 6.0) {
            feedback.append("Good scenario with room for improvement.\n\n");
        } else if (result.getOverallScore() >= 4.0) {
            feedback.append("Scenario needs improvement in several areas.\n\n");
        } else {
            feedback.append("Scenario requires significant revision.\n\n");
        }
        
        // Specific feedback for low scores
        if (result.getClarityScore() < 6.0) {
            feedback.append("• Improve clarity: Use clear, concise language and proper formatting.\n");
        }
        if (result.getBusinessValueScore() < 6.0) {
            feedback.append("• Focus on business value: Describe user behavior, not technical implementation.\n");
        }
        if (result.getGherkinScore() < 6.0) {
            feedback.append("• Fix Gherkin structure: Ensure proper Given-When-Then format.\n");
        }
        if (result.getTestabilityScore() < 6.0) {
            feedback.append("• Improve testability: Use specific, measurable assertions.\n");
        }
        if (result.getSpecificityScore() < 6.0) {
            feedback.append("• Be more specific: Include concrete examples and data.\n");
        }
        
        // Anti-patterns feedback
        if (!result.getDetectedAntipatterns().isEmpty()) {
            feedback.append("\nDetected Issues:\n");
            for (String antipattern : result.getDetectedAntipatterns()) {
                feedback.append("• ").append(antipattern).append("\n");
            }
        }
        
        return feedback.toString();
    }
    
    /**
     * Check if scenario is automation-ready
     */
    private boolean isAutomationReady(AnalysisResult result) {
        return result.getGherkinScore() >= 7.0 && 
               result.getTestabilityScore() >= 7.0 &&
               result.getSpecificityScore() >= 6.0;
    }
    
    /**
     * Analysis result class
     */
    public static class AnalysisResult {
        private double clarityScore;
        private double businessValueScore;
        private double gherkinScore;
        private double testabilityScore;
        private double specificityScore;
        private double duplicationScore;
        private double overallScore;
        private String feedback;
        private List<String> detectedAntipatterns = new ArrayList<>();
        private boolean automationReady;
        
        public void calculateOverallScore() {
            double[] weights = {0.20, 0.20, 0.20, 0.15, 0.15, 0.10};
            double[] scores = {
                clarityScore, 
                businessValueScore, 
                gherkinScore, 
                testabilityScore, 
                specificityScore, 
                duplicationScore
            };
            
            double weightedSum = 0.0;
            for (int i = 0; i < weights.length; i++) {
                weightedSum += weights[i] * scores[i];
            }
            
            this.overallScore = Math.round(weightedSum * 100.0) / 100.0;
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
        
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
        
        public List<String> getDetectedAntipatterns() { return detectedAntipatterns; }
        public void setDetectedAntipatterns(List<String> detectedAntipatterns) { this.detectedAntipatterns = detectedAntipatterns; }
        
        public boolean isAutomationReady() { return automationReady; }
        public void setAutomationReady(boolean automationReady) { this.automationReady = automationReady; }
    }
}

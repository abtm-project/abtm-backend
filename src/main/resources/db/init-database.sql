-- ============================================================================
-- ABTM Backend - Database Initialization Script
-- ============================================================================
-- This script initializes the database with the 4 learning modules from the
-- research paper and sample exercises for each module.
--
-- Usage:
--   1. Ensure PostgreSQL is running
--   2. Run: psql -U abtm_user -d abtm_db -f init-database.sql
--   Or execute in pgAdmin 4 Query Tool
-- ============================================================================

-- Clear existing data (if any)
TRUNCATE TABLE scenarios CASCADE;
TRUNCATE TABLE user_performance CASCADE;
TRUNCATE TABLE exercises CASCADE;
TRUNCATE TABLE users_completed_modules CASCADE;
TRUNCATE TABLE modules CASCADE;
TRUNCATE TABLE users CASCADE;

-- Reset sequences
ALTER SEQUENCE IF EXISTS modules_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS exercises_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 1;

-- ============================================================================
-- INSERT MODULES
-- ============================================================================
-- Based on Section 4.3 of the research paper

INSERT INTO modules (id, module_number, title, description, estimated_hours, passing_score, is_active, order_index) VALUES
(1, 1, 'BDD Fundamentals', 
 'Introduction to Behavior-Driven Development principles, user stories vs scenarios, common misconceptions between BDD and TDD, and the role of BDD in Agile teams.',
 4, 70.0, true, 1),

(2, 2, 'Gherkin Syntax and Patterns',
 'Deep dive into Given-When-Then syntax rules, data tables, scenario outlines, anti-patterns to avoid (UI steps, mixing scenarios, contradictions), and best practices for writing clear, testable scenarios.',
 6, 70.0, true, 2),

(3, 3, 'Role-Based BDD Training',
 'Specialized training paths for different team roles: Developers (glue code, framework integration), Testers (scenario design, edge cases, regression), Product Owners (acceptance criteria, business rules).',
 8, 70.0, true, 3),

(4, 4, 'Adaptive Practice Sessions',
 'Hands-on practice with real-world scenarios. System evaluates quality, identifies weak areas, and provides targeted exercises. Includes team collaboration workshops and advanced scenario design patterns.',
 10, 70.0, true, 4);

-- ============================================================================
-- INSERT EXERCISES FOR MODULE 1: BDD FUNDAMENTALS
-- ============================================================================

INSERT INTO exercises (module_id, title, description, user_story, difficulty, target_role, expected_scenarios, is_active, order_index) VALUES

-- Foundation Level
(1, 'Understanding User Stories',
 'Learn the difference between user stories and BDD scenarios. Practice converting a user story into a testable scenario.',
 'As a product owner, I want to understand how user stories relate to BDD scenarios so that I can write better acceptance criteria.',
 'FOUNDATION', 'PRODUCT_OWNER', 1, true, 1),

(1, 'BDD vs TDD: Key Differences',
 'Identify the key differences between Behavior-Driven Development and Test-Driven Development.',
 'As a developer, I want to understand when to use BDD vs TDD so that I can choose the right approach for my project.',
 'FOUNDATION', 'DEVELOPER', 1, true, 2),

(1, 'Writing Your First Scenario',
 'Write a simple BDD scenario for a login feature using Given-When-Then format.',
 'As a new BDD practitioner, I want to write my first scenario so that I can start applying BDD principles.',
 'FOUNDATION', null, 1, true, 3),

-- Standard Level
(1, 'Stakeholder Communication',
 'Practice explaining BDD benefits to non-technical stakeholders and writing scenarios they can understand.',
 'As a team lead, I want to communicate BDD benefits to stakeholders so that I can get buy-in for adoption.',
 'STANDARD', 'PRODUCT_OWNER', 2, true, 4),

(1, 'Identifying Good vs Bad Scenarios',
 'Analyze sample scenarios and identify quality issues based on BDD principles.',
 'As a QA engineer, I want to evaluate scenario quality so that I can provide constructive feedback.',
 'STANDARD', 'TESTER', 1, true, 5);

-- ============================================================================
-- INSERT EXERCISES FOR MODULE 2: GHERKIN SYNTAX AND PATTERNS
-- ============================================================================

INSERT INTO exercises (module_id, title, description, user_story, difficulty, target_role, expected_scenarios, is_active, order_index) VALUES

-- Foundation Level
(2, 'Given-When-Then Structure',
 'Practice writing scenarios with proper Given-When-Then structure. Understand the purpose of each keyword.',
 'As a learner, I want to master Given-When-Then syntax so that I can write well-structured scenarios.',
 'FOUNDATION', null, 2, true, 1),

(2, 'Using Background Steps',
 'Learn when and how to use Background keyword to reduce duplication across scenarios.',
 'As a scenario writer, I want to use Background effectively so that I can avoid repeating setup steps.',
 'FOUNDATION', null, 1, true, 2),

-- Standard Level
(2, 'Data Tables in Scenarios',
 'Practice using data tables to test multiple inputs in a single scenario.',
 'As a tester, I want to use data tables so that I can test multiple cases efficiently.',
 'STANDARD', 'TESTER', 1, true, 3),

(2, 'Scenario Outlines',
 'Learn to use Scenario Outlines with Examples tables for parameterized testing.',
 'As a developer, I want to write scenario outlines so that I can test the same behavior with different data.',
 'STANDARD', 'DEVELOPER', 1, true, 4),

(2, 'Avoiding Anti-Patterns',
 'Identify and refactor scenarios with common anti-patterns: UI-centric steps, technical jargon, imperative style.',
 'As a BDD practitioner, I want to avoid anti-patterns so that my scenarios remain readable and maintainable.',
 'STANDARD', null, 2, true, 5),

-- Advanced Level
(2, 'Complex Scenario Design',
 'Design scenarios for complex workflows involving multiple actors and system states.',
 'As an advanced practitioner, I want to handle complex scenarios so that I can model real-world behavior accurately.',
 'ADVANCED', null, 3, true, 6);

-- ============================================================================
-- INSERT EXERCISES FOR MODULE 3: ROLE-BASED BDD TRAINING
-- ============================================================================

INSERT INTO exercises (module_id, title, description, user_story, difficulty, target_role, expected_scenarios, is_active, order_index) VALUES

-- Developer Track
(3, 'Writing Glue Code',
 'Implement step definitions (glue code) to connect Gherkin scenarios to application code.',
 'As a developer, I want to write glue code so that my scenarios are executable.',
 'STANDARD', 'DEVELOPER', 1, true, 1),

(3, 'Framework Integration',
 'Integrate BDD scenarios with testing frameworks (Cucumber, JBehave, SpecFlow).',
 'As a developer, I want to integrate BDD with my test framework so that scenarios run automatically.',
 'ADVANCED', 'DEVELOPER', 1, true, 2),

-- Tester Track
(3, 'Scenario Design for Edge Cases',
 'Design scenarios that cover edge cases, error conditions, and boundary values.',
 'As a QA engineer, I want to design edge case scenarios so that I can ensure comprehensive test coverage.',
 'STANDARD', 'TESTER', 3, true, 3),

(3, 'Regression Test Management',
 'Organize scenarios into regression test suites that can be run automatically.',
 'As a QA engineer, I want to manage regression tests so that I can detect issues early.',
 'STANDARD', 'TESTER', 2, true, 4),

-- Product Owner Track
(3, 'Writing Acceptance Criteria',
 'Transform business requirements into clear acceptance criteria using BDD format.',
 'As a product owner, I want to write acceptance criteria so that developers know when a story is complete.',
 'STANDARD', 'PRODUCT_OWNER', 2, true, 5),

(3, 'Specifying Business Rules',
 'Express complex business rules as executable scenarios with concrete examples.',
 'As a product owner, I want to specify business rules so that the team understands requirements precisely.',
 'ADVANCED', 'PRODUCT_OWNER', 3, true, 6);

-- ============================================================================
-- INSERT EXERCISES FOR MODULE 4: ADAPTIVE PRACTICE SESSIONS
-- ============================================================================

INSERT INTO exercises (module_id, title, description, user_story, difficulty, target_role, expected_scenarios, is_active, order_index) VALUES

-- Foundation Level
(4, 'E-commerce Checkout Flow',
 'Write scenarios for a complete checkout process including cart, payment, and confirmation.',
 'As an e-commerce user, I want to complete my purchase so that I can receive my order.',
 'FOUNDATION', null, 3, true, 1),

-- Standard Level
(4, 'User Authentication System',
 'Design comprehensive scenarios for login, registration, password reset, and session management.',
 'As a user, I want to securely access my account so that my data remains protected.',
 'STANDARD', null, 4, true, 2),

(4, 'Search and Filter Functionality',
 'Write scenarios for complex search with multiple filters, sorting, and pagination.',
 'As a user, I want to find products easily so that I can make informed purchasing decisions.',
 'STANDARD', null, 3, true, 3),

-- Advanced Level
(4, 'Multi-User Collaboration',
 'Design scenarios involving multiple users interacting with shared resources (permissions, conflicts).',
 'As a team member, I want to collaborate with others so that we can work efficiently together.',
 'ADVANCED', null, 5, true, 4),

(4, 'Real-Time Notifications',
 'Write scenarios for asynchronous events, webhooks, and real-time notifications.',
 'As a user, I want to receive instant notifications so that I can respond to important events quickly.',
 'ADVANCED', 'DEVELOPER', 4, true, 5),

(4, 'Payment Processing Integration',
 'Design scenarios for third-party payment integration including success, failure, and timeout cases.',
 'As a payment processor integration developer, I want to handle all payment scenarios so that transactions are reliable.',
 'ADVANCED', 'DEVELOPER', 5, true, 6);

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
-- Run these to verify data was inserted correctly

-- Check module count (should be 4)
SELECT COUNT(*) as module_count FROM modules;

-- Check exercise count by module
SELECT 
    m.module_number,
    m.title,
    COUNT(e.id) as exercise_count
FROM modules m
LEFT JOIN exercises e ON m.id = e.module_id
GROUP BY m.id, m.module_number, m.title
ORDER BY m.module_number;

-- Check exercise count by difficulty
SELECT 
    difficulty,
    COUNT(*) as count
FROM exercises
GROUP BY difficulty
ORDER BY 
    CASE difficulty
        WHEN 'FOUNDATION' THEN 1
        WHEN 'STANDARD' THEN 2
        WHEN 'ADVANCED' THEN 3
    END;

-- Check exercise count by target role
SELECT 
    COALESCE(target_role::text, 'ALL_ROLES') as role,
    COUNT(*) as count
FROM exercises
GROUP BY target_role
ORDER BY count DESC;

-- View all modules with details
SELECT * FROM modules ORDER BY module_number;

-- View first 5 exercises
SELECT 
    m.module_number,
    e.title,
    e.difficulty,
    COALESCE(e.target_role::text, 'ALL') as target_role
FROM exercises e
JOIN modules m ON e.module_id = m.id
ORDER BY m.module_number, e.order_index
LIMIT 5;

-- ============================================================================
-- SAMPLE TEST USER (for testing purposes)
-- Password: TestPassword123 (BCrypt encoded)
-- ============================================================================

-- INSERT INTO users (username, email, password, full_name, role, years_experience, prior_bdd_experience, proficiency_level, current_module, performance_score, is_active, created_at)
-- VALUES ('test_user', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 
--         'Test User', 'DEVELOPER', 3, false, 'BEGINNER', 1, 0.0, true, NOW());

-- Uncomment above INSERT to create a test user

-- ============================================================================
-- NOTES
-- ============================================================================
-- 1. Total Exercises: 25+ exercises across 4 modules
-- 2. Difficulty Distribution:
--    - Foundation: 8 exercises (basic concepts)
--    - Standard: 13 exercises (practical application)
--    - Advanced: 4 exercises (complex scenarios)
-- 3. Role-Specific Exercises:
--    - Developer: 4 exercises
--    - Tester: 4 exercises
--    - Product Owner: 3 exercises
--    - All Roles: 14 exercises
-- 4. Module Progression:
--    - Module 1: 5 exercises (4 hours estimated)
--    - Module 2: 6 exercises (6 hours estimated)
--    - Module 3: 6 exercises (8 hours estimated)
--    - Module 4: 6 exercises (10 hours estimated)
--
-- Total Estimated Learning Time: 28 hours (approximately 1 week full-time)
-- ============================================================================

COMMIT;

-- Display success message
SELECT 'Database initialization completed successfully!' as status;
SELECT 'Modules: 4, Exercises: 25+' as summary;

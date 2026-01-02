#!/bin/bash

# ABTM Backend Test Script
# This script tests the scenario analysis endpoint with various examples

BASE_URL="http://localhost:8080"

echo "═══════════════════════════════════════════════════════"
echo "   ABTM Backend Test Suite"
echo "═══════════════════════════════════════════════════════"
echo ""

# Test 1: Good Quality Scenario
echo "Test 1: High-Quality Scenario"
echo "----------------------------"
curl -s -X POST "$BASE_URL/api/scenarios/analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Feature: User Registration\n\nScenario: Successful user registration\n  Given a new user with email \"alice@example.com\" and password \"SecurePass123\"\n  When the user submits the registration form\n  Then the user receives a confirmation email\n  And the user account is created with status \"active\""
  }' | python3 -m json.tool

echo ""
echo ""

# Test 2: Poor Quality Scenario (UI-centric)
echo "Test 2: Poor Quality Scenario (UI-centric)"
echo "-------------------------------------------"
curl -s -X POST "$BASE_URL/api/scenarios/analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Feature: Login\n\nScenario: User login\n  When I click the login button\n  And I enter my username in the username textbox\n  And I enter my password in the password field\n  And I click submit button\n  Then I see the dashboard screen"
  }' | python3 -m json.tool

echo ""
echo ""

# Test 3: Vague Scenario
echo "Test 3: Vague Scenario (Missing specificity)"
echo "--------------------------------------------"
curl -s -X POST "$BASE_URL/api/scenarios/analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Feature: Shopping\n\nScenario: Add item\n  Given I am logged in\n  When I add an item\n  Then it should be added correctly"
  }' | python3 -m json.tool

echo ""
echo ""

# Test 4: Missing Given
echo "Test 4: Missing Given (Incomplete structure)"
echo "--------------------------------------------"
curl -s -X POST "$BASE_URL/api/scenarios/analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Feature: Payment\n\nScenario: Process payment\n  When the user pays with credit card\n  Then the payment is processed successfully"
  }' | python3 -m json.tool

echo ""
echo "═══════════════════════════════════════════════════════"
echo "   Tests Complete!"
echo "═══════════════════════════════════════════════════════"

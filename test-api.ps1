# ABTM Backend Test Script for Windows PowerShell
# This script tests the scenario analysis endpoint with various examples

$BaseUrl = "http://localhost:8080"

Write-Host "===============================================================" -ForegroundColor Cyan
Write-Host "   ABTM Backend Test Suite (PowerShell)" -ForegroundColor Cyan
Write-Host "===============================================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Good Quality Scenario
Write-Host "Test 1: High-Quality Scenario" -ForegroundColor Green
Write-Host "----------------------------" -ForegroundColor Green

$body1 = @{
    content = @"
Feature: User Registration

Scenario: Successful user registration
  Given a new user with email "alice@example.com" and password "SecurePass123"
  When the user submits the registration form
  Then the user receives a confirmation email
  And the user account is created with status "active"
"@
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "$BaseUrl/api/scenarios/analyze" `
        -Method Post `
        -Body $body1 `
        -ContentType "application/json"
    
    $response1 | ConvertTo-Json -Depth 10 | Write-Host
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host ""

# Test 2: Poor Quality Scenario (UI-centric)
Write-Host "Test 2: Poor Quality Scenario (UI-centric)" -ForegroundColor Yellow
Write-Host "-------------------------------------------" -ForegroundColor Yellow

$body2 = @{
    content = @"
Feature: Login

Scenario: User login
  When I click the login button
  And I enter my username in the username textbox
  And I enter my password in the password field
  And I click submit button
  Then I see the dashboard screen
"@
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "$BaseUrl/api/scenarios/analyze" `
        -Method Post `
        -Body $body2 `
        -ContentType "application/json"
    
    $response2 | ConvertTo-Json -Depth 10 | Write-Host
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host ""

# Test 3: Vague Scenario
Write-Host "Test 3: Vague Scenario (Missing specificity)" -ForegroundColor Yellow
Write-Host "--------------------------------------------" -ForegroundColor Yellow

$body3 = @{
    content = @"
Feature: Shopping

Scenario: Add item
  Given I am logged in
  When I add an item
  Then it should be added correctly
"@
} | ConvertTo-Json

try {
    $response3 = Invoke-RestMethod -Uri "$BaseUrl/api/scenarios/analyze" `
        -Method Post `
        -Body $body3 `
        -ContentType "application/json"
    
    $response3 | ConvertTo-Json -Depth 10 | Write-Host
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host ""

# Test 4: Missing Given
Write-Host "Test 4: Missing Given (Incomplete structure)" -ForegroundColor Yellow
Write-Host "--------------------------------------------" -ForegroundColor Yellow

$body4 = @{
    content = @"
Feature: Payment

Scenario: Process payment
  When the user pays with credit card
  Then the payment is processed successfully
"@
} | ConvertTo-Json

try {
    $response4 = Invoke-RestMethod -Uri "$BaseUrl/api/scenarios/analyze" `
        -Method Post `
        -Body $body4 `
        -ContentType "application/json"
    
    $response4 | ConvertTo-Json -Depth 10 | Write-Host
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "===============================================================" -ForegroundColor Cyan
Write-Host "   Tests Complete!" -ForegroundColor Cyan
Write-Host "===============================================================" -ForegroundColor Cyan
Write-Host ""

# Summary
Write-Host "Summary:" -ForegroundColor Green
Write-Host "  Test 1 (High Quality): Expected SQS > 4.0" -ForegroundColor White
Write-Host "  Test 2 (UI-centric): Expected SQS < 3.0" -ForegroundColor White
Write-Host "  Test 3 (Vague): Expected SQS < 3.0" -ForegroundColor White
Write-Host "  Test 4 (Missing Given): Expected SQS < 3.5" -ForegroundColor White
Write-Host ""

Read-Host "Press Enter to exit"

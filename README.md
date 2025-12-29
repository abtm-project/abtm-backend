# ABTM Backend - Adaptive BDD Training Model

## Overview

The Adaptive BDD Training Model (ABTM) is a research-based platform for teaching Behavior-Driven Development with personalized, adaptive learning paths. This repository contains the Java Spring Boot backend implementation.

**Based on the research paper:** "Bridging the Learning Curve in BDD: An Adaptive Training Model for Cross-Functional Agile Teams"

## Features

- **6-Dimensional Scenario Quality Analysis**
  - Clarity & Readability (20%)
  - Business Value Alignment (20%)
  - Gherkin Correctness (20%)
  - Testability (20%)
  - Specificity (10%)
  - Duplication Avoidance (10%)

- **Real-Time Feedback** - Immediate analysis with specific improvement suggestions
- **Anti-Pattern Detection** - Identifies UI-centric steps, vague terms, implementation details
- **Automation Readiness Check** - Validates if scenarios are executable
- **Adaptive Algorithm** - Personalized learning based on performance
- **Role-Based Learning Paths** - Specialized tracks for Developers, Testers, and Product Owners

## Technology Stack

- **Java 11**
- **Spring Boot 2.7.14**
- **PostgreSQL** - Database
- **Gherkin Parser** (Cucumber) - BDD scenario parsing
- **Maven** - Dependency management

## Prerequisites for Windows

- Java JDK 11 or higher
- Maven 3.6+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

## Installation on Windows

### 1. Install Java JDK 11

**Download and Install:**
1. Download Java 11 from [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or [Adoptium OpenJDK](https://adoptium.net/temurin/releases/?version=11)
2. Run the installer (e.g., `jdk-11.0.x_windows-x64_bin.exe`)
3. Follow installation wizard (default location: `C:\Program Files\Java\jdk-11.x.x`)

**Set JAVA_HOME Environment Variable:**
1. Press `Win + X` and select "System"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "System Variables", click "New"
5. Variable name: `JAVA_HOME`
6. Variable value: `C:\Program Files\Java\jdk-11.0.x` (your actual path)
7. Click OK

**Add to PATH:**
1. In "System Variables", find and select `Path`
2. Click "Edit"
3. Click "New"
4. Add: `%JAVA_HOME%\bin`
5. Click OK on all dialogs

**Verify Installation:**
```cmd
java -version
```

Expected output:
```
java version "11.0.x"
Java(TM) SE Runtime Environment (build 11.0.x+x)
```

### 2. Install Maven

**Download and Install:**
1. Download Maven from https://maven.apache.org/download.cgi
2. Download the Binary zip archive (e.g., `apache-maven-3.9.x-bin.zip`)
3. Extract to `C:\Program Files\Apache\maven` (or your preferred location)

**Set MAVEN_HOME Environment Variable:**
1. Press `Win + X` → "System" → "Advanced system settings" → "Environment Variables"
2. Under "System Variables", click "New"
3. Variable name: `MAVEN_HOME`
4. Variable value: `C:\Program Files\Apache\maven`
5. Click OK

**Add to PATH:**
1. In "System Variables", find and select `Path`
2. Click "Edit" → "New"
3. Add: `%MAVEN_HOME%\bin`
4. Click OK

**Verify Installation:**
```cmd
mvn -version
```

### 3. Install PostgreSQL

**Download and Install:**
1. Download PostgreSQL from https://www.postgresql.org/download/windows/
2. Run the installer (e.g., `postgresql-15.x-windows-x64.exe`)
3. Follow installation wizard:
   - Installation directory: `C:\Program Files\PostgreSQL\15`
   - Select components: PostgreSQL Server, pgAdmin 4, Command Line Tools
   - Password: Create a strong password for the `postgres` superuser
   - Port: 5432 (default)
   - Locale: Default locale

**Verify Installation:**
```cmd
psql --version
```

### 4. Create Database

**Method 1: Using pgAdmin 4 (Recommended for Beginners):**
1. Open pgAdmin 4 from Start Menu
2. Enter your PostgreSQL master password
3. Right-click "Databases" → "Create" → "Database"
4. Database name: `abtm_db`
5. Click "Save"
6. Right-click "Login/Group Roles" → "Create" → "Login/Group Role"
7. General tab: Name: `abtm_user`
8. Definition tab: Password: `abtm_password`
9. Privileges tab: Check "Can login?"
10. Click "Save"
11. Right-click `abtm_db` → "Properties" → "Security" → Add `abtm_user` with all privileges

**Method 2: Using Command Line:**

Open Command Prompt as Administrator:
```cmd
cd "C:\Program Files\PostgreSQL\15\bin"
psql -U postgres
```

Enter your PostgreSQL password, then in the PostgreSQL prompt:
```sql
CREATE DATABASE abtm_db;
CREATE USER abtm_user WITH PASSWORD 'abtm_password';
GRANT ALL PRIVILEGES ON DATABASE abtm_db TO abtm_user;
\q
```

### 5. Clone or Create Project

**Option A: Clone from Git (if repository exists):**
```cmd
cd C:\Users\YourUsername\Documents
git clone https://github.com/your-org/abtm-backend.git
cd abtm-backend
```

**Option B: Create Project Directory:**
```cmd
mkdir C:\Users\YourUsername\Documents\abtm-backend
cd C:\Users\YourUsername\Documents\abtm-backend
```

Then copy all project files into this directory.

## Project Structure

```
abtm-backend\
├── src\
│   ├── main\
│   │   ├── java\com\abtm\
│   │   │   ├── AbtmApplication.java          # Main Spring Boot application
│   │   │   ├── model\                         # Entity models
│   │   │   │   ├── User.java
│   │   │   │   ├── Module.java
│   │   │   │   ├── Exercise.java
│   │   │   │   ├── Scenario.java
│   │   │   │   └── UserPerformance.java
│   │   │   ├── repository\                    # Data access layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── ScenarioRepository.java
│   │   │   ├── service\                       # Business logic
│   │   │   │   ├── ScenarioAnalyzer.java     # Core quality analysis
│   │   │   │   ├── ScenarioService.java
│   │   │   │   └── AdaptiveEngine.java
│   │   │   └── controller\                    # REST API endpoints
│   │   │       └── ScenarioController.java
│   │   └── resources\
│   │       └── application.properties         # Configuration
│   └── test\                                  # Unit and integration tests
├── pom.xml                                    # Maven dependencies
├── test-api.bat                               # Windows batch test script
├── test-api.ps1                               # PowerShell test script
└── README.md                                  # This file
```

### 6. Configure Application

The configuration file is located at `src\main\resources\application.properties`:

```properties
# Application Configuration
spring.application.name=abtm-backend
server.port=8080

# Database Configuration (Windows localhost)
spring.datasource.url=jdbc:postgresql://localhost:5432/abtm_db
spring.datasource.username=abtm_user
spring.datasource.password=abtm_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=ABTMSecretKeyForJWTTokenGenerationPleaseChangeInProduction2024
jwt.expiration=86400000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Configuration
logging.level.com.abtm=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=INFO

# CORS Configuration
cors.allowed-origins=http://localhost:3000,http://localhost:3001
```

### 7. Build the Project

Open Command Prompt in project directory:
```cmd
cd C:\Users\YourUsername\Documents\abtm-backend
mvn clean install
```

Expected output should end with:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

### 8. Run the Application

**Method 1: Using Maven (Recommended for Development):**
```cmd
mvn spring-boot:run
```

**Method 2: Using Java JAR:**
```cmd
mvn clean package
java -jar target\abtm-backend-1.0.0.jar
```

**Method 3: Using IDE:**
- IntelliJ IDEA: Right-click `AbtmApplication.java` → Run
- Eclipse: Right-click project → Run As → Spring Boot App
- VS Code: F5 (with Spring Boot Extension)

**Success Message:**
```
╔══════════════════════════════════════════════════════╗
║   ABTM Backend Server Started Successfully          ║
║   Adaptive BDD Training Model v1.0.0                 ║
║   Running on: http://localhost:8080                  ║
║   API Docs: http://localhost:8080/api/docs          ║
╚══════════════════════════════════════════════════════╝
```

## Testing the API on Windows

### Method 1: Using PowerShell (Recommended)

**Quick Test:**
```powershell
# Run the automated test script
.\test-api.ps1
```

**Manual Test with PowerShell:**
```powershell
$body = @{
    content = @"
Feature: User Login

Scenario: Successful login
  Given a user with valid credentials
  When the user logs in
  Then the user should see the dashboard
"@
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/scenarios/analyze" `
    -Method Post `
    -Body $body `
    -ContentType "application/json"
```

### Method 2: Using Windows Batch Script

```cmd
test-api.bat
```

### Method 3: Using curl (if installed via Git Bash or Chocolatey)

```cmd
curl -X POST http://localhost:8080/api/scenarios/analyze ^
  -H "Content-Type: application/json" ^
  -d "{\"content\": \"Feature: Login\n\nScenario: User login\nGiven a user with valid credentials\nWhen the user logs in\nThen the user should see the dashboard\"}"
```

### Method 4: Using Postman (Recommended for Windows Users)

1. Download Postman from https://www.postman.com/downloads/
2. Create new POST request
3. URL: `http://localhost:8080/api/scenarios/analyze`
4. Headers: `Content-Type: application/json`
5. Body (raw JSON):
```json
{
  "content": "Feature: User Login\n\nScenario: Successful login\nGiven a user with valid credentials\nWhen the user logs in\nThen the user should see the dashboard"
}
```
6. Click "Send"

## API Endpoints

### Scenario Analysis

#### Analyze a Scenario (Testing/Demo)
**Endpoint:** `POST http://localhost:8080/api/scenarios/analyze`

**Request Body:**
```json
{
  "content": "Feature: User Login\n\nScenario: Successful login\nGiven a user with valid credentials\nWhen the user logs in\nThen the user should see the dashboard"
}
```

**Example Response:**
```json
{
  "clarityScore": 4.5,
  "businessValueScore": 4.0,
  "gherkinScore": 5.0,
  "testabilityScore": 4.2,
  "specificityScore": 4.0,
  "duplicationScore": 5.0,
  "overallSqs": 4.38,
  "detectedAntipatterns": [],
  "automationReady": true,
  "feedback": "Scenario Analysis Summary:\n\nStrengths:\n✓ Good Gherkin structure\n✓ Clear and readable steps\n✓ Focused on business value\n\nAreas for Improvement:\n(none)\n"
}
```

#### Submit a Scenario (Authenticated)
**Endpoint:** `POST http://localhost:8080/api/scenarios/submit`

**Request Body:**
```json
{
  "userId": 1,
  "exerciseId": 1,
  "content": "Feature: ...\n\nScenario: ..."
}
```

#### Get User's Scenarios
**Endpoint:** `GET http://localhost:8080/api/scenarios/user/{userId}`

#### Get User Statistics
**Endpoint:** `GET http://localhost:8080/api/scenarios/user/{userId}/stats}`

## IDE Setup on Windows

### IntelliJ IDEA (Recommended)

1. **Import Project:**
   - File → Open
   - Navigate to `C:\Users\YourUsername\Documents\abtm-backend`
   - Select `pom.xml`
   - Click "Open as Project"
   - Wait for Maven to download dependencies

2. **Configure Run Configuration:**
   - Run → Edit Configurations
   - Click "+" → Spring Boot
   - Name: ABTM Backend
   - Main class: `com.abtm.AbtmApplication`
   - Click OK

3. **Run:**
   - Click the green play button
   - Or press `Shift + F10`

### Eclipse

1. **Import Project:**
   - File → Import → Maven → Existing Maven Projects
   - Browse to `C:\Users\YourUsername\Documents\abtm-backend`
   - Click Finish

2. **Run:**
   - Right-click project in Package Explorer
   - Run As → Spring Boot App

### Visual Studio Code

1. **Install Extensions:**
   - Java Extension Pack (Microsoft)
   - Spring Boot Extension Pack (VMware)
   - Spring Boot Dashboard

2. **Open Project:**
   - File → Open Folder
   - Select `C:\Users\YourUsername\Documents\abtm-backend`

3. **Run:**
   - Press `F5`
   - Or click "Run" in Spring Boot Dashboard

## Troubleshooting on Windows

### Problem: "java is not recognized as an internal or external command"

**Solution:**
- Verify JAVA_HOME is set correctly
- Check PATH includes `%JAVA_HOME%\bin`
- Restart Command Prompt after setting environment variables

```cmd
echo %JAVA_HOME%
echo %PATH%
```

### Problem: "mvn is not recognized as an internal or external command"

**Solution:**
- Verify MAVEN_HOME is set
- Check PATH includes `%MAVEN_HOME%\bin`
- Restart Command Prompt

### Problem: "Could not connect to PostgreSQL server"

**Solution:**
1. Check PostgreSQL service is running:
   - Press `Win + R`
   - Type `services.msc`
   - Find "postgresql-x64-15" service
   - Right-click → Start (if not running)

2. Verify connection:
```cmd
cd "C:\Program Files\PostgreSQL\15\bin"
psql -U postgres -h localhost
```

3. Check `application.properties` has correct:
   - Database URL: `jdbc:postgresql://localhost:5432/abtm_db`
   - Username: `abtm_user`
   - Password: `abtm_password`

### Problem: Port 8080 already in use

**Solution:**
1. Find process using port 8080:
```cmd
netstat -ano | findstr :8080
```

2. Kill the process:
```cmd
taskkill /PID <process_id> /F
```

Or change port in `application.properties`:
```properties
server.port=8081
```

### Problem: Build fails with "dependency not found"

**Solution:**
1. Clear Maven cache:
```cmd
cd %USERPROFILE%\.m2\repository
rmdir /s /q *
```

2. Rebuild:
```cmd
mvn clean install -U
```

## Development Workflow on Windows

### 1. Make Code Changes
Edit files in `src\main\java\com\abtm\`

### 2. Run Tests
```cmd
mvn test
```

### 3. Run Application
```cmd
mvn spring-boot:run
```

### 4. Test in Browser
Open: http://localhost:8080

### 5. Package for Deployment
```cmd
mvn clean package
```

Creates: `target\abtm-backend-1.0.0.jar`

### 6. Run JAR File
```cmd
java -jar target\abtm-backend-1.0.0.jar
```

## Database Management on Windows

### Using pgAdmin 4 (GUI - Recommended)

1. Open pgAdmin 4 from Start Menu
2. Connect to PostgreSQL (enter master password)
3. Navigate to: Servers → PostgreSQL 15 → Databases → abtm_db
4. Right-click Tables to view schema
5. Right-click any table → View/Edit Data → All Rows

### Using Command Line

```cmd
cd "C:\Program Files\PostgreSQL\15\bin"
psql -U abtm_user -d abtm_db
```

**Useful SQL Commands:**
```sql
-- List all tables
\dt

-- View users table
SELECT * FROM users;

-- View scenarios table
SELECT * FROM scenarios;

-- Count scenarios by user
SELECT user_id, COUNT(*) as scenario_count 
FROM scenarios 
GROUP BY user_id;

-- Exit
\q
```

## Running as Windows Service

### Create Windows Service (Production)

1. Download NSSM (Non-Sucking Service Manager):
   - https://nssm.cc/download
   - Extract to `C:\nssm`

2. Build JAR file:
```cmd
mvn clean package
```

3. Install as service:
```cmd
cd C:\nssm\win64
nssm install ABTMBackend "C:\Program Files\Java\jdk-11\bin\java.exe" "-jar C:\Users\YourUsername\Documents\abtm-backend\target\abtm-backend-1.0.0.jar"
```

4. Configure service:
```cmd
nssm set ABTMBackend AppDirectory C:\Users\YourUsername\Documents\abtm-backend
nssm set ABTMBackend DisplayName "ABTM Backend Service"
nssm set ABTMBackend Description "Adaptive BDD Training Model Backend"
nssm set ABTMBackend Start SERVICE_AUTO_START
```

5. Start service:
```cmd
nssm start ABTMBackend
```

6. Check status:
```cmd
nssm status ABTMBackend
```

7. Stop/Remove service:
```cmd
nssm stop ABTMBackend
nssm remove ABTMBackend confirm
```


## Database Schema

Key tables (auto-created by Hibernate on first run):
- `users` - Learner profiles with role and performance data
- `modules` - Learning modules (1-4)
- `exercises` - Practice exercises within modules
- `scenarios` - Submitted BDD scenarios with quality scores
- `user_performance` - Performance tracking for adaptive algorithm

**Note:** Tables are auto-created by Hibernate (see `spring.jpa.hibernate.ddl-auto=update` in `application.properties`)

## Example Scenarios for Testing

### High Quality Scenario (Expected SQS > 4.0)
```gherkin
Feature: User Registration

Scenario: Successful registration with valid email
  Given a new user with email "alice@example.com"
  And the email is not already registered
  When the user submits the registration form
  Then the user receives a confirmation email
  And the user account is created with status "active"
  And the user can login with the registered credentials
```

### Medium Quality Scenario (Expected SQS ~3.0)
```gherkin
Feature: Login

Scenario: User login
  Given the user has an account
  When the user logs in
  Then the user sees their dashboard
```

### Low Quality Scenario (Expected SQS < 2.5)
```gherkin
Feature: Login

Scenario: Click login button
  When I click the login button
  And I type my username in the textbox
  And I type my password
  And I click submit
  Then I see the dashboard page
```

## Adaptive Algorithm

The Performance Score (PS) is calculated using the formula from the research paper:

```
PS = 0.2×KS + 0.4×SQS + 0.15×CS + 0.15×AR + 0.1×TE
```

Where:
- **KS** = Knowledge Score (quiz performance, 0-100)
- **SQS** = Scenario Quality Score (0-100)
- **CS** = Collaboration Score (0-100)
- **AR** = Automation Readiness (0-100)
- **TE** = Time Efficiency (0-100)

**Proficiency Levels:**
- **Struggling:** PS < 60% → Additional support, simplified examples, more practice
- **Progressing:** 60% ≤ PS < 85% → Standard learning path
- **Mastering:** PS ≥ 85% → Advanced challenges, optional content can be skipped

**Implementation:** See `UserPerformance.java` and `calculatePerformanceScore()` method

## Contributing

1. Fork the repository
2. Create a feature branch
   ```cmd
   git checkout -b feature/amazing-feature
   ```
3. Commit your changes
   ```cmd
   git add .
   git commit -m "Add amazing feature"
   ```
4. Push to the branch
   ```cmd
   git push origin feature/amazing-feature
   ```
5. Open a Pull Request on GitHub

## Research Citation

If you use ABTM in your research, please cite:

```bibtex
@article{abtm2025,
  title={Bridging the Learning Curve in BDD: An Adaptive Training Model for Cross-Functional Agile Teams},
  author={[Your Name]},
  journal={IEEE Transactions on Software Engineering},
  year={2025}
}
```

## License

MIT License - see LICENSE file for details

## Support and Contact

For issues or questions:
- **Email:** [your.email@university.edu]
- **GitHub Issues:** https://github.com/your-org/abtm-backend/issues
- **Documentation:** [Project documentation link]

## Acknowledgments

- Research supported by [Your Institution]
- Based on systematic literature review of 31 BDD studies
- Validated through empirical study with 48 participants across multiple roles
- Implements 6-dimensional Scenario Quality Rubric with proven efficacy (Cohen's d = 2.38)

---

**Status:** ✅ Active Development | 📊 Research Stage | 🚀 Ready for Pilot Testing

**Windows Compatibility:** ✅ Fully Tested on Windows 10/11

**Java Version:** ☕ Java 11 (LTS)

**Last Updated:** December 2024

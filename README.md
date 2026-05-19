# FlightHub Automation Framework

A professional, enterprise-grade Selenium WebDriver automation framework built in Java with TestNG for testing the FlightHub.com travel booking website.

## Features

- **Page Object Model (POM)** design pattern for maintainable and reusable code
- **Selenium Grid** support for distributed cross-browser testing
- **TestNG** for powerful test configuration and parallel execution
- **ExtentReports** for detailed HTML test reports
- **Log4j2** for comprehensive logging
- **Maven** build automation
- **Screenshot capture** on test failure
- **Data-driven testing** with TestNG DataProviders
- **Configurable** via properties files and system properties
- **Retry mechanism** for flaky tests
- **Multiple browser support**: Chrome, Firefox, Edge, Safari

## Project Structure

```
flighthub-automation/
|-- pom.xml                          # Maven configuration
|-- testng.xml                       # TestNG suite configuration
|-- .gitignore                       # Git ignore rules
|-- screenshots/                     # Test failure screenshots
|-- src/
    |-- main/
    |   |-- java/com/flighthub/
    |   |   |-- config/
    |   |   |   |-- ConfigReader.java     # Configuration management
    |   |   |-- driver/
    |   |   |   |-- DriverFactory.java    # WebDriver factory (local + Grid)
    |   |   |-- utils/
    |   |       |-- ElementActions.java   # WebElement action wrappers
    |   |       |-- ScreenshotHandler.java # Screenshot utilities
    |   |       |-- WaitUtils.java        # Advanced wait utilities
    |   |       |-- TestDataGenerator.java # Fake data generation
    |   |
    |-- test/
    |   |-- java/com/flighthub/
    |   |   |-- base/
    |   |   |   |-- BaseTest.java         # Base test class
    |   |   |-- listeners/
    |   |   |   |-- TestListener.java     # TestNG test listener
    |   |   |   |-- RetryListener.java    # Retry failed tests
    |   |   |-- pages/
    |   |   |   |-- HomePage.java         # Home page objects
    |   |   |   |-- LoginPage.java        # Login/Register page objects
    |   |   |   |-- FlightSearchResultsPage.java # Search results page
    |   |   |   |-- BookingPage.java      # Booking/payment page
    |   |   |-- tests/
    |   |   |   |-- LoginTests.java       # Login test cases
    |   |   |   |-- RegisterTest.java     # Registration test cases
    |   |   |   |-- FlightSearchTests.java # Flight search test cases
    |   |   |   |-- BookingTests.java     # Booking test cases
    |   |   |-- testdata/
    |   |       |-- LoginDataProvider.java # Test data providers
    |   |
    |   |-- resources/
    |       |-- config.properties          # Framework configuration
    |       |-- log4j2.xml                 # Log4j2 configuration
```

## Prerequisites

| Requirement | Version |
|------------|---------|
| Java JDK   | 11+     |
| Maven      | 3.8+    |
| Chrome     | Latest  |
| Firefox    | Latest  |
| Edge       | Latest  |

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd flighthub-automation
mvn clean compile
```

### 2. Run All Tests

```bash
mvn clean test
```

### 3. Run Specific Test Class

```bash
mvn test -Dtest=LoginTests
mvn test -Dtest=RegisterTest
mvn test -Dtest=FlightSearchTests
mvn test -Dtest=BookingTests
```

### 4. Run with Different Browser

```bash
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

## Configuration

### config.properties

| Property          | Default                          | Description                         |
|-------------------|-----------------------------------|-------------------------------------|
| base.url          | https://www.flighthub.com        | Application base URL                |
| browser           | chrome                           | Browser type                        |
| headless          | false                            | Run in headless mode                |
| execution.mode    | local                            | local or grid                       |
| hub.url           | http://localhost:4444/wd/hub     | Selenium Grid Hub URL               |
| implicit.wait     | 10                               | Implicit wait timeout (seconds)     |
| explicit.wait     | 15                               | Explicit wait timeout (seconds)     |
| page.load.timeout | 30                               | Page load timeout (seconds)         |
| screenshots       | true                             | Enable screenshots                  |

### Override via System Properties

```bash
mvn test -Dbrowser=firefox -Dheadless=true -Dexecution.mode=grid -Dhub.url=http://grid-hub:4444/wd/hub
```

---

## SELENIUM GRID SETUP

### What is Selenium Grid?

Selenium Grid allows you to run tests on different machines against different browsers in parallel. This means you can run your tests simultaneously on:
- Multiple browsers (Chrome, Firefox, Edge)
- Multiple operating systems (Windows, Mac, Linux)
- Multiple machines (distributed execution)

### Option 1: Selenium Grid using Docker (Recommended)

#### Prerequisites
- Docker installed
- Docker Compose installed

#### Step 1: Create docker-compose.yml

```yaml
version: "3"
services:
  selenium-hub:
    image: selenium/hub:latest
    container_name: selenium-hub
    ports:
      - "4444:4444"
    environment:
      - GRID_MAX_SESSION=16
      - GRID_BROWSER_TIMEOUT=300
      - GRID_TIMEOUT=300

  chrome:
    image: selenium/node-chrome:latest
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - HUB_HOST=selenium-hub
      - HUB_PORT=4444
      - NODE_MAX_INSTANCES=5
      - NODE_MAX_SESSION=5
    volumes:
      - /dev/shm:/dev/shm

  firefox:
    image: selenium/node-firefox:latest
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - HUB_HOST=selenium-hub
      - HUB_PORT=4444
      - NODE_MAX_INSTANCES=5
      - NODE_MAX_SESSION=5
    volumes:
      - /dev/shm:/dev/shm

  edge:
    image: selenium/node-edge:latest
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - HUB_HOST=selenium-hub
      - HUB_PORT=4444
      - NODE_MAX_INSTANCES=2
      - NODE_MAX_SESSION=2
    volumes:
      - /dev/shm:/dev/shm
```

#### Step 2: Start the Grid

```bash
# Create a directory for your grid setup
mkdir selenium-grid
cd selenium-grid

# Save the docker-compose.yml above

# Start the grid
docker-compose up -d

# Verify the grid is running
curl http://localhost:4444/grid/console
# Or open http://localhost:4444/ui in browser
```

#### Step 3: Run Tests on Grid

```bash
# Navigate to your project
cd /path/to/flighthub-automation

# Run with Grid profile
mvn clean test -Pgrid

# Or specify browser for grid
mvn clean test -Pgrid -Dbrowser=chrome
mvn clean test -Pgrid -Dbrowser=firefox
mvn clean test -Pgrid -Dbrowser=edge
```

#### Step 4: Stop the Grid

```bash
cd selenium-grid
docker-compose down
```

### Option 2: Selenium Grid Standalone Mode

For simple single-machine testing:

```bash
# Chrome standalone
docker run -d -p 4444:4444 -p 7900:7900 --shm-size="2g" selenium/standalone-chrome:latest

# Firefox standalone
docker run -d -p 4444:4444 -p 7900:7900 --shm-size="2g" selenium/standalone-firefox:latest

# View browser (VNC) at http://localhost:7900 (password: secret)
```

### Option 3: Manual Selenium Grid Setup

#### On Hub Machine

```bash
# Download Selenium Server JAR
wget https://github.com/SeleniumHQ/selenium/releases/download/selenium-4.18.0/selenium-server-4.18.0.jar

# Start Hub
java -jar selenium-server-4.18.0.jar hub
```

#### On Node Machine(s)

```bash
# Download drivers (chromedriver, geckodriver, msedgedriver)
# Place them in PATH

# Start Node (point to Hub)
java -jar selenium-server-4.18.0.jar node --hub http://HUB_IP:4444
```

### Cross-Browser Parallel Execution with Grid

Create a new testng-grid.xml for parallel cross-browser testing:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="FlightHub Cross-Browser Grid" parallel="tests" thread-count="3">

    <test name="Chrome Tests">
        <parameter name="browser" value="chrome"/>
        <parameter name="executionMode" value="grid"/>
        <classes>
            <class name="com.flighthub.tests.LoginTests"/>
            <class name="com.flighthub.tests.FlightSearchTests"/>
        </classes>
    </test>

    <test name="Firefox Tests">
        <parameter name="browser" value="firefox"/>
        <parameter name="executionMode" value="grid"/>
        <classes>
            <class name="com.flighthub.tests.RegisterTest"/>
            <class name="com.flighthub.tests.BookingTests"/>
        </classes>
    </test>

    <test name="Edge Tests">
        <parameter name="browser" value="edge"/>
        <parameter name="executionMode" value="grid"/>
        <classes>
            <class name="com.flighthub.tests.LoginTests"/>
        </classes>
    </test>

</suite>
```

Run with:
```bash
mvn test -Dsuite.file=testng-grid.xml -Pgrid
```

---

## JENKINS CI/CD INTEGRATION

### What is Jenkins?

Jenkins is an open-source automation server that enables developers to build, test, and deploy their software automatically. It provides continuous integration (CI) and continuous delivery (CD) capabilities.

### Step 1: Install Jenkins

#### Option A: Docker (Recommended)

```bash
# Pull Jenkins image
docker pull jenkins/jenkins:lts

# Run Jenkins
docker run -d -p 8080:8080 -p 50000:50000 \
    -v jenkins_home:/var/jenkins_home \
    --name jenkins \
    jenkins/jenkins:lts

# Get initial admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Open http://localhost:8080
# Enter the password and install suggested plugins
```

#### Option B: Direct Installation (Linux)

```bash
# Install Java
sudo apt update
sudo apt install openjdk-11-jdk

# Add Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
    /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
    https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
    /etc/apt/sources.list.d/jenkins.list > /dev/null

# Install Jenkins
sudo apt update
sudo apt install jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

### Step 2: Configure Jenkins

1. Open Jenkins in browser: `http://localhost:8080`
2. Complete the setup wizard
3. Install required plugins:
   - **Maven Integration Plugin** (for Maven projects)
   - **TestNG Results Plugin** (for TestNG reports)
   - **HTML Publisher Plugin** (for ExtentReports)
   - **Selenium Plugin** (optional)

### Step 3: Configure Jenkins Global Tools

1. Go to **Manage Jenkins > Global Tool Configuration**
2. Configure JDK:
   - Name: `JDK-11`
   - JAVA_HOME: `/usr/lib/jvm/java-11-openjdk` (adjust path as needed)
3. Configure Maven:
   - Name: `Maven-3.9`
   - MAVEN_HOME: `/usr/share/maven` (or auto-install)
4. Configure Git:
   - Path to Git executable: `git`

### Step 4: Create Jenkins Pipeline Job

1. Click **New Item**
2. Enter name: `FlightHub-Automation`
3. Select **Pipeline** and click OK

#### Pipeline Script (Declarative)

```groovy
pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-11'
    }

    environment {
        SELENIUM_HUB_URL = 'http://selenium-hub:4444/wd/hub'
        BROWSER = 'chrome'
    }

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to run tests on'
        )
        choice(
            name: 'TEST_SUITE',
            choices: ['regression', 'login', 'registration', 'search', 'booking'],
            description: 'Test suite to execute'
        )
        choice(
            name: 'EXECUTION_MODE',
            choices: ['local', 'grid'],
            description: 'Execution mode: local or Selenium Grid'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                // Or: git 'https://github.com/yourusername/flighthub-automation.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    def groups = params.TEST_SUITE == 'regression' ? 'regression' : params.TEST_SUITE
                    def profile = params.EXECUTION_MODE == 'grid' ? '-Pgrid' : ''

                    sh """
                        mvn test ${profile} \
                            -Dbrowser=${params.BROWSER} \
                            -Dexecution.mode=${params.EXECUTION_MODE} \
                            -Dhub.url=${env.SELENIUM_HUB_URL} \
                            -Dgroups=${groups} \
                            -Dsuite.file=testng.xml
                    """
                }
            }
        }
    }

    post {
        always {
            // Publish TestNG results
            publishTestNGResult(
                testResultsPattern: 'target/surefire-reports/testng-results.xml'
            )

            // Publish ExtentReports
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/reports',
                reportFiles: '*.html',
                reportName: 'ExtentReports'
            ])

            // Archive screenshots
            archiveArtifacts(
                artifacts: 'screenshots/*.png',
                allowEmptyArchive: true
            )

            // Archive test logs
            archiveArtifacts(
                artifacts: 'target/logs/*.log',
                allowEmptyArchive: true
            )

            // Clean workspace (optional)
            cleanWs(
                deleteDirs: true,
                notFailBuild: true
            )
        }

        success {
            echo 'All tests passed!'
            // Send notification (configure as needed)
            // slackSend channel: '#automation', color: 'good', message: 'Tests Passed!'
        }

        failure {
            echo 'Some tests failed!'
            // Send notification
            // slackSend channel: '#automation', color: 'danger', message: 'Tests Failed! Check reports.'
        }
    }
}
```

### Step 5: Jenkins with Selenium Grid Integration

For running tests on Selenium Grid from Jenkins:

#### Option A: Docker Compose (Jenkins + Grid)

Create `docker-compose-jenkins.yml`:

```yaml
version: "3"
services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    ports:
      - "8080:8080"
      - "50000:50000"
    volumes:
      - jenkins_home:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock
    environment:
      - JAVA_OPTS=-Djenkins.install.runSetupWizard=false

  selenium-hub:
    image: selenium/hub:latest
    container_name: selenium-hub
    ports:
      - "4444:4444"

  chrome:
    image: selenium/node-chrome:latest
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - HUB_HOST=selenium-hub
      - HUB_PORT=4444

  firefox:
    image: selenium/node-firefox:latest
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - HUB_HOST=selenium-hub
      - HUB_PORT=4444

volumes:
  jenkins_home:
```

Run:
```bash
docker-compose -f docker-compose-jenkins.yml up -d
```

Configure Jenkins job with hub URL: `http://selenium-hub:4444/wd/hub`

### Step 6: Jenkins Freestyle Job (Alternative)

1. Click **New Item**
2. Enter name: `FlightHub-Automation-Freestyle`
3. Select **Freestyle project** and click OK
4. In **Source Code Management**:
   - Select **Git**
   - Repository URL: Your repo URL
5. In **Build Triggers**:
   - Check **Poll SCM** with schedule: `H */4 * * *` (every 4 hours)
   - Or check **GitHub hook trigger for GITScm polling**
6. In **Build > Invoke top-level Maven targets**:
   - Maven Version: `Maven-3.9`
   - Goals: `clean test`
   - Properties:
     ```
     browser=chrome
     execution.mode=local
     ```
7. In **Post-build Actions**:
   - **Publish TestNG Results**: `target/surefire-reports/*.xml`
   - **Publish HTML Reports**: `target/reports`

### Step 7: Schedule Jenkins Job

1. In your job configuration, go to **Build Triggers**
2. Check **Build periodically**
3. Enter schedule (cron syntax):
   - `H 2 * * *` - Daily at 2 AM
   - `H */6 * * *` - Every 6 hours
   - `H 0 * * 1` - Weekly on Monday

### Step 8: View Reports in Jenkins

After test execution:
1. Go to your job page
2. Click on a build number
3. Click **ExtentReports** in left sidebar for HTML report
4. Click **TestNG Results** for test summary
5. Check **Console Output** for execution logs

---

## Test Groups

| Group         | Description                          |
|---------------|--------------------------------------|
| login         | Login functionality tests            |
| registration  | User registration tests              |
| search        | Flight search tests                  |
| booking       | Booking/payment tests                |
| regression    | All regression tests                 |
| critical      | Critical path tests                  |
| negative      | Negative test cases                  |
| validation    | Form validation tests                |
| ui            | UI verification tests                |
| e2e           | End-to-end tests                     |
| edge          | Edge cases                           |

### Run by Groups

```bash
# Login tests only
mvn test -Dgroups=login

# Critical tests
mvn test -Dgroups=critical

# Exclude negative tests
mvn test -Dgroups=regression -DexcludedGroups=negative

# Multiple groups
mvn test -Dgroups="login,search"
```

## Viewing Reports

### ExtentReports

After test execution, open:
```
target/reports/FlightHub_TestReport_*.html
```

### TestNG Reports

```
target/surefire-reports/index.html
target/surefire-reports/emailable-report.html
```

## Troubleshooting

### ChromeDriver Version Mismatch
```bash
mvn dependency:resolve
# WebDriverManager will auto-download the correct driver
```

### Selenium Grid Connection Issues
```bash
# Verify Grid is running
curl http://localhost:4444/wd/hub/status

# Check node status
curl http://localhost:4444/grid/api/proxy?id=http://NODE_IP:5555
```

### Port Conflicts
```bash
# Kill processes using port 4444
sudo lsof -ti:4444 | xargs kill -9
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/my-feature`
5. Submit a pull request

## License

This project is for educational and testing purposes only. FlightHub is a trademark of its respective owners.

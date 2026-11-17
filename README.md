# Online Food Ordering and Restaurant Sales Analysis System — Scenario 12

This repository upgrades the supplied Scenario 12 project while preserving its original problem statement and seven functional requirements. It now contains a Spring Boot + Thymeleaf web application, MySQL persistence, reporting APIs, Python analytics, Docker/Railway deployment files and the original academic documentation/diagrams.

## Scenario 12 — original problem statement
A restaurant manages its menu, customer orders, food preparation status and payment records. Manual handling can cause order-processing errors, difficulty tracking order status, and delays in identifying popular dishes and analyzing sales performance. This project manages menu items, processes customer orders, tracks order status and analyzes sales trends.

## Functional requirements
1. Maintain menu item ID, name, category, price and availability.
2. Maintain customer details and contact information.
3. Place orders containing multiple food items and quantities.
4. Calculate the total order amount automatically from database prices and quantities.
5. Update order status: Placed, Preparing, Completed or Cancelled.
6. Generate customer-order, popular-item and daily-sales reports.
7. Retrieve database records, preprocess them and generate Python visualizations.

Payment records are retained as required by the original scenario description.

## Technology
- Java 17, Spring Boot 3.4, Spring MVC, JDBC Template, Thymeleaf
- MySQL 8+
- HTML, CSS, JavaScript, responsive layout
- Python, Pandas, Matplotlib, mysql-connector-python
- Maven, Docker, Railway-compatible deployment configuration

## Project structure
```text
SE-Documentation/       SRS and deployment documentation
SE-Diagrams/            Use case, class, ER and architecture diagrams
JP-Source/              Spring Boot web application and resources
DBMS-Database/          MySQL schema, sample data, queries and guide
FDS-Python/             Database retrieval and preprocessing pipeline
FDS-Visualization/      Matplotlib chart generation
SE-Testing/             Test plan, test cases and verification status
Deployment/             Railway configuration and deployment guide
Executable/             Windows/Linux launch scripts and JAR destination
Dockerfile              Container build
```

## 1. Database setup (local)
1. Install MySQL Server 8+ and start the MySQL service.
2. Open MySQL Workbench or a terminal and execute `DBMS-Database/schema.sql`.
3. Execute `DBMS-Database/sample-data.sql` once to insert starter menu/customer rows.
4. The database name is `restaurant_db`. Never commit credentials.

Example command-line setup:
```bash
mysql -u root -p < DBMS-Database/schema.sql
mysql -u root -p restaurant_db < DBMS-Database/sample-data.sql
```

## 2. Run from source (Windows)
Install JDK 17+ and Maven 3.9+ (Maven is required only to build from source).
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
cd JP-Source
mvn clean package
java -jar target/restaurant-management.jar
```
Open `http://localhost:8080`.

Linux/macOS:
```bash
export DB_URL='jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USER=root
export DB_PASSWORD='YOUR_MYSQL_PASSWORD'
cd JP-Source && mvn clean package
java -jar target/restaurant-management.jar
```

## 3. Executable JAR for college staff
After a successful Maven build, copy `JP-Source/target/restaurant-management.jar` to `Executable/restaurant-management.jar`. Staff need Java 17+ and access to a MySQL database; they do not need VS Code or Maven. Set `DB_URL`, `DB_USER`, and `DB_PASSWORD`, then run:
- Windows: double-click `Executable/run-windows.bat` after setting environment variables, or run it from Command Prompt.
- Linux/macOS: `chmod +x Executable/run-linux-macos.sh && ./Executable/run-linux-macos.sh`.

**Build status in this handoff:** the source and Maven packaging configuration are provided, but the JAR has not been generated here. This execution environment has Java 21 but no Maven installation and no network/DNS access to download Maven dependencies. Therefore no unverified or placeholder JAR is included. Build using Maven on a network-enabled computer before handing the ZIP to staff.

## 4. Web modules
- Dashboard: database-backed counts, completed sales and latest orders.
- Menu Management: add items, change availability, delete unreferenced items and search.
- Customer Management: register and search customers.
- Place New Order / Order Management: multi-item order, positive quantity validation, server-side total calculation and status updates.
- Payment Management: record Cash/UPI/Card/Other payment entries.
- Sales Reports: customer orders, popular menu items and completed daily sales.
- JSON endpoints: `/api/health`, `/api/menu`, `/api/customers`, `/api/orders`, `/api/reports/daily-sales`.

## 5. Python data analysis
From the repository root:
```bash
python -m venv .venv
# Windows: .venv\\Scripts\\activate
# Linux/macOS: source .venv/bin/activate
pip install -r FDS-Python/requirements.txt
```
Set `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` for the same MySQL database, then run:
```bash
python FDS-Python/run_pipeline.py
```
Raw/cleaned CSV files are written to `FDS-Python/output/`; charts are written to `FDS-Visualization/output/`. The chart pipeline reports when no source rows exist.

## 6. Docker / Railway deployment
Read `Deployment/DEPLOYMENT-GUIDE.md`. Configure a cloud MySQL database and the Railway service variables. Deploy from the GitHub repository using the root `Dockerfile`. Railway supplies HTTPS after deployment. The app health endpoint is `/api/health`.

**No live deployment URL is included:** deployment requires the project owner's GitHub/Railway authorization and cloud database credentials. A public URL must only be shared after the deployed health endpoint and UI are opened and tested.

## 7. Security notes
- Do not put passwords in Java properties, Python files, Dockerfile, GitHub or screenshots.
- Set `DB_PASSWORD` and `DB_URL` as environment variables/secrets.
- Restrict cloud database network access to the application service where the provider permits it.
- Use HTTPS at the hosting provider's ingress. Do not expose the MySQL port publicly without access controls.

## Verification transparency
Python syntax and repository structure can be checked in this environment. Java dependency resolution, Maven packaging, live MySQL CRUD, cloud database connectivity and Railway deployment require Maven/dependency access, a running MySQL instance and owner-controlled hosting credentials. See `SE-Testing/VERIFICATION-STATUS.md` for the exact distinction.

## Optional local Docker test
With Docker Engine and Docker Compose installed, set `DB_PASSWORD` in the shell and run `docker compose up --build`. The compose file initializes a local MySQL container and starts the web app at `http://localhost:8080`. This is a local convenience workflow, not a public deployment.

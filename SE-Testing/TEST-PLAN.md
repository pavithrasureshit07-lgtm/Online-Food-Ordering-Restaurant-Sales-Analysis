# Test plan — Scenario 12 upgraded application

## Scope
Validate Spring Boot web routes and database operations, menu/customer management, multi-item order transactions and totals, order status, payment records, reports, Python data preparation/visualizations, executable packaging and deployment.

## Test levels
- Static checks: project layout, Maven POM XML, Python syntax and template structure.
- Unit/integration: Maven tests plus endpoint/service tests against MySQL.
- Database: schema constraints, foreign keys, transaction rollback and report aggregates.
- System: browser workflow on desktop/mobile and JAR startup.
- Deployment: Docker/Railway build, cloud DB connection, HTTPS and cross-device access.

## Environment
JDK 17+, Maven 3.9+, MySQL 8+, Python 3.10+, browser; Docker/Railway only for deployment testing.

## Entry / exit
Entry: schema initialized and environment variables configured. Exit: all critical tests pass, no credentials committed, `/api/health` succeeds, and the deployed HTTPS URL is tested externally. Tests not executable in the handoff environment are listed in `VERIFICATION-STATUS.md` and must not be represented as passed.

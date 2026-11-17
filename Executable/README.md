# Executable JAR handoff

Build the application using `cd JP-Source && mvn clean package`, then copy `JP-Source/target/restaurant-management.jar` into this folder. The resulting JAR is a Spring Boot executable JAR with web server, application classes and runtime dependencies. Java 17+ and a reachable MySQL 8 database are required on the staff computer. Maven and an IDE are not required to run the built JAR.

The JAR is intentionally not fabricated in this source-only handoff: Maven is not installed and dependency downloads are unavailable in the build environment. See the root README for build commands.

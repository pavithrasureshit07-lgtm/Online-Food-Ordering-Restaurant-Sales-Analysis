# Software Requirements Specification
## Online Food Ordering and Restaurant Sales Analysis System — Scenario 12

### 1. Introduction
**Purpose:** Define requirements for a restaurant system implemented using Java 17, Spring Boot, MySQL, a responsive Thymeleaf web interface and Python analytics.

**Problem statement:** A restaurant manages its menu, customer orders, food preparation status and payment records. Manual handling can cause order-processing errors, difficulty tracking order status, and delays in identifying popular dishes and analyzing sales performance. The restaurant requires a system to manage menu items, process customer orders, track order status and analyze sales trends.

**Objectives:** Maintain menu/customer data; place multi-item orders; calculate totals; track order status; store payment records; report customer orders, popular items and daily sales; retrieve, preprocess and visualize database data.

**Scope:** A Spring Boot web application backed by MySQL, with a runnable executable JAR and a Python analysis pipeline reading the same database. The application provides staff-facing dashboard pages and JSON endpoints. It records payments but does not integrate a payment gateway. Delivery tracking and customer login are outside the assigned scenario.

### 2. Overall description
**Product perspective:** Spring Boot/Spring MVC/JDBC Template operational web application, Thymeleaf/HTML/CSS/JavaScript interface, MySQL shared relational database, Python/Pandas/Matplotlib analytics.
**Users:** Restaurant staff/administrator and project evaluator.
**Environment:** Windows/Linux/macOS; JDK 17; Maven; MySQL 8+; Python 3.10+.

### 3. Functional requirements
| ID | Requirement |
|---|---|
| FR-01 | Maintain menu item ID, name, category, price and availability. |
| FR-02 | Maintain customer details and contact information. |
| FR-03 | Place orders with one or more food items and quantities. |
| FR-04 | Calculate order amount from item prices and quantities. |
| FR-05 | Support Placed, Preparing, Completed and Cancelled statuses. |
| FR-06 | Maintain payment records and identify pending/partial/paid balances. |
| FR-07 | Generate customer order, popular menu item and daily sales reports. |
| FR-08 | Retrieve database data, preprocess it and generate visualizations using Python. |

### 4. Non-functional requirements
- Usability: responsive dashboard, navigation, forms, search and feedback messages.
- Reliability: order header and item rows are committed or rolled back together.
- Integrity: keys, constraints and transactions protect records.
- Performance: indexed joins and aggregate queries support reports.
- Maintainability: separate Java services, SQL scripts and Python modules.
- Portability: supported Java, MySQL and Python environments.
- Security: credentials come from environment variables, not source control.
- Accuracy: `DECIMAL` and `BigDecimal` are used for money.

### 5. Hardware and software requirements
Hardware: dual-core processor, 4 GB RAM minimum (8 GB recommended), 1 GB free disk.
Software: JDK 17+, Maven 3.9+, MySQL 8+, Python 3.10+, Docker for container deployment, packages in `FDS-Python/requirements.txt`.

### 6. Data requirements
`menu_items`, `customers`, `orders`, `order_items`, `payments`. `order_items.unit_price` stores the price at purchase time so historical order totals do not change when menu prices change.

### 7. Assumptions and constraints
Staff enter valid data; quantity is a positive integer; an item must be available when ordered; cancelled orders are excluded from completed daily sales; order/payment statuses are restricted to the documented values; MySQL is reachable.

### 8. Acceptance criteria
Staff can use the web dashboard to maintain menu and customer records, place a multi-item order, see the calculated total, update status, record payment and view reports. The packaged JAR starts the same web application without an IDE. Python can retrieve the same stored records, preprocess them and create charts.

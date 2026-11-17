# Test cases — Scenario 12

| ID | Feature | Positive test | Expected result | Negative test |
|---|---|---|---|---|
| TC01 | Menu | Add valid name/category/price | Item stored and listed | Blank name or negative price rejected |
| TC02 | Availability | Disable then enable an item | Availability changes in MySQL | Unknown ID reports no item |
| TC03 | Customer | Register name, phone, optional email | Customer stored | Duplicate phone/email rejected by unique constraint |
| TC04 | Multi-item order | Select two items and positive quantities | Order and item rows stored | Zero quantity/unavailable item rejected and transaction rolled back |
| TC05 | Total | Order prices 90×2 and 50×1 | Total equals ₹230.00 | Client cannot submit a trusted total; server recalculates from DB prices |
| TC06 | Status | Set Placed/Preparing/Completed/Cancelled | Status persisted | Invalid status rejected |
| TC07 | Payment | Record UPI/Cash/Card payment | Payment row stored with computed payment state | Negative amount/unknown order rejected |
| TC08 | Customer report | Query orders by customer | Actual customer order rows displayed | Empty data produces empty report, not demo values |
| TC09 | Popular items | Aggregate non-cancelled order items | Quantity-sorted item report | No orders produces empty report |
| TC10 | Daily sales | Aggregate completed orders by date | Actual daily totals displayed | Non-completed orders excluded |
| TC11 | Python | Retrieve and clean MySQL tables | Raw/clean CSV files generated | Missing DB connection gives connector error; no fake records used |
| TC12 | Visualization | Generate charts from cleaned data | PNG charts saved | Empty applicable data skips chart with message |
| TC13 | Deployment | Open deployed HTTPS app from another device | Public site and health endpoint respond | Invalid DB credentials cause startup/health failure, not false success |

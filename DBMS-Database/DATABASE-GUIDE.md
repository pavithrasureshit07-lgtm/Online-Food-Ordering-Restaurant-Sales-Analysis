# Database guide — MySQL 8+

## Setup
Execute `schema.sql`, then execute `sample-data.sql` once. Database: `restaurant_db`. The schema uses InnoDB, primary/foreign keys, unique constraints, CHECK constraints, ENUM status domains and indexes for report joins/filtering.

## Tables and 3NF notes
- `menu_items(item_id, item_name, category, price, available)`: item ID determines all menu attributes.
- `customers(customer_id, customer_name, phone, email)`: customer ID determines customer details; phone/email are unique candidate keys.
- `orders(order_id, customer_id, order_date, status, total_amount)`: order ID determines order header fields; customer details are stored only in customers.
- `order_items(order_item_id, order_id, item_id, quantity, unit_price, line_total)`: each row records an item on an order. `unit_price` is a historical snapshot at order time, not a lookup of today's menu price.
- `payments(payment_id, order_id, amount, payment_method, payment_status, payment_date)`: payment ID determines the payment record; order details are not duplicated.

The relations separate menu, customer, order header, order lines and payment facts. Non-key attributes depend on the key, the whole key and not another non-key attribute, satisfying the intended 3NF design. `orders.total_amount` and `order_items.line_total` are stored operational totals for fast display/audit; Java calculates them during order placement in a transaction.

## Key constraints
- Menu prices, order totals, item prices, line totals and payment amounts cannot be negative.
- Order item quantity must be greater than zero.
- Order status is limited to Placed, Preparing, Completed and Cancelled.
- Payment method is limited to Cash, UPI, Card and Other.
- Customer phone and optional email are unique.
- An order must reference an existing customer; order items must reference an existing order and menu item.
- Referenced menu items cannot be deleted; mark them unavailable instead.

## Cloud use
Run the same schema against the cloud MySQL instance. Set JDBC connection details in the hosting provider's environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`). Do not put credentials in this repository.

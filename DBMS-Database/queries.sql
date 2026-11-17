USE restaurant_db;
-- SELECT available menu
SELECT * FROM menu_items WHERE available=TRUE;
-- INSERT menu item
INSERT INTO menu_items(item_name,category,price,available) VALUES('Gobi Manchurian','Starter',110.00,TRUE);
-- UPDATE menu availability
UPDATE menu_items SET available=FALSE WHERE item_id=6;
-- DELETE only an item with no historical order references
DELETE FROM menu_items WHERE item_id=7;
-- JOIN customer orders
SELECT o.order_id,c.customer_name,o.order_date,o.status,o.total_amount
FROM orders o JOIN customers c ON c.customer_id=o.customer_id;
-- JOIN order line details
SELECT o.order_id,m.item_name,oi.quantity,oi.unit_price,oi.line_total
FROM orders o JOIN order_items oi ON oi.order_id=o.order_id
JOIN menu_items m ON m.item_id=oi.item_id;
-- Popular menu items
SELECT m.item_name,SUM(oi.quantity) quantity_sold
FROM order_items oi JOIN menu_items m ON m.item_id=oi.item_id
JOIN orders o ON o.order_id=oi.order_id WHERE o.status<>'Cancelled'
GROUP BY m.item_id,m.item_name ORDER BY quantity_sold DESC;
-- Daily completed sales
SELECT DATE(order_date) sales_day,SUM(total_amount) daily_sales
FROM orders WHERE status='Completed' GROUP BY DATE(order_date) ORDER BY sales_day;
-- Payment records by order
SELECT o.order_id,o.total_amount,COALESCE(SUM(p.amount),0) amount_recorded
FROM orders o LEFT JOIN payments p ON p.order_id=o.order_id
GROUP BY o.order_id,o.total_amount;

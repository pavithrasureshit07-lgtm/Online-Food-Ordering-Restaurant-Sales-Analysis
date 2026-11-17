package com.restaurant;
import java.sql.*;
import java.math.BigDecimal;
public class ReportService {
 private void print(String sql,int columns) throws SQLException {
  try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){
   ResultSetMetaData m=r.getMetaData();for(int i=1;i<=columns;i++)System.out.print(m.getColumnLabel(i)+(i==columns?"\n":" | "));
   while(r.next()){for(int i=1;i<=columns;i++)System.out.print(r.getString(i)+(i==columns?"\n":" | "));}
  }
 }
 public void customerOrders() throws SQLException {
  print("SELECT c.customer_name,o.order_id,o.order_date,o.status,o.total_amount FROM customers c JOIN orders o ON o.customer_id=c.customer_id ORDER BY c.customer_name,o.order_date DESC",5);
 }
 public void popularItems() throws SQLException {
  print("SELECT m.item_name,SUM(oi.quantity) quantity_sold FROM order_items oi JOIN menu_items m ON m.item_id=oi.item_id JOIN orders o ON o.order_id=oi.order_id WHERE o.status<>'Cancelled' GROUP BY m.item_id,m.item_name ORDER BY quantity_sold DESC",2);
 }
 public void dailySales() throws SQLException {
  print("SELECT DATE(order_date) sales_day,SUM(total_amount) daily_sales FROM orders WHERE status='Completed' GROUP BY DATE(order_date) ORDER BY sales_day",2);
 }
 public void paymentStatus() throws SQLException {
  String q="SELECT o.order_id,o.total_amount,COALESCE(SUM(p.amount),0) received FROM orders o LEFT JOIN payments p ON p.order_id=o.order_id GROUP BY o.order_id,o.total_amount ORDER BY o.order_id";
  try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(q)){
   System.out.println("Order | Total | Received | Balance | Status");
   while(r.next()){BigDecimal total=r.getBigDecimal(2),received=r.getBigDecimal(3),balance=total.subtract(received).max(BigDecimal.ZERO);String status=balance.signum()==0?"Paid":received.signum()>0?"Partial":"Pending";System.out.printf("%d | %s | %s | %s | %s%n",r.getInt(1),total,received,balance,status);}
  }
 }
}

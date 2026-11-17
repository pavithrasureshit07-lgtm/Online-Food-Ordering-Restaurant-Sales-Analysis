package com.restaurant;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
public class OrderService {
 public void place() throws SQLException {
  int customerId=Input.positiveInt("Customer ID: ");Map<Integer,Integer> items=new LinkedHashMap<>();
  System.out.println("Enter item IDs and quantities. Enter 0 as item ID to finish.");
  while(true){int id=Input.integer("Menu item ID (0 to finish): ");if(id==0)break;if(id<0){System.out.println("ID must be positive.");continue;}items.merge(id,Input.positiveInt("Quantity: "),Integer::sum);}
  if(items.isEmpty()){System.out.println("Order cancelled: add at least one item.");return;}
  try(Connection c=DB.getConnection()){
   c.setAutoCommit(false);
   try{
    try(PreparedStatement p=c.prepareStatement("SELECT customer_id FROM customers WHERE customer_id=?")){p.setInt(1,customerId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new IllegalArgumentException("Customer does not exist.");}}
    BigDecimal total=BigDecimal.ZERO;Map<Integer,BigDecimal> prices=new LinkedHashMap<>();
    try(PreparedStatement p=c.prepareStatement("SELECT price,available FROM menu_items WHERE item_id=? FOR UPDATE")){
     for(var e:items.entrySet()){p.setInt(1,e.getKey());try(ResultSet r=p.executeQuery()){
      if(!r.next())throw new IllegalArgumentException("Menu item "+e.getKey()+" does not exist.");
      if(!r.getBoolean("available"))throw new IllegalArgumentException("Menu item "+e.getKey()+" is unavailable.");
      BigDecimal price=r.getBigDecimal("price");prices.put(e.getKey(),price);total=total.add(price.multiply(BigDecimal.valueOf(e.getValue())));
     }}
    }
    int orderId;
    try(PreparedStatement p=c.prepareStatement("INSERT INTO orders(customer_id,status,total_amount) VALUES(?,'Placed',?)",Statement.RETURN_GENERATED_KEYS)){
     p.setInt(1,customerId);p.setBigDecimal(2,total);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(!k.next())throw new SQLException("Could not retrieve order ID.");orderId=k.getInt(1);}
    }
    try(PreparedStatement p=c.prepareStatement("INSERT INTO order_items(order_id,item_id,quantity,unit_price,line_total) VALUES(?,?,?,?,?)")){
     for(var e:items.entrySet()){BigDecimal price=prices.get(e.getKey());p.setInt(1,orderId);p.setInt(2,e.getKey());p.setInt(3,e.getValue());p.setBigDecimal(4,price);p.setBigDecimal(5,price.multiply(BigDecimal.valueOf(e.getValue())));p.addBatch();}p.executeBatch();
    }
    c.commit();System.out.println("Order placed successfully. Order ID: "+orderId);System.out.println("Total amount: "+total.setScale(2));
   }catch(Exception e){c.rollback();if(e instanceof IllegalArgumentException)System.out.println("Order not placed: "+e.getMessage());else if(e instanceof SQLException se)throw se;else throw new SQLException("Order failed.",e);}
   finally{c.setAutoCommit(true);}
  }
 }
 public void list() throws SQLException {
  String q="SELECT o.order_id,c.customer_name,o.order_date,o.status,o.total_amount FROM orders o JOIN customers c ON c.customer_id=o.customer_id ORDER BY o.order_date DESC";
  try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(q)){
   System.out.println("Order | Customer | Date | Status | Total");while(r.next())System.out.printf("%d | %s | %s | %s | %s%n",r.getInt(1),r.getString(2),r.getTimestamp(3),r.getString(4),r.getBigDecimal(5));
  }
 }
 public void updateStatus() throws SQLException {
  int id=Input.positiveInt("Order ID: ");String status=Input.text("Status (Placed/Preparing/Completed/Cancelled): ");
  if(!List.of("Placed","Preparing","Completed","Cancelled").contains(status)){System.out.println("Invalid status.");return;}
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("UPDATE orders SET status=? WHERE order_id=?")){
   p.setString(1,status);p.setInt(2,id);System.out.println(p.executeUpdate()==1?"Order status updated.":"Order not found.");
  }
 }
 public void payment() throws SQLException {
  int id=Input.positiveInt("Order ID: ");BigDecimal amount=Input.money("Payment amount: ");String method=Input.text("Method (Cash/UPI/Card/Other): ");
  if(!List.of("Cash","UPI","Card","Other").contains(method)){System.out.println("Invalid method.");return;}
  String q="INSERT INTO payments(order_id,amount,payment_method,payment_status) SELECT ?,?,?,CASE WHEN ?>=total_amount THEN 'Paid' WHEN ?>0 THEN 'Partial' ELSE 'Pending' END FROM orders WHERE order_id=?";
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(q)){
   p.setInt(1,id);p.setBigDecimal(2,amount);p.setString(3,method);p.setBigDecimal(4,amount);p.setBigDecimal(5,amount);p.setInt(6,id);
   System.out.println(p.executeUpdate()==1?"Payment record saved.":"Order not found.");
  }
 }
}

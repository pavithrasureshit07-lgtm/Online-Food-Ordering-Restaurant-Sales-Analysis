package com.restaurant;
import java.sql.*;
public class CustomerService {
 public void add() throws SQLException {
  String name=Input.text("Customer name: "),phone=Input.text("Phone: "),email=Input.optionalText("Email (optional): ");
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO customers(customer_name,phone,email) VALUES(?,?,?)")){
   p.setString(1,name);p.setString(2,phone);if(email.isEmpty())p.setNull(3,Types.VARCHAR);else p.setString(3,email);
   p.executeUpdate();System.out.println("Customer registered.");
  }catch(SQLIntegrityConstraintViolationException e){System.out.println("Phone or email already exists. Use unique contact details.");}
 }
 public void list() throws SQLException {
  try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM customers ORDER BY customer_id")){
   System.out.println("ID | Name | Phone | Email");while(r.next())System.out.printf("%d | %s | %s | %s%n",r.getInt(1),r.getString(2),r.getString(3),r.getString(4));
  }
 }
}

package com.restaurant;
import java.math.BigDecimal;
import java.sql.*;
public class MenuService {
 public void add() throws SQLException {
  String sql="INSERT INTO menu_items(item_name,category,price,available) VALUES(?,?,?,?)";
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setString(1,Input.text("Item name: "));p.setString(2,Input.text("Category: "));
   p.setBigDecimal(3,Input.money("Price: "));p.setBoolean(4,Input.yesNo("Available"));
   p.executeUpdate();System.out.println("Menu item added.");
  }
 }
 public void list() throws SQLException {
  try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM menu_items ORDER BY item_id")){
   System.out.println("ID | Item | Category | Price | Available");
   while(r.next())System.out.printf("%d | %s | %s | %s | %s%n",r.getInt("item_id"),r.getString("item_name"),r.getString("category"),r.getBigDecimal("price"),r.getBoolean("available")?"Yes":"No");
  }
 }
 public void availability() throws SQLException {
  int id=Input.positiveInt("Item ID: ");boolean a=Input.yesNo("Set available");
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("UPDATE menu_items SET available=? WHERE item_id=?")){
   p.setBoolean(1,a);p.setInt(2,id);System.out.println(p.executeUpdate()==1?"Availability updated.":"Item not found.");
  }
 }
 public void delete() throws SQLException {
  int id=Input.positiveInt("Item ID to delete: ");
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM menu_items WHERE item_id=?")){
   p.setInt(1,id);System.out.println(p.executeUpdate()==1?"Item deleted.":"Item not found or referenced by order history.");
  }catch(SQLIntegrityConstraintViolationException e){System.out.println("Cannot delete an item used in an existing order.");}
 }
}

package com.restaurant;
import java.sql.SQLException;
public class Main {
 public static void main(String[] args) {
  MenuService menu=new MenuService();CustomerService customers=new CustomerService();OrderService orders=new OrderService();ReportService reports=new ReportService();
  try(var connection=DB.getConnection()){System.out.println("MySQL connection successful.");}
  catch(SQLException e){System.out.println("Cannot connect to MySQL: "+e.getMessage());System.out.println("Check MySQL service, schema and DB_USER/DB_PASSWORD.");return;}
  boolean run=true;
  while(run){
   System.out.println("\n===== RESTAURANT MANAGEMENT =====\n1 Add menu item\n2 View menu\n3 Update item availability\n4 Delete menu item\n5 Register customer\n6 View customers\n7 Place order\n8 View orders\n9 Update order status\n10 Record payment\n11 Customer order report\n12 Popular items report\n13 Daily sales report\n14 Payment status report\n0 Exit");
   int choice=Input.integer("Choice: ");
   try{switch(choice){
    case 1->menu.add();case 2->menu.list();case 3->menu.availability();case 4->menu.delete();
    case 5->customers.add();case 6->customers.list();case 7->orders.place();case 8->orders.list();
    case 9->orders.updateStatus();case 10->orders.payment();case 11->reports.customerOrders();
    case 12->reports.popularItems();case 13->reports.dailySales();case 14->reports.paymentStatus();
    case 0->run=false;default->System.out.println("Choose a number from 0 to 14.");
   }}catch(SQLException e){System.out.println("Database operation failed: "+e.getMessage());}
   catch(RuntimeException e){System.out.println("Operation failed: "+e.getMessage());}
  }
  System.out.println("Application closed.");
 }
}

package com.restaurant.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.*;

@Controller
@Validated
public class RestaurantController {
    private final JdbcTemplate db;
    private final NamedParameterJdbcTemplate named;
    public RestaurantController(JdbcTemplate db, NamedParameterJdbcTemplate named) { this.db=db; this.named=named; }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model m) {
        m.addAttribute("page","Dashboard");
        m.addAttribute("menuCount", scalar("SELECT COUNT(*) FROM menu_items"));
        m.addAttribute("customerCount", scalar("SELECT COUNT(*) FROM customers"));
        m.addAttribute("orderCount", scalar("SELECT COUNT(*) FROM orders"));
        m.addAttribute("sales", db.queryForObject("SELECT COALESCE(SUM(total_amount),0) FROM orders WHERE status='Completed'", BigDecimal.class));
        m.addAttribute("recentOrders", db.queryForList("SELECT o.order_id,c.customer_name,o.order_date,o.status,o.total_amount FROM orders o JOIN customers c ON c.customer_id=o.customer_id ORDER BY o.order_date DESC LIMIT 8"));
        return "dashboard";
    }
    private long scalar(String sql) { Long n=db.queryForObject(sql,Long.class); return n==null?0:n; }

    @GetMapping("/menu") public String menu(Model m) { m.addAttribute("page","Menu Management"); m.addAttribute("items",db.queryForList("SELECT * FROM menu_items ORDER BY item_id DESC")); return "menu"; }
    @PostMapping("/menu") public String addMenu(@RequestParam String itemName,@RequestParam String category,@RequestParam BigDecimal price,RedirectAttributes ra) {
        if(itemName.isBlank()||category.isBlank()||price.signum()<0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Enter valid menu details.");
        db.update("INSERT INTO menu_items(item_name,category,price,available) VALUES(?,?,?,true)",itemName.trim(),category.trim(),price); ra.addFlashAttribute("success","Menu item added."); return "redirect:/menu";
    }
    @PostMapping("/menu/{id}/edit") public String editMenu(@PathVariable int id,@RequestParam String itemName,@RequestParam String category,@RequestParam BigDecimal price,RedirectAttributes ra) {
        if(itemName.isBlank()||category.isBlank()||price.signum()<0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Enter valid menu details.");
        int n=db.update("UPDATE menu_items SET item_name=?,category=?,price=? WHERE item_id=?",itemName.trim(),category.trim(),price,id); ra.addFlashAttribute(n>0?"success":"error",n>0?"Menu item updated.":"Menu item not found."); return "redirect:/menu";
    }
    @PostMapping("/menu/{id}/availability") public String availability(@PathVariable int id,@RequestParam boolean available,RedirectAttributes ra) { int n=db.update("UPDATE menu_items SET available=? WHERE item_id=?",available,id); ra.addFlashAttribute(n>0?"success":"error",n>0?"Availability updated.":"Menu item not found."); return "redirect:/menu"; }
    @PostMapping("/menu/{id}/delete") public String deleteMenu(@PathVariable int id,RedirectAttributes ra) { try { int n=db.update("DELETE FROM menu_items WHERE item_id=?",id); ra.addFlashAttribute(n>0?"success":"error",n>0?"Menu item deleted.":"Menu item not found."); } catch(DataAccessException e) { ra.addFlashAttribute("error","This item is linked to an order and cannot be deleted. Mark it unavailable instead."); } return "redirect:/menu"; }

    @GetMapping("/customers") public String customers(Model m) { m.addAttribute("page","Customer Management"); m.addAttribute("customers",db.queryForList("SELECT * FROM customers ORDER BY customer_id DESC")); return "customers"; }
    @PostMapping("/customers") public String addCustomer(@RequestParam String customerName,@RequestParam String phone,@RequestParam(required=false) String email,RedirectAttributes ra) {
        if(customerName.isBlank()||phone.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name and phone are required.");
        db.update("INSERT INTO customers(customer_name,phone,email) VALUES(?,?,?)",customerName.trim(),phone.trim(),email==null||email.isBlank()?null:email.trim()); ra.addFlashAttribute("success","Customer registered."); return "redirect:/customers";
    }

    @PostMapping("/customers/{id}/edit") public String editCustomer(@PathVariable int id,@RequestParam String customerName,@RequestParam String phone,@RequestParam(required=false) String email,RedirectAttributes ra) {
        if(customerName.isBlank()||phone.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name and phone are required.");
        int n=db.update("UPDATE customers SET customer_name=?,phone=?,email=? WHERE customer_id=?",customerName.trim(),phone.trim(),email==null||email.isBlank()?null:email.trim(),id); ra.addFlashAttribute(n>0?"success":"error",n>0?"Customer updated.":"Customer not found."); return "redirect:/customers";
    }

    @GetMapping("/orders") public String orders(Model m) { m.addAttribute("page","Order Management"); m.addAttribute("orders",db.queryForList("SELECT o.order_id,c.customer_name,o.order_date,o.status,o.total_amount FROM orders o JOIN customers c ON c.customer_id=o.customer_id ORDER BY o.order_date DESC")); m.addAttribute("customers",db.queryForList("SELECT customer_id,customer_name FROM customers ORDER BY customer_name")); m.addAttribute("items",db.queryForList("SELECT item_id,item_name,price FROM menu_items WHERE available=true ORDER BY item_name")); m.addAttribute("statuses",List.of("Placed","Preparing","Completed","Cancelled")); return "orders"; }
    @PostMapping("/orders") @Transactional public String placeOrder(@RequestParam int customerId,@RequestParam("itemId") List<Integer> itemIds,@RequestParam("quantity") List<Integer> quantities,RedirectAttributes ra) {
        if(itemIds.isEmpty()||itemIds.size()!=quantities.size()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Select at least one item.");
        Integer exists=db.queryForObject("SELECT COUNT(*) FROM customers WHERE customer_id=?",Integer.class,customerId); if(exists==null||exists==0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Customer not found.");
        var keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        db.update(connection -> { var ps=connection.prepareStatement("INSERT INTO orders(customer_id,status,total_amount) VALUES(?,'Placed',0)", java.sql.Statement.RETURN_GENERATED_KEYS); ps.setInt(1,customerId); return ps; }, keyHolder);
        if(keyHolder.getKey()==null) throw new IllegalStateException("Could not generate order ID.");
        int orderId=keyHolder.getKey().intValue();
        BigDecimal total=BigDecimal.ZERO;
        for(int i=0;i<itemIds.size();i++){int qty=quantities.get(i),item=itemIds.get(i);if(qty<=0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quantity must be positive."); var rows=db.queryForList("SELECT price FROM menu_items WHERE item_id=? AND available=true",item); if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Selected item is unavailable."); BigDecimal price=(BigDecimal)rows.get(0).get("price");BigDecimal line=price.multiply(BigDecimal.valueOf(qty));total=total.add(line);db.update("INSERT INTO order_items(order_id,item_id,quantity,unit_price,line_total) VALUES(?,?,?,?,?)",orderId,item,qty,price,line);}
        db.update("UPDATE orders SET total_amount=? WHERE order_id=?",total,orderId);ra.addFlashAttribute("success","Order #"+orderId+" placed. Total: ₹"+total);return "redirect:/orders";
    }
    @PostMapping("/orders/{id}/status") public String status(@PathVariable int id,@RequestParam String status,RedirectAttributes ra){if(!Set.of("Placed","Preparing","Completed","Cancelled").contains(status))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid order status.");int n=db.update("UPDATE orders SET status=? WHERE order_id=?",status,id);ra.addFlashAttribute(n>0?"success":"error",n>0?"Order status updated.":"Order not found.");return "redirect:/orders";}

    @GetMapping("/payments") public String payments(Model m){m.addAttribute("page","Payment Management");m.addAttribute("orders",db.queryForList("SELECT order_id,total_amount FROM orders ORDER BY order_id DESC"));m.addAttribute("payments",db.queryForList("SELECT p.*,o.total_amount FROM payments p JOIN orders o ON o.order_id=p.order_id ORDER BY payment_date DESC"));return "payments";}
    @PostMapping("/payments") public String payment(@RequestParam int orderId,@RequestParam BigDecimal amount,@RequestParam String method,RedirectAttributes ra){if(amount.signum()<0||!Set.of("Cash","UPI","Card","Other").contains(method))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid payment details.");BigDecimal total=db.queryForObject("SELECT total_amount FROM orders WHERE order_id=?",BigDecimal.class,orderId);if(total==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Order not found.");BigDecimal paid=db.queryForObject("SELECT COALESCE(SUM(amount),0) FROM payments WHERE order_id=?",BigDecimal.class,orderId);String state=paid.add(amount).compareTo(total)>=0?"Paid":amount.signum()>0||paid.signum()>0?"Partial":"Pending";db.update("INSERT INTO payments(order_id,amount,payment_method,payment_status) VALUES(?,?,?,?)",orderId,amount,method,state);ra.addFlashAttribute("success","Payment recorded.");return "redirect:/payments";}

    @GetMapping("/reports") public String reports(Model m){m.addAttribute("page","Sales Reports");m.addAttribute("customerOrders",db.queryForList("SELECT c.customer_name,o.order_id,o.order_date,o.status,o.total_amount FROM customers c JOIN orders o ON o.customer_id=c.customer_id ORDER BY o.order_date DESC"));m.addAttribute("popular",db.queryForList("SELECT m.item_name,SUM(oi.quantity) quantity_sold FROM order_items oi JOIN menu_items m ON m.item_id=oi.item_id JOIN orders o ON o.order_id=oi.order_id WHERE o.status<>'Cancelled' GROUP BY m.item_id,m.item_name ORDER BY quantity_sold DESC"));m.addAttribute("daily",db.queryForList("SELECT DATE(order_date) sales_day,SUM(total_amount) daily_sales FROM orders WHERE status='Completed' GROUP BY DATE(order_date) ORDER BY sales_day DESC"));return "reports";}

}

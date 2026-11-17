package com.restaurant.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final JdbcTemplate db;
    public ApiController(JdbcTemplate db) { this.db=db; }

    @GetMapping("/health")
    public Map<String,Object> health() {
        return Map.of("status","UP","database",db.queryForObject("SELECT 1",Integer.class));
    }
    @GetMapping("/menu")
    public List<Map<String,Object>> menu() { return db.queryForList("SELECT * FROM menu_items ORDER BY item_id"); }
    @GetMapping("/customers")
    public List<Map<String,Object>> customers() { return db.queryForList("SELECT * FROM customers ORDER BY customer_id"); }
    @GetMapping("/orders")
    public List<Map<String,Object>> orders() { return db.queryForList("SELECT * FROM orders ORDER BY order_date DESC"); }
    @GetMapping("/reports/daily-sales")
    public List<Map<String,Object>> sales() { return db.queryForList("SELECT DATE(order_date) sales_day,SUM(total_amount) daily_sales FROM orders WHERE status='Completed' GROUP BY DATE(order_date) ORDER BY sales_day"); }
}

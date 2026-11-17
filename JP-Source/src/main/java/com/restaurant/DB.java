package com.restaurant;
import java.sql.*;
public final class DB {
 private static final String URL=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
 private static final String USER=System.getenv().getOrDefault("DB_USER","root");
 private static final String PASS=System.getenv().getOrDefault("DB_PASSWORD","");
 private DB(){}
 public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL,USER,PASS); }
}

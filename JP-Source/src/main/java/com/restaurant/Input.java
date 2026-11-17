package com.restaurant;
import java.math.*;
import java.util.Scanner;
public final class Input {
 private static final Scanner IN=new Scanner(System.in);
 private Input(){}
 public static String text(String p){while(true){System.out.print(p);String s=IN.nextLine().trim();if(!s.isEmpty())return s;System.out.println("Input cannot be empty.");}}
 public static int integer(String p){while(true){System.out.print(p);try{return Integer.parseInt(IN.nextLine().trim());}catch(NumberFormatException e){System.out.println("Enter a whole number.");}}}
 public static int positiveInt(String p){while(true){int n=integer(p);if(n>0)return n;System.out.println("Enter a value greater than zero.");}}
 public static BigDecimal money(String p){while(true){System.out.print(p);try{BigDecimal n=new BigDecimal(IN.nextLine().trim());if(n.signum()>=0)return n.setScale(2,RoundingMode.HALF_UP);}catch(NumberFormatException ignored){}System.out.println("Enter a valid non-negative amount.");}}
 public static String optionalText(String p){System.out.print(p);return IN.nextLine().trim();}
 public static boolean yesNo(String p){while(true){System.out.print(p+" (y/n): ");String s=IN.nextLine().trim().toLowerCase();if(s.equals("y"))return true;if(s.equals("n"))return false;System.out.println("Enter y or n.");}}
}

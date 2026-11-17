from pathlib import Path
import pandas as pd
import mysql.connector
from config import DB_CONFIG
OUT=Path(__file__).resolve().parent/"output"
OUT.mkdir(parents=True,exist_ok=True)
QUERIES={
 "orders": "SELECT o.order_id,o.customer_id,c.customer_name,o.order_date,o.status,o.total_amount FROM orders o JOIN customers c ON c.customer_id=o.customer_id ORDER BY o.order_date",
 "order_items": "SELECT oi.order_item_id,oi.order_id,oi.item_id,m.item_name,m.category,oi.quantity,oi.unit_price,oi.line_total FROM order_items oi JOIN menu_items m ON m.item_id=oi.item_id ORDER BY oi.order_item_id",
 "menu_items": "SELECT item_id,item_name,category,price,available FROM menu_items",
 "customers": "SELECT customer_id,customer_name,phone,email FROM customers",
 "payments": "SELECT payment_id,order_id,amount,payment_method,payment_status,payment_date FROM payments ORDER BY payment_date",
}
def retrieve_all():
 conn=mysql.connector.connect(**DB_CONFIG)
 try:
  data={}
  for name,sql in QUERIES.items():
   data[name]=pd.read_sql(sql,conn)
   data[name].to_csv(OUT/f"{name}_raw.csv",index=False)
   print(f"Retrieved {len(data[name])} {name} record(s).")
  return data
 finally: conn.close()
if __name__=="__main__": retrieve_all()
